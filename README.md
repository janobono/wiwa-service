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
docker build -t sk.janobono.wiwa/wiwa-service:latest .
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

- [/livez](http://localhost:8080/api/livez)
- [/readyz](http://localhost:8080/api/readyz)
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

| Name                               | Default                              |
|------------------------------------|--------------------------------------|
| PORT                               | 8080                                 |
| CONTEXT_PATH                       | /api                                 |
| LOG_LEVEL                          | debug                                |
| DB_URL                             | jdbc:postgresql://localhost:5432/app |
| DB_USER                            | app                                  |
| DB_PASS                            | app                                  |
| DB_POOL_SIZE                       | 5                                    |
| DB_POOL_IDLE                       | 2                                    |
| MAIL_HOST                          | localhost                            |
| MAIL_PORT                          | 1025                                 |
| MAIL_USER                          |                                      |
| MAIL_PASS                          |                                      |
| MAIL_AUTH                          | false                                |
| MAIL_TLS_ENABLE                    | false                                |
| DEFAULT_LOCALE                     | en_US                                |
| APP_TITLE                          | Wiwa                                 |
| APP_DESCRIPTION                    | Woodworking Industry Web Application |
| WEB_URL                            | http://localhost:8080                |
| CONFIRM_PATH                       | /ui/confirm/                         |
| ORDERS_PATH                        | /fixme/                              |
| MAIL                               | mail@wiwa.sk                         |
| ORDERS_MAIL                        | orders@wiwa.sk                       |
| MAX_IMAGE_RESOLUTION               | 1000                                 |
| MAX_THUMBNAIL_RESOLUTION           | 130                                  |
| CAPTCHA_LENGTH                     | 4                                    |
| CURRENCY_CODE                      | EUR                                  |
| CURRENCY_SYMBOL                    | €                                    |
| SIGN_UP_TOKEN_EXPIRES_IN           | 12 (hour)                            |
| RESET_PASSWORD_TOKEN_EXPIRES_IN    | 12 (hour)                            |
| REFRESH_TOKEN_EXPIRES_IN           | 5 (min)                              |
| TOKEN_ISSUER                       | wiwa                                 |
| TOKEN_EXPIRES_IN                   | 1 (min)                              |
| SECURITY_PUBLIC_PATH_PATTERN_REGEX |                                      |
| VERIFICATION_TOKEN_ISSUER          | verification                         |
| CORS_ALLOWED_ORIGINS               | http://localhost:5173                |
| CORS_ALLOWED_METHODS               | GET,POST,PUT,OPTIONS,PATCH,DELETE    |
| CORS_ALLOWED_HEADERS               | Authorization,Content-Type           |
| CORS_ALLOW_CREDENTIALS             | true                                 |
