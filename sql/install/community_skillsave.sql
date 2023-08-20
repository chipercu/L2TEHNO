DROP TABLE IF EXISTS `community_skillsave`;
CREATE TABLE `community_skillsave` (
  `charId` int(10) DEFAULT NULL,
  `schameid` int(10) NOT NULL AUTO_INCREMENT,
  `name` varchar(250) DEFAULT '',
  `skills` varchar(1000) DEFAULT '',
  PRIMARY KEY (`schameid`)
) ENGINE=MyISAM AUTO_INCREMENT=0 DEFAULT CHARSET=utf8;
