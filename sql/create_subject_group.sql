create table group_subject
(
    subject_id int not null
        constraint group_subject_subject_id_fk
            references subject,
    group_id int not null
        constraint group_subject_group_id_fk
            references "group"
);

