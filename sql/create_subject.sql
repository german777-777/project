create table subject
(
    id serial,
    name varchar(40) not null
);

create unique index subject_id_uindex
    on subject (id);

alter table subject
    add constraint subject_pk
        primary key (id);

