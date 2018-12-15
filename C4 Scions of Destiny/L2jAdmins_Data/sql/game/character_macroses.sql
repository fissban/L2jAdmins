CREATE TABLE IF NOT EXISTS `character_macroses` (
  `char_obj_id` int NOT NULL DEFAULT 0,
  `id` int NOT NULL DEFAULT 0,
  `icon` int,
  `name` varchar(40) ,
  `descr` varchar(80) ,
  `acronym` varchar(4) ,
  `commands` varchar(255) ,
  PRIMARY KEY  (`char_obj_id`,`id`)
);