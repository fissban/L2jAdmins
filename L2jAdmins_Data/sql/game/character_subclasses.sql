CREATE TABLE IF NOT EXISTS `character_subclasses` (
	`char_obj_id` int(11) NOT NULL DEFAULT 0,
	`class_id` tinyint(2) NOT NULL DEFAULT 0,
	`exp` int(11) NOT NULL DEFAULT 0,
	`sp` int(11) NOT NULL DEFAULT 0,
	`level` tinyint(2) NOT NULL DEFAULT 40,
	`class_index` tinyint(1) NOT NULL DEFAULT 0,
	PRIMARY KEY  (`char_obj_id`,`class_id`)
);