--liquibase formatted sql
--changeset rniesler:1
create table event (
  aggregate_id BINARY(16) NOT NULL,
  version INT NOT NULL,
  type VARCHAR(100) NOT NULL,
  data VARCHAR(9999),
  FOREIGN KEY (aggregate_id) REFERENCES aggregate(id));
