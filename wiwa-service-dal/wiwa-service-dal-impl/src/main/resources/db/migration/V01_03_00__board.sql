-- TABLE
create table wiwa_board
(
    id             bigserial primary key,
    code           varchar(255)                       not null unique,
    board_code     varchar(255)                       not null,
    structure_code varchar(255)                       not null,
    name           varchar(255) collate "sk-SK-x-icu" not null,
    description    text,
    orientation    boolean                            not null,
    weight         numeric(19, 3)                     not null,
    length         numeric(19, 3)                     not null,
    width          numeric(19, 3)                     not null,
    thickness      numeric(19, 3)                     not null,
    price          numeric(19, 3)                     not null
);

create table wiwa_board_image
(
    board_id  bigint primary key references wiwa_board (id) on delete cascade,
    file_type varchar(255) not null,
    data      bytea        not null
);

create table wiwa_board_code_list_item
(
    board_id          bigint not null references wiwa_board (id) on delete cascade,
    code_list_item_id bigint not null references wiwa_code_list_item (id) on delete cascade,
    unique (board_id, code_list_item_id)
);

-- INDEX
create index idx_wiwa_board_code_list_item1 on wiwa_board_code_list_item (board_id);
create index idx_wiwa_board_code_list_item2 on wiwa_board_code_list_item (code_list_item_id);
