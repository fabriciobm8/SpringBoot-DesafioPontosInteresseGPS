# GPS - Sistema de Gerenciamento de Pontos de Interesse

## Descrição

O **GPS** é uma aplicação backend desenvolvida com **Spring Boot** para gerenciar pontos de interesse, como lanchonetes, postos, joalherias, entre outros, com base em coordenadas geográficas (x, y). A aplicação permite criar, listar, buscar, atualizar e deletar pontos de interesse, além de buscar pontos próximos a uma localização específica dentro de uma distância máxima usando a distância euclidiana. O sistema utiliza um banco de dados **H2** em memória para persistência e oferece uma API RESTful com validação de entrada, tratamento de erros, e documentação via OpenAPI (Swagger).

Este projeto é ideal para aplicações que precisam gerenciar e consultar locais com base em suas coordenadas, com suporte a paginação e ordenação para resultados eficientes.

## Tecnologias Utilizadas

- **Java 21**: Linguagem de programação principal.
- **Spring Boot 3.5.0**: Framework para desenvolvimento do backend.
- **Spring Data JPA**: Para acesso e manipulação do banco de dados.
- **H2 Database**: Banco de dados em memória para desenvolvimento e testes.
- **Lombok**: Para reduzir código boilerplate (getters, setters, builders).
- **Springdoc OpenAPI**: Para documentação automática da API com Swagger UI.
- **Spring Validation**: Para validação de entrada nos endpoints.
- **Maven**: Gerenciador de dependências e build.

## Pré-requisitos

Para executar o projeto, você precisa ter instalado:
- **Java 21** (JDK).
- **Maven** (3.6.0 ou superior).
- Um IDE como **IntelliJ IDEA** ou **Eclipse** (opcional, mas recomendado).
- **cURL**, **Postman**, ou um navegador para testar os endpoints.
- Opcionalmente, um cliente HTTP para explorar a interface Swagger UI.

## Configuração e Instalação

1. **Clone o repositório**:
   - Clone o projeto do GitHub para sua máquina local:
     ```bash
     git clone https://github.com/seu-usuario/gps.git
     ```
   - Navegue até o diretório do projeto:
     ```bash
     cd gps
     ```

2. **Configure o ambiente**:
   - O projeto usa um banco de dados H2 em memória, que é inicializado automaticamente.
   - As configurações do banco estão no arquivo `application.properties`:
     - Banco: `jdbc:h2:mem:gps`
     - Usuário: `sa`
     - Senha: (vazia)
     - Modo: `spring.jpa.hibernate.ddl-auto=update`
     - Console H2 habilitado em `/h2-console`.

3. **Compile o projeto**:
   - Use o Maven para baixar dependências e compilar:
     ```bash
     mvn clean install
     ```

4. **Execute a aplicação**:
   - Inicie o Spring Boot:
     ```bash
     mvn spring-boot:run
     ```
   - A aplicação estará disponível em `http://localhost:8080`.

5. **Acesse o console H2** (opcional):
   - Abra `http://localhost:8080/h2-console` no navegador.
   - Use a URL `jdbc:h2:mem:gps`, usuário `sa`, e senha vazia para acessar o banco.

6. **Acesse a documentação da API**:
   - A interface Swagger UI está disponível em `http://localhost:8080/swagger-ui/index.html`.
   - Use-a para explorar e testar os endpoints da API.

## Estrutura do Projeto

- **`src/main/java/com/example/gps/`**:
  - `GpsApplication.java`: Ponto de entrada da aplicação, inicializa dados de exemplo.
  - **`model/`**:
    - `PontosInteresse.java`: Entidade JPA que representa um ponto de interesse (id, nome, x, y).
    - `PontosInteresseRequestDTO.java`: DTO para entrada de dados (validações inclusas).
    - `PontosInteresseResponseDTO.java`: DTO para saída de dados.
    - `ErrorResponse.java`: Formato de respostas de erro.
  - **`repository/`**:
    - `PontosInteresseRepository.java`: Repositório JPA com query personalizada para busca de pontos próximos.
  - **`controller/`**:
    - `PontosInteresseController.java`: Controlador REST com endpoints CRUD e busca por proximidade.
  - **`exception/`**:
    - `GlobalExceptionHandler.java`: Tratamento centralizado de exceções com respostas padronizadas.
