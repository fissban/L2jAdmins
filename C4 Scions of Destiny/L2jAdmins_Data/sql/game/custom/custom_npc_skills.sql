DROP TABLE IF EXISTS custom_npc_skills;
CREATE TABLE custom_npc_skills (
  npcid int(11) NOT NULL DEFAULT 0,
  skillid int(11) NOT NULL DEFAULT 0,
  level int(11) NOT NULL DEFAULT 0,
  PRIMARY KEY  (npcid,skillid,level)
);