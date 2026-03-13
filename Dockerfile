# Usa a imagem do Java 21
FROM eclipse-temurin:21-jdk-alpine

# Define a pasta de trabalho
WORKDIR /app

# Copia todo o código
COPY . .

# Instala o Maven nativamente no servidor (isso resolve o erro da pasta .mvn)
RUN apk add --no-cache maven

# Compila o projeto usando o Maven nativo (mvn) em vez do script (./mvnw)
RUN mvn clean package -DskipTests

# Expõe a porta
EXPOSE 8080

# Liga o servidor
CMD ["java", "-jar", "target/fertintelligence-0.0.1-SNAPSHOT.jar"]