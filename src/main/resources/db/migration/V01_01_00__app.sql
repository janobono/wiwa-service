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

-- INDEX
create index idx_wiwa_application_property on wiwa_application_property (property_group);
