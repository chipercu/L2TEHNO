CREATE TABLE IF NOT EXISTS `character_subclasses` (
	`char_obj_id` int NOT NULL,
	`class_id` tinyint unsigned NOT NULL,
	`level` tinyint unsigned NOT NULL DEFAULT 1,
	`exp` bigint unsigned NOT NULL DEFAULT 0,
	`sp` bigint unsigned NOT NULL DEFAULT 0,
	`curHp` DECIMAL(11,4)  unsigned NOT NULL DEFAULT 0,
	`curMp` DECIMAL(11,4)  unsigned NOT NULL DEFAULT 0,
	`curCp` DECIMAL(11,4) unsigned NOT NULL DEFAULT 0,
	`maxHp` MEDIUMINT  unsigned NOT NULL DEFAULT 0,
	`maxMp` MEDIUMINT  unsigned NOT NULL DEFAULT 0,
	`maxCp` MEDIUMINT  unsigned NOT NULL DEFAULT 0,
	`active` BOOLEAN NOT NULL DEFAULT 0,
	`isBase` BOOLEAN NOT NULL DEFAULT 0,
	`death_penalty` tinyint NOT NULL default 0,
	`certification` smallint(5) default '0',
	PRIMARY KEY  (`char_obj_id`,`class_id`)
) ENGINE=MyISAM;