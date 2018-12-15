CREATE TABLE IF NOT EXISTS character_shortcuts (
  char_obj_id int(11) NOT NULL DEFAULT 0,
  slot int(3) NOT NULL DEFAULT 0,
  page int(3) NOT NULL DEFAULT 0,
  type int(3) ,
  shortcut_id int(11) ,
  level varchar(4) ,
  class_index tinyint(1) NOT NULL DEFAULT 0,
  PRIMARY KEY  (char_obj_id,slot,page,class_index),
  KEY `shortcut_id` (`shortcut_id`)
);
