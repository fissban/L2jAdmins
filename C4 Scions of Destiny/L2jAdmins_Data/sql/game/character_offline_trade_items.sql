CREATE TABLE IF NOT EXISTS `character_offline_trade_items` (
  `char_id` int(11) NOT NULL DEFAULT 0,
  `item` int(11) NOT NULL DEFAULT 0,
  `count` int(11) NOT NULL DEFAULT 0,
  `price` int(11) NOT NULL DEFAULT 0,
  PRIMARY KEY (`char_id`,`item`)
);