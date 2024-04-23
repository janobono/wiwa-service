FROM public.ecr.aws/docker/library/maven:3-eclipse-temurin-21-alpine as builder

RUN apk add git

WORKDIR /r3n

RUN git clone -b 6.1.2 https://github.com/janobono/r3n-api.git .

RUN mvn clean install -DskipTests

WORKDIR /app

COPY . .

RUN mvn clean install -DskipTests

FROM public.ecr.aws/amazoncorretto/amazoncorretto:21-al2023-headless as production

WORKDIR /app

COPY --from=builder app/wiwa-service-app/target/wiwa-service-app-*.jar ./wiwa-service-app.jar

EXPOSE 8080

CMD java -jar wiwa-service-app.jar
