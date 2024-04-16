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
    property_key   varchar(255) not null,
    property_value text         not null,
    primary key (property_key)
);
