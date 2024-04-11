-- TABLE
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

-- INDEX
create index idx_wiwa_code_list_item on wiwa_code_list_item (code_list_id);
