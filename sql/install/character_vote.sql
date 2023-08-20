DROP TABLE IF EXISTS `character_vote`;
CREATE TABLE `character_vote` (
  `type` int(1) NOT NULL DEFAULT '0',
  `vote_id` int(11) NOT NULL DEFAULT '0',
  `date` bigint(14) NOT NULL DEFAULT '0',
  `id` int(10) NOT NULL DEFAULT '0',
  `nick` varchar(255) NOT NULL DEFAULT '''''',
  `multipler` int(9) NOT NULL DEFAULT '0',
  `has_reward` int(1) NOT NULL DEFAULT '0'
) ENGINE=MyISAM DEFAULT CHARSET=utf8;
