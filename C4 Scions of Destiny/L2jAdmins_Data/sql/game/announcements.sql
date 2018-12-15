CREATE TABLE IF NOT EXISTS `announcements` (
  `text` varchar(200) character set latin1 collate latin1_spanish_ci NOT NULL,
  `sayType` text character set latin1 collate latin1_spanish_ci,
  `repeatable` tinyint(1) DEFAULT NULL,
  `reuse` int(10) DEFAULT NULL,
  PRIMARY KEY  (`text`)
) ;