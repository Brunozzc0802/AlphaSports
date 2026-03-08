package com.alphasports.service;

import com.alphasports.model.Pedido;
import com.alphasports.model.StatusPedido;
import com.alphasports.repository.ClienteRepository;
import com.alphasports.repository.PedidoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class RelatorioService {

    @Autowired
    private PedidoRepository pedidoRepository;

    @Autowired
    private ClienteRepository clienteRepository;

    public RelatorioDTO gerarRelatorio() {
        List<Pedido> todos = pedidoRepository.findAllComItens();
        RelatorioDTO dto = new RelatorioDTO();
        calcularVendas(dto, todos);
        calcularProdutos(dto, todos);
        calcularClientes(dto, todos);
        return dto;
    }


    private void calcularVendas(RelatorioDTO dto, List<Pedido> todos) {
        List<Pedido> pagos = todos.stream()
                .filter(p -> p.getStatus() != StatusPedido.CANCELADO
                        && p.getStatus() != StatusPedido.AGUARDANDO_PAGAMENTO)
                .collect(Collectors.toList());

        BigDecimal receita = pagos.stream()
                .map(Pedido::getTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        dto.setReceitaTotal(receita);
        dto.setTotalPedidos(todos.size());
        dto.setTicketMedio(pagos.isEmpty() ? BigDecimal.ZERO
                : receita.divide(BigDecimal.valueOf(pagos.size()), 2, RoundingMode.HALF_UP));

        long entregues = todos.stream().filter(p -> p.getStatus() == StatusPedido.ENTREGUE).count();
        dto.setTaxaConclusao(todos.isEmpty() ? 0
                : (int) Math.round((double) entregues / todos.size() * 100));

        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("MMM/yy", new Locale("pt", "BR"));
        Map<String, long[]> porMes = new LinkedHashMap<>();
        for (Pedido p : pagos) {
            String mes = p.getDataPedido().format(fmt);
            porMes.computeIfAbsent(mes, k -> new long[]{0})[0] += p.getTotal().longValue();
        }
        List<Map<String, Object>> receitaMensal = new ArrayList<>();
        porMes.forEach((mes, val) -> {
            Map<String, Object> m = new LinkedHashMap<>();
            m.put("mes", mes);
            m.put("valor", val[0]);
            receitaMensal.add(m);
        });
        dto.setReceitaMensal(receitaMensal);

        List<RelatorioDTO.StatusItem> porStatus = new ArrayList<>();
        for (StatusPedido s : StatusPedido.values()) {
            List<Pedido> grupo = todos.stream()
                    .filter(p -> p.getStatus() == s).collect(Collectors.toList());
            BigDecimal valor = grupo.stream().map(Pedido::getTotal)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            int pct = todos.isEmpty() ? 0
                    : (int) Math.round((double) grupo.size() / todos.size() * 100);
            RelatorioDTO.StatusItem item = new RelatorioDTO.StatusItem();
            item.setStatus(s.name());
            item.setDescricao(s.getDescricao());
            item.setQuantidade(grupo.size());
            item.setValor(valor);
            item.setPercentual(pct);
            porStatus.add(item);
        }
        dto.setPorStatus(porStatus);
    }

    private void calcularProdutos(RelatorioDTO dto, List<Pedido> todos) {
        Map<String, long[]> contagem = new LinkedHashMap<>();
        Map<String, BigDecimal> receita = new LinkedHashMap<>();
        Map<String, long[]> categoriaMap = new LinkedHashMap<>();
        Map<String, long[]> marcaMap = new LinkedHashMap<>();

        todos.forEach(p -> p.getItens().forEach(item -> {
            if (item.getQuantidade() == null) return;

            String nome = item.getNomeProduto() != null ? item.getNomeProduto() : "Desconhecido";
            contagem.computeIfAbsent(nome, k -> new long[]{0})[0] += item.getQuantidade();
            receita.merge(nome,
                    item.getSubtotal() != null ? item.getSubtotal() : BigDecimal.ZERO,
                    BigDecimal::add);

            if (item.getProduto() != null) {
                String cat = item.getProduto().getCategoria() != null
                        ? item.getProduto().getCategoria() : "Outros";
                categoriaMap.computeIfAbsent(cat, k -> new long[]{0})[0] += item.getQuantidade();

                if (item.getProduto().getMarca() != null
                        && item.getProduto().getMarca().getNome() != null) {
                    String marca = item.getProduto().getMarca().getNome();
                    marcaMap.computeIfAbsent(marca, k -> new long[]{0})[0] += item.getQuantidade();
                }
            }
        }));

        long totalItens = contagem.values().stream().mapToLong(v -> v[0]).sum();
        dto.setTotalItensVendidos((int) totalItens);
        dto.setTotalCategorias(categoriaMap.size());

        dto.setMarcaTop(marcaMap.entrySet().stream()
                .max(Comparator.comparingLong(e -> e.getValue()[0]))
                .map(Map.Entry::getKey)
                .orElse("—"));

        List<RelatorioDTO.ProdutoItem> top = contagem.entrySet().stream()
                .sorted((a, b) -> Long.compare(b.getValue()[0], a.getValue()[0]))
                .limit(5)
                .map(e -> {
                    RelatorioDTO.ProdutoItem pi = new RelatorioDTO.ProdutoItem();
                    pi.setNome(e.getKey());
                    pi.setQuantidade((int) e.getValue()[0]);
                    pi.setReceita(receita.getOrDefault(e.getKey(), BigDecimal.ZERO));
                    pi.setPercentual(totalItens == 0 ? 0
                            : (int) Math.round((double) e.getValue()[0] / totalItens * 100));
                    return pi;
                })
                .collect(Collectors.toList());
        dto.setTopProdutos(top);

        if (!top.isEmpty()) {
            dto.setProdutoMaisVendido(top.get(0).getNome());
            dto.setQtdProdutoMaisVendido(top.get(0).getQuantidade());
        } else {
            dto.setProdutoMaisVendido("—");
            dto.setQtdProdutoMaisVendido(0);
        }

        List<Map<String, Object>> catList = new ArrayList<>();
        categoriaMap.forEach((cat, val) -> {
            Map<String, Object> m = new LinkedHashMap<>();
            m.put("categoria", cat);
            m.put("quantidade", val[0]);
            catList.add(m);
        });
        dto.setVendasPorCategoria(catList);
    }

    private void calcularClientes(RelatorioDTO dto, List<Pedido> todos) {
        dto.setTotalClientes((int) clienteRepository.count());

        Map<Long, List<Pedido>> porCliente = todos.stream()
                .filter(p -> p.getCliente() != null)
                .collect(Collectors.groupingBy(p -> p.getCliente().getId()));

        dto.setClientesAtivos(porCliente.size());
        dto.setClientesRecorrentes((int) porCliente.values().stream()
                .filter(l -> l.size() >= 2).count());

        Map<Long, BigDecimal> gastoCliente = new HashMap<>();
        porCliente.forEach((id, pedidos) -> {
            BigDecimal total = pedidos.stream().map(Pedido::getTotal)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            gastoCliente.put(id, total);
        });

        BigDecimal maxGasto = gastoCliente.values().stream()
                .max(Comparator.naturalOrder()).orElse(BigDecimal.ONE);

        List<RelatorioDTO.ClienteItem> topClientes = porCliente.entrySet().stream()
                .sorted((a, b) -> gastoCliente.get(b.getKey()).compareTo(gastoCliente.get(a.getKey())))
                .limit(10)
                .map(e -> {
                    RelatorioDTO.ClienteItem ci = new RelatorioDTO.ClienteItem();
                    ci.setNome(e.getValue().get(0).getCliente().getNome());
                    ci.setEmail(e.getValue().get(0).getCliente().getEmail());
                    ci.setTotalPedidos(e.getValue().size());
                    ci.setTotalGasto(gastoCliente.get(e.getKey()));
                    BigDecimal pct = maxGasto.compareTo(BigDecimal.ZERO) == 0 ? BigDecimal.ZERO
                            : gastoCliente.get(e.getKey())
                            .divide(maxGasto, 2, RoundingMode.HALF_UP)
                            .multiply(BigDecimal.valueOf(100));
                    ci.setPercentual(pct.intValue());
                    return ci;
                })
                .collect(Collectors.toList());
        dto.setTopClientes(topClientes);

        if (!topClientes.isEmpty()) {
            dto.setMaiorComprador(topClientes.get(0).getNome());
            dto.setValorMaiorComprador(topClientes.get(0).getTotalGasto());
        } else {
            dto.setMaiorComprador("—");
            dto.setValorMaiorComprador(BigDecimal.ZERO);
        }

        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("MMM/yy", new Locale("pt", "BR"));
        Map<String, long[]> porMes = new LinkedHashMap<>();
        todos.stream()
                .filter(p -> p.getCliente() != null)
                .collect(Collectors.toMap(
                        p -> p.getCliente().getId(),
                        p -> p,
                        (p1, p2) -> p1.getDataPedido().isBefore(p2.getDataPedido()) ? p1 : p2
                ))
                .values()
                .forEach(p -> {
                    String mes = p.getDataPedido().format(fmt);
                    porMes.computeIfAbsent(mes, k -> new long[]{0})[0]++;
                });

        List<Map<String, Object>> clientesMensais = new ArrayList<>();
        porMes.forEach((mes, val) -> {
            Map<String, Object> m = new LinkedHashMap<>();
            m.put("mes", mes);
            m.put("quantidade", val[0]);
            clientesMensais.add(m);
        });
        dto.setClientesMensais(clientesMensais);
    }

    public static class RelatorioDTO {
        private BigDecimal receitaTotal;
        private int totalPedidos;
        private BigDecimal ticketMedio;
        private int taxaConclusao;
        private List<Map<String, Object>> receitaMensal = new ArrayList<>();
        private List<StatusItem> porStatus = new ArrayList<>();
        private String produtoMaisVendido;
        private int qtdProdutoMaisVendido;
        private int totalItensVendidos;
        private int totalCategorias;
        private String marcaTop;
        private List<ProdutoItem> topProdutos = new ArrayList<>();
        private List<Map<String, Object>> vendasPorCategoria = new ArrayList<>();
        private int totalClientes;
        private int clientesAtivos;
        private int clientesRecorrentes;
        private String maiorComprador;
        private BigDecimal valorMaiorComprador;
        private List<ClienteItem> topClientes = new ArrayList<>();
        private List<Map<String, Object>> clientesMensais = new ArrayList<>();

        public BigDecimal getReceitaTotal() { return receitaTotal; }
        public void setReceitaTotal(BigDecimal v) { this.receitaTotal = v; }
        public int getTotalPedidos() { return totalPedidos; }
        public void setTotalPedidos(int v) { this.totalPedidos = v; }
        public BigDecimal getTicketMedio() { return ticketMedio; }
        public void setTicketMedio(BigDecimal v) { this.ticketMedio = v; }
        public int getTaxaConclusao() { return taxaConclusao; }
        public void setTaxaConclusao(int v) { this.taxaConclusao = v; }
        public List<Map<String, Object>> getReceitaMensal() { return receitaMensal; }
        public void setReceitaMensal(List<Map<String, Object>> v) { this.receitaMensal = v; }
        public List<StatusItem> getPorStatus() { return porStatus; }
        public void setPorStatus(List<StatusItem> v) { this.porStatus = v; }
        public String getProdutoMaisVendido() { return produtoMaisVendido; }
        public void setProdutoMaisVendido(String v) { this.produtoMaisVendido = v; }
        public int getQtdProdutoMaisVendido() { return qtdProdutoMaisVendido; }
        public void setQtdProdutoMaisVendido(int v) { this.qtdProdutoMaisVendido = v; }
        public int getTotalItensVendidos() { return totalItensVendidos; }
        public void setTotalItensVendidos(int v) { this.totalItensVendidos = v; }
        public int getTotalCategorias() { return totalCategorias; }
        public void setTotalCategorias(int v) { this.totalCategorias = v; }
        public String getMarcaTop() { return marcaTop; }
        public void setMarcaTop(String v) { this.marcaTop = v; }
        public List<ProdutoItem> getTopProdutos() { return topProdutos; }
        public void setTopProdutos(List<ProdutoItem> v) { this.topProdutos = v; }
        public List<Map<String, Object>> getVendasPorCategoria() { return vendasPorCategoria; }
        public void setVendasPorCategoria(List<Map<String, Object>> v) { this.vendasPorCategoria = v; }
        public int getTotalClientes() { return totalClientes; }
        public void setTotalClientes(int v) { this.totalClientes = v; }
        public int getClientesAtivos() { return clientesAtivos; }
        public void setClientesAtivos(int v) { this.clientesAtivos = v; }
        public int getClientesRecorrentes() { return clientesRecorrentes; }
        public void setClientesRecorrentes(int v) { this.clientesRecorrentes = v; }
        public String getMaiorComprador() { return maiorComprador; }
        public void setMaiorComprador(String v) { this.maiorComprador = v; }
        public BigDecimal getValorMaiorComprador() { return valorMaiorComprador; }
        public void setValorMaiorComprador(BigDecimal v) { this.valorMaiorComprador = v; }
        public List<ClienteItem> getTopClientes() { return topClientes; }
        public void setTopClientes(List<ClienteItem> v) { this.topClientes = v; }
        public List<Map<String, Object>> getClientesMensais() { return clientesMensais; }
        public void setClientesMensais(List<Map<String, Object>> v) { this.clientesMensais = v; }

        public static class StatusItem {
            private String status, descricao;
            private int quantidade, percentual;
            private BigDecimal valor;
            public String getStatus() { return status; }
            public void setStatus(String v) { this.status = v; }
            public String getDescricao() { return descricao; }
            public void setDescricao(String v) { this.descricao = v; }
            public int getQuantidade() { return quantidade; }
            public void setQuantidade(int v) { this.quantidade = v; }
            public int getPercentual() { return percentual; }
            public void setPercentual(int v) { this.percentual = v; }
            public BigDecimal getValor() { return valor; }
            public void setValor(BigDecimal v) { this.valor = v; }
        }

        public static class ProdutoItem {
            private String nome;
            private int quantidade, percentual;
            private BigDecimal receita;
            public String getNome() { return nome; }
            public void setNome(String v) { this.nome = v; }
            public int getQuantidade() { return quantidade; }
            public void setQuantidade(int v) { this.quantidade = v; }
            public int getPercentual() { return percentual; }
            public void setPercentual(int v) { this.percentual = v; }
            public BigDecimal getReceita() { return receita; }
            public void setReceita(BigDecimal v) { this.receita = v; }
        }

        public static class ClienteItem {
            private String nome, email;
            private int totalPedidos, percentual;
            private BigDecimal totalGasto;
            public String getNome() { return nome; }
            public void setNome(String v) { this.nome = v; }
            public String getEmail() { return email; }
            public void setEmail(String v) { this.email = v; }
            public int getTotalPedidos() { return totalPedidos; }
            public void setTotalPedidos(int v) { this.totalPedidos = v; }
            public int getPercentual() { return percentual; }
            public void setPercentual(int v) { this.percentual = v; }
            public BigDecimal getTotalGasto() { return totalGasto; }
            public void setTotalGasto(BigDecimal v) { this.totalGasto = v; }
        }
    }
}