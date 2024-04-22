-- TABLE
create table wiwa_board
(
    id               bigserial primary key,
    code             varchar(255)                       not null unique,
    board_code       varchar(255)                       not null,
    structure_code   varchar(255)                       not null,
    name             varchar(255) collate "sk-SK-x-icu" not null,
    description      text,
    orientation      boolean                            not null,
    sale_value       numeric(19, 3)                     not null,
    sale_unit        varchar(255)                       not null,
    net_weight_value numeric(19, 3),
    net_weight_unit  varchar(255),
    length_value     numeric(19, 3)                     not null,
    length_unit      varchar(255)                       not null,
    width_value      numeric(19, 3)                     not null,
    width_unit       varchar(255)                       not null,
    thickness_value  numeric(19, 3)                     not null,
    thickness_unit   varchar(255)                       not null,
    price_value      numeric(19, 3)                     not null,
    price_unit       varchar(255)                       not null
);

create table wiwa_board_image
(
    id        bigserial primary key,
    board_id  bigint       not null references wiwa_board (id) on delete cascade,
    file_name varchar(255) not null,
    file_type varchar(255) not null,
    thumbnail bytea        not null,
    data      bytea        not null,
    unique (board_id, file_name)
);

create table wiwa_board_code_list_item
(
    board_id          bigint not null references wiwa_board (id) on delete cascade,
    code_list_item_id bigint not null references wiwa_code_list_item (id) on delete cascade,
    unique (board_id, code_list_item_id)
);

-- INDEX
create index idx_wiwa_board_image on wiwa_board_image (board_id);

create index idx_wiwa_board_code_list_item1 on wiwa_board_code_list_item (board_id);
create index idx_wiwa_board_code_list_item2 on wiwa_board_code_list_item (code_list_item_id);
