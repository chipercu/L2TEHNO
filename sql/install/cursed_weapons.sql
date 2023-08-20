CREATE TABLE IF NOT EXISTS `cursed_weapons` (
  `item_id` smallint unsigned NOT NULL,
  `player_id` int unsigned NOT NULL DEFAULT 0,
  `player_karma` int unsigned NOT NULL default '0',
  `player_pkkills` int unsigned NOT NULL default '0',
  `nb_kills` int unsigned NOT NULL default '0',
  `x` int NOT NULL default 0,
  `y` int NOT NULL default 0,
  `z` int NOT NULL default 0,
  `end_time` INT UNSIGNED NOT NULL DEFAULT 0,
  PRIMARY KEY (`item_id`)
) ENGINE=MyISAM;

