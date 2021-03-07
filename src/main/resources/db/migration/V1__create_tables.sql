drop table if exists users;
create table users
(
    id           int          not null auto_increment primary key,
    is_moderator tinyint      not null,
    reg_time     datetime     not null,
    name         varchar(255) not null,
    email        varchar(255) not null,
    password     varchar(255) not null,
    code         varchar(255),
    photo        text
);

drop table if exists posts;
create table posts
(
    id                int                                  not null auto_increment primary key,
    is_active         tinyint                              not null,
    moderation_status enum ('NEW', 'ACCEPTED', 'DECLINED') not null,
    moderator_id      int,
    user_id           int                                  not null,
    time              datetime                             not null,
    title             varchar(255)                         not null,
    text              text                                 not null,
    view_count        int                                  not null
);

drop table if exists post_votes;
create table post_votes
(
    id      int      not null auto_increment primary key,
    user_id int      not null,
    post_id int      not null,
    time    datetime not null,
    value   tinyint  not null
);

drop table if exists tags;
create table tags
(
    id   int          not null auto_increment primary key,
    name varchar(255) not null
);

drop table if exists tag2post;
create table tag2post
(
    id      int not null auto_increment primary key,
    post_id int not null,
    tag_id  int not null
);

drop table if exists post_comments;
create table post_comments
(
    id        int      not null auto_increment primary key,
    parent_id int,
    post_id   int      not null,
    user_id   int      not null,
    time      datetime not null,
    text      TEXT     not null
);

drop table if exists captcha_codes;
create table captcha_codes
(
    id          int      not null auto_increment primary key,
    time        datetime not null,
    code        tinytext not null,
    secret_code tinytext not null
);

drop table if exists global_settings;
create table global_settings
(
    id    int          not null auto_increment primary key,
    code  varchar(255) not null,
    name  varchar(255) not null,
    value varchar(255) not null
);