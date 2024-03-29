FROM maven:3-amazoncorretto-17 AS build

ENV HOME=/usr/app

RUN mkdir -p $HOME

WORKDIR $HOME

ADD . $HOME

RUN --mount=type=cache,target=/root/.m2 mvn -Dmaven.test.skip clean package

#
# Package stage
#
FROM maven:3-amazoncorretto-17

ARG JAR_FILE=/usr/app/target/*.jar

COPY --from=build $JAR_FILE /app/runner.jar

EXPOSE 8080

ENTRYPOINT ["java","-jar", "/app/runner.jar"]