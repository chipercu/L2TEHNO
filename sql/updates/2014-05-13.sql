CREATE TABLE IF NOT EXISTS hwid_lock
(
	login varchar(45) NOT NULL,
	hwid text  NOT NULL,
	PRIMARY KEY(login)
)
ENGINE=MyISAM DEFAULT CHARSET=utf8;

INSERT INTO `hwid_lock` (login, hwid) SELECT account_name, value FROM character_variables AS cv LEFT JOIN characters AS c ON (cv.obj_id=c.obj_Id) WHERE cv.name='AccHWIDLock' GROUP BY c.account_name;
