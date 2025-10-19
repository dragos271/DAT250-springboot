
FROM gradle:8.6-jdk21 AS builder
WORKDIR /home/gradle/project


COPY gradlew .
COPY gradle gradle
COPY build.gradle* settings.gradle* ./
RUN if [ -f ./gradlew ]; then chmod +x ./gradlew; fi


RUN if [ -x "./gradlew" ]; then ./gradlew assemble -x test --no-daemon; else gradle assemble -x test --no-daemon; fi || true


COPY . .
RUN if [ -x "./gradlew" ]; then ./gradlew bootJar -x test --no-daemon; else gradle bootJar -x test --no-daemon; fi


FROM eclipse-temurin:21-jre
WORKDIR /app

RUN groupadd --system appgroup && useradd --system --gid appgroup --home /app --shell /sbin/nologin appuser \
 && mkdir -p /app

COPY --from=builder /home/gradle/project/build/libs/*.jar /app/app.jar
RUN chown -R appuser:appgroup /app && chmod 755 /app/app.jar

USER appuser
EXPOSE 8080
ENTRYPOINT ["java","-jar","/app/app.jar"]


