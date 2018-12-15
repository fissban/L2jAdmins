DROP TABLE IF EXISTS `custom_merchant_shopids`;
CREATE TABLE `custom_merchant_shopids` (
  `shop_id` int(9) NOT NULL DEFAULT 0,
  `npc_id` int(9) DEFAULT NULL,
  PRIMARY KEY (`shop_id`)
);