CREATE TABLE IF NOT EXISTS `clan_privs` (
  `clan_id` int NOT NULL default 0,
  `rank` int NOT NULL default 0,
  `privilleges` int NOT NULL default 0,
  PRIMARY KEY  (`clan_id`,`rank`)
) ENGINE=MyISAM;
