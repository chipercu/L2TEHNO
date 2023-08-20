update npc set type='L2Boss', ai_type='AntQueen.ai_boss01_queen_ant' where id='29001';
update npc set type='L2Minion', ai_type='AntQueen.ai_boss01_queen_ant_larva' where id='29002';
update npc set type='L2Minion', ai_type='AntQueen.ai_boss01_nurse_ant' where id='29003';
update npc set type='L2SpecialMonster', ai_type='AntQueen.ai_boss01_guard_ant' where id='29004';
update npc set type='L2Minion', ai_type='AntQueen.ai_boss01_royal_guard_ant' where id='29005';
update npc set undying='1' where id='29002';

INSERT INTO `locations` VALUES ('1385011', 'queenant_room', '-21490', '178613', '-5947', '-5547', '0');
INSERT INTO `locations` VALUES ('1385011', 'queenant_room', '-20762', '179129', '-5947', '-5547', '0');
INSERT INTO `locations` VALUES ('1385011', 'queenant_room', '-20310', '182673', '-5947', '-5547', '0');
INSERT INTO `locations` VALUES ('1385011', 'queenant_room', '-20570', '183381', '-5947', '-5547', '0');
INSERT INTO `locations` VALUES ('1385011', 'queenant_room', '-21594', '183945', '-5947', '-5547', '0');
INSERT INTO `locations` VALUES ('1385011', 'queenant_room', '-22770', '183129', '-5947', '-5547', '0');
INSERT INTO `locations` VALUES ('1385011', 'queenant_room', '-22890', '182205', '-5947', '-5547', '0');
INSERT INTO `locations` VALUES ('1385011', 'queenant_room', '-22258', '178961', '-5947', '-5547', '0');
INSERT INTO `locations` VALUES ('1385111', 'new_queen_ant_guard_room', '-20873', '179313', '-5866', '-5666', '0');
INSERT INTO `locations` VALUES ('1385111', 'new_queen_ant_guard_room', '-21102', '179511', '-5866', '-5666', '0');
INSERT INTO `locations` VALUES ('1385111', 'new_queen_ant_guard_room', '-21154', '179816', '-5866', '-5666', '0');
INSERT INTO `locations` VALUES ('1385111', 'new_queen_ant_guard_room', '-21502', '180054', '-5866', '-5666', '0');
INSERT INTO `locations` VALUES ('1385111', 'new_queen_ant_guard_room', '-21828', '179930', '-5866', '-5666', '0');
INSERT INTO `locations` VALUES ('1385111', 'new_queen_ant_guard_room', '-22015', '179449', '-5866', '-5666', '0');
INSERT INTO `locations` VALUES ('1385111', 'new_queen_ant_guard_room', '-22187', '179239', '-5866', '-5666', '0');
INSERT INTO `locations` VALUES ('1385111', 'new_queen_ant_guard_room', '-22069', '178967', '-5866', '-5666', '0');
INSERT INTO `locations` VALUES ('1385111', 'new_queen_ant_guard_room', '-21852', '179031', '-5866', '-5666', '0');
INSERT INTO `locations` VALUES ('1385111', 'new_queen_ant_guard_room', '-21766', '178964', '-5866', '-5666', '0');
INSERT INTO `locations` VALUES ('1385111', 'new_queen_ant_guard_room', '-21710', '178743', '-5866', '-5666', '0');
INSERT INTO `locations` VALUES ('1385111', 'new_queen_ant_guard_room', '-21509', '178713', '-5866', '-5666', '0');
INSERT INTO `locations` VALUES ('1385111', 'new_queen_ant_guard_room', '-21293', '178832', '-5866', '-5666', '0');
INSERT INTO `locations` VALUES ('1385111', 'new_queen_ant_guard_room', '-21381', '179118', '-5866', '-5666', '0');
INSERT INTO `locations` VALUES ('1385111', 'new_queen_ant_guard_room', '-21247', '179137', '-5866', '-5666', '0');
INSERT INTO `locations` VALUES ('1385211', 'queen_ant_guard_room', '-21490', '178613', '-5947', '-5547', '0');
INSERT INTO `locations` VALUES ('1385211', 'queen_ant_guard_room', '-20762', '179129', '-5947', '-5547', '0');
INSERT INTO `locations` VALUES ('1385211', 'queen_ant_guard_room', '-20310', '182673', '-5947', '-5547', '0');
INSERT INTO `locations` VALUES ('1385211', 'queen_ant_guard_room', '-20570', '183381', '-5947', '-5547', '0');
INSERT INTO `locations` VALUES ('1385211', 'queen_ant_guard_room', '-21594', '183945', '-5947', '-5547', '0');
INSERT INTO `locations` VALUES ('1385211', 'queen_ant_guard_room', '-22770', '183129', '-5947', '-5547', '0');
INSERT INTO `locations` VALUES ('1385211', 'queen_ant_guard_room', '-22890', '182205', '-5947', '-5547', '0');
INSERT INTO `locations` VALUES ('1385211', 'queen_ant_guard_room', '-22258', '178961', '-5947', '-5547', '0');

