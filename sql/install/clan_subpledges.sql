CREATE TABLE IF NOT EXISTS `clan_subpledges` (
  `clan_id` int unsigned NOT NULL default 0,
  `type` smallint NOT NULL DEFAULT 0,
  `name` varchar(45) character set utf8 NOT NULL default '',
  `leader_id` int unsigned NOT NULL DEFAULT 0,
  PRIMARY KEY  (`clan_id`,`type`)
) ENGINE=MyISAM;
