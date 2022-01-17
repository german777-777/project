-- auto-generated definition
create table person
(
    id            serial
        constraint person_pk
            primary key,
    first_name    varchar(30) not null,
    last_name     varchar(30) not null,
    patronymic    varchar(30) not null,
    date_of_birth date        not null,
    credential_id integer     not null
        constraint credential__fk
            references credentials,
    role          varchar(10) not null
);

alter table persons
    owner to mongol;

create unique index person_id_uindex
    on persons (id);