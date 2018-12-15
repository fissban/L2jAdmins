CREATE TABLE IF NOT EXISTS `seven_signs` (
  `char_obj_id` int NOT NULL DEFAULT 0,
  `cabal` varchar(4) NOT NULL DEFAULT '',
  `seal` int NOT NULL DEFAULT 0,
  `red_stones` int NOT NULL DEFAULT 0,
  `green_stones` int NOT NULL DEFAULT 0,
  `blue_stones` int NOT NULL DEFAULT 0,
  `ancient_adena_amount` bigint(20) NOT NULL DEFAULT 0,
  `contribution_score` bigint(20) NOT NULL DEFAULT 0,
  PRIMARY KEY  (`char_obj_id`)
);
