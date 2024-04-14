-- TABLE
create table wiwa_order_number
(
    creator      varchar(255) primary key,
    order_number bigserial not null
);

create table wiwa_order
(
    id               bigserial primary key,
    creator          varchar(255)   not null,
    created          timestamp      not null,
    status           varchar(255)   not null,
    order_number     bigserial      not null,
    description      text,
    weight_value     numeric(19, 3) not null,
    weight_unit      varchar(255)   not null,
    net_weight_value numeric(19, 3) not null,
    net_weight_unit  varchar(255)   not null,
    total_value      numeric(19, 3) not null,
    total_unit       varchar(255)   not null,
    unique (creator, order_number)
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
    id          bigserial primary key,
    order_id    bigint       not null references wiwa_order (id) on delete cascade,
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

create table wiwa_order_item
(
    id               bigserial primary key,
    order_id         bigint         not null references wiwa_order (id) on delete cascade,
    name             varchar(255)   not null,
    sort_num         integer        not null,
    description      text,
    weight_value     numeric(19, 3) not null,
    weight_unit      varchar(255)   not null,
    net_weight_value numeric(19, 3) not null,
    net_weight_unit  varchar(255)   not null,
    price_value      numeric(19, 3) not null,
    price_unit       varchar(255)   not null,
    amount_value     numeric(19, 3) not null,
    amount_unit      varchar(255)   not null,
    total_value      numeric(19, 3) not null,
    total_unit       varchar(255)   not null
);

create table wiwa_order_item_attribute
(
    order_item_id   bigint       not null references wiwa_order (id) on delete cascade,
    attribute_key   varchar(255) not null,
    attribute_value text         not null,
    primary key (order_item_id, attribute_key)
);

-- INDEX
create index idx_wiwa_order_attribute on wiwa_order_attribute (order_id);

create index idx_wiwa_order_contact on wiwa_order_contact (order_id);

create index idx_wiwa_order_item on wiwa_order_item (order_id);

create index idx_wiwa_order_item_attribute on wiwa_order_item_attribute (order_item_id);
