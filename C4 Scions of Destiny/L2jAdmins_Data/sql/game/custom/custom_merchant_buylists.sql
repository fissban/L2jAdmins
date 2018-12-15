DROP TABLE IF EXISTS `custom_merchant_buylists`;
CREATE TABLE `custom_merchant_buylists` (
  `item_id` int(9) NOT NULL DEFAULT 0,
  `price` int(11) NOT NULL DEFAULT 0,
  `shop_id` int(9) NOT NULL DEFAULT 0,
  `order` int(4) NOT NULL DEFAULT 0,
  `count` int(11) NOT NULL DEFAULT '-1',
  `currentCount` int(11) NOT NULL DEFAULT '-1',
  `time` int NOT NULL DEFAULT 0,
  `savetimer` bigint(20) NOT NULL DEFAULT 0,
  PRIMARY KEY (`shop_id`,`order`)
);