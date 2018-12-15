CREATE TABLE IF NOT EXISTS character_skills_save (
  char_obj_id int NOT NULL DEFAULT 0,
  skill_id int NOT NULL DEFAULT 0,
  skill_level int NOT NULL DEFAULT 0,
  effect_count int NOT NULL DEFAULT 0,
  effect_cur_time int NOT NULL DEFAULT 0,
  reuse_delay int(8) NOT NULL DEFAULT 0,
  systime bigint UNSIGNED NOT NULL DEFAULT 0,
  restore_type tinyint(1) NOT NULL DEFAULT 0,
  class_index tinyint(1) NOT NULL DEFAULT 0,
  buff_index tinyint(2) NOT NULL DEFAULT 0,
  PRIMARY KEY  (char_obj_id,skill_id,class_index)
);
