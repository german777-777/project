create table admin
(
    id serial,
    first_name varchar(20) not null,
    last_name varchar(30) not null,
    patronymic varchar(30) not null,
    date_of_birth date not null,
    credential_id int not null
        constraint admin_credentials_id_fk
            references credentials
);

create unique index admin_id_uindex
    on admin (id);

alter table admin
    add constraint admin_pk
        primary key (id);

