-- Update Для логин сервера (отдельно так как логин бывает разделен с гейм сервером)
-- Использовать только во время перехода на с версии 794 и меньше на 795 и выше
ALTER TABLE `accounts` ADD COLUMN `activated` TINYINT(10) UNSIGNED NOT NULL DEFAULT '1';
ALTER TABLE `accounts` ADD COLUMN `lock_expire` INTEGER(11) NOT NULL DEFAULT '604800';
ALTER TABLE `accounts` MODIFY COLUMN `password` VARCHAR(256) DEFAULT '';

CREATE TABLE IF NOT EXISTS `lock` (
	`login` varchar(45) NOT NULL,
	`type` ENUM('HWID','IP') NOT NULL,
	`string` varchar(32)  NOT NULL,
	PRIMARY KEY  (`login`,`string`)
) ENGINE=MyISAM;

CREATE TABLE `referrals` (
	`login` VARCHAR(45) NOT NULL DEFAULT '',
	`server` TINYINT(3) UNSIGNED NOT NULL DEFAULT '0',
	`char` INT(11) UNSIGNED NOT NULL DEFAULT '0',
	`bonus1` VARCHAR(32) NOT NULL DEFAULT '',
	`bonus2` VARCHAR(32) NOT NULL DEFAULT ''
) ENGINE=MyISAM;

CREATE TABLE `banned_ips` (
  `ip` VARCHAR(15) NOT NULL,
  `admin` VARCHAR(45) DEFAULT NULL,
  `expiretime` INT UNSIGNED NOT NULL DEFAULT '0',
  `comments` VARCHAR(255) DEFAULT NULL,
  PRIMARY KEY  (`ip`),
  UNIQUE KEY `ip` (`ip`)
) ENGINE=MyISAM;
