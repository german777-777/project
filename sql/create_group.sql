-- auto-generated definition
create table "group"
(
    id         serial
        constraint group_pk
            primary key,
    teacher_id integer     not null
        constraint group__teacher__fk
            references person,
    name       varchar(20) not null
);

alter table "group"
    owner to mongol;

create unique index group_id_uindex
    on "group" (id);

