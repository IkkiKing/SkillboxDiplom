delete from users;
insert into users(id, is_moderator, reg_time, name, email, password, code, photo)
values (1, 1, '2014-12-18 13:17:17', 'Васильев Василий', 'VasilievVasiliy@gmail.com', 'XcAvvA85z', 'code_number_1', null);

insert into users(id, is_moderator, reg_time, name, email, password, code, photo)
values (2, 0, '2016-01-15 14:13:00', 'Алексеев Алексей', 'AlexeevAlexey@gmail.com', 'ZZbasdq54', 'code_number_2', null);

insert into users(id, is_moderator, reg_time, name, email, password, code, photo)
values (3, 0, '2017-05-16 17:22:00', 'Петров Петр', 'PetrovPetr@yandex.ru', 'hdfAg76zx', 'code_number_3', 'https://avatarko.ru/img/kartinka/34/devushka_kapyushon_anime_protivogaz_33074.jpg');

insert into users(id, is_moderator, reg_time, name, email, password, code, photo)
values (4, 0, '2017-06-18 09:02:00', 'Максимов Влад', 'MaximovVlad@mail.ru', 'asr56nxHguk', 'code_number_4', null);

insert into users(id, is_moderator, reg_time, name, email, password, code, photo)
values (5, 0, '2017-01-14 13:12:00', 'Сахаров Семён', 'SaharovSema@gmail.com', '6546assaqh', 'code_number_5', null);

insert into users(id, is_moderator, reg_time, name, email, password, code, photo)
values (6, 0, '2017-11-22 13:12:00', 'Макрсов Карл', 'kapital2020@gmail.com', 'asdhtrh1KJa', 'code_number_6', null);

insert into users(id, is_moderator, reg_time, name, email, password, code, photo)
values (7, 1, '2017-12-18 13:12:00', 'Скиллбоксов Программ', 'Skillboxer2020@gmail.com', 'asdasth4rt', 'code_number_7', null);

delete from posts;
insert into posts(id, is_active, moderation_status, moderator_id, user_id, time, title, text, view_count)
VALUES(1,  1, 'ACCEPTED', 1, 1, '2014-09-01 13:12:00', 'Приветствие', 'Приветсвуем всех на нашем форуме посвященному курсу Java разработчик с 0!', 5);

insert into posts(id, is_active, moderation_status, moderator_id, user_id, time, title, text, view_count)
VALUES(2,  1, 'NEW', 1, 2, '2014-11-02 12:15:17', 'Черная пятница', 'Скоро черная пятница, а значит все курсы обучения будут доступны по скидкам', 1);

insert into posts(id, is_active, moderation_status, moderator_id, user_id, time, title, text, view_count)
VALUES(3,  1, 'NEW', 1, 5, '2015-12-03 11:15:17', 'Какой курс выбрать?', 'Среди курсорв Skillbox каждый найдет себе что-то по душе. Есть курсы по направлениям: Программирование, Веб-дизайн, Маркетинг, Управление, Игры', 1);

insert into posts(id, is_active, moderation_status, moderator_id, user_id, time, title, text, view_count)
VALUES(4,  1, 'DECLINED', 1, 3, '2016-05-05 12:13:17', 'Приветствие', 'Приветсвуем всех на нашем форуме посвященному курсу Java разработчик с 0!', 0);

insert into posts(id, is_active, moderation_status, moderator_id, user_id, time, title, text, view_count)
VALUES(5,  1, 'ACCEPTED', 1, 1, '2016-06-11 13:12:17', 'Веб-дизайн', 'Вы узнаете принципы композиции в дизайне сайтов, научитесь работать с типографикой, 3D и анимацией, сможете создавать не только красивые, но и удобные для пользователей сайты.', 2);

insert into posts(id, is_active, moderation_status, moderator_id, user_id, time, title, text, view_count)
VALUES(6,  1, 'ACCEPTED', 1, 1, '2017-04-22 14:11:17', 'Профессия Python-разработчик', 'На практике научитесь писать программы и разрабатывать веб-приложения с индивидуальной помощью от наставника. За 12 месяцев станете востребованным разработчиком, даже если вы новичок в программировании.', 3);

insert into posts(id, is_active, moderation_status, moderator_id, user_id, time, title, text, view_count)
VALUES(7,  1, 'ACCEPTED', 7, 1, '2018-05-23 15:59:17', 'Графический дизайн', 'Вы узнаете, как создавать фирменный стиль, крутые логотипы, дизайн для полиграфии и веба. Научитесь уверенно работать в Illustrator и Photoshop — и добавите мощный проект в портфолио. Сможете брать первые заказы и зарабатывать уже через 4 месяца.', 4);

