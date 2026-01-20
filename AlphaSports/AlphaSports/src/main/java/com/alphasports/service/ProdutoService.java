package com.alphasports.service;

import com.alphasports.model.Produto;
import com.alphasports.repository.ProdutoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;

@Service
public class ProdutoService {

    @Autowired
    private ProdutoRepository produtoRepository;

    private static final String UPLOAD_DIR = "uploads/produtos/";

    public List<Produto> listarTodos() {
        return produtoRepository.findAll();
    }

    public List<Produto> listarPorCategoria(String categoria) {
        return produtoRepository.findByCategoria(categoria);
    }

    public List<Produto> buscarPorNome(String nome) {
        return produtoRepository.findByNomeContainingIgnoreCase(nome);
    }

    public Produto buscarPorId(Long id) {
        return produtoRepository.findById(id).orElse(null);
    }

    public Produto salvar(Produto produto) {
        return produtoRepository.save(produto);
    }

    public Produto atualizar(Produto produto) {
        if (produtoRepository.existsById(produto.getId())) {
            return produtoRepository.save(produto);
        }
        return null;
    }

    public boolean deletar(Long id) {
        if (produtoRepository.existsById(id)) {
            produtoRepository.deleteById(id);
            return true;
        }
        return false;
    }

    public String salvarImagem(Long produtoId, MultipartFile file) throws Exception {
        Produto produto = buscarPorId(produtoId);
        if (produto == null) {
            throw new Exception("Produto não encontrado");
        }

        // Criar diretório se não existir
        File uploadDir = new File(UPLOAD_DIR);
        if (!uploadDir.exists()) {
            uploadDir.mkdirs();
        }

        // Gerar nome único para o arquivo
        String nomeOriginal = file.getOriginalFilename();
        String extensao = nomeOriginal.substring(nomeOriginal.lastIndexOf("."));
        String nomeArquivo = UUID.randomUUID().toString() + extensao;

        // Salvar arquivo
        Path caminho = Paths.get(UPLOAD_DIR + nomeArquivo);
        Files.write(caminho, file.getBytes());

        // Atualizar produto com URL da imagem
        String urlImagem = "/uploads/produtos/" + nomeArquivo;
        produto.setImagem(urlImagem);
        produtoRepository.save(produto);

        return urlImagem;
    }

    public List<Produto> listarMaisVendidos() {
        return produtoRepository.findByMaisVendidoTrue();
    }

    public List<Produto> listarNovos() {
        return produtoRepository.findByNovoTrue();
    }

    public List<Produto> listarPorMarca(String marca) {
        return produtoRepository.findByMarca(marca);
    }
}
