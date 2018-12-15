DROP TABLE IF EXISTS `clanhall`;
CREATE TABLE IF NOT EXISTS `clanhall` (
  `id` int(11) NOT NULL DEFAULT 0,
  `name` varchar(40) NOT NULL DEFAULT '',
  `ownerId` int(11) NOT NULL DEFAULT 0,
  `lease` int(10) NOT NULL DEFAULT 0,
  `desc` text NOT NULL,
  `location` varchar(15) NOT NULL DEFAULT '',
  `paidUntil` bigint(20) NOT NULL DEFAULT 0,
  `Grade` tinyint(1) NOT NULL DEFAULT 0,
  `paid` tinyint(1) NOT NULL DEFAULT 0,
  PRIMARY KEY  (`id`,`name`),
  KEY `id` (`id`)
);

INSERT IGNORE INTO `clanhall` VALUES ('21', 'Partisan Hideaway', '0', '500000', 'Ol Mahum Partisan Hideaway', 'Dion', '0', '0', '0');
INSERT IGNORE INTO `clanhall` VALUES ('22', 'Moonstone Hall', '0', '500000', 'Clan hall located in the Town of Gludio', 'Gludio', '0', '2', '0');
INSERT IGNORE INTO `clanhall` VALUES ('23', 'Onyx Hall', '0', '500000', 'Clan hall located in the Town of Gludio', 'Gludio', '0', '2', '0');
INSERT IGNORE INTO `clanhall` VALUES ('24', 'Topaz Hall', '0', '500000', 'Clan hall located in the Town of Gludio', 'Gludio', '0', '2', '0');
INSERT IGNORE INTO `clanhall` VALUES ('25', 'Ruby Hall', '0', '500000', 'Clan hall located in the Town of Gludio', 'Gludio', '0', '2', '0');
INSERT IGNORE INTO `clanhall` VALUES ('26', 'Crystal Hall', '0', '500000', 'Clan hall located in Gludin Village', 'Gludin', '0', '2', '0');
INSERT IGNORE INTO `clanhall` VALUES ('27', 'Onyx Hall', '0', '500000', 'Clan hall located in Gludin Village', 'Gludin', '0', '2', '0');
INSERT IGNORE INTO `clanhall` VALUES ('28', 'Sapphire Hall', '0', '500000', 'Clan hall located in Gludin Village', 'Gludin', '0', '2', '0');
INSERT IGNORE INTO `clanhall` VALUES ('29', 'Moonstone Hall', '0', '500000', 'Clan hall located in Gludin Village', 'Gludin', '0', '2', '0');
INSERT IGNORE INTO `clanhall` VALUES ('30', 'Emerald Hall', '0', '500000', 'Clan hall located in Gludin Village', 'Gludin', '0', '2', '0');
INSERT IGNORE INTO `clanhall` VALUES ('31', 'The Atramental Barracks', '0', '500000', 'Clan hall located in the Town of Dion', 'Dion', '0', '1', '0');
INSERT IGNORE INTO `clanhall` VALUES ('32', 'The Scarlet Barracks', '0', '500000', 'Clan hall located in the Town of Dion', 'Dion', '0', '1', '0');
INSERT IGNORE INTO `clanhall` VALUES ('33', 'The Viridian Barracks', '0', '500000', 'Clan hall located in the Town of Dion', 'Dion', '0', '1', '0');
INSERT IGNORE INTO `clanhall` VALUES ('34', 'Devastated Castle', '0', '500000', 'Contestable Clan Hall', 'Aden', '0', '0', '0');
INSERT IGNORE INTO `clanhall` VALUES ('35', 'Bandit Stronghold', '0', '500000', 'Contestable Clan Hall', 'Oren', '0', '0', '0');
INSERT IGNORE INTO `clanhall` VALUES ('36', 'The Golden Chamber', '0', '500000', 'Clan hall located in the Town of Aden', 'Aden', '0', '3', '0');
INSERT IGNORE INTO `clanhall` VALUES ('37', 'The Silver Chamber', '0', '500000', 'Clan hall located in the Town of Aden', 'Aden', '0', '3', '0');
INSERT IGNORE INTO `clanhall` VALUES ('38', 'The Mithril Chamber', '0', '500000', 'Clan hall located in the Town of Aden', 'Aden', '0', '3', '0');
INSERT IGNORE INTO `clanhall` VALUES ('39', 'Silver Manor', '0', '500000', 'Clan hall located in the Town of Aden', 'Aden', '0', '3', '0');
INSERT IGNORE INTO `clanhall` VALUES ('40', 'Gold Manor', '0', '500000', 'Clan hall located in the Town of Aden', 'Aden', '0', '3', '0');
INSERT IGNORE INTO `clanhall` VALUES ('41', 'The Bronze Chamber', '0', '500000', 'Clan hall located in the Town of Aden', 'Aden', '0', '3', '0');
INSERT IGNORE INTO `clanhall` VALUES ('42', 'The Golden Chamber', '0', '500000', 'Clan hall located in the Town of Giran', 'Giran', '0', '3', '0');
INSERT IGNORE INTO `clanhall` VALUES ('43', 'The Silver Chamber', '0', '500000', 'Clan hall located in the Town of Giran', 'Giran', '0', '3', '0');
INSERT IGNORE INTO `clanhall` VALUES ('44', 'The Mithril Chamber', '0', '500000', 'Clan hall located in the Town of Giran', 'Giran', '0', '3', '0');
INSERT IGNORE INTO `clanhall` VALUES ('45', 'The Bronze Chamber', '0', '500000', 'Clan hall located in the Town of Giran', 'Giran', '0', '3', '0');
INSERT IGNORE INTO `clanhall` VALUES ('46', 'Silver Manor', '0', '500000', 'Clan hall located in the Town of Giran', 'Giran', '0', '3', '0');
INSERT IGNORE INTO `clanhall` VALUES ('47', 'Moonstone Hall', '0', '500000', 'Clan hall located in the Town of Goddard', 'Goddard', '0', '3', '0');
INSERT IGNORE INTO `clanhall` VALUES ('48', 'Onyx Hall', '0', '500000', 'Clan hall located in the Town of Goddard', 'Goddard', '0', '3', '0');
INSERT IGNORE INTO `clanhall` VALUES ('49', 'Emerald Hall', '0', '500000', 'Clan hall located in the Town of Goddard', 'Goddard', '0', '3', '0');
INSERT IGNORE INTO `clanhall` VALUES ('50', 'Sapphire Hall', '0', '500000', 'Clan hall located in the Town of Goddard', 'Goddard', '0', '3', '0');