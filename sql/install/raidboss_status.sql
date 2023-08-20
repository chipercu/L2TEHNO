CREATE TABLE IF NOT EXISTS `raidboss_status` (
  `id` int NOT NULL,
  `current_hp` int default NULL,
  `current_mp` int default NULL,
  `respawn_delay` int NOT NULL default 0,
  PRIMARY KEY  (`id`)
) ENGINE=MyISAM;