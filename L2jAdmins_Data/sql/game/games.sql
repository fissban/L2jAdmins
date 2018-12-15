CREATE TABLE IF NOT EXISTS games (
  id int NOT NULL DEFAULT 0,
  idnr int NOT NULL DEFAULT 0,
  number1 int NOT NULL DEFAULT 0,
  number2 int NOT NULL DEFAULT 0,
  prize  int NOT NULL DEFAULT 0,
  newprize  int NOT NULL DEFAULT 0,
  prize1  int NOT NULL DEFAULT 0,
  prize2  int NOT NULL DEFAULT 0,
  prize3  int NOT NULL DEFAULT 0,
  enddate bigint NOT NULL DEFAULT 0,
  finished int NOT NULL DEFAULT 0,
  PRIMARY KEY (`id`,`idnr`)
);

