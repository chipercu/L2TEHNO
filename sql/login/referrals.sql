CREATE TABLE `referrals` (
	`login` VARCHAR(45) NOT NULL DEFAULT '',
	`server` TINYINT(3) UNSIGNED NOT NULL DEFAULT '0',
	`char` INT(11) UNSIGNED NOT NULL DEFAULT '0',
	`bonus1` VARCHAR(32) NOT NULL DEFAULT '',
	`bonus2` VARCHAR(32) NOT NULL DEFAULT ''
) ENGINE=MyISAM;