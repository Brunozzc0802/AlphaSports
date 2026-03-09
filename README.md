# AlphaSports 🏀

AlphaSports é um projeto de **e-commerce de produtos esportivos** desenvolvido com **Java e Spring Boot**, simulando funcionalidades reais de uma loja online como cadastro de usuários, catálogo de produtos, carrinho de compras, pedidos e controle de estoque.

## 🚀 Tecnologias Utilizadas

Backend:
- Java
- Spring Boot
- Spring MVC
- Spring Security
- Spring Data JPA
- Maven

Frontend:
- HTML
- CSS
- JavaScript
- Thymeleaf

Banco de Dados:
- PostgresSQL

## 📦 Funcionalidades

- Cadastro e login de usuários
- Catálogo de produtos
- Carrinho de compras
- Criação de pedidos
- Controle de estoque
- Área administrativa para gestão do sistema
- Relatórios de vendas e pedidos

## 🧱 Arquitetura

O projeto segue o padrão de **arquitetura em camadas**:

Controller → Service → Repository → Model

- **Controller:** responsável por receber as requisições HTTP
- **Service:** contém as regras de negócio da aplicação
- **Repository:** responsável pela comunicação com o banco de dados
- **Model:** representa as entidades do sistema

Essa separação facilita manutenção, organização e escalabilidade do projeto.

## 📂 Estrutura do Projeto

src  
 ├── controller  
 ├── service  
 ├── repository  
 ├── model  
 ├── dto  
 ├── config  
 └── resources  
      ├── templates  
      ├── static  
      └── application.properties  

## ⚙️ Como Executar o Projeto

1. Clone o repositório

git clone https://github.com/seuusuario/alphasports.git

2. Configure o banco de dados no arquivo **application.properties**

spring.datasource.url=  
spring.datasource.username=  
spring.datasource.password=

3. Execute o projeto com Maven

mvn spring-boot:run

ou execute a classe principal **AlphaSportsApplication.java**.

## 🔐 Segurança

O sistema utiliza **Spring Security** para autenticação e controle de acesso, protegendo rotas administrativas e criptografando senhas com **BCrypt**.

## 📌 Melhorias Futuras

- Integração com gateway de pagamento
- Upload de imagens de produtos
- API REST completa
- Dashboard administrativo com gráficos
- Testes automatizados

## 👨‍💻 Autor

Desenvolvido por **Bruno Tesser**.
