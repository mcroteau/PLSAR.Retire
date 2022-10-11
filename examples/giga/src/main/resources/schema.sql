create table if not exists users (
	id bigint PRIMARY KEY AUTO_INCREMENT,
	phone character varying(40) NOT NULL,
	email character varying(40) NOT NULL,
	password character varying(155) NOT NULL
);

create table if not exists user_permissions(
	user_id bigint REFERENCES users(id),
	permission character varying(55)
);

insert into users(email, phone, password) values ('croteau.mike+uno@gmail.com','9079878652','3b1a5b7b9b996e21e81ae1b12abacab5c463707ccb0206535889c815cde5f650');
insert into users(email, phone, password) values ('croteau.mike+dos@gmail.com','9079878652','3b1a5b7b9b996e21e81ae1b12abacab5c463707ccb0206535889c815cde5f650');
insert into user_permissions(user_id, permission) values (1, 'users:maintenance:1');
insert into user_permissions(user_id, permission) values (1, 'users:maintenance:2');
insert into user_permissions(user_id, permission) values (2, 'users:maintenance:2');