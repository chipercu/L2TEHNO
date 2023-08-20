DROP TABLE IF EXISTS `summon_save`;
CREATE TABLE `summon_save` (
  `char_obj_id` int NOT NULL DEFAULT 0,
  `class_id` int NOT NULL DEFAULT 0,
  `item_obj_id` int NOT NULL DEFAULT 0,
  `npc_id` int(11) unsigned NOT NULL DEFAULT '0',
  `life_time` int NOT NULL DEFAULT '0',
  `item_consume_idInTime` int(6) unsigned NOT NULL DEFAULT '0',
  `item_consume_countInTime` int(6) unsigned NOT NULL DEFAULT '0',
  `item_consume_delay` int(11) unsigned NOT NULL DEFAULT '0',
  `exp_penalty` DOUBLE NOT NULL default '0.00',
  PRIMARY KEY (`char_obj_id`,`npc_id`)
) ENGINE=MyISAM;