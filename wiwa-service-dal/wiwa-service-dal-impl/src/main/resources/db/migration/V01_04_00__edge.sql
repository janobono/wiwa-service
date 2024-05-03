-- TABLE
create table wiwa_edge
(
    id          bigserial primary key,
    code        varchar(255)                       not null unique,
    name        varchar(255) collate "sk-SK-x-icu" not null,
    description text,
    weight      numeric(19, 3)                     not null,
    width       numeric(19, 3)                     not null,
    thickness   numeric(19, 3)                     not null,
    price       numeric(19, 3)                     not null
);

create table wiwa_edge_image
(
    edge_id   bigint primary key references wiwa_edge (id) on delete cascade,
    file_type varchar(255) not null,
    data      bytea        not null
);

create table wiwa_edge_code_list_item
(
    edge_id           bigint not null references wiwa_edge (id) on delete cascade,
    code_list_item_id bigint not null references wiwa_code_list_item (id) on delete cascade,
    unique (edge_id, code_list_item_id)
);

-- INDEX
create index idx_wiwa_edge_code_list_item1 on wiwa_edge_code_list_item (edge_id);
create index idx_wiwa_edge_code_list_item2 on wiwa_edge_code_list_item (code_list_item_id);
