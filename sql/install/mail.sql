CREATE TABLE IF NOT EXISTS `mail` (
  `messageId` INT UNSIGNED NOT NULL auto_increment,
  `sender` INT UNSIGNED NOT NULL,
  `receiver` INT UNSIGNED NOT NULL,
  `expire` TIMESTAMP NOT NULL DEFAULT '1980-01-01 00:00:00',
  `topic` VARCHAR(30) CHARACTER SET utf8 NOT NULL,
  `body` TEXT CHARACTER SET utf8 NOT NULL,
  `attachments` TINYINT UNSIGNED NOT NULL DEFAULT 0,
  `needsPayment` TINYINT UNSIGNED NOT NULL DEFAULT 0,
  `price` BIGINT UNSIGNED NOT NULL DEFAULT 0,
  `system` TINYINT UNSIGNED NOT NULL DEFAULT 0,
  `unread` TINYINT UNSIGNED NOT NULL DEFAULT 1,
  `hideSender` TINYINT UNSIGNED NOT NULL DEFAULT 0,
  PRIMARY KEY (`messageId`),
  KEY `sender` (`sender`),
  KEY `receiver` (`receiver`),
  KEY `attachments` (`attachments`)
) ENGINE=MyISAM;

CREATE TABLE IF NOT EXISTS `mail_attachments` (
  `messageId` INT UNSIGNED NOT NULL,
  `itemId` INT UNSIGNED NOT NULL,
  PRIMARY KEY (`messageId`,`itemId`),
  KEY `messageId` (`messageId`)
) ENGINE=MyISAM;