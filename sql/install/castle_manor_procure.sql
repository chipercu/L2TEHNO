CREATE TABLE `castle_manor_procure` (
  `castle_id` tinyint unsigned NOT NULL DEFAULT 0,
  `crop_id` smallint unsigned NOT NULL DEFAULT 0,
  `can_buy` BIGINT NOT NULL DEFAULT '0',
  `start_buy` BIGINT NOT NULL DEFAULT '0',
  `price` BIGINT NOT NULL DEFAULT '0',
  `reward_type` tinyint unsigned NOT NULL DEFAULT 0,
  `period` int NOT NULL DEFAULT 1,
  PRIMARY KEY  (`castle_id`,`crop_id`,`period`)
) ENGINE=MyISAM;
