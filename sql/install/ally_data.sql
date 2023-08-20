CREATE TABLE IF NOT EXISTS `ally_data` (
  `ally_id` int NOT NULL default 0,
  `ally_name` varchar(45) character set utf8 default NULL,
  `leader_id` int NOT NULL DEFAULT 0,
  `expelled_member` int unsigned NOT NULL DEFAULT 0,
  `crest` VARBINARY(192) NULL DEFAULT NULL,
  PRIMARY KEY  (`ally_id`),
  KEY `leader_id` (`leader_id`)
) ENGINE=MyISAM;
