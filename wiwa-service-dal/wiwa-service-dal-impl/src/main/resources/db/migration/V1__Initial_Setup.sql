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
    property_group    varchar(255) not null,
    property_key      varchar(255) not null,
    property_language varchar(255),
    property_value    text         not null,
    primary key (property_group, property_key, property_language)
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

-- INDEX
create index idx_wiwa_application_property on wiwa_application_property (property_group);

create index idx_wiwa_user on wiwa_user (username);

create index idx_wiwa_user_authority1 on wiwa_user_authority (user_id);
create index idx_wiwa_user_authority2 on wiwa_user_authority (authority_id);
