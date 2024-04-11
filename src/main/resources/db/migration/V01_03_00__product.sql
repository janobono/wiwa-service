-- TABLE
create table wiwa_product
(
    id           bigserial primary key,
    code         varchar(255)                       not null unique,
    name         varchar(255) collate "sk-SK-x-icu" not null,
    description  text,
    stock_status varchar(255)                       not null
);

create table wiwa_product_attribute
(
    id         bigserial primary key,
    product_id bigint       not null references wiwa_product (id) on delete cascade,
    key        varchar(255) not null,
    value      varchar(255) not null,
    unique (product_id, key)
);

create table wiwa_product_image
(
    id         bigserial primary key,
    product_id bigint       not null references wiwa_product (id) on delete cascade,
    file_name  varchar(255) not null,
    file_type  varchar(255) not null,
    thumbnail  bytea        not null,
    data       bytea        not null,
    unique (product_id, file_name)
);

create table wiwa_product_quantity
(
    id         bigserial primary key,
    product_id bigint         not null references wiwa_product (id) on delete cascade,
    key        varchar(255)   not null,
    value      numeric(19, 3) not null,
    unit       varchar(255)   not null,
    unique (product_id, key)
);

create table wiwa_product_unit_price
(
    id         bigserial primary key,
    product_id bigint         not null references wiwa_product (id) on delete cascade,
    valid_from date           not null,
    valid_to   date,
    value      numeric(19, 3) not null,
    unit       varchar(255)   not null,
    unique (product_id, valid_from)
);

create table wiwa_product_code_list_item
(
    product_id        bigint not null references wiwa_product (id) on delete cascade,
    code_list_item_id bigint not null references wiwa_code_list_item (id) on delete cascade,
    unique (product_id, code_list_item_id)
);

-- INDEX
create index idx_wiwa_product_attribute on wiwa_product_attribute (product_id);

create index idx_wiwa_product_image on wiwa_product_image (product_id);

create index idx_wiwa_product_quantity on wiwa_product_quantity (product_id);

create index idx_wiwa_product_unit_price on wiwa_product_unit_price (product_id);

create index idx_wiwa_product_code_list_item1 on wiwa_product_code_list_item (product_id);
create index idx_wiwa_product_code_list_item2 on wiwa_product_code_list_item (code_list_item_id);
