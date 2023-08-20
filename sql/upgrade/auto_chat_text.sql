DROP TABLE IF EXISTS `auto_chat_text`;
CREATE TABLE `auto_chat_text` (
  `groupId` INTEGER(11) NOT NULL DEFAULT '0',
  `chatText` VARCHAR(255) NOT NULL DEFAULT '',
  PRIMARY KEY (`groupId`, `chatText`)
) ENGINE=MyISAM, DEFAULT CHARSET=utf8;

INSERT INTO `auto_chat_text` VALUES
(1,'%player_cabal_loser%! All is lost! Prepare to meet the goddess of death!'),
(1,'%player_cabal_loser%! You bring an ill wind!'),
(1,'%player_cabal_loser%! You might as well give up!'),
(1,'A curse upon you!'),
(1,'All is lost! Prepare to meet the goddess of death!'),
(1,'All is lost! The prophecy of destruction has been fulfilled!'),
(1,'The prophecy of doom has awoken!'),
(1,'This world will soon be annihilated!'),
(2,'%player_cabal_winner%! I bestow on you the authority of the abyss!'),
(2,'%player_cabal_winner%, Darkness shall be banished forever!'),
(2,'%player_cabal_winner%, the time for glory is at hand!'),
(2,'All hail the eternal twilight!'),
(2,'As foretold in the prophecy of darkness, the era of chaos has begun!'),
(2,'The day of judgment is near!'),
(2,'The prophecy of darkness has been fulfilled!'),
(2,'The prophecy of darkness has come to pass!'),
(3,'Dear %player_random%, may the blessing of Einhasad be with you always.'),
(4,'!Rulers of the seal! I bring you wondrous gifts!'),
(4,'!Rulers of the seal! I have some excellent weapons to show you!'),
(4,'!I''ve been so busy lately, in addition to planning my trip!'),
(5,'!Who will be the lucky one tonight? Ha-ha! Curious, very curious!'),
(6,'!Courage! Ambition! Passion! Mercenaries who want to realize their dream of fighting in the territory war, come tome! Fortune and glory are waiting you!');
