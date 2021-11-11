create table credentials
(
    id serial,
    login varchar(30) not null,
    password varchar(40) not null
);

create unique index credentials_id_uindex
    on credentials (id);

create unique index credentials_login_uindex
    on credentials (login);

create unique index credentials_password_uindex
    on credentials (password);

alter table credentials
    add constraint credentials_pk
        primary key (id);