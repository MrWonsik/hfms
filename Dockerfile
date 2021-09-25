# syntax=docker/dockerfile:1

FROM openjdk:15-jdk-alpine as base

WORKDIR /hfms

COPY .mvn/ .mvn
COPY mvnw pom.xml ./
COPY src ./src
RUN mkdir -p /src/test/resources/savedReceipt
RUN ./mvnw dependency:go-offline

FROM base as test
CMD ["./mvnw", "test"]

FROM base as development
CMD ["./mvnw", "spring-boot:run", "-Dspring-boot.run.jvmArguments='-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:8000'"]

FROM base as build
RUN ./mvnw package

FROM openjdk:15-jdk-alpine as production
EXPOSE 8081

COPY --from=build /hfms/target/*.jar /hfms.jar

CMD ["java", "-Djava.security.egd=file:/dev/./urandom", "-jar", "/hfms.jar"]