CREATE TABLE IF NOT EXISTS `character_skills` (
  `char_obj_id` int NOT NULL default 0,
  `skill_id` smallint unsigned NOT NULL default 0,
  `skill_level` smallint unsigned NOT NULL default 0,
  `skill_name` varchar(100) default NULL,
  `class_index` tinyint unsigned NOT NULL default 0,
  PRIMARY KEY  (`char_obj_id`,`skill_id`,`class_index`)
) ENGINE=MyISAM;