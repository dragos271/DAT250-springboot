# file: `Dockerfile`
# Builder: build jar with Gradle (Java 21)
FROM gradle:8.6-jdk21 AS builder
WORKDIR /home/gradle/project

# copy minimal files to leverage cache
COPY gradlew .
COPY gradle gradle
COPY build.gradle* settings.gradle* ./
RUN if [ -f ./gradlew ]; then chmod +x ./gradlew; fi

# fetch dependencies (no sources yet)
RUN if [ -x "./gradlew" ]; then ./gradlew assemble -x test --no-daemon; else gradle assemble -x test --no-daemon; fi || true

# copy sources and build the bootJar
COPY . .
RUN if [ -x "./gradlew" ]; then ./gradlew bootJar -x test --no-daemon; else gradle bootJar -x test --no-daemon; fi

# Runtime: slim JRE, non-root user
FROM eclipse-temurin:21-jre
WORKDIR /app

# create non-root user and group
RUN groupadd --system appgroup && useradd --system --gid appgroup --home /app --shell /sbin/nologin appuser \
 && mkdir -p /app

# copy artifact and set ownership
COPY --from=builder /home/gradle/project/build/libs/*.jar /app/app.jar
RUN chown -R appuser:appgroup /app && chmod 755 /app/app.jar

USER appuser
EXPOSE 8080
ENTRYPOINT ["java","-jar","/app/app.jar"]