- **`src/main/resources/`**:
  - `application.properties`: Configurações do banco H2, Spring Boot, e console H2.

## Endpoints da API

A API oferece endpoints REST para gerenciar pontos de interesse. Todos os endpoints estão mapeados em `/pontos-de-interesse` ou `/listar`.

| Método | Endpoint                                     | Descrição                                                                 |
|--------|----------------------------------------------|---------------------------------------------------------------------------|
| POST   | `/pontos-de-interesse`                       | Cria um novo ponto de interesse.                                         |
| GET    | `/listar/pontos-de-interesse`                | Lista todos os pontos de interesse com suporte a paginação e ordenação.  |
| GET    | `/{id}`                                      | Busca um ponto de interesse por ID.                                      |
| PUT    | `/{id}`                                      | Atualiza um ponto de interesse existente por ID.                         |
| DELETE | `/{id}`                                      | Deleta um ponto de interesse por ID.                                     |
| GET    | `/listar/pontos-proximos`                    | Lista pontos de interesse próximos a uma coordenada (x, y) dentro de uma distância máxima (dmax). |

### **Detalhes dos endpoints**

1. **POST /pontos-de-interesse**
   - **Descrição**: Cria um novo ponto de interesse.
   - **Corpo da requisição**:
     - `nome`: Nome do ponto (não nulo, não vazio).
     - `x`: Coordenada x (não nula, maior ou igual a 0).
     - `y`: Coordenada y (não nula, maior ou igual a 0).
   - **Exemplo**:
     ```json
     {
       "nome": "Cinema",
       "x": 10,
       "y": 15
     }
     ```
   - **Resposta**: 200 OK (sem corpo).

2. **GET /listar/pontos-de-interesse**
   - **Descrição**: Retorna uma lista paginada de pontos de interesse.
   - **Parâmetros**:
     - `page`: Número da página (padrão: 0, mínimo: 0).
     - `size`: Tamanho da página (padrão: 10, mínimo: 1, máximo: 100).
     - `sort`: Ordenação (ex.: `id,asc` ou `nome,desc`).
   - **Exemplo**:
     ```
     http://localhost:8080/listar/pontos-de-interesse?page=0&size=2&sort=nome,asc
     ```
   - **Resposta**:
     ```json
     {
       "content": [
         {
           "id": 1,
           "nome": "Churrascaria",
           "x": 28,
           "y": 2
         },
         {
           "id": 2,
           "nome": "Floricultura",
           "x": 19,
           "y": 21
         }
       ],
       "pageable": {...},
       "totalElements": 7,
       "totalPages": 4,
       ...
     }
     ```

3. **GET /{id}**
   - **Descrição**: Busca um ponto de interesse por ID.
   - **Exemplo**:
     ```
     http://localhost:8080/pontos-de-interesse/1
     ```
   - **Resposta**:
     ```json
     {
       "id": 1,
       "nome": "Lanchonete",
       "x": 27,
       "y": 12
     }
     ```
   - **Erro**: 404 se o ID não for encontrado.

4. **PUT /{id}**
   - **Descrição**: Atualiza um ponto de interesse existente.
   - **Corpo da requisição**: Mesmo formato do POST.
   - **Exemplo**:
     ```
     http://localhost:8080/pontos-de-interesse/1
     ```
     ```json
     {
       "nome": "Nova Lanchonete",
       "x": 30,
       "y": 10
     }
     ```
   - **Resposta**: 200 OK com o ponto atualizado.
   - **Erro**: 404 se o ID não for encontrado.

5. **DELETE /{id}**
   - **Descrição**: Deleta um ponto de interesse por ID.
   - **Exemplo**:
     ```
     http://localhost:8080/pontos-de-interesse/1
     ```
   - **Resposta**: 204 No Content.
   - **Erro**: 404 se o ID não for encontrado.

