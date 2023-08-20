DROP TABLE IF EXISTS `character_mail`;
CREATE TABLE `character_mail` (
  `obj_id` INT(10) NOT NULL DEFAULT '0',
  `letterId` INT(11) NOT NULL AUTO_INCREMENT,
  `senderId` INT(10) DEFAULT NULL,
  `location` VARCHAR(45) DEFAULT NULL,
  `recipientNames` VARCHAR(45) NOT NULL,
  `subject` TEXT NOT NULL,
  `message` TEXT NOT NULL,
  `sendDate` DECIMAL(20,0) DEFAULT NULL,
  `deleteDate` DECIMAL(20,0) DEFAULT NULL,
  `unread` VARCHAR(10) DEFAULT NULL,
  PRIMARY KEY (`letterId`)
) DEFAULT CHARSET=utf8;