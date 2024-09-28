insert into users (name, username, password)
values ('John Doe', 'johndoe@gmail.com', '$2a$12$bhg5VveBoLPANzlPx4GXLuvT1KwKDaIj.jokQVmNqLVJu5D0xelMW'),
       ('Jane Smith', 'janesmith@gmail.com', '$2a$12$0jGpnjmLlrSQwNBnDc0xbu3uztwEoGaknDb.UT1.5CDzEYtHCdljy');

insert into users_roles (user_id, role)
values (1, 'ROLE_USER'),
       (1, 'ROLE_ADMIN'),
       (2, 'ROLE_USER');

INSERT INTO user_recipes (user_id, recipe_id)
VALUES (1, 'recipe_id_1'),
       (1, 'recipe_id_2'),
       (2, 'recipe_id_3');