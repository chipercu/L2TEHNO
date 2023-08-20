DROP TABLE IF EXISTS `summon_effects_save`;
CREATE TABLE `summon_effects_save` (
  `char_obj_id` int NOT NULL DEFAULT 0,
  `npc_id` int(11) unsigned NOT NULL DEFAULT '0',
  `skill_id` mediumint unsigned NOT NULL DEFAULT 0,
  `skill_level` tinyint unsigned NOT NULL DEFAULT 0,
  `effect_count` tinyint unsigned NOT NULL DEFAULT 0,
  `effect_cur_time` int NOT NULL DEFAULT 0,
  `duration` int NOT NULL DEFAULT 0,
  `order` tinyint NOT NULL DEFAULT 0,
  PRIMARY KEY (`char_obj_id`,`npc_id`,`skill_id`)
) ENGINE=MyISAM;