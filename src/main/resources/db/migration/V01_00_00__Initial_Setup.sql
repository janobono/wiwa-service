-- EXTENSION
create
    extension if not exists unaccent;

-- TABLE
create table wiwa_application_image
(
    file_name varchar(255) primary key,
    file_type varchar(255) not null,
    thumbnail bytea        not null,
    data      bytea        not null
);

create table wiwa_application_property
(
    property_group varchar(255) not null,
    property_key   varchar(255) not null,
    property_value text         not null,
    primary key (property_group, property_key)
);

create table wiwa_authority
(
    id        bigserial primary key,
    authority varchar(255) not null unique
);

create table wiwa_code_list
(
    id   bigserial primary key,
    code varchar(255)                       not null unique,
    name varchar(255) collate "sk-SK-x-icu" not null
);

create table wiwa_code_list_item
(
    id           bigserial primary key,
    code_list_id bigint                     not null references wiwa_code_list (id) on delete cascade,
    parent_id    bigint,
    tree_code    text                       not null unique,
    code         varchar(255)               not null unique,
    value        text collate "sk-SK-x-icu" not null,
    sort_num     integer                    not null
);

create table wiwa_order
(
    id          bigserial primary key,
    creator     varchar(255)   not null,
    created     timestamp      not null,
    modifier    varchar(255),
    modified    timestamp,
    status      varchar(255)   not null,
    total       numeric(19, 3) not null,
    name        varchar(255),
    description text
);

create table wiwa_order_item
(
    id          bigserial primary key,
    order_id    bigint                             not null references wiwa_order (id) on delete cascade,
    creator     varchar(255)                       not null,
    created     timestamp                          not null,
    modifier    varchar(255),
    modified    timestamp,
    type        varchar(255)                       not null,
    code        varchar(255)                       not null,
    name        varchar(255) collate "sk-SK-x-icu" not null,
    price_value numeric(19, 3)                     not null,
    price_unit  varchar(255)                       not null,
    amount      numeric(19, 3)                     not null,
    total       numeric(19, 3)                     not null
);

create table wiwa_order_item_attribute
(
    id            bigserial primary key,
    order_item_id bigint       not null references wiwa_order_item (id) on delete cascade,
    key           varchar(255) not null,
    value         text         not null,
    unique (order_item_id, key)
);

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

create table wiwa_user
(
    id           bigserial primary key,
    username     varchar(255)                       not null unique,
    password     varchar(255)                       not null,
    title_before varchar(255),
    first_name   varchar(255) collate "sk-SK-x-icu" not null,
    mid_name     varchar(255) collate "sk-SK-x-icu",
    last_name    varchar(255) collate "sk-SK-x-icu" not null,
    title_after  varchar(255),
    email        varchar(255)                       not null unique,
    gdpr         bool                               not null,
    confirmed    bool                               not null,
    enabled      bool                               not null
);

create table wiwa_user_authority
(
    user_id      bigint not null references wiwa_user (id) on delete cascade,
    authority_id bigint not null references wiwa_authority (id) on delete cascade,
    unique (user_id, authority_id)
);

create table wiwa_product_code_list_item
(
    product_id        bigint not null references wiwa_product (id) on delete cascade,
    code_list_item_id bigint not null references wiwa_code_list_item (id) on delete cascade,
    unique (product_id, code_list_item_id)
);

-- VIEW
create view wiwa_board_product_view
            (
             id, code, name, description, stock_status,
             board_code, structure_code, orientation,
             sale_value, sale_unit,
             weight_value, weight_unit,
             net_weight_value, net_weight_unit,
             length_value, length_unit,
             width_value, width_unit,
             thickness_value, thickness_unit,
             price_value, price_unit
                )
as
SELECT p.id,
       p.code,
       p.name,
       p.description,
       p.stock_status,
       pa1.value,
       pa2.value,
       pa3.value,
       pq1.value,
       pq1.unit,
       pq2.value,
       pq2.unit,
       pq3.value,
       pq3.unit,
       pq4.value,
       pq4.unit,
       pq5.value,
       pq5.unit,
       pq6.value,
       pq6.unit,
       pr.value,
       pr.unit
FROM wiwa_product p
         LEFT JOIN wiwa_product_attribute pa1 on p.id = pa1.product_id and pa1.key = 'BOARD_CODE'
         LEFT JOIN wiwa_product_attribute pa2 on p.id = pa2.product_id and pa2.key = 'STRUCTURE_CODE'
         LEFT JOIN wiwa_product_attribute pa3 on p.id = pa3.product_id and pa3.key = 'ORIENTATION'
         LEFT JOIN wiwa_product_quantity pq1 on p.id = pq1.product_id and pq1.key = 'SALE'
         LEFT JOIN wiwa_product_quantity pq2 on p.id = pq2.product_id and pq2.key = 'WEIGHT'
         LEFT JOIN wiwa_product_quantity pq3 on p.id = pq3.product_id and pq3.key = 'NET_WEIGHT'
         LEFT JOIN wiwa_product_quantity pq4 on p.id = pq4.product_id and pq4.key = 'LENGTH'
         LEFT JOIN wiwa_product_quantity pq5 on p.id = pq5.product_id and pq5.key = 'WIDTH'
         LEFT JOIN wiwa_product_quantity pq6 on p.id = pq6.product_id and pq6.key = 'THICKNESS'
         LEFT JOIN wiwa_product_unit_price pr on p.id = pr.product_id
