CREATE TABLE IF NOT EXISTS `character_recipebook` (
  `char_id` int NOT NULL DEFAULT 0,
  `id` smallint unsigned NOT NULL DEFAULT 0,
  PRIMARY KEY  (`id`,`char_id`)
) ENGINE=MyISAM;