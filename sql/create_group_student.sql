-- auto-generated definition
create table group_student
(
    student_id integer not null
        constraint group_student_student_id_fk
            references student,
    group_id   integer not null
        constraint group_student_group_id_fk
            references "group"
);

alter table group_student
    owner to mongol;

