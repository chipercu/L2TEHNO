DROP TABLE IF EXISTS `level_rewards`;
CREATE TABLE `level_rewards` (
  `objectId` int NOT NULL DEFAULT 0,
  `classId` int NOT NULL DEFAULT 0,
  `classLevel` int NOT NULL DEFAULT 0,
  PRIMARY KEY (`objectId`,`classId`,`classLevel`)
) ENGINE=MyISAM;
