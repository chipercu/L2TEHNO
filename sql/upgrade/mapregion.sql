DROP TABLE IF EXISTS `mapregion`;
CREATE TABLE `mapregion` (
  `y10_plus` int(11) NOT NULL default '0',
  `x11` int(2) NOT NULL default '0',
  `x12` int(2) NOT NULL default '0',
  `x13` int(2) NOT NULL default '0',
  `x14` int(2) NOT NULL default '0',
  `x15` int(2) NOT NULL default '0',
  `x16` int(2) NOT NULL default '0',
  `x17` int(2) NOT NULL default '0',
  `x18` int(2) NOT NULL default '0',
  `x19` int(2) NOT NULL default '0',
  `x20` int(2) NOT NULL default '0',
  `x21` int(2) NOT NULL default '0',
  `x22` int(2) NOT NULL default '0',
  `x23` int(2) NOT NULL default '0',
  `x24` int(2) NOT NULL default '0',
  `x25` int(2) NOT NULL default '0',
  `x26` int(2) NOT NULL default '0',

  PRIMARY KEY  (`y10_plus`)
) ENGINE=MyISAM;

INSERT INTO `mapregion` VALUES
(0,21,21,21,21,4,4,4,8,10,12,5,5,5,5,5,5),
(1,21,21,21,21,4,4,4,4,4,4,5,5,5,5,5,5),
(2,21,21,21,21,4,4,4,4,4,4,16,16,5,5,5,5),
(3,21,21,21,21,4,4,4,4,4,4,16,16,16,15,15,15),
(4,21,21,21,21,4,4,4,4,4,4,16,16,16,15,15,15),
(5,21,21,21,21,19,19,19,4,4,4,14,14,14,15,15,15),
(6,21,21,21,21,19,19,19,19,14,14,14,14,15,15,15,15),
(7,21,21,21,21,17,17,17,3,3,18,14,10,11,11,11,11),
(8,21,21,21,21,17,17,17,3,3,3,10,10,11,11,11,11),
(9,21,21,21,21,17,17,17,3,3,2,2,10,12,11,11,11),
(10,21,21,21,21,17,17,7,3,6,2,2,10,12,12,12,12),
(11,21,21,21,21,7,7,7,6,6,8,8,12,12,12,12,12),
(12,21,21,21,21,7,7,7,7,6,8,8,9,9,9,9,9),
(13,21,21,21,21,1,1,7,7,6,8,9,13,13,13,13,13),
(14,21,21,21,21,1,1,1,7,7,8,9,13,13,13,13,13),
(15,21,21,21,21,1,1,1,1,20,20,1,13,13,13,13,13),
(16,21,21,21,21,1,1,1,1,22,22,1,1,1,1,1,1);

UPDATE mapregion SET x13='23', x14='23' WHERE y10_plus='13';

-- Столбцы -это координаты квадратов по Х, а строки по Y. Система координат аналогична гео
-- 1 = "Talking Island Village"
-- 2 = "Elven Village"
-- 3 = "Dark Elven Village"
-- 4 = "Orc Village"
-- 5 = "Dwarven Village"
-- 6 = "Town of Gludio"
-- 7 = "Gludin Village"
-- 8 = "Town of Dion"
-- 9 = "Town of Giran"
-- 10 = "Town of Oren"
-- 11 = "Town of Aden"
-- 12 = "Hunters Village"
-- 13 = "Heine"
-- 14 = "Rune Township"
-- 15 = "Town of Goddard"
-- 16 = "Town of Schuttgart"
-- 17 = "Kamael Village"
-- 18 = "Primeval Isle"
-- 19 = "Fantasy Isle"
-- 20 = "Hellbound"
-- 21 = "Keucereus Alliance Base"
-- 22 = "Steel Citadel"
