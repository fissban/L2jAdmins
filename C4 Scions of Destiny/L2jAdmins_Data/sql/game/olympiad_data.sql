CREATE TABLE IF NOT EXISTS `olympiad_data` (
  `id` int UNSIGNED NOT NULL DEFAULT 0,
  `current_cycle` int UNSIGNED NOT NULL DEFAULT 1,
  `period` int UNSIGNED NOT NULL DEFAULT 0,
  `olympiad_end` bigint(13) unsigned NOT NULL DEFAULT 0,
  `validation_end` bigint(13) unsigned NOT NULL DEFAULT 0,
  `next_weekly_change` bigint(13) unsigned NOT NULL DEFAULT 0,
  PRIMARY KEY (`id`)
);