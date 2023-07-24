-- EXTENSION
create extension if not exists unaccent;

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

create table wiwa_product_category
(
    id        bigserial primary key,
    parent_id bigint,
    tree_code text                               not null unique,
    code      varchar(255)                       not null unique,
    name      varchar(255) collate "sk-SK-x-icu" not null,
    sort_num  integer                            not null
);

create table wiwa_product
(
    id               bigserial primary key,
    type             varchar(255)                       not null,
    code             varchar(255)                       not null unique,
    name             varchar(255) collate "sk-SK-x-icu" not null,
    note             text collate "sk-SK-x-icu",
    sale_value       numeric(19, 3),
    sale_unit        varchar(255),
    weight_value     numeric(19, 3),
    weight_unit      varchar(255),
    net_weight_value numeric(19, 3),
    net_weight_unit  varchar(255),
    length_value     numeric(19, 3),
    length_unit      varchar(255),
    width_value      numeric(19, 3),
    width_unit       varchar(255),
    thickness_value  numeric(19, 3),
    thickness_unit   varchar(255),
    stock_status     varchar(255)                       not null
);

create table wiwa_product_attribute
(
    product_id bigint       not null references wiwa_product (id) on delete cascade,
    key        varchar(255) not null,
    value      varchar(255) not null,
    unique (product_id, key)
);

create table wiwa_product_image
(
    product_id bigint       not null references wiwa_product (id) on delete cascade,
    file_name  varchar(255) primary key,
    file_type  varchar(255) not null,
    thumbnail  bytea        not null,
    data       bytea        not null,
    unique (product_id, file_name)
);

create table wiwa_product_unit_price
(
    product_id bigint         not null references wiwa_product (id) on delete cascade,
    valid_from date           not null,
    valid_to   date,
    value      numeric(19, 3) not null,
    unit       varchar(255)   not null,
    unique (product_id, valid_from)
);

create table wiwa_product_categories
(
    product_id          bigint not null references wiwa_product (id) on delete cascade,
    product_category_id bigint not null references wiwa_product_category (id) on delete cascade,
    unique (product_id, product_category_id)
);

-- INDEX
create index idx_wiwa_application_property on wiwa_application_property (property_group);

create index idx_wiwa_user on wiwa_user (username);

create index idx_wiwa_user_authority1 on wiwa_user_authority (user_id);
create index idx_wiwa_user_authority2 on wiwa_user_authority (authority_id);

create index idx_wiwa_product_attribute on wiwa_product_attribute (product_id);

create index idx_wiwa_product_image on wiwa_product_image (product_id);

create index idx_wiwa_product_unit_price on wiwa_product_unit_price (product_id);

create index idx_wiwa_product_categories1 on wiwa_product_categories (product_id);
create index idx_wiwa_product_categories2 on wiwa_product_categories (product_category_id);
