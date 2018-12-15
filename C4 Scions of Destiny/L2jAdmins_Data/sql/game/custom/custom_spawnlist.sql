DROP TABLE IF EXISTS `custom_spawnlist`;
CREATE TABLE `custom_spawnlist` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `location` varchar(40) NOT NULL DEFAULT '',
  `count` int(9) NOT NULL DEFAULT 0,
  `npc_templateid` int(9) NOT NULL DEFAULT 0,
  `locx` int(9) NOT NULL DEFAULT 0,
  `locy` int(9) NOT NULL DEFAULT 0,
  `locz` int(9) NOT NULL DEFAULT 0,
  `randomx` int(9) NOT NULL DEFAULT 0,
  `randomy` int(9) NOT NULL DEFAULT 0,
  `heading` int(9) NOT NULL DEFAULT 0,
  `respawn_delay` int(9) NOT NULL DEFAULT 0,
  `loc_id` int(9) NOT NULL DEFAULT 0,
  `periodOfDay` tinyint(2) DEFAULT 0,
  PRIMARY KEY (`id`)
);