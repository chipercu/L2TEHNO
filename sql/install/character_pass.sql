DROP TABLE IF EXISTS `character_pass`;
CREATE TABLE `character_pass` (
  `login` VARCHAR(45) NOT NULL DEFAULT '',
  `obj_id` INT(10) NOT NULL DEFAULT '0',
  `question` VARCHAR(45) NOT NULL DEFAULT '',
  `answer` VARCHAR(45) NOT NULL DEFAULT '',
  `password` VARCHAR(45) NOT NULL DEFAULT '',
  PRIMARY KEY (`login`, `obj_id`)
) DEFAULT CHARSET=utf8;