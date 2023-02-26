# wiwa-service

Woodworking Industry Web Application Service.

- [Apache License 2.0](./LICENSE)

## build

- [Docker](https://docs.docker.com/get-docker/)

```shell
./build.sh
```

or

```shell
docker build -t sk.janobono.wiwa/wiwa-service-app:latest .
```

## run application

### start

```shell
docker compose up
```

### stop

```shell
docker compose down
```

### endpoints

- [/actuator/health](http://localhost:8080/api/actuator/health)
- [/actuator/info](http://localhost:8080/api/actuator/info)
- [/actuator/metrics](http://localhost:8080/api/actuator/metrics)
- [/api-docs.yaml](http://localhost:8080/api/api-docs.yaml)
- [/swagger-ui.html](http://localhost:8080/api/swagger-ui.html)
- [smtp mail dev](http://localhost:8081)

## run just local services

### start

```shell
docker compose -f docker-compose-local-dev.yaml up
```

### stop

```shell
docker compose -f docker-compose-local-dev.yaml down
```

## docker commands

### kill all containers

```
docker kill $(docker ps -q)
```

### remove all containers

```
docker rm $(docker ps -a -q)
```

### remove all unused volumes

```
docker volume rm $(docker volume ls -q --filter dangling=true)
```

## environment variables

| Name                            | Default                              |
|---------------------------------|--------------------------------------|
| PORT                            | 8080                                 |
| CONTEXT_PATH                    | /api                                 |
| LOG_LEVEL                       | debug                                |
| DB_URL                          | jdbc:postgresql://localhost:5432/app |
| DB_USER                         | app                                  |
| DB_PASSWORD                     | app                                  |
| MAIL_HOST                       | localhost                            |
| MAIL_PORT                       | 1025                                 |
| MAIL_USER_NAME                  |                                      |
| MAIL_USER_PASSWORD              |                                      |
| MAIL_TLS_ENABLE                 | false                                |
| MAIL_TLS_REQUIRED               | false                                |
| MAIL_AUTH                       | false                                |
| WEB_URL                         | http://localhost:8080                |
| MAIL                            | mail@wiwa.sk                         |
| INIT_DATA_PATH                  | ./wiwa/data                          |
| MAX_IMAGE_RESOLUTION            | 1000                                 |
| MAX_THUMBNAIL_RESOLUTION        | 130                                  |
| CAPTCHA_LENGTH                  | 4                                    |
| SIGN_UP_TOKEN_EXPIRES_IN        | 120                                  |
| RESET_PASSWORD_TOKEN_EXPIRES_IN | 120                                  |
| REFRESH_TOKEN_EXPIRES_IN        | 36                                   |
| TOKEN_ISSUER                    | wiwa                                 |
| TOKEN_EXPIRES_IN                | 1                                    |
