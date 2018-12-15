-- Currently - only in update file. After all rework in this case, main files will be updated too.
ALTER TABLE  `pets` CHANGE  `maxload`  `maxload` DECIMAL( 11, 0 ) NULL DEFAULT  '54510';
UPDATE `pets_stats` SET atk_speed=282, owner_exp_taken = 0.00 WHERE `typeID` IN (12077, 12311, 12312, 12313, 12526, 12527, 12528);
UPDATE `pets` SET maxload = 54510 WHERE maxload IS NULL;