insert into posts(id, is_active, moderation_status, moderator_id, user_id, time, title, text, view_count)
VALUES(8,  1, 'ACCEPTED', 7, 1, '2018-06-24 16:58:17', 'Дизайн жилых и коммерческих интерьеров', 'Вы научитесь создавать уникальные жилые и коммерческие интерьеры, а также продвигать свои услуги. Сможете стать более универсальным и востребованным специалистом или управлять собственной дизайн-студией.', 5);

insert into posts(id, is_active, moderation_status, moderator_id, user_id, time, title, text, view_count)
VALUES(9,  1, 'ACCEPTED', 7, 7, '2018-07-30 17:57:17', 'Дизайнер интерьеров', 'Вы научитесь создавать стильные интерьеры, делать удобные планировки и 3D-визуализации. Сможете получить востребованную профессию, выйти на новый уровень или построить дом мечты для себя.!', 6);

insert into posts(id, is_active, moderation_status, moderator_id, user_id, time, title, text, view_count)
VALUES(10,  1, 'ACCEPTED', 1, 1, '2019-08-07 18:56:17', 'Профессия Интернет-маркетолог', 'Освойте полный комплекс услуг по интернет-продвижению и станьте профессиональным маркетологом, стратегом, аналитиком, контекстологом и таргетологом в одном лице.', 1);

insert into posts(id, is_active, moderation_status, moderator_id, user_id, time, title, text, view_count)
VALUES(11,  1, 'ACCEPTED', 1, 1, '2019-09-08 19:55:17', 'UX-дизайн', 'Вы научитесь создавать крутой дизайн, разрабатывать удобные интерфейсы — и станете востребованным специалистом в UX.', 5);

insert into posts(id, is_active, moderation_status, moderator_id, user_id, time, title, text, view_count)
VALUES(12,  1, 'ACCEPTED', 1, 1, '2019-10-09 20:44:17', 'Профессия 3D-дженералист', 'Вы станете универсальным специалистом, который умеет в 3D всё: создавать объекты и окружение, настраивать текстуры и цвет, анимировать и внедрять модели в 3D-сцену!', 4);

insert into posts(id, is_active, moderation_status, moderator_id, user_id, time, title, text, view_count)
VALUES(13,  1, 'ACCEPTED', 1, 1, '2019-11-10 21:33:17', 'Профессия Ландшафтный дизайнер', 'Вы научитесь проектировать участки, сады и парки, создавать эскизы, чертежи и ландшафтные проекты в 3D — и точно попадать в желания заказчика. Соберёте портфолио и сможете работать в дизайн-студии или на фрилансе.', 3);

insert into posts(id, is_active, moderation_status, moderator_id, user_id, time, title, text, view_count)
VALUES(14,  1, 'ACCEPTED', 1, 7, '2020-12-11 22:22:17', 'Профессия Бизнес-аналитик', 'Вы освоите системный и бизнес-анализ, научитесь строить бизнес-модели, чтобы помогать компаниям принимать стратегические решения и увеличивать прибыль. Сможете стать высокооплачиваемым специалистом.', 7);

insert into posts(id, is_active, moderation_status, moderator_id, user_id, time, title, text, view_count)
VALUES(15,  1, 'ACCEPTED', 1, 7, '2020-12-16 23:11:17', 'Профессия‌ ‌Data‌ ‌Scientist‌', 'Вы станете специалистом по анализу данных, алгоритмам машинного обучения и нейросетям, сможете построить карьеру в крупной технологической компании — в России или за рубежом.!', 6);


delete from post_votes;
insert into post_votes(user_id, post_id, time, value)
VALUES (2, 1, '2018-01-01 01:10:00', 1);
insert into post_votes(user_id, post_id, time, value)
VALUES (3, 1, '2018-02-02 02:20:57', 1);
insert into post_votes(user_id, post_id, time, value)
VALUES (4, 1, '2018-03-01 02:30:00', 1);
insert into post_votes(user_id, post_id, time, value)
VALUES (5, 1, '2018-03-03 03:40:00', 1);
insert into post_votes(user_id, post_id, time, value)
VALUES (6, 1, '2018-04-14 04:50:00', 1);
insert into post_votes(user_id, post_id, time, value)
VALUES (1, 4, '2018-05-15 05:55:00', 0);

