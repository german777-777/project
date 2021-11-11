create table salary
(
    id serial,
    teacher_id int not null
        constraint salary_teacher_id_fk
            references teacher,
    date_of_salary date not null,
    count int not null
);

create unique index salary_id_uindex
    on salary (id);

alter table salary
    add constraint salary_pk
        primary key (id);