delete from spawnlist where location='queenant_room';
delete from spawnlist where location='queen_ant_guard_room';

INSERT INTO `spawnlist` (`location`,`count`,`npc_templateid`,`locx`,`locy`,`locz`,`heading`,`respawn_delay`,`respawn_delay_rnd`,`loc_id`,`periodOfDay`,`reflection`,`statParam`,`aiParam`) VALUES
('queenant_room', '1', '29001', '-21610', '181594', '-5734', '0', '129600', '61200', '0', '0', '0', '-1', 'loc_id=1385011'),
('new_queen_ant_guard_room','1','29004','-22005','179205','-5846','0','360','20','0','0','0','-1','loc_id=1385111'),
('new_queen_ant_guard_room','1','29004','-21088','179338','-5846','0','360','20','0','0','0','-1','loc_id=1385111'),
('new_queen_ant_guard_room','1','29004','-21516','178956','-5846','0','360','20','0','0','0','-1','loc_id=1385111'),
('new_queen_ant_guard_room','1','29004','-21945','179506','-5846','0','360','20','0','0','0','-1','loc_id=1385111'),
('new_queen_ant_guard_room','1','29004','-21328','179248','-5846','0','360','20','0','0','0','-1','loc_id=1385111'),
('new_queen_ant_guard_room','1','29004','-21248','179696','-5846','0','360','20','0','0','0','-1','loc_id=1385111'),
('new_queen_ant_guard_room','1','29004','-21760','179088','-5846','0','360','20','0','0','0','-1','loc_id=1385111'),
('queen_ant_guard_room','1','29004','-22154','181958','-5734','0','360','20','0','0','0','-1','loc_id=1385211'),
('queen_ant_guard_room','1','29004','-21672','181903','-5734','0','360','20','0','0','0','-1','loc_id=1385211'),
('queen_ant_guard_room','1','29004','-20992','181627','-5734','0','360','20','0','0','0','-1','loc_id=1385211'),
('queen_ant_guard_room','1','29004','-21861','182279','-5734','0','360','20','0','0','0','-1','loc_id=1385211'),
('queen_ant_guard_room','1','29004','-21693','182717','-5734','0','360','20','0','0','0','-1','loc_id=1385211'),
('queen_ant_guard_room','1','29004','-22070','182890','-5734','0','360','20','0','0','0','-1','loc_id=1385211'),
('queen_ant_guard_room','1','29004','-22262','183156','-5734','0','360','20','0','0','0','-1','loc_id=1385211'),
('queen_ant_guard_room','1','29004','-21308','182278','-5734','0','360','20','0','0','0','-1','loc_id=1385211'),
('queen_ant_guard_room','1','29004','-21113','182681','-5734','0','360','20','0','0','0','-1','loc_id=1385211'),
('queen_ant_guard_room','1','29004','-21446','183135','-5734','0','360','20','0','0','0','-1','loc_id=1385211'),
('queen_ant_guard_room','1','29004','-21854','183450','-5734','0','360','20','0','0','0','-1','loc_id=1385211'),
('queen_ant_guard_room','1','29004','-20714','182103','-5734','0','360','20','0','0','0','-1','loc_id=1385211'),
('queen_ant_guard_room','1','29004','-20720','182775','-5734','0','360','20','0','0','0','-1','loc_id=1385211'),
('queen_ant_guard_room','1','29004','-20949','183233','-5734','0','360','20','0','0','0','-1','loc_id=1385211'),
('queen_ant_guard_room','1','29004','-22349','182333','-5734','0','360','20','0','0','0','-1','loc_id=1385211'),
('queen_ant_guard_room','1','29004','-22406','182733','-5734','0','360','20','0','0','0','-1','loc_id=1385211'),
('queen_ant_guard_room','1','29004','-22700','182486','-5734','0','360','20','0','0','0','-1','loc_id=1385211'),
('queen_ant_guard_room','1','29004','-22015','181518','-5734','0','360','20','0','0','0','-1','loc_id=1385211');

delete from minions where minion_id='29003';
delete from minions where minion_id='29005';





