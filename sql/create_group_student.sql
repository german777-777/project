-- auto-generated definition
create table group_student
(
    student_id integer not null
        constraint group_student_id__fk
            references person,
    group_id   integer not null
        constraint group_group_id_fk
            references "group"
);

alter table group_student
    owner to mongol;

