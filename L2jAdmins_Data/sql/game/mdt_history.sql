CREATE TABLE IF NOT EXISTS `mdt_history` (
  `race_id` MEDIUMINT DEFAULT 0,
  `first` int DEFAULT 0,
  `second` int DEFAULT 0,
  `odd_rate` int DEFAULT 0,
  PRIMARY KEY (`race_id`)
);