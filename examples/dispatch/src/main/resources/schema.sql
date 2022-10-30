create table users (
    id bigint PRIMARY KEY AUTO_INCREMENT,
    guid character varying(155),
    code character varying (255),
    name character varying (253) NOT NULL,
    phone character varying(14) NOT NULL,
    email character varying(253),
    photo clob,
    password character varying(155) NOT NULL,
    time_created bigint
);

create table user_permissions(
    id bigint PRIMARY KEY AUTO_INCREMENT,
    user_id bigint REFERENCES users(id),
    permission character varying(55)
);

create table user_follows (
    id bigint PRIMARY KEY AUTO_INCREMENT,
    user_id bigint,
    following_id bigint
);

create table sheets (
    id bigint PRIMARY KEY AUTO_INCREMENT,
    user_id bigint REFERENCES users(id),
    content text,
    sixty_four clob,
    video character varying(29),
    origin_id bigint,
    origin_content text,
    likes_count bigint,
    shares_count bigint,
    time_created bigint
);


create table ats_to (
    id bigint PRIMARY KEY AUTO_INCREMENT,
    user_id bigint REFERENCES users(id),
    at_id bigint REFERENCES users(id)
);

insert into users(code, name, phone, email, photo, password) values ('seb','Sebastien Tellier','9079878652','croteau.mike+seb@gmail.com','data:image/jpeg;base64,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','3b1a5b7b9b996e21e81ae1b12abacab5c463707ccb0206535889c815cde5f650');
insert into users(code, name, phone, email, photo, password) values ('royk','Royksopp','0987654321','croteau.mike+royk@gmail.com','data:image/jpeg;base64,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','3b1a5b7b9b996e21e81ae1b12abacab5c463707ccb0206535889c815cde5f650');
insert into users(code, name, phone, email, photo, password) values ('men','Men Whom I Trust','1234567890','croteau.mike+trust@gmail.com','data:image/jpeg;base64,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','3b1a5b7b9b996e21e81ae1b12abacab5c463707ccb0206535889c815cde5f650');
insert into user_permissions(user_id, permission) values (1, 'users:maintenance:1');
insert into user_permissions(user_id, permission) values (1, 'users:maintenance:2');
insert into user_permissions(user_id, permission) values (1, 'users:maintenance:3');
insert into user_permissions(user_id, permission) values (2, 'users:maintenance:2');
insert into user_permissions(user_id, permission) values (3, 'users:maintenance:3');
