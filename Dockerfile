# 第一階段：建置 Spring Boot 應用程式
FROM maven:3.9-eclipse-temurin-17 AS build

# 設定工作目錄
WORKDIR /app

# 複製 pom.xml 和 src 目錄到容器內
COPY pom.xml ./
COPY src ./src

# 使用 Maven 編譯並打包成 JAR 檔案
RUN mvn clean package -DskipTests

# 第二階段：運行 Spring Boot 應用程式
FROM eclipse-temurin:17-jdk

# 設定工作目錄
WORKDIR /app

# 定義運行時端口
ARG PORT=8080
ENV PORT=${PORT}

# 複製 build 階段的 JAR 檔案到當前工作目錄
COPY --from=build /app/target/*.jar /app/app.jar

# 開放應用程式所需的端口
EXPOSE 8080

# 預設執行命令
CMD ["sh", "-c", "java -jar /app/app.jar --server.port=${PORT}"]