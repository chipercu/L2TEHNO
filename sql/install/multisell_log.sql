CREATE TABLE `multisell_log` (
  `id` int(11) unsigned NOT NULL DEFAULT '0',
  `date` varchar(2048) NOT NULL DEFAULT '',
  `itemId` varchar(2048) NOT NULL DEFAULT '0',
  `count` varchar(2048) NOT NULL DEFAULT '0',
  `dItemId` varchar(2048) NOT NULL DEFAULT '0',
  `dCount` varchar(2048) NOT NULL DEFAULT '0',
  `name` varchar(16) NOT NULL DEFAULT ''
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

