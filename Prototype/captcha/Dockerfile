FROM eclipse-temurin:21-jdk-alpine as builder
WORKDIR /opt/app

COPY .mvn/ .mvn
COPY mvnw pom.xml ./
RUN ./mvnw dependency:go-offline

COPY ./src ./src
RUN ./mvnw clean install -DskipTests

FROM eclipse-temurin:21-jdk-alpine
WORKDIR /opt/app
EXPOSE 8081
COPY --from=builder /opt/app/target/*.jar /opt/app/*.jar
ENTRYPOINT ["java", "-Xdebug", "-Xrunjdwp:server=y,transport=dt_socket,address=*:5005,suspend=n", "-jar","/opt/app/*.jar"]