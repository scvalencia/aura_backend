# --- Created by Ebean DDL
# To stop Ebean DDL generation, remove this comment and start using Evolutions

# --- !Ups

create table doctor (
  id                        bigint not null,
  gender                    integer,
  name                      varchar(255),
  email                     varchar(255),
  date                      timestamp,
  password                  varchar(255),
  discipline                integer,
  link                      varchar(255),
  constraint pk_doctor primary key (id))
;

create table episode (
  id                        bigint not null,
  url_id                    bigint,
  pub_date                  timestamp,
  intensity                 integer,
  sleep_hours               integer,
  regular_sleep             boolean,
  location                  integer,
  stress                    boolean,
  voice_episode_id          varchar(40),
  constraint pk_episode primary key (id))
;

create table food (
  id                        bigint not null,
  name                      varchar(255),
  quantity                  integer,
  constraint pk_food primary key (id))
;

create table medicine (
  id                        bigint not null,
  name                      varchar(255),
  hours_ago                 integer,
  constraint pk_medicine primary key (id))
;

create table patient (
  id                        bigint not null,
  name                      varchar(255),
  password                  varchar(255),
  date                      timestamp,
  email                     varchar(255),
  gender                    integer,
  constraint pk_patient primary key (id))
;

create table s3file (
  id                        varchar(40) not null,
  bucket                    varchar(255),
  name                      varchar(255),
  constraint pk_s3file primary key (id))
;

create table sport (
  id                        bigint not null,
  description               integer,
  intensity                 integer,
  place                     integer,
  climate                   integer,
  hydration                 boolean,
  constraint pk_sport primary key (id))
;

create table symptom (
  id                        bigint not null,
  symptom                   integer,
  constraint pk_symptom primary key (id))
;


create table episode_symptom (
  episode_id                     bigint not null,
  symptom_id                     bigint not null,
  constraint pk_episode_symptom primary key (episode_id, symptom_id))
;

create table episode_food (
  episode_id                     bigint not null,
  food_id                        bigint not null,
  constraint pk_episode_food primary key (episode_id, food_id))
;

create table episode_sport (
  episode_id                     bigint not null,
  sport_id                       bigint not null,
  constraint pk_episode_sport primary key (episode_id, sport_id))
;

create table episode_medicine (
  episode_id                     bigint not null,
  medicine_id                    bigint not null,
  constraint pk_episode_medicine primary key (episode_id, medicine_id))
;

create table patient_episode (
  patient_id                     bigint not null,
  episode_id                     bigint not null,
  constraint pk_patient_episode primary key (patient_id, episode_id))
;
create sequence doctor_seq;

create sequence episode_seq;

create sequence food_seq;

create sequence medicine_seq;

create sequence patient_seq;

create sequence sport_seq;

create sequence symptom_seq;

alter table episode add constraint fk_episode_voiceEpisode_1 foreign key (voice_episode_id) references s3file (id);
create index ix_episode_voiceEpisode_1 on episode (voice_episode_id);



alter table episode_symptom add constraint fk_episode_symptom_episode_01 foreign key (episode_id) references episode (id);

alter table episode_symptom add constraint fk_episode_symptom_symptom_02 foreign key (symptom_id) references symptom (id);

alter table episode_food add constraint fk_episode_food_episode_01 foreign key (episode_id) references episode (id);

alter table episode_food add constraint fk_episode_food_food_02 foreign key (food_id) references food (id);

alter table episode_sport add constraint fk_episode_sport_episode_01 foreign key (episode_id) references episode (id);

alter table episode_sport add constraint fk_episode_sport_sport_02 foreign key (sport_id) references sport (id);

alter table episode_medicine add constraint fk_episode_medicine_episode_01 foreign key (episode_id) references episode (id);

alter table episode_medicine add constraint fk_episode_medicine_medicine_02 foreign key (medicine_id) references medicine (id);

alter table patient_episode add constraint fk_patient_episode_patient_01 foreign key (patient_id) references patient (id);

alter table patient_episode add constraint fk_patient_episode_episode_02 foreign key (episode_id) references episode (id);

# --- !Downs

drop table if exists doctor cascade;

drop table if exists episode cascade;

drop table if exists episode_symptom cascade;

drop table if exists episode_food cascade;

drop table if exists episode_sport cascade;

drop table if exists episode_medicine cascade;

drop table if exists food cascade;

drop table if exists medicine cascade;

drop table if exists patient cascade;

drop table if exists patient_episode cascade;

drop table if exists s3file cascade;

drop table if exists sport cascade;

drop table if exists symptom cascade;

drop sequence if exists doctor_seq;

drop sequence if exists episode_seq;

drop sequence if exists food_seq;

drop sequence if exists medicine_seq;

drop sequence if exists patient_seq;

drop sequence if exists sport_seq;

drop sequence if exists symptom_seq;

