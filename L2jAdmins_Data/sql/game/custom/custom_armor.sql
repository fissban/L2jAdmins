DROP TABLE IF EXISTS custom_armor;
CREATE TABLE `custom_armor` (
  `item_id` int(11) NOT NULL DEFAULT 0,
  `name` varchar(70) DEFAULT NULL,
  `bodypart` varchar(15) NOT NULL DEFAULT '',
  `crystallizable` enum('true','false') NOT NULL DEFAULT 'true',
  `armor_type` varchar(5) NOT NULL DEFAULT '',
  `weight` int(5) NOT NULL DEFAULT 0,
  `material` varchar(15) NOT NULL DEFAULT '',
  `crystal_type` enum('none','d','c','b','a','s') NOT NULL DEFAULT 'none',
  `avoid_modify` int(1) NOT NULL DEFAULT 0,
  `p_def` int(3) NOT NULL DEFAULT 0,
  `m_def` int(2) NOT NULL DEFAULT 0,
  `mp_bonus` int(3) NOT NULL DEFAULT 0,
  `price` int(11) NOT NULL DEFAULT 0,
  `crystal_count` int(4) DEFAULT NULL,
  `sellable` enum('true','false') NOT NULL DEFAULT 'true',
  `dropable` enum('true','false') NOT NULL DEFAULT 'true',
  `destroyable` enum('true','false') NOT NULL DEFAULT 'true',
  `tradeable` enum('true','false') NOT NULL DEFAULT 'true',
  `item_skill_id` int(11) NOT NULL DEFAULT 0,
  `item_skill_lvl` int(11) NOT NULL DEFAULT 0,
  PRIMARY KEY (`item_id`)
);