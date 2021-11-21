-- auto-generated definition
create table salary
(
    id             serial
        constraint salary_pk
            primary key,
    teacher_id     integer not null
        constraint salary_teacher_id__fk
            references person,
    date_of_salary date    not null,
    count          integer not null
);

alter table salary
    owner to mongol;

create unique index salary_id_uindex
    on salary (id);

