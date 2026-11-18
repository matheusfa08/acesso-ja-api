# Acesso Já API

API REST (Spring Boot, Java 25, Oracle) do projeto Acesso Já. O cidadão avisa, o órgão responde, todo mundo acompanha.
A organização do código segue o projeto **Soulpass** (DAOs simples com `ConnectionFactory`, mappers estáticos,
services com `ResponseStatusException`, DTOs como `record`).

## Como rodar

1. Rode `Acesso-ja.sql` e depois `src/main/resources/sql/ajustes_sugeridos.sql` no Oracle
   (o segundo cria a coluna `senha` e as sequences que a API usa).
2. Confira URL, usuário e senha na classe `dao/ConnectionFactory`.
   **Atenção:** esses dados estão escritos no código. Não envie para um repositório público.
   Dá para voltar a usar variáveis de ambiente (as linhas comentadas na própria classe).
3. Teste a conexão rodando o `main` de `tests/TestConnection` pela IDE.
4. `./mvnw spring-boot:run`. A API sobe em `http://localhost:8080/api/v1`.
5. Para um usuário de exemplo conseguir entrar, defina a senha dele com o `UPDATE` indicado no script de ajustes
   (a senha é guardada como texto simples).

## Como o projeto está organizado

| Pasta | O que tem | Quando mexer |
|---|---|---|
| `controller` | Recebe a requisição, chama o service e devolve o status HTTP. Sem regra de negócio. | Mudou um endpoint (caminho, método, permissão). |
| `services` | Regras de negócio (RN01 a RN12). Cria os DAOs com `new`, como no Soulpass. | Mudou uma regra. |
| `dao` | SQL com JDBC puro (um DAO por tabela) e a `ConnectionFactory`. | Mudou o banco ou uma consulta. |
| `entity` | Uma classe por tabela (Lombok `@Getter` e `@Setter`). | Mudou uma coluna. |
| `dto/request` e `dto/response` | O que entra e o que sai da API (`record`). | Mudou o contrato com o front. |
| `mapper` | Métodos estáticos `toEntity`, `toResponse` e afins. | Mudou o que aparece na resposta. |
| `enums` | Perfil, status e esfera (iguais aos `CHECK` do banco). | Mudou uma lista fixa. |
| `contexto` | `ContextoUsuario`: descobre quem chama a API pelo cabeçalho `X-Usuario-Id`. | Mudou a forma de identificar o usuário. |
| `config` | `ConfiguracaoWeb`: CORS. | Mudou as origens liberadas. |
| `tests` | Testes manuais (`main`) de conexão e de DAO. | Quiser conferir o banco. |

## Onde mudar as coisas mais comuns

- **Mínimo de confirmações**: `ConfirmacaoService.MINIMO_CONFIRMACOES` (RN03).
- **Limite de avisos por dia e janela de duplicidade**: `AvisoService.LIMITE_AVISOS_DIA` e `AvisoService.DUPLICADO_MINUTOS` (RN05 e RN11).
- **Categoria que pergunta "Está acontecendo agora?"**: `CategoriaService.NOME_CATEGORIA_DELITO` (RN10).
- **Números de emergência**: `EmergenciaService.listarContatos()`.
- **Tamanho máximo da foto**: `FotoService.MAX_BYTES` e `spring.servlet.multipart.max-file-size`.
- **Origens liberadas no CORS**: `acessoja.cors.origens` no `application.properties`.
- **Pasta das fotos**: `acessoja.upload.pasta`. Para usar S3 ou outro serviço, troque só o `FotoService`.

## Usuário

Não há autenticação, token nem perfil por endpoint. `POST /auth/login` confere e-mail e senha e devolve o usuário.
Nas chamadas que precisam saber quem é o usuário, o front envia o id no cabeçalho `X-Usuario-Id: <id>`.
A coluna `usuario.senha` guarda a senha em texto simples.

## Erros

Como no Soulpass, os erros saem como `ResponseStatusException`, e a mensagem aparece no corpo
(`spring.web.error.include-message=always`). Códigos usados: 400 (campo inválido), 401 (usuário não informado ou senha incorreta), 403 (regras de negócio, como confirmar o próprio aviso),
404 (não encontrado), 409 (duplicado), 422 (regra de negócio, como o delito em andamento), 429 (limite diário).

## Pendências conhecidas

- **Mapa** (`GET /avisos/mapa`): responde 501 porque o banco não tem latitude e longitude no aviso.
- **Foto com placa borrada** (RN09): hoje todo mundo recebe a foto original. Veja o TODO em `AvisoService.buscarFoto`.
- **Mais de uma foto por aviso**: o banco guarda uma só (`foto_url`).
- **Título do aviso**: o protótipo tem, o banco não.
- **Fotos no Render/Docker**: o disco do contêiner é apagado a cada deploy. Para produção, use um serviço de arquivos.
- **Compilação**: este código foi escrito sem poder compilar no ambiente de origem. Rode `./mvnw compile` e corrija qualquer detalhe.
- `HttpStatus.UNPROCESSABLE_ENTITY` e `PAYLOAD_TOO_LARGE` podem aparecer como "deprecated" nas versões mais novas do Spring. É só aviso do compilador.

Veja também `PROGRESSO.md`.
