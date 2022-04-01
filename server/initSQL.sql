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

CREATE TABLE IF NOT EXISTS public.users
(
    id integer NOT NULL,
    name character varying COLLATE pg_catalog."default" NOT NULL,
    gender character varying COLLATE pg_catalog."default" NOT NULL,
    heading character varying COLLATE pg_catalog."default" NOT NULL,
    description character varying COLLATE pg_catalog."default" NOT NULL,
    CONSTRAINT users_pkey PRIMARY KEY (id)
)

    TABLESPACE pg_default;

create table lovers
(
    user_id       integer not null
        constraint user_id
            references users,
    lover_id      integer not null
        constraint lover_id
            references users,
    user_lover_id serial
        constraint lovers_pk_2
            primary key,
    constraint lovers_pk
        unique (lover_id, user_id)
);

alter table lovers
    owner to postgres;

create unique index lovers_user_lover_id_uindex
    on lovers (user_lover_id);
