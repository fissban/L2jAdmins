CREATE TABLE IF NOT EXISTS `clan_wars` (
  `clan1` varchar(35) NOT NULL DEFAULT '',
  `clan2` varchar(35) NOT NULL DEFAULT '',
  `wantspeace1` tinyint(1) NOT NULL DEFAULT 0,
  `wantspeace2` tinyint(1) NOT NULL DEFAULT 0
);