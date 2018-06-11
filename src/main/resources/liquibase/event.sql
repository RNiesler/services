--liquibase formatted sql
--changeset event:1
create table event (
  aggregate_id BINARY(16) NOT NULL,
  aggregate_id_text varchar(36) generated always as
   (insert(
      insert(
        insert(
          insert(hex(aggregate_id),9,0,'-'),
          14,0,'-'),
        19,0,'-'),
      24,0,'-')
   ) virtual,
  version INT NOT NULL,
  type VARCHAR(100) NOT NULL,
  data VARCHAR(9999) NOT NULL,
  FOREIGN KEY (aggregate_id) REFERENCES aggregate(id));