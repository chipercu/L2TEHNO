CREATE TABLE IF NOT EXISTS `couples` (
  `id` int NOT NULL,
  `player1Id` int NOT NULL default 0,
  `player2Id` int NOT NULL default 0,
  `maried` varchar(5) default NULL,
  `affiancedDate` bigint default 0,
  `weddingDate` bigint default 0,
  PRIMARY KEY  (`id`)
) ENGINE=MyISAM;