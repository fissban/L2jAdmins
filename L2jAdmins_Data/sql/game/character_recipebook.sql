CREATE TABLE IF NOT EXISTS character_recipebook (
  char_id int(11) NOT NULL DEFAULT 0,
  id int(11) NOT NULL DEFAULT 0,
  type int NOT NULL DEFAULT 0,
  PRIMARY KEY  (id,char_id)
);