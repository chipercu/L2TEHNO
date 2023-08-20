DROP TABLE IF EXISTS `castle`;
CREATE TABLE `castle` (
  `id` tinyint(3) unsigned NOT NULL DEFAULT '0',
  `name` varchar(25) NOT NULL,
  `taxPercent` tinyint(3) unsigned NOT NULL DEFAULT '15',
  `treasury` bigint(20) unsigned NOT NULL DEFAULT '0',
  `siegeDate` int(10) unsigned NOT NULL DEFAULT '0',
  `siegeDayOfWeek` tinyint(3) unsigned NOT NULL DEFAULT '1',
  `siegeHourOfDay` tinyint(3) unsigned NOT NULL DEFAULT '16',
  `townId` tinyint(3) unsigned NOT NULL DEFAULT '0',
  `skills` varchar(32) NOT NULL DEFAULT '0;0',
  `flags` varchar(32) NOT NULL DEFAULT '0;0',
  `ownDate` int(11) NOT NULL DEFAULT '0',
  `dominionLord` int(11) NOT NULL DEFAULT '0',
  `setSiege` tinyint(1) NOT NULL DEFAULT '1',
  PRIMARY KEY (`name`),
  KEY `id` (`id`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

INSERT INTO `castle` VALUES ('1', 'Gludio', '0', '0', '0', '1', '16', '6', '593;1;600;1;606;1', '1', '0', '0', '1');
INSERT INTO `castle` VALUES ('2', 'Dion', '0', '0', '0', '1', '16', '8', '609;1;597;1;591;1', '2', '0', '0', '1');
INSERT INTO `castle` VALUES ('3', 'Giran', '0', '0', '0', '1', '16', '9', '592;1;601;1;610;1', '3', '0', '0', '1');
INSERT INTO `castle` VALUES ('4', 'Oren', '0', '0', '0', '1', '16', '10', '590;1;598;1;605;1', '4', '0', '0', '1');
INSERT INTO `castle` VALUES ('5', 'Aden', '0', '0', '0', '1', '16', '11', '596;1;602;1;608;1', '5', '0', '0', '1');
INSERT INTO `castle` VALUES ('6', 'Innadril', '0', '0', '0', '1', '16', '13', '595;1;599;1;607;1', '6', '0', '0', '1');
INSERT INTO `castle` VALUES ('7', 'Goddard', '0', '0', '0', '1', '16', '15', '590;1;591;1;603;1', '7', '0', '0', '1');
INSERT INTO `castle` VALUES ('8', 'Rune', '0', '0', '0', '1', '16', '14', '593;1;599;1;604;1', '8', '0', '0', '1');
INSERT INTO `castle` VALUES ('9', 'Schuttgart', '0', '0', '0', '1', '16', '16', '610;1;600;1;592;1', '9', '0', '0', '1');
