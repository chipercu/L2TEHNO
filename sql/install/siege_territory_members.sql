CREATE TABLE IF NOT EXISTS `siege_territory_members` (
  `obj_Id` int(11) NOT NULL default '0',
  `side` int(11) NOT NULL default '0',
  `type` int(11) NOT NULL default '0',
  PRIMARY KEY  (`obj_Id`)
) ENGINE=MyISAM;