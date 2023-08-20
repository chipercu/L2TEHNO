CREATE TABLE IF NOT EXISTS `heroes` (
  `char_id` int NOT NULL default 0,
  `count` tinyint unsigned NOT NULL default 0,
  `played` tinyint NOT NULL default 0,
  `active` tinyint NOT NULL default 0,
  `message` varchar(300) NOT NULL default '',
  PRIMARY KEY (`char_id`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;