CREATE TABLE IF NOT EXISTS `accounts` (
  `login` varchar(45) NOT NULL DEFAULT '',
  `password` varchar(45) ,
  `lastactive` bigint(20),
  `access_level` int,
  `lastIP` varchar(20),
  `lastServer` int(4) DEFAULT 1,
  PRIMARY KEY (`login`)
);
