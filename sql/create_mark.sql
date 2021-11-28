-- auto-generated definition
create table mark
(
    id           serial
        constraint mark_pk
            primary key,
    student_id   integer not null
        constraint mark_student_id__fk
            references persons,
    group_id     integer not null
        constraint mark_group_id_fk
            references groups,
    subject_id   integer not null
        constraint mark_subject_id_fk
            references subjects,
    date_of_mark date    not null,
    point        integer not null
);

alter table marks
    owner to mongol;

create unique index mark_id_uindex
    on marks (id);