insert into post_votes(user_id, post_id, time, value)
VALUES (4, 5, '2018-06-02 11:11:00', 1);
insert into post_votes(user_id, post_id, time, value)
VALUES (3, 5, '2018-07-03 12:12:00', 1);
insert into post_votes(user_id, post_id, time, value)
VALUES (2, 5, '2018-07-04 13:13:00', 1);
insert into post_votes(user_id, post_id, time, value)
VALUES (6, 5, '2018-07-06 11:14:00', 0);


insert into post_votes(user_id, post_id, time, value)
VALUES (6, 6, '2018-08-17 10:41:05', 1);
insert into post_votes(user_id, post_id, time, value)
VALUES (3, 6, '2018-08-18 12:42:00', 1);

insert into post_votes(user_id, post_id, time, value)
VALUES (2, 7, '2019-01-10 14:20:17', 1);
insert into post_votes(user_id, post_id, time, value)
VALUES (4, 7, '2019-02-18 14:27:17', 1);

insert into post_votes(user_id, post_id, time, value)
VALUES (1, 11, '2019-11-01 15:47:11', 1);
insert into post_votes(user_id, post_id, time, value)
VALUES (2, 11, '2020-12-02 16:53:05', 1);
insert into post_votes(user_id, post_id, time, value)
VALUES (3, 11, '2020-12-03 16:51:00', 1);

delete from post_comments;
insert into post_comments(id, parent_id, post_id, user_id, time, text) VALUES
(1, null, 1, 2, '2018-05-01 10:17:00', 'Привет всем!');

insert into post_comments(id, parent_id, post_id, user_id, time, text) VALUES
(2, null, 1, 3, '2018-06-10 10:59:00', 'Всем привет!');

insert into post_comments(id, parent_id, post_id, user_id, time, text) VALUES
(3, null, 1, 4, '2018-06-11 11:47:00', 'Привет!');

insert into post_comments(id, parent_id, post_id, user_id, time, text) VALUES
(4, null, 1, 5, '2018-08-19 04:46:07', 'Доброго времени суток!');

insert into post_comments(id, parent_id, post_id, user_id, time, text) VALUES
(5, null, 5, 2, '2018-09-22 17:48:08', 'Отличный курс, всем советую!');

insert into post_comments(id, parent_id, post_id, user_id, time, text) VALUES
(6, 5, 5, 6, '2019-11-23 18:23:00', 'Я себе тоже взял, очень крутой!');

insert into post_comments(id, parent_id, post_id, user_id, time, text) VALUES
(7, null, 11, 3, '2019-11-04 19:22:00', 'Курс зачёт!');

insert into post_comments(id, parent_id, post_id, user_id, time, text) VALUES
(8, null, 6, 3, '2019-12-24 23:11:00', 'Будут ли курсы по языку Prolog? :D');

insert into post_comments(id, parent_id, post_id, user_id, time, text) VALUES
(9, 8, 6, 7, '2020-05-25 20:05:00', 'Конечно, добавят в скором времени!');

insert into post_comments(id, parent_id, post_id, user_id, time, text) VALUES
(10, null, 14, 4, '2020-07-26 10:00:00', 'Реально крутой курс!!!');

delete from tags;
insert into tags(id, name)
VALUES (1, 'Программирование');
insert into tags(id, name)
VALUES (2, 'Дизайн');
insert into tags(id, name)
VALUES (3, 'Управление');

delete from tag2post;
insert into tag2post(post_id, tag_id)
VALUES (5, 2);
insert into tag2post(post_id, tag_id)
VALUES (7, 2);
insert into tag2post(post_id, tag_id)
VALUES (8, 2);
insert into tag2post(post_id, tag_id)
VALUES (9, 2);
insert into tag2post(post_id, tag_id)
VALUES (11, 2);
insert into tag2post(post_id, tag_id)
VALUES (12, 2);
insert into tag2post(post_id, tag_id)
VALUES (13, 2);


insert into tag2post(post_id, tag_id)
VALUES (6, 1);
insert into tag2post(post_id, tag_id)
VALUES (15, 1);

insert into tag2post(post_id, tag_id)
VALUES (10, 3);
insert into tag2post(post_id, tag_id)
VALUES (14, 3);

delete  from captcha_codes;
insert into captcha_codes(time, code, secret_code)
values ('2014-12-18 13:17:17', 'captcha_1', 'captcha_secret_1');
insert into captcha_codes(time, code, secret_code)
values ('2014-12-18 13:17:17', 'captcha_2', 'captcha_secret_2');
insert into captcha_codes(time, code, secret_code)
values ('2014-12-18 13:17:17', 'captcha_3', 'captcha_secret_3');