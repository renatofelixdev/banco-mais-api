# --- Created by Ebean DDL
# To stop Ebean DDL generation, remove this comment and start using Evolutions

# --- !Ups

create table account_history (
  id                            bigserial not null,
  active                        boolean,
  removed                       boolean,
  date                          date,
  operation                     varchar(20),
  value                         decimal(10,2),
  source_id                     bigint,
  target_id                     bigint,
  date_created                  timestamp not null,
  date_updated                  timestamp not null,
  constraint ck_account_history_operation check (operation in ('BANK_STATEMENT','ACCOUNT_WITHDRAWAL','DEPOSIT_INTO_ACCOUNT','BANK_TRANSFER')),
  constraint pk_account_history primary key (id)
);

create table bank (
  id                            bigserial not null,
  active                        boolean,
  removed                       boolean,
  name                          varchar(255),
  code                          varchar(255),
  date_created                  timestamp not null,
  date_updated                  timestamp not null,
  constraint pk_bank primary key (id)
);

create table bank_account (
  id                            bigserial not null,
  active                        boolean,
  removed                       boolean,
  number                        varchar(255),
  balance                       decimal(10,2),
  bank_account_type             varchar(16),
  bank_agency_id                bigint,
  user_client_id                bigint,
  date_created                  timestamp not null,
  date_updated                  timestamp not null,
  constraint ck_bank_account_bank_account_type check (bank_account_type in ('SAVINGS_ACCOUNT','CHECKING_ACCOUNT')),
  constraint uq_bank_account_user_client_id unique (user_client_id),
  constraint pk_bank_account primary key (id)
);

create table bank_agency (
  id                            bigserial not null,
  active                        boolean,
  removed                       boolean,
  name                          varchar(255),
  code                          varchar(255),
  bank_id                       bigint,
  date_created                  timestamp not null,
  date_updated                  timestamp not null,
  constraint pk_bank_agency primary key (id)
);

create table user_client (
  id                            bigserial not null,
  active                        boolean,
  removed                       boolean,
  name                          varchar(255),
  cpf                           varchar(255),
  address                       varchar(255),
  date_created                  timestamp not null,
  date_updated                  timestamp not null,
  constraint pk_user_client primary key (id)
);

create table user_master (
  id                            bigserial not null,
  active                        boolean,
  removed                       boolean,
  login                         varchar(255),
  password                      varchar(255),
  date_created                  timestamp not null,
  date_updated                  timestamp not null,
  constraint pk_user_master primary key (id)
);

alter table account_history add constraint fk_account_history_source_id foreign key (source_id) references bank_account (id) on delete restrict on update restrict;
create index ix_account_history_source_id on account_history (source_id);

alter table account_history add constraint fk_account_history_target_id foreign key (target_id) references bank_account (id) on delete restrict on update restrict;
create index ix_account_history_target_id on account_history (target_id);

alter table bank_account add constraint fk_bank_account_bank_agency_id foreign key (bank_agency_id) references bank_agency (id) on delete restrict on update restrict;
create index ix_bank_account_bank_agency_id on bank_account (bank_agency_id);

alter table bank_account add constraint fk_bank_account_user_client_id foreign key (user_client_id) references user_client (id) on delete restrict on update restrict;

alter table bank_agency add constraint fk_bank_agency_bank_id foreign key (bank_id) references bank (id) on delete restrict on update restrict;
create index ix_bank_agency_bank_id on bank_agency (bank_id);


# --- !Downs

alter table if exists account_history drop constraint if exists fk_account_history_source_id;
drop index if exists ix_account_history_source_id;

alter table if exists account_history drop constraint if exists fk_account_history_target_id;
drop index if exists ix_account_history_target_id;

alter table if exists bank_account drop constraint if exists fk_bank_account_bank_agency_id;
drop index if exists ix_bank_account_bank_agency_id;

alter table if exists bank_account drop constraint if exists fk_bank_account_user_client_id;

alter table if exists bank_agency drop constraint if exists fk_bank_agency_bank_id;
drop index if exists ix_bank_agency_bank_id;

drop table if exists account_history cascade;

drop table if exists bank cascade;

drop table if exists bank_account cascade;

drop table if exists bank_agency cascade;

drop table if exists user_client cascade;

drop table if exists user_master cascade;

