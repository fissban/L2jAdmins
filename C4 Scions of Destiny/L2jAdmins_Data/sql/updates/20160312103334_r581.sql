ALTER TABLE `auction` DROP PRIMARY KEY;
ALTER TABLE `auction` DROP COLUMN itemType, DROP COLUMN itemId, DROP COLUMN itemQuantity;
ALTER TABLE `auction` ADD PRIMARY KEY (`id`);