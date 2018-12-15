-- ----------------------------
-- Table structure for character_quests
-- ----------------------------
CREATE TABLE IF NOT EXISTS `character_quests` (
  `char_id` int NOT NULL DEFAULT 0,
  `name` varchar(40) NOT NULL DEFAULT '',
  `var`  varchar(20) NOT NULL DEFAULT '',
  `value` varchar(255) ,
  `class_index` int(1) NOT NULL DEFAULT 0,
  PRIMARY KEY  (`char_id`,`name`,`var`,`class_index`)
);
