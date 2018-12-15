ALTER TABLE `items` ADD `freightLocation` INT NOT NULL DEFAULT 0 AFTER `loc_data`;
UPDATE items SET freightLocation = loc_data WHERE loc = 'FREIGHT';
UPDATE items SET freightLocation = loc_data, loc_data = 15 WHERE loc_data >= 16 AND loc = 'FREIGHT';
UPDATE items SET freightLocation = 0 WHERE loc != 'FREIGHT';