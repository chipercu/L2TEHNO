DROP TABLE IF EXISTS `community_perform_buffs`;
CREATE TABLE `community_perform_buffs` (
  `id` int(10) NOT NULL,
  `display_level` int(10) DEFAULT NULL,
  `name` varchar(100) DEFAULT '',
  `duration` int(10) DEFAULT NULL,
  `price` int(10) DEFAULT '1',
  `price_item` int(10) DEFAULT '57',
  `minLevel` int(10) DEFAULT NULL,
  `maxLevel` int(10) DEFAULT NULL,
  `icon` varchar(50) DEFAULT '',
  `type_` varchar(50) DEFAULT NULL
) ENGINE=MyISAM DEFAULT CHARSET=utf8;
