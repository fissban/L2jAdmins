CREATE TABLE IF NOT EXISTS character_recommends ( 
 char_id int NOT NULL DEFAULT 0, 
 target_id int(11) NOT NULL DEFAULT 0, 
 PRIMARY KEY (char_id,target_id) 
); 
