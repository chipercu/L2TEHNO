DROP TABLE IF EXISTS `community_perform_buffs`;
CREATE TABLE `community_perform_buffs` (
  `id` int(10) NOT NULL AUTO_INCREMENT,
  `display_level` int(10) DEFAULT NULL,
  `skill_id` int(10) DEFAULT NULL,
  `skill_level` int(10) DEFAULT NULL,
  `name` varchar(100) DEFAULT '',
  `price` int(10) DEFAULT '1',
  `price_item` int(10) DEFAULT '57',
  `minLevel` int(10) DEFAULT NULL,
  `maxLevel` int(10) DEFAULT NULL,
  `icon` varchar(50) DEFAULT '',
  `type_` varchar(50) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=MyISAM AUTO_INCREMENT=0 DEFAULT CHARSET=utf8;
