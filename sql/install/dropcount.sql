CREATE TABLE IF NOT EXISTS `dropcount` (
  `char_id` int unsigned NOT NULL,
  `item_id` smallint unsigned NOT NULL,
  `count` bigint unsigned default 0,
  UNIQUE KEY `char_id` (`char_id`,`item_id`),
  KEY `char_id_2` (`char_id`)
) ENGINE=MyISAM;