6. **GET /listar/pontos-proximos**
   - **Descrição**: Lista pontos de interesse dentro de uma distância máxima (dmax) de uma coordenada (x, y), usando distância euclidiana.
   - **Parâmetros**:
     - `x`: Coordenada x (obrigatória, maior que 0).
     - `y`: Coordenada y (obrigatória, maior que 0).
     - `dmax`: Distância máxima (obrigatória, maior que 0).
     - `page`, `size`, `sort`: Mesmos parâmetros do endpoint de listagem.
   - **Exemplo**:
     ```
     http://localhost:8080/listar/pontos-proximos?x=20&y=10&dmax=10&page=0&size=2&sort=nome,asc
     ```
   - **Resposta**:
     ```json
     {
       "content": [
         {
           "id": 3,
           "nome": "Joalheria",
           "x": 15,
           "y": 12
         },
         {
           "id": 5,
           "nome": "Pub",
           "x": 12,
           "y": 8
         }
       ],
       "pageable": {...},
       "totalElements": 2,
       "totalPages": 1,
       ...
     }
     ```

## Como Testar

1. **Usando cURL**:
   - Criar um ponto:
     ```bash
     curl -X POST http://localhost:8080/pontos-de-interesse -H "Content-Type: application/json" -d '{"nome":"Cinema","x":10,"y":15}'
     ```
   - Listar pontos próximos:
     ```bash
     curl "http://localhost:8080/listar/pontos-proximos?x=20&y=10&dmax=10&page=0&size=2&sort=nome,asc"
     ```

2. **Usando Swagger UI**:
   - Acesse `http://localhost:8080/swagger-ui/index.html` para testar todos os endpoints interativamente.

3. **Usando Postman**:
   - Configure requisições HTTP com os endpoints acima e envie JSON conforme os exemplos.

## Dados de Exemplo

Ao iniciar a aplicação, os seguintes pontos de interesse são carregados automaticamente no banco H2:
- Lanchonete (x: 27, y: 12)
- Posto (x: 31, y: 18)
- Joalheria (x: 15, y: 12)
- Floricultura (x: 19, y: 21)
- Pub (x: 12, y: 8)
- Supermercado (x: 23, y: 6)
- Churrascaria (x: 28, y: 2)

## Tratamento de Erros

A aplicação inclui um manipulador global de exceções que retorna respostas padronizadas para erros:
- **400 Bad Request**: Para validações inválidas (ex.: coordenadas negativas, nome vazio).
- **404 Not Found**: Quando um ponto de interesse não é encontrado pelo ID.
- **500 Internal Server Error**: Para erros genéricos do servidor.
- **Exemplo de erro**:
  ```json
  {
    "status": 400,
    "error": "Bad Request",
    "message": "Erro de validação nos dados enviados",
    "timestamp": "2025-07-14T23:27:00",
    "details": {
      "x": "A coordenada X deve ser maior que zero"
    },
    "path": "/listar/pontos-proximos"
  }
  ```

## Possíveis Melhorias

- **Autenticação**: Adicionar Spring Security para proteger endpoints.
- **Banco de dados persistente**: Migrar do H2 para PostgreSQL ou MySQL para produção.
- **PostGIS**: Usar PostGIS para cálculos de distância mais precisos em coordenadas geográficas reais.
- **Testes unitários**: Adicionar mais testes JUnit para os endpoints e lógica de negócio.
- **Cache**: Implementar cache (ex.: com Redis) para buscas frequentes de pontos próximos.

## Contribuição

1. Faça um fork do repositório.
2. Crie uma branch para suas alterações (`git checkout -b feature/nova-funcionalidade`).
3. Commit suas alterações (`git commit -m "Adiciona nova funcionalidade"`).
4. Push para a branch (`git push origin feature/nova-funcionalidade`).
5. Abra um Pull Request no GitHub.

## Licença

Este projeto é licenciado sob a [MIT License](LICENSE) (adicione um arquivo LICENSE, se desejar).

## Contato

Para dúvidas ou sugestões, entre em contato com [fabriciobm@gmail.com](mailto:seu-email@exemplo.com) ou abra uma issue no repositório.