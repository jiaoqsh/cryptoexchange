create table clearing_transaction (id bigint not null auto_increment, created_at bigint not null, updated_at bigint not null, version bigint not null, maker_order_id bigint not null, match_amount bigint not null, match_price bigint not null, taker_order_id bigint not null, primary key (id))
create table match_records (id bigint not null auto_increment, created_at bigint not null, updated_at bigint not null, version bigint not null, match_amount decimal(20,8) not null, match_price decimal(20,8) not null, match_type integer not null, matched_at bigint not null, order_id bigint not null, primary key (id))
create table match_results (id bigint not null auto_increment, created_at bigint not null, updated_at bigint not null, version bigint not null, order_id bigint not null, type integer not null, primary key (id))
create table order_sequences (id bigint not null, created_at bigint not null, order_id bigint not null, primary key (id))
create table orders (id bigint not null auto_increment, created_at bigint not null, updated_at bigint not null, version bigint not null, amount decimal(20,8) not null, filled_amount decimal(20,8) not null, price decimal(20,8) not null, ref_order_id bigint not null, ref_seq_id bigint not null, seq_id bigint not null, status integer not null, symbol integer not null, type integer not null, user_id bigint not null, primary key (id))
create table password_auths (id bigint not null auto_increment, created_at bigint not null, updated_at bigint not null, version bigint not null, passwd varchar(50) not null, random bigint not null, user_id bigint not null, primary key (id))
create table spot_accounts (id bigint not null auto_increment, created_at bigint not null, updated_at bigint not null, version bigint not null, balance decimal(20,8) not null, currency integer not null, frozen decimal(20,8) not null, user_id bigint not null, primary key (id))
create table ticks (id bigint not null auto_increment, created_at bigint not null, updated_at bigint not null, version bigint not null, amount decimal(20,8) not null, price bigint not null, symbol integer not null, time bigint not null, primary key (id))
create table users (id bigint not null auto_increment, created_at bigint not null, updated_at bigint not null, version bigint not null, email varchar(50) not null, name varchar(50) not null, type integer not null, primary key (id))
alter table clearing_transaction add constraint UKbsjh8yhstbccts4hc5a1w9e5t unique (taker_order_id, maker_order_id)
create index IDX_ORDERID on match_records (order_id)
alter table match_results add constraint UNI_TYPE_ORDER_ID unique (type, order_id)
create index IDX_USERID_STATUS on orders (user_id, status)
create index IDX_CREATEDAT on orders (created_at)
alter table password_auths add constraint UNI_USERID unique (user_id)
alter table spot_accounts add constraint UNI_USERID_CURRENCY unique (user_id, currency)
alter table ticks add constraint UK_b09rltmt69iyfiat06ps3q8aj unique (time)
alter table users add constraint UNI_EMAIL unique (email)
