# 1. Base leve com Java 21
FROM eclipse-temurin:21-jdk-alpine

# 2. Metadados
LABEL maintainer="lucassaragao.dev@gmail.com"

# 3. Diretório de trabalho
WORKDIR /app

# 4. Copia o JAR gerado pelo Maven para dentro do container
# O nome do jar pode variar, o * garante que pegue qualquer um
COPY target/*.jar app.jar

# 5. Comando de inicialização
ENTRYPOINT ["java", "-jar", "app.jar"]