CREATE TABLE IF NOT EXISTS `pets` (
  `item_obj_id` int NOT NULL default 0,
  `objId` int,
  `name` varchar(12) character set utf8 default NULL,
  `level` tinyint unsigned,
  `curHp` mediumint unsigned,
  `curMp` mediumint unsigned,
  `exp` bigint,
  `sp` int unsigned,
  `fed` smallint unsigned,
  `max_fed` smallint unsigned,
  PRIMARY KEY (item_obj_id)
) ENGINE=MyISAM;