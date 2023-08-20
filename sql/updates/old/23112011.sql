ALTER TABLE `heroes` ADD COLUMN `message` varchar(300) NOT NULL default '' AFTER `active`;

CREATE TABLE IF NOT EXISTS `heroes_diary` (
  `charId` int(10) unsigned NOT NULL,
  `time` bigint(13) unsigned NOT NULL DEFAULT '0',
  `action` tinyint(2) unsigned NOT NULL default '0',
  `param` int(11) unsigned NOT NULL default '0',
  KEY `charId` (`charId`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

CREATE TABLE IF NOT EXISTS `olympiad_history` (
  `object_id_1` INT(11) NOT NULL,
  `object_id_2` INT(11) NOT NULL,
  `class_id_1` INT(11) NOT NULL,
  `class_id_2` INT(11) NOT NULL,
  `name_1` VARCHAR(255) CHARACTER SET UTF8 NOT NULL DEFAULT '',
  `name_2` VARCHAR(255) CHARACTER SET UTF8 NOT NULL DEFAULT '',
  `game_start_time` BIGINT(20) NOT NULL,
  `game_time` INT(11) NOT NULL,
  `game_status` INT(11) NOT NULL,
  `game_type` INT(11) NOT NULL,
  `old` INT(11) NOT NULL
);
