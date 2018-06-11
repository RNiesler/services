--liquibase formatted sql

--changeset rniesler:1
create table aggregate (
  id BINARY(16) NOT NULL,
  last_modified DATETIME,
  type VARCHAR(100) NOT NULL,
  version INT NOT NULL,
  PRIMARY KEY(id));

