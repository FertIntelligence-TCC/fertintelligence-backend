# Usa uma imagem oficial, leve e super estável do Java 21
FROM eclipse-temurin:21-jdk-alpine

# Define a pasta de trabalho dentro do servidor
WORKDIR /app

# Copia todo o código do seu repositório para o servidor
COPY . .

# Dá permissão ao Maven e compila o projeto (pulando os testes)
RUN chmod +x ./mvnw
RUN ./mvnw clean package -DskipTests

# Expõe a porta padrão do Spring Boot
EXPOSE 8080

# Comando que o Render vai rodar para ligar a sua API
CMD ["java", "-jar", "target/fertintelligence-0.0.1-SNAPSHOT.jar"]