CREATE TABLE IF NOT EXISTS `raidboss_points` (
  `owner_id` int NOT NULL,
  `boss_id` smallint unsigned NOT NULL,
  `points` int NOT NULL default 0,
  KEY `owner_id` (`owner_id`),
  KEY `boss_id` (`boss_id`)
) ENGINE=MyISAM;