DROP TABLE IF EXISTS castle;
CREATE TABLE castle (
  id int NOT NULL DEFAULT 0,
  name varchar(25) NOT NULL,
  taxPercent int NOT NULL DEFAULT 15,
  nextTaxPercent int NOT NULL DEFAULT 0,
  treasury int NOT NULL DEFAULT 0,
  siegeDate bigint(20) NOT NULL DEFAULT 0,
  siegeDayOfWeek int NOT NULL DEFAULT 7,
  siegeHourOfDay int NOT NULL DEFAULT 20,
  PRIMARY KEY  (name),
  KEY id (id)
);

INSERT IGNORE INTO `castle` VALUES
(1, 'Gludio', 0, 0, 0, 0, 7, 20),
(2, 'Dion', 0, 0, 0, 0, 7, 20),
(3, 'Giran', 0, 0, 0, 0, 1, 16),
(4, 'Oren', 0, 0, 0, 0, 1, 16),
(5, 'Aden', 0, 0, 0, 0, 7, 20),
(6, 'Innadril', 0, 0, 0, 0, 1, 16),
(7, 'Goddard', 0, 0, 0, 0, 1, 16);
