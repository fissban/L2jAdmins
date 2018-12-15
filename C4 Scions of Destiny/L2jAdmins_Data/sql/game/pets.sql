CREATE TABLE IF NOT EXISTS pets (
  item_obj_id int(11) NOT NULL DEFAULT 0,
  name varchar(16) ,
  level int(11) ,
  curHp int(11) ,
  curMp int(11) ,
  exp int(11) ,
  sp int(11) ,
  maxload int(11) ,
  fed int(11) ,
  weapon int(5) ,
  armor int(5) ,
  jewel int(5) ,
  PRIMARY KEY  (item_obj_id)
);
