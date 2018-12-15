CREATE TABLE IF NOT EXISTS `character_offline_trade` (
  `char_id` int(11) NOT NULL,
  `time` bigint(20) unsigned NOT NULL DEFAULT '0',
  `type` int(4) NOT NULL DEFAULT '0',
  `title` varchar(50) DEFAULT NULL,
  PRIMARY KEY (`char_id`)
);