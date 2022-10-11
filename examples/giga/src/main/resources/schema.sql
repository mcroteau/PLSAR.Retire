create table users (
	id bigint PRIMARY KEY AUTO_INCREMENT,
    name character varying(140) NOT NULL,
	phone character varying(40) NOT NULL,
	email character varying(40) NOT NULL,
	password character varying(155) NOT NULL
);

create table user_permissions(
	user_id bigint REFERENCES users(id),
	permission character varying(55)
);

create table search_engine_stats(
    id bigint PRIMARY KEY AUTO_INCREMENT,
    results_count bigint,
    duration bigint,
    time bigint
);

create table title_variances(
    id bigint PRIMARY KEY AUTO_INCREMENT,
    site character varying(255),
    variance character varying(255)
);

create table key_words(
    id bigint PRIMARY KEY AUTO_INCREMENT,
    site character varying(255),
    variance character varying(255)
);

create table content_variance(
    id bigint PRIMARY KEY AUTO_INCREMENT,
    site character varying(255),
    variance character varying(255)
);

create table site_likes(
    id bigint PRIMARY KEY AUTO_INCREMENT,
    site character varying(255),
    likes bigint
);

create table search_stats(
    id bigint PRIMARY KEY AUTO_INCREMENT,
    user_id bigint REFERENCES users(id),
    search character varying(255),
    site character varying(255),
);

create table user_sites(
    id bigint PRIMARY KEY AUTO_INCREMENT,
    user_id bigint REFERENCES users(id),
    site_id bigint REFERENCES site_details(id)
);

create table site_details(
    id bigint PRIMARY KEY AUTO_INCREMENT,
    site character varying(255),
    variance character varying(255)
);





insert into users(email, phone, password) values ('croteau.mike+uno@gmail.com','9079878652','3b1a5b7b9b996e21e81ae1b12abacab5c463707ccb0206535889c815cde5f650');
insert into users(email, phone, password) values ('croteau.mike+dos@gmail.com','9079878652','3b1a5b7b9b996e21e81ae1b12abacab5c463707ccb0206535889c815cde5f650');
insert into user_permissions(user_id, permission) values (1, 'users:maintenance:1');
insert into user_permissions(user_id, permission) values (1, 'users:maintenance:2');
insert into user_permissions(user_id, permission) values (2, 'users:maintenance:2');