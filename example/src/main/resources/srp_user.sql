-- auto-generated definition
create table srp_user
(
    id        int auto_increment
        primary key,
    user_name varchar(255) not null comment '用户名',
    user_v    text         not null comment '验证值 v',
    salt      varchar(255) not null comment '加密盐值',
    constraint srp_user_pk
        unique (user_name)
);

