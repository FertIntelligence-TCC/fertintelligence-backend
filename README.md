# FertIntelligence Backend

API principal em Java 17, Spring Boot 3.5 e Maven, empacotada em Docker para o
Render.

## Execução local

Configure um PostgreSQL e as variáveis:

- `SPRING_DATASOURCE_URL`, `SPRING_DATASOURCE_USERNAME` e
  `SPRING_DATASOURCE_PASSWORD`;
- `JWT_PUBLIC_KEY` e `JWT_PRIVATE_KEY` (PEM RSA; `\n` literal é aceito);
- `APP_FRONTEND_URL` (padrão `http://localhost:5173`);
- `FERT_AI_BASE_URL` (padrão `http://localhost:8001`);
- `SPRING_PROFILES_ACTIVE` (`dev` local, `prod` no Render).

```bash
mvn spring-boot:run
mvn test
mvn clean package
curl http://localhost:8080/health
```

O servidor lê `PORT`, com padrão local `8080`. O CORS aceita a origem definida
por `APP_FRONTEND_URL`.

O Maven Wrapper presente no repositório está incompleto (a pasta
`.mvn/wrapper` não está versionada); por isso os comandos documentados usam o
Maven instalado e o deploy usa o Dockerfile.

## JWT compartilhado

O backend assina tokens com RS256. O Fert-IA deve receber em
`SPRING_JWT_PUBLIC_KEY` exatamente a chave pública correspondente a
`JWT_PRIVATE_KEY`, além do mesmo issuer (`spring-security-jwt`). Chaves
versionadas anteriormente devem ser consideradas comprometidas e rotacionadas.

## Render Blueprint

Em **New + > Blueprint**, conecte este repositório na branch
`m-fertilization`. O `render.yaml` não cria outro PostgreSQL. Preencha
manualmente todas as variáveis `sync: false` usando as credenciais do banco
`fertintelligence-bd` existente e um novo par RSA. Valores PEM podem ser
colados com quebras reais ou `\n`.
