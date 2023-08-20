CREATE TABLE IF NOT EXISTS `bans` (
  `account_name` VARCHAR(45) DEFAULT NULL,
  `obj_Id` int unsigned NOT NULL DEFAULT 0,
  `baned` varchar(20) character set utf8 default NULL,
  `unban` varchar(20) character set utf8 default NULL,
  `reason` varchar(200) character set utf8 default NULL,
  `GM` varchar(35) character set utf8 default NULL,
  `endban` int unsigned DEFAULT NULL,
  `karma` int DEFAULT NULL
) ENGINE=MyISAM;
