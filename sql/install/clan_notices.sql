CREATE TABLE `clan_notices` (
  `clanID` int NOT NULL,
  `notice` varchar(512) NOT NULL,
  `enabled` varchar(5) NOT NULL,
  PRIMARY KEY  (`clanID`)
) DEFAULT CHARSET=utf8;