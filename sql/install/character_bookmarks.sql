CREATE TABLE IF NOT EXISTS `character_bookmarks` (
  `char_Id` INT NOT NULL,
  `idx` TINYINT UNSIGNED NOT NULL,
  `name` varchar(32) character set utf8 NOT NULL,
  `acronym` varchar(4) character set utf8 NOT NULL,
  `icon` TINYINT UNSIGNED NOT NULL,
  `x` INT NOT NULL,
  `y` INT NOT NULL,
  `z` INT NOT NULL,
  PRIMARY KEY  (`char_Id`,`idx`)
) ENGINE=MyISAM;