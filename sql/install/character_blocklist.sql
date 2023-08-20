CREATE TABLE IF NOT EXISTS `character_blocklist` (
  `obj_Id` int NOT NULL,
  `target_Id` int NOT NULL,
  PRIMARY KEY  (`obj_Id`,`target_Id`)
) ENGINE=MyISAM;