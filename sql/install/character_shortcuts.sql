CREATE TABLE IF NOT EXISTS `character_shortcuts` (
  `char_obj_id` int NOT NULL DEFAULT 0,
  `slot` tinyint unsigned NOT NULL DEFAULT 0,
  `page` tinyint unsigned NOT NULL DEFAULT 0,
  `type` tinyint unsigned,
  `shortcut_id` int,
  `level` smallint,
  `class_index` tinyint unsigned NOT NULL default 0,
  PRIMARY KEY (`char_obj_id`,`slot`,`page`,`class_index`),
  KEY `shortcut_id` (`shortcut_id`)
) ENGINE=MyISAM;