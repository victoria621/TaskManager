FROM openjdk:21-jdk-slim

WORKDIR /app

COPY target/task-manager-1.0.0.jar app.jar

RUN addgroup --system --gid 1000 appgroup && \
    adduser --system --uid 1000 --gid 1000 appuser

USER appuser

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]