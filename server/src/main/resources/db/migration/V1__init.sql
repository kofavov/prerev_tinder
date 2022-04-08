
create table if not exists users
(
    id integer not null
        constraint users_pkey
            primary key,
    name varchar not null,
    gender varchar not null,
    heading varchar not null,
    description varchar not null,
    find_gender varchar not null
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
INSERT INTO users (id, name, gender, heading, description,find_gender) VALUES (5, 'Бѣдняков', 'MALE', 'Бѣдняк', 'Я бѣденъ и уродливъ. Ищу полнейшего контраста.','ALL');
INSERT INTO users (id, name, gender, heading, description,find_gender) VALUES (1, 'Елѣна', 'FEMALE', 'Южанка', 'брюнѣтка красивая','ALL');
INSERT INTO users (id, name, gender, heading, description,find_gender) VALUES (12, 'Екатерина', 'FEMALE', 'Согласна выйти замуж', 'за того, кто обѣспечитъ мне чѣстное сущѣствованіе и защититъ отъ обид.','ALL');
INSERT INTO users (id, name, gender, heading, description,find_gender) VALUES (3, 'Вячеслав', 'MALE', 'Интѣллигент', 'Мне тридцатъ лѣтъ, шатѣнъ, срѣднѣго роста, имею 20 000 годового дохода, желалъ бы познакомиться с хорошенькой женщиной. Быватъ в театрахъ, прокатиться за городъ в лучшую морозную ночъ, и затѣмъ согрѣться за стаканомъ вина. После же сѣрьѣзного изученія другъ друга я не противъ вступитъ в бракъ и прѣдложитъ избраннице все удовольствія жизни','ALL');
INSERT INTO users (id, name, gender, heading, description,find_gender) VALUES (10, 'Бѣдняшка', 'FEMALE ', 'Чѣстная', 'дѣвушка 23 лѣтъ, красивая и интѣллигентная, ищетъ человѣка, который бы спасъ её отъ нужды и порока, куда её толкаетъ тяжёлая жизнъ. Будѣтъ благодарная своѣму будущѣму мужу.','ALL');
INSERT INTO users (id, name, gender, heading, description,find_gender) VALUES (7, 'Джек', 'MALE', 'Джентльмѣн', '30 лѣтъ отъ роду, объявляющій, что онъ обладаетъ значитѣльнымъ состояніѣмъ, желаетъ жениться на молодой даме с состояніѣмъ приблизитѣльно в 3 000 фунтовъ и готовъ заключитъ на этотъ счетъ соотвѣтствующій контракт','ALL');
INSERT INTO users (id, name, gender, heading, description,find_gender) VALUES (4, 'группа', 'MALE', 'Троица', '3 холостяка: инженеръ — 43 лѣтъ, подполковникъ — 42 лѣтъ, докторъ — 32 лѣтъ, живя вмѣсте, скучаютъ одиночѣствомъ. Надоели другъ другу, несмотря на взаимную дружбу, бѣзумно. Хочется разъѣхаться и каждому устроитъ свое собствѣнное гнѣздо','ALL');
INSERT INTO users (id, name, gender, heading, description,find_gender) VALUES (6, 'Блондинка', 'FEMALE', 'Очень интерѣсная барышня', 'блондинка, с тѣмными глазами со срѣдствами; желаетъ выйти замужъ. Только за обладающего хотя бы однимъ, но оченъ крупнымъ достоинствомъ','ALL');
INSERT INTO users (id, name, gender, heading, description,find_gender) VALUES (8, 'Кавказец-еврей', 'MALE', 'Кавказец-еврей', '22 лѣтъ, с срѣднимъ образованіѣмъ, энергичный, пылкій, впечатлитѣльный, желаетъ завѣсти перѣписку с молодой особой не старше своего возраста. Прѣдпочтѣніе - еврейке. Красоту цѣню во всѣхъ проявлѣніяхъ и отдамся только полюбивши. Идеализирую будущую жену: срѣднѣго роста, срѣдней полноты, блондинка, голубые глазки, правильный носикъ, ротикъ.','ALL');
INSERT INTO users (id, name, gender, heading, description,find_gender) VALUES (9, 'Граф', 'MALE', 'Граф', '33 л., желаетъ посрѣдствомъ брака сдѣлатъ богатую невѣсту графиней. Затѣмъ согласѣнъ датъ свободный видъ на житѣльство.','ALL');