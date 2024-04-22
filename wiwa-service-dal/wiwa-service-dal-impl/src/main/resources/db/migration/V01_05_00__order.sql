-- TABLE
create table wiwa_order_number
(
    user_id      bigint primary key references wiwa_user (id) on delete cascade,
    order_number bigserial not null
);

create table wiwa_order
(
    id               bigserial primary key,
    user_id          bigint         not null references wiwa_user (id) on delete cascade,
    created          timestamp      not null,
    status           varchar(255)   not null,
    order_number     bigserial      not null,
    net_weight_value numeric(19, 3) not null,
    net_weight_unit  varchar(255)   not null,
    total_value      numeric(19, 3) not null,
    total_unit       varchar(255)   not null,
    delivery         date,
    ready            timestamp,
    finished         timestamp,
    unique (user_id, order_number)
);

create table wiwa_order_attribute
(
    order_id        bigint       not null references wiwa_order (id) on delete cascade,
    attribute_key   varchar(255) not null,
    attribute_value text         not null,
    primary key (order_id, attribute_key)
);

create table wiwa_order_contact
(
    order_id    bigint primary key references wiwa_order (id) on delete cascade,
    name        varchar(255) not null,
    street      varchar(255) not null,
    zip_code    varchar(255) not null,
    city        varchar(255) not null,
    state       varchar(255) not null,
    phone       varchar(255) not null,
    email       varchar(255) not null,
    business_id varchar(255),
    tax_id      varchar(255)
);

-- INDEX
create index idx_wiwa_order_number on wiwa_order_number (user_id);

create index idx_wiwa_order on wiwa_order (user_id);

create index idx_wiwa_order_attribute on wiwa_order_attribute (order_id);
