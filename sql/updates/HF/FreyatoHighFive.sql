ALTER TABLE `olympiad_nobles` ADD COLUMN `noneclass_competitions` smallint(6) unsigned NOT NULL DEFAULT '0' AFTER `competitions_loose`;
ALTER TABLE `olympiad_nobles` ADD COLUMN `class_competitions` smallint(6) unsigned NOT NULL DEFAULT '0' AFTER `noneclass_competitions`;
ALTER TABLE `olympiad_nobles` ADD COLUMN `team_competitions` smallint(6) unsigned NOT NULL DEFAULT '0' AFTER `class_competitions`;

-- OlympiadStadium_1 - OlympiadStadium_4
INSERT INTO `doors` VALUES ('17100001', 'Door.OlympiadStadium_1', '158250', '644', '518', '0', '0', '1', '0', '-89120', '-252843', '-3341', '-89135', '-252911', '-89106', '-252912', '-89105', '-252774', '-89135', '-252774', '-3344', '-3331', 'false', 'true');
INSERT INTO `doors` VALUES ('17100002', 'Door.OlympiadStadium_1', '158250', '644', '518', '0', '0', '1', '0', '-87043', '-252843', '-3341', '-87058', '-252911', '-87029', '-252912', '-87028', '-252774', '-87058', '-252774', '-3344', '-3331', 'false', 'true');
INSERT INTO `doors` VALUES ('17100101', 'Door.OlympiadStadium_2', '158250', '644', '518', '0', '0', '1', '0', '-76480', '-252461', '-7741', '-76495', '-252529', '-76466', '-252530', '-76465', '-252392', '-76495', '-252392', '-7744', '-7731', 'false', 'true');
INSERT INTO `doors` VALUES ('17100102', 'Door.OlympiadStadium_2', '158250', '644', '518', '0', '0', '1', '0', '-74450', '-252459', '-7741', '-74465', '-252527', '-74436', '-252528', '-74435', '-252390', '-74465', '-252390', '-7744', '-7731', 'false', 'true');
INSERT INTO `doors` VALUES ('17100201', 'Door.OlympiadStadium_3', '158250', '644', '518', '0', '0', '1', '0', '-88912', '-239351', '-8475', '-88931', '-239437', '-88895', '-239437', '-88899', '-239264', '-88931', '-239265', '-8478', '-8465', 'false', 'true');
INSERT INTO `doors` VALUES ('17100202', 'Door.OlympiadStadium_3', '158250', '644', '518', '0', '0', '1', '0', '-87278', '-239351', '-8475', '-87297', '-239437', '-87261', '-239437', '-87265', '-239264', '-87297', '-239265', '-8478', '-8465', 'false', 'true');
INSERT INTO `doors` VALUES ('17100301', 'Door.OlympiadStadium_4', '158250', '644', '518', '0', '0', '1', '0', '-76375', '-239182', '-8218', '-76384', '-239251', '-76365', '-239250', '-76367', '-239111', '-76385', '-239110', '-8221', '-8208', 'false', 'true');
INSERT INTO `doors` VALUES ('17100302', 'Door.OlympiadStadium_4', '158250', '644', '518', '0', '0', '1', '0', '-74538', '-239182', '-8218', '-74547', '-239251', '-74528', '-239250', '-74530', '-239111', '-74548', '-239110', '-8221', '-8208', 'false', 'true');

CREATE TABLE IF NOT EXISTS `character_post_friends`(
  `object_id` INT(11) NOT NULL,
  `post_friend` INT(11) NOT NULL,
  PRIMARY KEY (`object_id`,`post_friend`)
);

ALTER TABLE `characters` ADD COLUMN `navitPoints` smallint(5) default '0' AFTER `bookmarks`;
ALTER TABLE `characters` ADD COLUMN `navitBonus` smallint(5) default '0' AFTER `navitPoints`;

UPDATE character_subclasses set `exp` = '474205751' WHERE level = '71';
UPDATE character_subclasses set `exp` = '532692055' WHERE level = '72';
UPDATE character_subclasses set `exp` = '606319094' WHERE level = '73';
UPDATE character_subclasses set `exp` = '696376867' WHERE level = '74';
UPDATE character_subclasses set `exp` = '804219972' WHERE level = '75';
UPDATE character_subclasses set `exp` = '931275828' WHERE level = '76';
UPDATE character_subclasses set `exp` = '1151275834' WHERE level = '77';
UPDATE character_subclasses set `exp` = '1511275834' WHERE level = '78';
UPDATE character_subclasses set `exp` = '2044287599' WHERE level = '79';
UPDATE character_subclasses set `exp` = '3075966164' WHERE level = '80';
UPDATE character_subclasses set `exp` = '4295351949' WHERE level = '81';
UPDATE character_subclasses set `exp` = '5766985062' WHERE level = '82';
UPDATE character_subclasses set `exp` = '7793077345' WHERE level = '83';
UPDATE character_subclasses set `exp` = '10235368963' WHERE level = '84';
UPDATE character_subclasses set `exp` = '13180481103' WHERE level = '85';







