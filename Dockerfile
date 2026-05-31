FROM gradle:8.13-jdk21 AS builder

WORKDIR /app
COPY . .

RUN gradle :kiteapi:build --no-daemon -x test

FROM eclipse-temurin:21-jre-slim

WORKDIR /app

RUN groupadd -r trading && useradd -r -g trading trading

COPY --from=builder /app/kiteapi/build/libs/*.jar app.jar

RUN mkdir -p /app/logs && chown -R trading:trading /app

USER trading

EXPOSE 8080

ENV JAVA_OPTS="-XX:+UseZGC -XX:MaxRAMPercentage=75.0 -Djava.security.egd=file:/dev/./urandom" \
    SPRING_PROFILES_ACTIVE=prod

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
