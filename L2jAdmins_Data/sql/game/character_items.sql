CREATE TABLE IF NOT EXISTS character_items (
  `owner_id` int, -- object id of the player or clan,owner of this item
  `object_id` int NOT NULL DEFAULT 0,-- object id of the item
  `item_id` int,   -- item id
  `count` int,
  `enchant_level` int,
  `loc` varchar(10),      -- inventory,paperdoll,npc,clan warehouse,pet,and so on
  `loc_data` int, -- depending on location: equiped slot,npc id,pet id,etc
  `freightLocation` int  NOT NULL DEFAULT 0, -- depending on location: equiped slot,npc id,pet id,etc
  `price_sell` int,
  `price_buy` int,
  `time_of_use` int, -- time of item use, for calculate of breackages
  `custom_type1` int DEFAULT 0,
  `custom_type2` int DEFAULT 0,
  PRIMARY KEY (`object_id`),
  KEY `key_owner_id` (`owner_id`),
  KEY `key_loc` (`loc`),
  KEY `key_item_id` (`item_id`),
  KEY `key_time_of_use` (`time_of_use`)
);
