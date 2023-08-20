CREATE TABLE IF NOT EXISTS `character_variables` (
  `obj_id` int NOT NULL default 0,
  `type` varchar(86) NOT NULL default 0,
  `name` varchar(86) character set utf8 NOT NULL default 0,
  `value` varchar(500) character set utf8 NOT NULL default 0,
  `expire_time` bigint(15) NOT NULL default 0,
  UNIQUE KEY `prim` (`obj_id`,`type`,`name`),
  KEY `obj_id` (`obj_id`),
  KEY `type` (`type`),
  KEY `name` (`name`),
  KEY `value` (`value`(165)),
  KEY `expire_time` (`expire_time`)
) ENGINE=MyISAM CHARSET=latin1;
