CREATE TABLE IF NOT EXISTS `killcount` (
  `char_id` int NOT NULL,
  `npc_id` smallint unsigned NOT NULL,
  `count` int unsigned default 0,
  UNIQUE KEY `char_id` (`char_id`,`npc_id`),
  KEY `char_id_2` (`char_id`)
) ENGINE=MyISAM;