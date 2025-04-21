# �Ĥ@���q�G�ظm Spring Boot ���ε{��
FROM maven:3.9-eclipse-temurin-17 AS build

# �]�w�u�@�ؿ�
WORKDIR /app

# �ƻs pom.xml �M src �ؿ���e����
COPY pom.xml ./
COPY src ./src

# �ϥ� Maven �sĶ�å��]�� JAR �ɮ�
RUN mvn clean package -DskipTests

# �ĤG���q�G�B�� Spring Boot ���ε{��
FROM eclipse-temurin:17-jdk

# �]�w�u�@�ؿ�
WORKDIR /app

# �w�q�B��ɺݤf
ARG PORT=8080
ENV PORT=${PORT}

# �ƻs build ���q�� JAR �ɮר��e�u�@�ؿ�
COPY --from=build /app/target/*.jar /app/app.jar

# �}�����ε{���һݪ��ݤf
EXPOSE 8080

# �w�]����R�O
CMD ["sh", "-c", "java -jar /app/app.jar --server.port=${PORT}"]