# 多阶段构建：第一阶段编译
FROM eclipse-temurin:25-jdk-alpine AS builder

WORKDIR /app
COPY pom.xml .
COPY src ./src
COPY mvnw .
COPY .mvn ./.mvn

RUN chmod +x mvnw && ./mvnw clean package -DskipTests

# 第二阶段：最小 JRE 运行环境
FROM eclipse-temurin:25-jre-alpine

WORKDIR /app

# 从构建阶段复制 jar
COPY --from=builder /app/target/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
