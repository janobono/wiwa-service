-- TABLE
create table wiwa_edge
(
    id               bigserial primary key,
    code             varchar(255)                       not null unique,
    name             varchar(255) collate "sk-SK-x-icu" not null,
    description      text,
    sale_value       numeric(19, 3)                     not null,
    sale_unit        varchar(255)                       not null,
    weight_value     numeric(19, 3),
    weight_unit      varchar(255),
    net_weight_value numeric(19, 3),
    net_weight_unit  varchar(255),
    width_value      numeric(19, 3)                     not null,
    width_unit       varchar(255)                       not null,
    thickness_value  numeric(19, 3)                     not null,
    thickness_unit   varchar(255)                       not null,
    price_value      numeric(19, 3)                     not null,
    price_unit       varchar(255)                       not null
);

create table wiwa_edge_image
(
    id        bigserial primary key,
    edge_id   bigint       not null references wiwa_edge (id) on delete cascade,
    file_name varchar(255) not null,
    file_type varchar(255) not null,
    thumbnail bytea        not null,
    data      bytea        not null,
    unique (edge_id, file_name)
);

create table wiwa_edge_code_list_item
(
    edge_id           bigint not null references wiwa_edge (id) on delete cascade,
    code_list_item_id bigint not null references wiwa_code_list_item (id) on delete cascade,
    unique (edge_id, code_list_item_id)
);

-- INDEX
create index idx_wiwa_edge_image on wiwa_edge_image (edge_id);

create index idx_wiwa_edge_code_list_item1 on wiwa_edge_code_list_item (edge_id);
create index idx_wiwa_edge_code_list_item2 on wiwa_edge_code_list_item (code_list_item_id);
