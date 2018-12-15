CREATE TABLE IF NOT EXISTS `character_friends` (
  `char_id` int UNSIGNED NOT NULL DEFAULT 0,
  `friend_id` int UNSIGNED NOT NULL DEFAULT 0,
  `relation` int UNSIGNED NOT NULL DEFAULT 0,
  PRIMARY KEY (`char_id`,`friend_id`)
);