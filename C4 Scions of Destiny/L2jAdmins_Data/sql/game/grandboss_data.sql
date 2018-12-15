DROP TABLE IF EXISTS `grandboss_data`;
CREATE TABLE IF NOT EXISTS grandboss_data (
  `boss_id` int NOT NULL DEFAULT 0,
  `loc_x` int NOT NULL DEFAULT 0,
  `loc_y` int NOT NULL DEFAULT 0,
  `loc_z` int NOT NULL DEFAULT 0,
  `heading` int NOT NULL DEFAULT 0,
  `respawn_time` bigint NOT NULL DEFAULT 0,
  `currentHP` int(8) DEFAULT NULL,
  `currentMP` int(8) DEFAULT NULL,
  `status` tinyint(1) NOT NULL DEFAULT 0,
  PRIMARY KEY(`boss_id`)
);

INSERT IGNORE INTO `grandboss_data` VALUES
(12001, -21610, 181594, -5734, 0, 0, 229898, 667, 0),      -- Queen Ant
(12052, 17726, 108915, -6480, 0, 0, 162561, 575, 0),       -- Core
(12169, 55024, 17368, -5412, 0, 0, 325124, 1660, 0),   -- Orfen
(12211, 185708,114298,-8221,32768, 0, 13090000, 22197, 0), -- Antharas
(12372, 115213,16623,10080,41740, 0, 790857, 3347, 0),     -- Baium
(12374, 55312, 219168, -3223, 0, 0, 858518, 1975, 0),      -- Zaken
(12899, 213389,-115026,-1636,0, 0, 16660000, 22197, 0);  -- Valakas