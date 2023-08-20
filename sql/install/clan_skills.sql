CREATE TABLE `clan_skills` (
  `clan_id` int(11) NOT NULL DEFAULT '0',
  `skill_id` smallint(5) unsigned NOT NULL DEFAULT '0',
  `skill_level` tinyint(3) unsigned NOT NULL DEFAULT '0',
  `skill_name` varchar(26) DEFAULT NULL,
  `squad_index` smallint(6) NOT NULL DEFAULT '-1',
  PRIMARY KEY (`clan_id`,`skill_id`,`squad_index`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

