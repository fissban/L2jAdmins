CREATE TABLE IF NOT EXISTS `heroes` (
  `char_id` int(11) NOT NULL DEFAULT 0,
  `class_id` int(3) NOT NULL DEFAULT 0,
  `count` int(3) NOT NULL DEFAULT 0,
  `played` tinyint(1) NOT NULL DEFAULT 0,
  PRIMARY KEY  (`char_id`)
);