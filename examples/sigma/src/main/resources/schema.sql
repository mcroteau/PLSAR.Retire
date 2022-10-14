create table users (
	id bigint PRIMARY KEY AUTO_INCREMENT,
	phone character varying(40) NOT NULL,
	email character varying(40) NOT NULL,
	password character varying(155) NOT NULL
);

create table user_permissions(
	user_id bigint REFERENCES users(id),
	permission character varying(55)
);


create table workouts (
    id bigint PRIMARY KEY AUTO_INCREMENT,
    workout_sets bigint NOT NULL default 3,
    duration bigint NOT NULL default 20,
    break_duration bigint NOT NULL default 15,
    pushups bigint default 0.0,
    pullups bigint default 0.0,
    situps bigint default 0.0,
    sigma_pushups bigint default 0.0,
    sigma_pullups bigint default 0.0,
    sigma_situps bigint default 0.0,
    goal_pushups bigint default 0.0,
    goal_pullups bigint default 0.0,
    goal_situps bigint default 0.0,
    sigma_set_pushups bigint default 0.0,
    sigma_set_pullups bigint default 0.0,
    sigma_set_situps bigint default 0.0,
    start_time bigint,
    finish_time bigint,
    intervals_remaining bigint,
    duration_remaining bigint,
    paused boolean,
    break_session boolean,
    set_countdown bigint default 0.0,
    user_id bigint NOT NULL REFERENCES users(id)
);

insert into users(email, phone, password) values ('croteau.mike+uno@gmail.com','9079878652','3b1a5b7b9b996e21e81ae1b12abacab5c463707ccb0206535889c815cde5f650');
insert into users(email, phone, password) values ('croteau.mike+dos@gmail.com','9079878652','3b1a5b7b9b996e21e81ae1b12abacab5c463707ccb0206535889c815cde5f650');
insert into user_permissions(user_id, permission) values (1, 'users:maintenance:1');
insert into user_permissions(user_id, permission) values (1, 'users:maintenance:2');
insert into user_permissions(user_id, permission) values (2, 'users:maintenance:2');