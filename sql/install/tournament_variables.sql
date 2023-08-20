CREATE TABLE `tournament_variables` (
  `name` varchar(255) character set utf8 NOT NULL default '',
  `value` varchar(255) character set utf8 default NULL,
  PRIMARY KEY  (`name`)
) ENGINE=MyISAM;

INSERT INTO `tournament_variables` VALUES ('start','0');