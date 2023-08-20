DROP TABLE IF EXISTS `bbs_lottery`;
CREATE TABLE `bbs_lottery` (
	`count` bigint(20) NOT NULL DEFAULT '0',
	`type` VARCHAR(86) NOT NULL DEFAULT '0',
	`name` VARCHAR(86) CHARACTER SET UTF8 NOT NULL DEFAULT '0'
);

INSERT INTO bbs_lottery VALUES ('0', 'simple_total_games', '0');
INSERT INTO bbs_lottery VALUES ('0', 'simple_jackpot', '0');
INSERT INTO bbs_lottery VALUES ('0', 'premium_total_games', '0');
INSERT INTO bbs_lottery VALUES ('0', 'premium_jackpot', '0');
INSERT INTO bbs_lottery VALUES ('0', 'simple_wins', '0');
INSERT INTO bbs_lottery VALUES ('0', 'simple_win_count', '0');
INSERT INTO bbs_lottery VALUES ('0', 'simple_loss', '0');
INSERT INTO bbs_lottery VALUES ('0', 'simple_looss_count', '0');
INSERT INTO bbs_lottery VALUES ('0', 'simple_jackpot_win', '0');
INSERT INTO bbs_lottery VALUES ('0', 'simple_jackpot_count', '0');
INSERT INTO bbs_lottery VALUES ('0', 'premium_wins', '0');
INSERT INTO bbs_lottery VALUES ('0', 'premium_win_count', '0');
INSERT INTO bbs_lottery VALUES ('0', 'premium_loss', '0');
INSERT INTO bbs_lottery VALUES ('0', 'premium_looss_count', '0');
INSERT INTO bbs_lottery VALUES ('0', 'premium_jackpot_win', '0');
INSERT INTO bbs_lottery VALUES ('0', 'premium_jackpot_count', '0');
