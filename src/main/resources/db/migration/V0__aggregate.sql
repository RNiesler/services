create table aggregate (
  id BINARY(16) NOT NULL,
  id_text varchar(36) generated always as
   (insert(
      insert(
        insert(
          insert(hex(id),9,0,'-'),
          14,0,'-'),
        19,0,'-'),
      24,0,'-')
   ) virtual,
  type VARCHAR(100) NOT NULL,
  version INT NOT NULL,
  PRIMARY KEY(id));