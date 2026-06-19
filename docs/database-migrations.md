# Migrations de banco de dados

Este projeto usa Flyway integrado ao Spring Boot para aplicar scripts SQL versionados durante a inicialização da aplicação.

## Como o Flyway funciona no projeto

- As migrations ficam em `src/main/resources/db/migration/`.
- O Spring Boot carrega esse diretório via `spring.flyway.locations=classpath:db/migration`.
- Cada arquivo deve seguir o padrão do Flyway: `V<versao>__<descricao>.sql`.
- O Flyway registra as migrations aplicadas na tabela `flyway_schema_history` do banco.
- Em produção, o Hibernate permanece com `spring.jpa.hibernate.ddl-auto=none`; alterações estruturais devem ser feitas por migrations, não por atualização automática do JPA.

## Como criar novas migrations

1. Nunca edite, renomeie ou remova migrations já aplicadas em qualquer ambiente compartilhado.
2. Crie um novo arquivo em `src/main/resources/db/migration/` com uma versão ainda não usada.
3. Use nomes descritivos, por exemplo:

   ```text
   V20260620_01__add_new_column_to_recommendations.sql
   ```

4. Para bancos PostgreSQL legados, prefira scripts idempotentes quando possível:

   ```sql
   ALTER TABLE MINHA_TABELA
       ADD COLUMN IF NOT EXISTS NOVA_COLUNA VARCHAR(255);

   CREATE INDEX IF NOT EXISTS IDX_MINHA_TABELA_NOVA_COLUNA
       ON MINHA_TABELA (NOVA_COLUNA);
   ```

5. Para constraints e FKs, use bloco `DO $$ ... END $$;` consultando `pg_constraint`, porque PostgreSQL não possui `ADD CONSTRAINT IF NOT EXISTS`.

## Como aplicar migrations localmente

Ao iniciar a aplicação com Flyway habilitado, o Spring Boot executa automaticamente as migrations pendentes:

```bash
mvn spring-boot:run
```

Também é recomendado validar build e testes antes de subir alterações:

```bash
mvn clean package -DskipTests
mvn test
```

Em um banco local novo, mantenha `SPRING_FLYWAY_BASELINE_ON_MIGRATE=false` para que o histórico completo seja executado desde a primeira migration.

## Baseline para bancos já existentes

O banco de produção no Render já possui schema criado antes da adoção do Flyway. Por isso, o profile `prod` usa:

```properties
spring.flyway.baseline-on-migrate=${SPRING_FLYWAY_BASELINE_ON_MIGRATE:true}
```

Na primeira execução contra um schema não vazio sem `flyway_schema_history`, o Flyway cria a tabela de histórico e registra um baseline. Isso evita que migrations antigas tentem recriar estruturas que já existem no banco legado. Depois do baseline, migrations com versões superiores ao baseline ficam disponíveis para execução normal.

Para bancos novos ou bancos de desenvolvimento que precisam executar todo o histórico, deixe `SPRING_FLYWAY_BASELINE_ON_MIGRATE=false`.

## Deploy no Render

No Render, a aplicação inicia com o profile de produção. Como Flyway está habilitado, o startup passa a:

1. Conectar no PostgreSQL configurado por `SPRING_DATASOURCE_URL`, `SPRING_DATASOURCE_USERNAME` e `SPRING_DATASOURCE_PASSWORD`.
2. Verificar ou criar `flyway_schema_history`.
3. Registrar baseline no primeiro deploy contra o banco legado, se necessário.
4. Aplicar migrations pendentes posteriores ao baseline.
5. Iniciar a aplicação com Hibernate em `ddl-auto=none`.

Se for necessário desabilitar temporariamente as migrations em uma emergência operacional, defina `SPRING_FLYWAY_ENABLED=false`. Essa opção deve ser temporária, porque novas alterações de schema dependem do Flyway para serem aplicadas de forma rastreável.
