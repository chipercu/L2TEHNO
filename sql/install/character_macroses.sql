CREATE TABLE IF NOT EXISTS `character_macroses` (
  `char_obj_id` int NOT NULL DEFAULT 0,
  `id` smallint unsigned NOT NULL DEFAULT 0,
  `icon` tinyint unsigned,
  `name` varchar(40) character set utf8 default NULL,
  `descr` varchar(80) character set utf8 default NULL,
  `acronym` varchar(4) character set utf8 default NULL,
  `commands` varchar(1024) character set utf8 default NULL,
  PRIMARY KEY  (`char_obj_id`,`id`)
) ENGINE=MyISAM;