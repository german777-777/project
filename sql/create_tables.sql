create table credentials
(
    id       serial
        constraint credentials_pkey
            primary key,
    login    varchar(255),
    password varchar(255)
);

alter table credentials
    owner to mongol;

create table persons
(
    role          varchar(31) not null,
    id            serial
        constraint persons_pkey
            primary key,
    date_of_birth date,
    first_name    varchar(255),
    last_name     varchar(255),
    patronymic    varchar(255),
    credential_id integer
        constraint fkmo4ifcdjrtukdqxb5ps6dqo35
            references credentials
);

alter table persons
    owner to mongol;

create table groups
(
    id         serial
        constraint groups_pkey
            primary key,
    name       varchar(255),
    teacher_id integer
        constraint fk2usjbs9tagqo9b71mm8ra790p
            references persons
);

alter table groups
    owner to mongol;

create table group_student
(
    group_id   integer not null
        constraint fkent8q7mj5tq6lup5v1e7alrqx
            references groups,
    student_id integer not null
        constraint fkju4ijt5jr29o4f7b254eou74w
            references persons
);

alter table group_student
    owner to mongol;

create table salaries
(
    id             serial
        constraint salaries_pkey
            primary key,
    date_of_salary date,
    count          integer,
    teacher_id     integer
        constraint fkeuvfci1ldskaogco874xqkund
            references persons
);

alter table salaries
    owner to mongol;

create table subjects
(
    id   serial
        constraint subjects_pkey
            primary key,
    name varchar(255)
);

alter table subjects
    owner to mongol;

create table group_subject
(
    group_id   integer not null
        constraint fkma8bk9fi6g9bwl1ybe4em41tl
            references groups,
    subject_id integer not null
        constraint fk2qu1afk2lnpuu3y670eofwqxp
            references subjects
);

alter table group_subject
    owner to mongol;

create table marks
(
    id           serial
        constraint marks_pkey
            primary key,
    date_of_mark date,
    point        integer,
    group_id     integer not null
        constraint uk_7ye8nb74n9pmyl9fpsm3dxkix
            unique
        constraint fkf2kxy4twm1kkhm57i66ntfmru
            references groups,
    student_id   integer
        constraint fkl2jo6eh0ldfna7h6ru74rfm33
            references persons,
    subject_id   integer not null
        constraint uk_4pnuumjkh6yadhn9gjn7uj6x0
            unique
        constraint fkbobdu0dce8k5vpm5mbd5ggpb1
            references subjects
);

alter table marks
    owner to mongol;

