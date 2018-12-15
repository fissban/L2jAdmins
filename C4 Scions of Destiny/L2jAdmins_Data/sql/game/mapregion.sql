DROP TABLE IF EXISTS mapregion;
CREATE TABLE `mapregion` (
`region` int(11) NOT NULL DEFAULT 0,
`sec0` tinyint(2) NOT NULL DEFAULT 0,
`sec1` tinyint(2) NOT NULL DEFAULT 0,
`sec2` tinyint(2) NOT NULL DEFAULT 0,
`sec3` tinyint(2) NOT NULL DEFAULT 0,
`sec4` tinyint(2) NOT NULL DEFAULT 0,
`sec5` tinyint(2) NOT NULL DEFAULT 0,
`sec6` tinyint(2) NOT NULL DEFAULT 0,
`sec7` tinyint(2) NOT NULL DEFAULT 0,
`sec8` tinyint(2) NOT NULL DEFAULT 0,
`sec9` tinyint(2) NOT NULL DEFAULT 0,
`sec10` tinyint(2) NOT NULL DEFAULT 0,
PRIMARY KEY (`region`)
);

-- 0 = "Talking Island Village"
-- 1 = "Elven Village"
-- 2 = "Dark Elven Village"
-- 3 = "Orc Village"
-- 4 = "Dwarven Village"
-- 5 = "Town of Gludio"
-- 6 = "Gludin Village"
-- 7 = "Town of Dion"
-- 8 = "Town of Giran"
-- 9 = "Town of Oren"
-- 10 = "Town of Aden"
-- 11 = "Hunters Village"
-- 12 = "Giran Harbor"
-- 13 = "Heine"
-- 14 = "Rune Township"
-- 15 = "Town of Goddard"
-- 16 = "Floran Village"
-- DEFAULT = "Town of Aden"

INSERT INTO mapregion VALUES 
(0, 3, 3, 3, 3, 3, 4, 4, 4, 4, 4, 4),
(1, 3, 3, 3, 3, 3, 4, 4, 4, 4, 4, 4), 
(2, 3, 3, 7, 9, 11, 4, 4, 4, 4, 4, 4), 
(3, 3, 3, 3, 3, 3, 4, 4, 4, 4, 4, 4), 
(4, 3, 3, 3, 3, 3, 4, 4, 4, 4, 4, 4), 
(5, 3, 3, 3, 3, 3, 14, 14, 15, 15, 15, 15),
(6, 3, 3, 3, 3, 3, 14, 14, 15, 15, 15, 15), 
(7, 3, 3, 3, 3, 14, 14, 14, 15, 15, 15, 15),
(8, 3, 3, 3, 3, 14, 14, 14, 15, 15, 15, 15), 
(9, 2, 2, 2, 2, 2, 14, 9, 9, 10, 10, 10),
(10, 2, 2, 2, 2, 2, 9, 9, 10, 10, 10, 10),
(11, 2, 2, 2, 2, 1, 1, 9, 11, 10, 10, 10), 
(12, 6, 6, 2, 5, 1, 1, 9, 11, 11, 11, 11), 
(13, 6, 6, 5, 5, 7, 7, 8, 8, 8, 8, 8),
(14, 6, 6, 6, 5, 7, 7, 8, 8, 8, 8, 8), 
(15, 0, 6, 6, 5, 16, 12, 13, 13, 13, 13, 13),
(16, 0, 0, 6, 6, 12, 12, 13, 13, 13, 13, 13),
(17, 0, 0, 0, 0, 0, 0, 13, 13, 13, 13, 13);