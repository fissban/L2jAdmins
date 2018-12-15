CREATE TABLE IF NOT EXISTS character_skills (
  char_obj_id int NOT NULL DEFAULT 0,
  skill_id int NOT NULL DEFAULT 0,
  skill_level varchar(5),
  class_index tinyint(1) NOT NULL DEFAULT 0,
  PRIMARY KEY  (char_obj_id,skill_id,class_index)
);
