-- Database: tinder

-- DROP DATABASE tinder;
--
-- CREATE DATABASE tinder
--     WITH
--     OWNER = postgres
--     ENCODING = 'UTF8'
--     LC_COLLATE = 'Russian_Russia.1251'
--     LC_CTYPE = 'Russian_Russia.1251'
--     TABLESPACE = pg_default
--     CONNECTION LIMIT = -1;

-- Table: public.users
--
-- DROP TABLE public.lovers;
-- DROP TABLE public.users;

-- ALTER TABLE public.users
--     OWNER to postgres;

create table if not exists users
(
    id integer not null
        constraint users_pkey
            primary key,
    name varchar not null,
    gender varchar not null,
    heading varchar not null,
    description varchar not null
);

alter table users owner to postgres;

create table if not exists lovers
(
    user_id integer not null
        constraint user_id
            references users,
    lover_id integer not null
        constraint lover_id
            references users,
    user_lover_id serial
        constraint lovers_pk_2
            primary key,
    constraint lovers_pk
        unique (lover_id, user_id)
);

alter table lovers owner to postgres;

create unique index if not exists lovers_user_lover_id_uindex
    on lovers (user_lover_id);
