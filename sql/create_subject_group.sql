-- auto-generated definition
create table group_subject
(
    subject_id integer not null
        constraint group_subject_subject_id_fk
            references subjects,
    group_id   integer not null
        constraint group_subject_group_id_fk
            references groups
);

alter table group_subject
    owner to mongol;

