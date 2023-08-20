CREATE TABLE IF NOT EXISTS hwid_lock
(
	login varchar(45) NOT NULL,
	hwid text  NOT NULL,
	PRIMARY KEY(login)
)
ENGINE=MyISAM DEFAULT CHARSET=utf8;
