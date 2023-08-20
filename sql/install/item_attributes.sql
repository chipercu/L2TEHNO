CREATE TABLE IF NOT EXISTS `item_attributes` (
  `itemId` int NOT NULL DEFAULT 0,
  `augAttributes` int NOT NULL DEFAULT -1,
  `augSkillId` int NOT NULL DEFAULT -1,
  `augSkillLevel` int NOT NULL DEFAULT -1,
  `elemType` tinyint NOT NULL DEFAULT -1,
  `elemValue` int NOT NULL DEFAULT 0,
  `elem0` int NOT NULL DEFAULT 0,
  `elem1` int NOT NULL DEFAULT 0,
  `elem2` int NOT NULL DEFAULT 0,
  `elem3` int NOT NULL DEFAULT 0,
  `elem4` int NOT NULL DEFAULT 0,
  `elem5` int NOT NULL DEFAULT 0,
  PRIMARY KEY (`itemId`)
) ENGINE=MyISAM;