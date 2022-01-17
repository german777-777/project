-- auto-generated definition
create table group_student
(
    student_id integer not null
        constraint group_student_id__fk
            references persons,
    group_id   integer not null
        constraint group_group_id_fk
            references groups
);

alter table group_student
    owner to mongol;