WHERE pr.id = (SELECT price.id
               from wiwa_product_unit_price price
               WHERE price.id = pr.id
                 and price.valid_from >= CURRENT_DATE
                 and (price.valid_to <= CURRENT_DATE or price.valid_to is null)
               ORDER BY price.valid_from
               LIMIT 1)
;

create view wiwa_edge_product_view
            (
             id, code, name, description, stock_status,
             sale_value, sale_unit,
             weight_value, weight_unit,
             net_weight_value, net_weight_unit,
             width_value, width_unit,
             thickness_value, thickness_unit,
             price_value, price_unit
                )
as
SELECT p.id,
       p.code,
       p.name,
       p.description,
       p.stock_status,
       pq1.value,
       pq1.unit,
       pq2.value,
       pq2.unit,
       pq3.value,
       pq3.unit,
       pq4.value,
       pq4.unit,
       pq5.value,
       pq5.unit,
       pr.value,
       pr.unit
FROM wiwa_product p
         LEFT JOIN wiwa_product_quantity pq1 on p.id = pq1.product_id and pq1.key = 'SALE'
         LEFT JOIN wiwa_product_quantity pq2 on p.id = pq2.product_id and pq2.key = 'WEIGHT'
         LEFT JOIN wiwa_product_quantity pq3 on p.id = pq3.product_id and pq3.key = 'NET_WEIGHT'
         LEFT JOIN wiwa_product_quantity pq4 on p.id = pq4.product_id and pq4.key = 'WIDTH'
         LEFT JOIN wiwa_product_quantity pq5 on p.id = pq5.product_id and pq5.key = 'THICKNESS'
         LEFT JOIN wiwa_product_unit_price pr on p.id = pr.product_id
WHERE pr.id = (SELECT price.id
               from wiwa_product_unit_price price
               WHERE price.id = pr.id
                 and price.valid_from >= CURRENT_DATE
                 and (price.valid_to <= CURRENT_DATE or price.valid_to is null)
               ORDER BY price.valid_from
               LIMIT 1)
;

create view wiwa_free_sale_product_view
            (
             id, code, name, description, stock_status,
             sale_value, sale_unit,
             weight_value, weight_unit,
             net_weight_value, net_weight_unit,
             length_value, length_unit,
             width_value, width_unit,
             thickness_value, thickness_unit,
             price_value, price_unit
                )
as
SELECT p.id,
       p.code,
       p.name,
       p.description,
       p.stock_status,
       pq1.value,
       pq1.unit,
       pq2.value,
       pq2.unit,
       pq3.value,
       pq3.unit,
       pq4.value,
       pq4.unit,
       pq5.value,
       pq5.unit,
       pq6.value,
       pq6.unit,
       pr.value,
       pr.unit
FROM wiwa_product p
         LEFT JOIN wiwa_product_quantity pq1 on p.id = pq1.product_id and pq1.key = 'SALE'
         LEFT JOIN wiwa_product_quantity pq2 on p.id = pq2.product_id and pq2.key = 'WEIGHT'
         LEFT JOIN wiwa_product_quantity pq3 on p.id = pq3.product_id and pq3.key = 'NET_WEIGHT'
         LEFT JOIN wiwa_product_quantity pq4 on p.id = pq4.product_id and pq4.key = 'LENGTH'
         LEFT JOIN wiwa_product_quantity pq5 on p.id = pq5.product_id and pq5.key = 'WIDTH'
         LEFT JOIN wiwa_product_quantity pq6 on p.id = pq6.product_id and pq6.key = 'THICKNESS'
         LEFT JOIN wiwa_product_unit_price pr on p.id = pr.product_id
WHERE pr.id = (SELECT price.id
               from wiwa_product_unit_price price
               WHERE price.id = pr.id
                 and price.valid_from >= CURRENT_DATE
                 and (price.valid_to <= CURRENT_DATE or price.valid_to is null)
               ORDER BY price.valid_from
               LIMIT 1)
;

-- INDEX
create index idx_wiwa_application_property on wiwa_application_property (property_group);

create index idx_wiwa_code_list_item on wiwa_code_list_item (code_list_id);

create index idx_wiwa_product_attribute on wiwa_product_attribute (product_id);

create index idx_wiwa_product_image on wiwa_product_image (product_id);

create index idx_wiwa_product_quantity on wiwa_product_quantity (product_id);

create index idx_wiwa_product_unit_price on wiwa_product_unit_price (product_id);

create index idx_wiwa_user on wiwa_user (username);

create index idx_wiwa_user_authority1 on wiwa_user_authority (user_id);
create index idx_wiwa_user_authority2 on wiwa_user_authority (authority_id);

create index idx_wiwa_product_code_list_item1 on wiwa_product_code_list_item (product_id);
create index idx_wiwa_product_code_list_item2 on wiwa_product_code_list_item (code_list_item_id);
