FROM docker.artifactory.tapsi.tech/gradle:8.5.0-jdk21-jammy as builder

WORKDIR /app

COPY build.gradle settings.gradle ./
COPY src ./src

ENV TZ=Asia/Tehran
RUN ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone

RUN gradle build -x test --console plain --no-daemon --no-watch-fs

RUN ls -l /app/build/libs

FROM docker.artifactory.tapsi.tech/eclipse-temurin:21.0.1_12-jre-jammy

WORKDIR /app

COPY --from=builder /app/build/libs/sso-java-client.jar sso-java-client.jar

RUN ls -l /app

ENV TZ=Asia/Tehran
RUN ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone

ENV PROFILE default

ENTRYPOINT ["java", "-Dspring.profiles.active=${PROFILE}", "-jar", "sso-java-client.jar"]