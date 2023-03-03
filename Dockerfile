FROM maven:3-eclipse-temurin-17-focal as builder

RUN apt-get -y update
RUN apt-get -y install git

WORKDIR /r3n

RUN git clone -b 6.0.2 https://github.com/janobono/r3n-api.git .

RUN mvn clean install -DskipTests

WORKDIR /app

COPY . .

RUN mvn clean install -DskipTests

FROM eclipse-temurin:17-jre-alpine as extractor
WORKDIR app
COPY --from=builder /app/wiwa-service-app/target/*.jar app.jar
RUN java -Djarmode=layertools -jar app.jar extract

FROM eclipse-temurin:17-jre-alpine as production

WORKDIR /app

RUN addgroup --gid 1000 app; \
    adduser --disabled-password --gecos "" --home "$(pwd)" --ingroup app --no-create-home --uid 1000 app

RUN chown -R app:app /app

USER app

COPY data ./data

COPY --from=extractor app/dependencies/ ./
COPY --from=extractor app/spring-boot-loader/ ./
COPY --from=extractor app/snapshot-dependencies/ ./
COPY --from=extractor app/application/ ./

EXPOSE 8080

ENTRYPOINT ["java","org.springframework.boot.loader.JarLauncher"]
