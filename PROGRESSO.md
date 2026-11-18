# Progresso da API Acesso Já

## Feito

- [x] Entidades (8 tabelas), ids `int`, Lombok `@Getter` e `@Setter`
- [x] DAOs simples com `ConnectionFactory.obterConexao()` (padrão Soulpass)
- [x] DTOs de requisição e resposta (um por endpoint da planilha, sem paginação)
- [x] Mappers estáticos
- [x] Services com `ResponseStatusException` e regras como constantes (RN01 a RN12, RF18, RF19)
- [x] Controllers (28 endpoints)
- [x] Sem autenticação: usuário identificado pelo cabeçalho `X-Usuario-Id` (`contexto/ContextoUsuario`), senha em texto simples, CORS (`config/ConfiguracaoWeb`)
- [x] Classes de teste manual (`tests/TestConnection`, `tests/TestCategoriaDAO`)
- [x] Removidas as classes que dependiam da pasta `config` (ConexaoOracle, RegrasNegocio, Paginacao, WebConfig)

## Próximos passos

- [ ] Rodar `./mvnw compile` e corrigir o que o compilador apontar.
- [ ] Rodar os dois scripts SQL e testar: cadastro, login, registrar aviso, confirmar, mudar status.
- [ ] Mais testes manuais de DAO na pasta `tests` (como no Soulpass).
- [ ] Decidir as pendências do README (mapa, foto com placa borrada, várias fotos, título do aviso).
- [ ] Tirar usuário e senha do código da `ConnectionFactory` antes de publicar o repositório.
