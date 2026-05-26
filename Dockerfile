# Estágio 1: Build da aplicação
FROM eclipse-temurin:17-jdk-alpine AS build
WORKDIR /app

# Copia os arquivos essenciais do Maven
COPY mvnw .
COPY .mvn .mvn
COPY pom.xml .
COPY src src

# Dá permissão de execução ao wrapper e compila o projeto ignorando os testes
RUN chmod +x ./mvnw
RUN ./mvnw clean package -DskipTests

# Estágio 2: Execução da aplicação
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

# Copia apenas o .jar gerado no estágio anterior (imagem mais leve)
COPY --from=build /app/target/*.jar app.jar

# Expõe a porta padrão do Spring
EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]