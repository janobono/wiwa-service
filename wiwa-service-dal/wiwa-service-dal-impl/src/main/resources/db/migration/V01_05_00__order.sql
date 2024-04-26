-- TABLE
create table wiwa_order_number
(
    user_id      bigint primary key references wiwa_user (id) on delete cascade,
    order_number bigint not null
);

create table wiwa_order
(
    id           bigserial primary key,
    user_id      bigint         not null references wiwa_user (id) on delete cascade,
    created      timestamp      not null,
    order_number bigint         not null,
    delivery     date,
    package_type varchar(255),
    weight       numeric(19, 3) not null,
    total        numeric(19, 3) not null,
    summary      text           not null,
    unique (user_id, order_number)
);

create table wiwa_order_comment
(
    id       bigserial primary key,
    order_id bigint    not null references wiwa_order (id) on delete cascade,
    user_id  bigint    not null references wiwa_user (id) on delete cascade,
    created  timestamp not null,
    comment  text      not null
);

create table wiwa_order_contact
(
    order_id    bigint primary key references wiwa_order (id) on delete cascade,
    name        varchar(255) not null,
    street      varchar(255) not null,
    zip_code    varchar(255) not null,
    city        varchar(255) not null,
    state       varchar(255) not null,
    phone       varchar(255) not null,
    email       varchar(255) not null,
    business_id varchar(255),
    tax_id      varchar(255)
);

create table wiwa_order_item
(
    id       bigserial primary key,
    order_id bigint       not null references wiwa_order (id) on delete cascade,
    sort_num integer      not null,
    name     varchar(255) not null,
    quantity integer      not null,
    part     text         not null,
    summary  text         not null
);

create table wiwa_order_item_summary
(
    order_item_id bigint         not null references wiwa_order_item (id) on delete cascade,
    code          varchar(255)   not null,
    amount        numeric(19, 3) not null,
    primary key (order_item_id, code)
);

create table wiwa_order_material
(
    order_id    bigint       not null references wiwa_order (id) on delete cascade,
    material_id bigint       not null,
    code        varchar(255) not null,
    data        text         not null,
    primary key (order_id, material_id, code)
);

create table wiwa_order_status
(
    id       bigserial primary key,
    order_id bigint       not null references wiwa_order (id) on delete cascade,
    user_id  bigint       not null references wiwa_user (id) on delete cascade,
    created  timestamp    not null,
    status   varchar(255) not null
);

create view wiwa_order_view (id, user_id, created, order_number, delivery, package_type, status, weight, total) as
SELECT o.id,
       o.user_id,
       o.created,
       o.order_number,
       o.delivery,
       o.package_type,
       COALESCE((SELECT wiwa_order_status.status
                 FROM wiwa_order_status
                 WHERE wiwa_order_status.order_id = o.id
                 ORDER BY wiwa_order_status.created DESC
                 LIMIT 1), 'NEW') as status,
       o.weight,
       o.total
FROM wiwa_order o;

create view wiwa_order_summary_view (id, code, amount) as
SELECT o.id,
       ois.code,
       sum(ois.amount)
FROM wiwa_order o
         LEFT JOIN wiwa_order_item oi on o.id = oi.order_id
         LEFT JOIN wiwa_order_item_summary ois on oi.id = ois.order_item_id
GROUP BY o.id, ois.code;

-- INDEX
create index idx_wiwa_order_number on wiwa_order_number (user_id);

create index idx_wiwa_order on wiwa_order (user_id);

create index idx_wiwa_order_comment on wiwa_order_comment (order_id);

create index idx_wiwa_order_item on wiwa_order_item (order_id);

create index idx_wiwa_order_status on wiwa_order_status (order_id);
