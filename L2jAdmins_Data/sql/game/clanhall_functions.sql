CREATE TABLE IF NOT EXISTS `clanhall_functions` (
  `hall_id` tinyint(2) NOT NULL DEFAULT 0,
  `type` tinyint(1) NOT NULL DEFAULT 0,
  `lvl` int(3) NOT NULL DEFAULT 0,
  `lease` int(10) NOT NULL DEFAULT 0,
  `rate` bigint(20) NOT NULL DEFAULT 0,
  `endTime` bigint(20) NOT NULL DEFAULT 0,
  PRIMARY KEY  (`hall_id`,`type`)
);
