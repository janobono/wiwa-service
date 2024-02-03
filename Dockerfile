FROM public.ecr.aws/docker/library/maven:3-eclipse-temurin-21-alpine as builder

RUN apk add git

WORKDIR /r3n

RUN git clone -b 6.0.4 https://github.com/janobono/r3n-api.git .

RUN mvn clean install -DskipTests

WORKDIR /app

COPY . .

RUN mvn clean install -DskipTests

FROM public.ecr.aws/amazoncorretto/amazoncorretto:21-al2023-headless as production

WORKDIR /app

COPY data ./data

COPY --from=builder app/target/wiwa-service-*.jar ./wiwa-service.jar

EXPOSE 8080

CMD java -jar wiwa-service.jar
