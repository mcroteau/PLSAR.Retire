create table if not exists users (
	id bigint PRIMARY KEY AUTO_INCREMENT,
	business_id bigint default 0,
	name character varying(250) default '',
	phone character varying(140) default '',
	username character varying(253),
	password character varying(253) NOT NULL,
	fresh_password character varying(251),
	ship_street character varying(253),
	ship_street_dos character varying(250),
	ship_city character varying(250),
	ship_state character varying(250),
	ship_zip character varying(90),
	ship_country character varying(250),
	payment_street character varying(253),
	payment_street_dos character varying(250),
	payment_city character varying(250),
	payment_state character varying(250),
	payment_zip character varying(90),
	payment_country character varying(250),
	notes text,
	mining boolean default true,
	date_joined bigint default 0,
	uuid character varying(155)
);

create table if not exists roles (
	id bigint PRIMARY KEY AUTO_INCREMENT,
	name character varying(155) NOT NULL UNIQUE
);

create table if not exists user_permissions(
	user_id bigint REFERENCES users(id),
	permission character varying(55)
);

create table if not exists user_roles(
	role_id bigint NOT NULL REFERENCES roles(id),
	user_id bigint NOT NULL REFERENCES users(id)
);

create table if not exists businesses (
	id bigint PRIMARY KEY AUTO_INCREMENT,
	user_id bigint default 0,
	primary_id bigint default 0,
    name character varying(254),
    phone character varying(100) default '',
    email character varying(250) default '',
    stripe_id character varying (254) default '',
    uri character varying(250),
	street character varying(253) default '',
	street_dos character varying(250) default '',
	city character varying(250) default '',
	state character varying(250) default '',
	zip character varying(90) default '',
	country character varying(250) default '',
	initial boolean default true,
	affiliate boolean default false,
	activation_complete boolean default false,
	base_commission decimal default 13.0,
	owner character varying (250) default '',
	flat_shipping boolean default true,
	shipping decimal default 13.0,
	live boolean default true,
	constraint unique_uri unique (uri)
);

create table if not exists designs (
	id bigint PRIMARY KEY AUTO_INCREMENT,
	business_id bigint NOT NULL REFERENCES businesses(id),
	name character varying(253) NOT NULL,
	design text default '',
	css text default '',
	javascript text default '',
	base_design boolean default false
);

create table if not exists categories (
	id bigint PRIMARY KEY AUTO_INCREMENT,
	business_id bigint NOT NULL REFERENCES businesses(id),
	name character varying(254) default '',
	uri character varying(254) default '',
	header text default '',
	top_level boolean default true,
	category_id bigint default 0,
	design_id bigint NOT NULL REFERENCES designs(id)
);

create table if not exists items (
	id bigint PRIMARY KEY AUTO_INCREMENT,
	design_id bigint NOT NULL REFERENCES designs(id),
	business_id bigint NOT NULL REFERENCES businesses(id),
	name character varying(252) NOT NULL,
	description text default '',
	image_uri character varying(250),
	price decimal default 0.0,
	affiliate_price decimal default 0.0,
	quantity decimal default 0.0,
	cost decimal default 0.0,
	weight decimal default 0.0,
	active boolean default true
);

create table if not exists category_items(
	item_id bigint NOT NULL REFERENCES items(id),
	category_id bigint NOT NULL REFERENCES categories(id),
	business_id bigint NOT NULL REFERENCES businesses(id)
);

create table if not exists item_options(
	id bigint PRIMARY KEY AUTO_INCREMENT,
	item_id bigint NOT NULL REFERENCES items(id),
	name character varying (254)
);

create table if not exists option_values(
	id bigint PRIMARY KEY AUTO_INCREMENT,
	item_option_id bigint NOT NULL REFERENCES item_options(id),
	value character varying (254),
	price decimal default 0.0,
	quantity decimal default 0.0
);

create table if not exists pages (
	id bigint PRIMARY KEY AUTO_INCREMENT,
	design_id bigint NOT NULL REFERENCES designs(id),
	business_id bigint NOT NULL REFERENCES businesses(id),
	name character varying(252) NOT NULL,
	uri character varying(250) default '',
	content text default ''
);

