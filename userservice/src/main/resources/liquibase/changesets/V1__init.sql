create table if not exists users
(
    id bigserial primary key,
    name varchar(255) not null,
    username varchar(255) not null unique,
    password varchar(255) not null

);

create table if not exists users_roles
(
    user_id bigint not null,
    role varchar(50) not null,
    constraint fk_users_roles_user foreign key(user_id) references users(id) on delete cascade on update no action

);

create table if not exists user_recipes
(
    user_id bigint not null,
    recipe_id varchar(255) not null,
    constraint fk_user_recipes_user foreign key(user_id) references users(id) on delete cascade on update no action
);