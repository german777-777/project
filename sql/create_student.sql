create table student
(
    id serial,
    first_name varchar(20) not null,
    last_name varchar(30) not null,
    patronymic varchar(30) not null,
    date_of_birth date not null,
    credential_id int not null
        constraint student_credentials_id_fk
            references credentials
);

create unique index student_id_uindex
    on student (id);

alter table student
    add constraint student_pk
        primary key (id);