create table if not exists carts (
	id bigint PRIMARY KEY AUTO_INCREMENT,
	total decimal default 0.0,
	subtotal decimal default 0.0,
	shipping decimal default 0.0,
	user_id bigint,
	business_id bigint NOT NULL REFERENCES businesses(id),
	session_id character varying(254) default '',
	ship_name character varying (250),
	ship_phone character varying (250),
	ship_email character varying (250),
	ship_street character varying(253),
	ship_street_dos character varying(250),
	ship_city character varying(250),
	ship_state character varying(250),
	ship_zip character varying(90),
	ship_country character varying(250),
	date_created bigint default 0,
	valid_address boolean default false,
	active boolean default true,
	sale boolean default false
);

create table if not exists cart_items(
	id bigint PRIMARY KEY AUTO_INCREMENT,
	item_id bigint NOT NULL REFERENCES items(id),
	cart_id bigint NOT NULL REFERENCES carts(id),
	business_id bigint NOT NULL REFERENCES businesses(id),
	price decimal default 0.0,
	quantity decimal default 1.0,
	item_total decimal default 0.0
);

create table if not exists cart_options (
	id bigint PRIMARY KEY AUTO_INCREMENT,
	cart_id bigint NOT NULL REFERENCES carts(id),
	cart_item_id bigint NOT NULL REFERENCES cart_items(id),
	item_option_id bigint NOT NULL REFERENCES item_options(id),
	option_value_id bigint NOT NULL REFERENCES option_values(id),
	price decimal default 0.0
);

create table if not exists shipment_rates (
	id bigint PRIMARY KEY AUTO_INCREMENT,
	cart_id bigint NOT NULL REFERENCES carts(id),
	rate decimal default 0.0,
	carrier character varying (254),
	delivery_days bigint default 0
);

create table if not exists sales (
	id bigint PRIMARY KEY AUTO_INCREMENT,
	sales_date bigint default 0,
	status character varying(250),
	amount decimal default 0.0,
	affiliate_amount bigint default 0,
	primary_amount bigint default 0,
    stripe_application_customer_id character varying (250),
    stripe_primary_customer_id character varying (250),
    stripe_application_charge_id character varying (250),
    stripe_primary_charge_id character varying (250),
	user_id bigint REFERENCES users(id),
	cart_id bigint NOT NULL REFERENCES carts(id),
	primary_id bigint REFERENCES businesses(id),
	affiliate_id bigint REFERENCES businesses(id)
);

create table if not exists assets(
	id bigint PRIMARY KEY AUTO_INCREMENT,
    name character varying(240) default '',
    meta character varying(240) default '',
    uri text default '',
    type character varying(250),
    date_added bigint default 0,
	user_id bigint REFERENCES users(id),
	business_id bigint REFERENCES businesses(id)
);

create table if not exists data_imports (
	id bigint PRIMARY KEY AUTO_INCREMENT,
	user_id bigint NOT NULL REFERENCES users(id),
	business_id bigint NOT NULL REFERENCES businesses(id),
	type character varying (250),
	date_import bigint default 0
);

create table if not exists media_imports (
	id bigint PRIMARY KEY AUTO_INCREMENT,
	import_id bigint NOT NULL REFERENCES data_imports(id),
	category_id bigint,
	name character varying (250),
	uri text default '',
	price decimal default 0.0,
	quantity decimal default 0.0,
	weight decimal default 0.0,
	meta character varying(190)
);

create table if not exists business_users (
	id bigint PRIMARY KEY AUTO_INCREMENT,
	user_id bigint NOT NULL REFERENCES users(id),
	business_id bigint NOT NULL REFERENCES businesses(id)
);

create table if not exists business_requests (
	id bigint PRIMARY KEY AUTO_INCREMENT,
	business_id bigint NOT NULL REFERENCES businesses(id),
	guid character varying (130),
	name character varying (254),
	business_name character varying (254),
	email character varying (254),
	phone character varying (254),
	notes text default '',
    approved boolean default false,
    denied boolean default false,
    pending boolean default true
);