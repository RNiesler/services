--liquibase formatted sql

--changeset rniesler:1 dbms:mysql
alter table aggregate add column
  id_text varchar(36) generated always as
   (insert(
      insert(
        insert(
          insert(hex(id),9,0,'-'),
          14,0,'-'),
        19,0,'-'),
      24,0,'-')
   ) virtual;

--changeset rniesler:2 dbms:mysql
alter table event add column
  aggregate_id_text varchar(36) generated always as
   (insert(
      insert(
        insert(
          insert(hex(aggregate_id),9,0,'-'),
          14,0,'-'),
        19,0,'-'),
      24,0,'-')
   ) virtual;