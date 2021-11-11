create table teacher
(
    id serial,
    first_name varchar(20) not null,
    last_name varchar(30) not null,
    patronymic varchar(30) not null,
    date_of_birth date not null,
    credential_id int not null
        constraint teacher_credentials_id_fk
            references credentials
);

create unique index teacher_id_uindex
    on teacher (id);

alter table teacher
    add constraint teacher_pk
        primary key (id);