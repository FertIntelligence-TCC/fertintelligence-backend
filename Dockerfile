# Estágio 1: Build da aplicação usando o Maven nativo
FROM maven:3.9-eclipse-temurin-17-alpine AS build
WORKDIR /app

# Copia apenas o pom.xml e o código-fonte (ignoramos o mvnw e a pasta oculta)
COPY pom.xml .
COPY src src

# Compila o projeto ignorando os testes
RUN mvn clean package -DskipTests

# Estágio 2: Execução da aplicação
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

# Copia apenas o .jar gerado no estágio anterior
COPY --from=build /app/target/*.jar app.jar

# Expõe a porta padrão do Spring
EXPOSE 8080

# ENTRYPOINT ["sh", "-c", "java -Dspring.datasource.url=${SPRING_DATASOURCE_URL} -Dspring.datasource.username=${SPRING_DATASOURCE_USERNAME} -Dspring.datasource.password=${SPRING_DATASOURCE_PASSWORD} -jar app.jar"]
# ENTRYPOINT ["java", "-jar", "app.jar", "--spring.datasource.url=${SPRING_DATASOURCE_URL}", "--spring.datasource.username=${SPRING_DATASOURCE_USERNAME}", "--spring.datasource.password=${SPRING_DATASOURCE_PASSWORD}"]
ENTRYPOINT ["java", "-Dspring.datasource.url=${SPRING_DATASOURCE_URL}", "-Dspring.datasource.username=${SPRING_DATASOURCE_USERNAME}", "-Dspring.datasource.password=${SPRING_DATASOURCE_PASSWORD}", "-jar", "app.jar"]