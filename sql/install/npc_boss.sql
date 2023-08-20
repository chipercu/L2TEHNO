CREATE TABLE `npc_boss`
(
	`npc_db_name` varchar(50) NOT NULL,
	`alive` smallint(1) default 1,
	`hp` int(10) NOT NULL,
	`mp` int(10) NOT NULL,
	`pos_x` int(6) NOT NULL,
	`pos_y` int(6) NOT NULL,
	`pos_z` int(6) NOT NULL,
	`time_low` bigint(14) NOT NULL,
	`time_high` bigint(14) NOT NULL,
	`i0` int(11) NOT NULL,
	PRIMARY KEY (`npc_db_name`)
);

