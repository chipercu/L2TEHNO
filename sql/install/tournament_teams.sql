CREATE TABLE `tournament_teams` (
  `obj_id` int NOT NULL default 0,
  `type` int default NULL,
  `team_id` int NOT NULL,
  `team_name` varchar(255) character set utf8 default NULL,
  `leader` int default NULL,
  `category` int default NULL,
  `wins` int NOT NULL default 0,
  `losts` int NOT NULL default 0,
  `status` int NOT NULL default 0
) ENGINE=MyISAM;