-- auto-generated definition
create table subject
(
    id   serial
        constraint subject_pk
            primary key,
    name varchar(40) not null
);

alter table subjects
    owner to mongol;

create unique index subject_id_uindex
    on subjects (id);

