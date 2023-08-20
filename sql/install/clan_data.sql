CREATE TABLE IF NOT EXISTS `clan_data` (
  `clan_id` int NOT NULL DEFAULT 0,
  `clan_name` varchar(45) character set utf8 default NULL,
  `clan_level` tinyint unsigned NOT NULL DEFAULT 0,
  `hasCastle` tinyint unsigned NOT NULL DEFAULT 0,
  `hasFortress` tinyint unsigned NOT NULL DEFAULT 0,
  `hasHideout` tinyint unsigned NOT NULL DEFAULT 0,
  `ally_id` int NOT NULL DEFAULT 0,
  `leader_id` int NOT NULL DEFAULT 0,
  `crest` VARBINARY(256) NULL DEFAULT NULL,
  `largecrest` VARBINARY(8192) NULL DEFAULT NULL,
  `reputation_score` int NOT NULL default 0,
  `warehouse` int NOT NULL default 0,
  `expelled_member` int unsigned NOT NULL DEFAULT 0,
  `leaved_ally` int unsigned NOT NULL DEFAULT 0,
  `dissolved_ally` int unsigned NOT NULL DEFAULT 0,
  `auction_bid_at` int NOT NULL default 0,
  `airship` SMALLINT NOT NULL DEFAULT -1,
  `point` int(11) NOT NULL DEFAULT '20',

  PRIMARY KEY (`clan_id`),
  KEY `leader_id` (`leader_id`),
  KEY `ally_id` (`ally_id`)
) ENGINE=MyISAM;
