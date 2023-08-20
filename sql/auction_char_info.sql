DROP TABLE IF EXISTS `auction_char_info`;
CREATE TABLE `auction_char_info` (
  `objectId` varchar(15) CHARACTER SET utf8 NOT NULL DEFAULT '',
  `player_name` varchar(45) CHARACTER SET utf8 DEFAULT NULL,
  `name` varchar(45) CHARACTER SET utf8 DEFAULT NULL,
  `firstname` varchar(45) CHARACTER SET utf8 DEFAULT NULL,
  `cash` varchar(13) CHARACTER SET utf8 DEFAULT NULL,
  `data` varchar(16) CHARACTER SET utf8 DEFAULT NULL,
  PRIMARY KEY (`objectId`),
  KEY `objectId` (`objectId`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;
