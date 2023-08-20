CREATE TABLE IF NOT EXISTS `character_skills_save` (
  `char_obj_id` int NOT NULL default 0,
  `skill_id` bigint NOT NULL default 0,
  `class_index` smallint NOT NULL default 0,
  `end_time` bigint NOT NULL default 0,
  `reuse_delay_org` int NOT NULL default 0,
  PRIMARY KEY  (`char_obj_id`,`skill_id`,`class_index`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;