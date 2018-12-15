ALTER TABLE `characters`
  DROP `maxCp`,
  DROP `acc`,
  DROP `crit`,
  DROP `evasion`,
  DROP `mAtk`,
  DROP `mDef`,
  DROP `mSpd`,
  DROP `pAtk`,
  DROP `pDef`,
  DROP `pSpd`,
  DROP `runSpd`,
  DROP `walkSpd`,
  DROP `str`,
  DROP `con`,
  DROP `dex`,
  DROP `_int`,
  DROP `men`,
  DROP `wit`,
  DROP `movement_multiplier`,
  DROP `attack_speed_multiplier`,
  DROP `colRad`,
  DROP `colHeight`,
  DROP `maxload`,
  DROP `char_slot`;

RENAME TABLE `c4game`.`npcskills` TO `c4game`.`npc_skills`;
RENAME TABLE `c4game`.`custom_npcskills` TO `c4game`.`custom_npc_skills`;
RENAME TABLE `c4game`.`droplist` TO `c4game`.`npc_droplist`;
RENAME TABLE `c4game`.`custom_droplist` TO `c4game`.`custom_npc_droplist`;
RENAME TABLE `c4game`.`minions` TO `c4game`.`npc_minions`;