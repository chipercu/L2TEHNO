CREATE TABLE IF NOT EXISTS `residence_functions` (
  `id` tinyint unsigned NOT NULL default 0,
  `type` tinyint unsigned NOT NULL default 0,
  `lvl` smallint unsigned NOT NULL default 0,
  `lease` int NOT NULL default 0,
  `rate` bigint NOT NULL default 0,
  `endTime` int NOT NULL default 0,
  `inDebt` tinyint NOT NULL default 0,
  PRIMARY KEY  (`id`,`type`)
) ENGINE=MyISAM;
