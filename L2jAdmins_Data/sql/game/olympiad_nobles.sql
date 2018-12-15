CREATE TABLE IF NOT EXISTS `olympiad_nobles` (
  `char_id` int UNSIGNED NOT NULL DEFAULT 0,
  `class_id` int(3) unsigned NOT NULL DEFAULT 0,
  `olympiad_points` int(10) NOT NULL DEFAULT 0,
  `competitions_done` int(3) NOT NULL DEFAULT 0,
  `competitions_won` int(3) NOT NULL DEFAULT 0,
  `competitions_lost` int(3) NOT NULL DEFAULT 0,
  `competitions_drawn` int(3) NOT NULL DEFAULT 0,
  PRIMARY KEY (`char_id`)
);