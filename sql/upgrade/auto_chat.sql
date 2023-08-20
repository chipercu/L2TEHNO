DROP TABLE IF EXISTS `auto_chat`;
CREATE TABLE `auto_chat` (
  `groupId` INT NOT NULL default '0',
  `npcId` INT NOT NULL default '0',
  `chatDelay` INT NOT NULL default '-1',
  PRIMARY KEY  (`groupId`, `npcId`)
  ) ENGINE=MyISAM;

INSERT INTO `auto_chat` VALUES
(1,31093,-1),
(2,31094,-1),
(3,22134,-1),
(4,31126,1800),
(5,32347,1800),
(6,36481,600),
(6,36482,600),
(6,36483,600),
(6,36484,600),
(6,36485,600),
(6,36486,600),
(6,36487,600),
(6,36488,600),
(6,36489,600);