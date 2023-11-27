FROM public.ecr.aws/docker/library/maven:3-eclipse-temurin-17-alpine as builder

WORKDIR /app

COPY . .

RUN mvn clean install -DskipTests

FROM public.ecr.aws/amazoncorretto/amazoncorretto:17-al2023-headless as extractor
WORKDIR app
COPY --from=builder /app/target/*.jar app.jar
RUN java -Djarmode=layertools -jar app.jar extract

FROM public.ecr.aws/amazoncorretto/amazoncorretto:17-al2023-headless as production

WORKDIR /app

COPY data ./data

COPY --from=extractor app/dependencies/ ./
COPY --from=extractor app/spring-boot-loader/ ./
COPY --from=extractor app/snapshot-dependencies/ ./
COPY --from=extractor app/application/ ./

EXPOSE 8080

ENTRYPOINT ["java","org.springframework.boot.loader.JarLauncher"]
