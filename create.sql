create sequence card_seq start with 1 increment by 50;
create sequence site_seq start with 1 increment by 50;
create table card (id bigint not null, content text not null, image bytea, status varchar(255) not null, url text not null, primary key (id));
create table site (id bigint not null, region varchar(255) not null, url text not null, primary key (id));
create table site_card (site_id bigint not null, card_id bigint not null);
alter table if exists site_card add constraint UK_orcn59j2g1h5uqginnxdfojdx unique (card_id);
alter table if exists site_card add constraint FK1c0n74612tgkw0pld2iv6m5dn foreign key (card_id) references card;
alter table if exists site_card add constraint FKb16ifl8aj9hw78t4jn55ces8e foreign key (site_id) references site;
