create table mark
(
    id serial,
    student_id int not null
        constraint mark_student_id_fk
            references student,
    group_id int not null
        constraint mark_group_id_fk
            references "group",
    subject_id int not null
        constraint mark_subject_id_fk
            references subject,
    date_of_mark date not null,
    point int not null
);

create unique index mark_id_uindex
    on mark (id);

alter table mark
    add constraint mark_pk
        primary key (id);

