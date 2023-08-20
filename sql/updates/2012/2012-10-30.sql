ALTER TABLE characters CHANGE navitPoints hunt_bonus smallint(5) default '0';
ALTER TABLE characters CHANGE navitBonus hunt_timeleft smallint(5) default '0';
UPDATE characters set `hunt_timeleft` = 0 WHERE hunt_timeleft > 0;