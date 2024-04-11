-- TABLE
create table wiwa_order
(
    id          bigserial primary key,
    creator     varchar(255)   not null,
    created     timestamp      not null,
    modifier    varchar(255),
    modified    timestamp,
    name        varchar(255),
    status      varchar(255)   not null,
    total_value numeric(19, 3) not null,
    total_unit  varchar(255)   not null
);

create table wiwa_order_data
(
    id       bigserial primary key,
    order_id bigint       not null references wiwa_order (id) on delete cascade,
    key      varchar(255) not null,
    data     text,
    unique (order_id, key)
);

create table wiwa_order_item
(
    id           bigserial primary key,
    order_id     bigint                             not null references wiwa_order (id) on delete cascade,
    parent_id    bigint,
    creator      varchar(255)                       not null,
    created      timestamp                          not null,
    modifier     varchar(255),
    modified     timestamp,
    type         varchar(255)                       not null,
    code         varchar(255)                       not null,
    name         varchar(255) collate "sk-SK-x-icu" not null,
    price_value  numeric(19, 3)                     not null,
    price_unit   varchar(255)                       not null,
    amount_value numeric(19, 3)                     not null,
    amount_unit  varchar(255)                       not null,
    total_value  numeric(19, 3)                     not null,
    total_unit   varchar(255)                       not null,
    data         text
);

create table wiwa_order_item_data
(
    id            bigserial primary key,
    order_item_id bigint       not null references wiwa_order_item (id) on delete cascade,
    key           varchar(255) not null,
    data          text,
    unique (order_item_id, key)
);


create table wiwa_task
(
    id      bigserial primary key,
    creator varchar(255) not null,
    created timestamp    not null,
    type    varchar(255) not null,
    status  varchar(255) not null,
    data    text,
    log     text
);

-- INDEX
create index idx_wiwa_order_data on wiwa_order_data (order_id);

create index idx_wiwa_order_item on wiwa_order_item (order_id);

create index idx_wiwa_order_item_data on wiwa_order_item_data (order_item_id);
