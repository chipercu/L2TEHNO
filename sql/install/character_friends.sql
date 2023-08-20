CREATE TABLE IF NOT EXISTS `character_friends` (
  `char_id` int NOT NULL default 0,
  `friend_id` int NOT NULL default 0,
  PRIMARY KEY  (`char_id`,`friend_id`)
) ENGINE=MyISAM;