DROP TABLE IF EXISTS `custom_etcitem`;
CREATE TABLE `custom_etcitem` (
  `item_id` int(11) NOT NULL DEFAULT 0,
  `name` varchar(100) DEFAULT NULL,
  `crystallizable` enum('true','false') DEFAULT 'true',
  `item_type` varchar(14) DEFAULT NULL,
  `weight` int(4) DEFAULT NULL,
  `consume_type` enum('asset','normal','stackable') DEFAULT NULL,
  `material` varchar(11) DEFAULT NULL,
  `crystal_type` enum('none','d','c','b','a','s') NOT NULL DEFAULT 'none',
  `price` int(11) DEFAULT NULL,
  `crystal_count` int(4) DEFAULT NULL,
  `sellable` enum('true','false') NOT NULL DEFAULT 'true',
  `dropable` enum('true','false') NOT NULL DEFAULT 'true',
  `destroyable` enum('true','false') NOT NULL DEFAULT 'true',
  `tradeable` enum('true','false') NOT NULL DEFAULT 'true',
  `oldname` varchar(100) NOT NULL DEFAULT '',
  `oldtype` varchar(100) NOT NULL DEFAULT '',
  PRIMARY KEY (`item_id`)
);