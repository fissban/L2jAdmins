CREATE TABLE IF NOT EXISTS `character_hennas` (
  `char_obj_id` int NOT NULL DEFAULT 0,
  `symbol_id` int,
  `slot` int NOT NULL DEFAULT 0,
  `class_index` int(1) NOT NULL DEFAULT 0,
  PRIMARY KEY (`char_obj_id`,`slot`,`class_index`)
);

