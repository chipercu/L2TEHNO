DROP TABLE IF EXISTS `community_perform_buffs`;
CREATE TABLE `community_perform_buffs` (
  `id` int(10) NOT NULL AUTO_INCREMENT,
  `buff_id` int(10) DEFAULT NULL,
  `buff_level` int(10) DEFAULT NULL,
  `display_level` int(10) DEFAULT NULL,
  `name` varchar(250) DEFAULT '',
  `enchant_name` varchar(250) DEFAULT '',
  `duration` int(10) DEFAULT NULL,
  `price` int(10) DEFAULT '1',
  `price_item` int(10) DEFAULT '57',
  `icon` varchar(250) DEFAULT '',
  `list_type` varchar(25) DEFAULT NULL,
  PRIMARY KEY (`Id`)
) ENGINE=MyISAM AUTO_INCREMENT=0 DEFAULT CHARSET=utf8;
