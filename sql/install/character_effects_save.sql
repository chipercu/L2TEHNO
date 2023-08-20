CREATE TABLE IF NOT EXISTS `character_effects_save` (
  `char_obj_id` int NOT NULL DEFAULT 0,
  `skill_id` mediumint unsigned NOT NULL DEFAULT 0,
  `skill_level` tinyint unsigned NOT NULL DEFAULT 0,
  `effect_count` tinyint unsigned NOT NULL DEFAULT 0,
  `effect_cur_time` int NOT NULL DEFAULT 0,
  `duration` int NOT NULL DEFAULT 0,
  `order` tinyint NOT NULL DEFAULT 0,
  `class_index` tinyint unsigned NOT NULL DEFAULT 0,
  PRIMARY KEY (`char_obj_id`,`skill_id`,`class_index`)
) ENGINE=MyISAM;