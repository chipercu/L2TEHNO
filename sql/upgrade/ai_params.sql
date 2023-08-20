DROP TABLE IF EXISTS `ai_params`;
CREATE TABLE `ai_params` (
  `npc_id` int(11) unsigned NOT NULL DEFAULT '0',
  `name` varchar(45) DEFAULT NULL,
  `param` varchar(25) NOT NULL DEFAULT '',
  `value` varchar(255) NOT NULL DEFAULT '',
  PRIMARY KEY (`npc_id`,`param`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

INSERT INTO `ai_params` (`npc_id`, `name`, `param`, `value`) VALUES
	('36499','Territory Catapult','noRandomWalk','true'),
	('36500','Territory Catapult','noRandomWalk','true'),
	('36501','Territory Catapult','noRandomWalk','true'),
	('36502','Territory Catapult','noRandomWalk','true'),
	('36503','Territory Catapult','noRandomWalk','true'),
	('36504','Territory Catapult','noRandomWalk','true'),
	('36505','Territory Catapult','noRandomWalk','true'),
	('36506','Territory Catapult','noRandomWalk','true'),
	('36507','Territory Catapult','noRandomWalk','true'),
	('36591','Supplies Safe','noRandomWalk','true'),
	('36592','Supplies Safe','noRandomWalk','true'),
	('36593','Supplies Safe','noRandomWalk','true'),
	('36594','Supplies Safe','noRandomWalk','true'),
	('36595','Supplies Safe','noRandomWalk','true'),
	('36596','Supplies Safe','noRandomWalk','true'),
	('36597','Supplies Safe','noRandomWalk','true'),
	('36598','Supplies Safe','noRandomWalk','true'),
	('36599','Supplies Safe','noRandomWalk','true'),	
	('22199','Pterosaur','canSeeInSilentMove','true'),
	('22215','Tiranosaurus','canSeeInSilentMove','true'),
	('22216','Tiranosaurus','canSeeInSilentMove','true'),
	('22217','Tiranosaurus','canSeeInSilentMove','true'),
	('32350','Sandstorm','canSeeInSilentMove','true'),
	('22137','Penance Guard','canSeeInSilentMove','true'),
	('22138','Chapel Guard','canSeeInSilentMove','true'),
	('22194','Penance Guard','canSeeInSilentMove','true'),
	('22327','Arcane Scout','canSeeInSilentMove','true'),
	('22360','Town Patrolman','canSeeInSilentMove','true'),
	('18329','Hall Keeper Captain','canSeeInSilentMove','true'),
	('18330','Hall Keeper Wizard','canSeeInSilentMove','true'),
	('18331','Hall Keeper Guard','canSeeInSilentMove','true'),
	('18332','Hall Keeper Patrol','canSeeInSilentMove','true'),
	('18333','Hall Keeper Suicidal Soldier','canSeeInSilentMove','true'),
	('18334','Dark Choir Captain','canSeeInSilentMove','true'),
	('18335','Dark Choir Prima Donna','canSeeInSilentMove','true'),
	('18336','Dark Choir Lancer','canSeeInSilentMove','true'),
	('18337','Dark Choir Archer','canSeeInSilentMove','true'),
	('18338','Dark Choir Witch Doctor','canSeeInSilentMove','true'),
	('18339','Dark Choir Player','canSeeInSilentMove','true'),
	('18340','Hall Keeper Suicidal Soldier','canSeeInSilentMove','true'),
	('18341','Hall Keeper Suicidal Soldier','canSeeInSilentMove','true'),
	('22536','Royal Guard Captain','canSeeInSilentMove','true'),
	('22537','Dragon Steed Troop Grand Magician','canSeeInSilentMove','true'),
	('22538','Dragon Steed Troop Commander','canSeeInSilentMove','true'),
	('22539','Dragon Steed Troops No 1 Battalion Commander','canSeeInSilentMove','true'),
	('22540','White Dragon Leader','canSeeInSilentMove','true'),
	('22541','Dragon Steed Troop Infantry','canSeeInSilentMove','true'),
	('22542','Dragon Steed Troop Magic Leader','canSeeInSilentMove','true'),
	('22543','Dragon Steed Troop Magician','canSeeInSilentMove','true'),
	('22544','Dragon Steed Troop Magic Soldier','canSeeInSilentMove','true'),
	('22546','Warrior of Light','canSeeInSilentMove','true'),
	('22547','Dragon Steed Troop Healer','canSeeInSilentMove','true'),
	('22548','Dragon Steed Troop Javelin Thrower','canSeeInSilentMove','true'),
	('22549','Dragon Steed Troop Javelin Thrower','canSeeInSilentMove','true'),
	('22596','White Dragon Leader','canSeeInSilentMove','true'),
	('29162','Soldiers of Bravery','canSeeInSilentMove','true'),
	('22708', 'Invader Warrior of Nightmare','canSeeInSilentMove','true'),
	('22709', 'Invader Healer of Nightmare','canSeeInSilentMove','true'),
	('22710', 'Invader Guide of Nightmare','canSeeInSilentMove','true'),
	('22711', 'Invader Destroyer of Nightmare','canSeeInSilentMove','true'),
	('22712', 'Invader Assassin of Nightmare','canSeeInSilentMove','true'),
	('22713', 'Invader Shaman of Nightmare','canSeeInSilentMove','true'),
	('22714', 'Invader Archer of Nightmare','canSeeInSilentMove','true'),
	('22715', 'Invader Soldier of Nightmare','canSeeInSilentMove','true'),
	('22716', 'Invader Soldier of Nightmare','canSeeInSilentMove','true'),
	('22717', 'Invader Disciple of Nightmare','canSeeInSilentMove','true'),
	('22718', 'Invader Elite Soldier of Nightmare','canSeeInSilentMove','true'),
	('22719', 'Nihil Invader Warrior','canSeeInSilentMove','true'),
	('22720', 'Nihil Invader Healer','canSeeInSilentMove','true'),
	('22721', 'Nihil Invader Guide','canSeeInSilentMove','true'),
	('22722', 'Nihil Invader Destroyer','canSeeInSilentMove','true'),
	('22723', 'Nihil Invader Assassin','canSeeInSilentMove','true'),
	('22724', 'Nihil Invader Shaman','canSeeInSilentMove','true'),
	('22725', 'Nihil Invader Archer','canSeeInSilentMove','true'),
	('22726', 'Nihil Invader Soldier','canSeeInSilentMove','true'),
	('22727', 'Nihil Invader Soldier','canSeeInSilentMove','true'),
	('22728', 'Nihil Invader Disciple','canSeeInSilentMove','true'),
	('22729', 'Nihil Invader Elite Soldier','canSeeInSilentMove','true'),
	('22730', 'Mutant Warrior','canSeeInSilentMove','true'),
	('22731', 'Mutant Healer','canSeeInSilentMove','true'),
	('22732', 'Mutant Guide','canSeeInSilentMove','true'),
	('22733', 'Mutant Destroyer','canSeeInSilentMove','true'),
	('22734', 'Mutant Assassin','canSeeInSilentMove','true'),
	('22735', 'Mutant Shaman','canSeeInSilentMove','true'),
	('22736', 'Mutant Overlord','canSeeInSilentMove','true'),
	('22737', 'Mutant Soldier','canSeeInSilentMove','true'),
	('22738', 'Mutant Soldier','canSeeInSilentMove','true'),
	('22739', 'Mutant Disciple','canSeeInSilentMove','true'),
	('22740', 'Mutant Elite Soldier','canSeeInSilentMove','true'),

	('29001','Queen Ant','MaxPursueRange','2500'),      
	('29004','Guard Ant','MaxPursueRange','3000'),      
	('29005','Royal Guard Ant','MaxPursueRange','3000'),
	('29014','Orfen','MaxPursueRange','5000'),
	('29019','Antharas','MaxPursueRange','30000'),
	('29020','Baium','MaxPursueRange','20000'),
	('29022','Zaken','MaxPursueRange','20000'),
	('29028','Valakas','MaxPursueRange','50000'),
	('29065','Sailren','MaxPursueRange','30000'),
	('29066','Antharas','MaxPursueRange','30000'),
	('29067','Antharas','MaxPursueRange','30000'),
	('29068','Antharas','MaxPursueRange','30000'),
	('29046','Scarlet van Halisha','MaxPursueRange','30000'),
	('29047','Scarlet van Halisha','MaxPursueRange','30000'),
	('29048','Evil Spirit','MaxPursueRange','30000'),
	('29049','Evil Spirit','MaxPursueRange','30000'),
	('29050','Breath of Halisha','MaxPursueRange','30000'),
	('29051','Breath of Halisha','MaxPursueRange','30000'),
	('29095','Gordon','MaxPursueRange','30000'),
	('29069','Behemoth Dragon','MaxPursueRange','10000'),
	('29070','Dragon Bomber','MaxPursueRange','10000'),
	('29071','Dragon Bomber','MaxPursueRange','10000'),
	('29072','Dragon Bomber','MaxPursueRange','10000'),
	('29073','Dragon Bomber','MaxPursueRange','10000'),
	('29074','Dragon Bomber','MaxPursueRange','10000'),
	('29075','Dragon Bomber','MaxPursueRange','10000'),
	('29076','Dragon Bomber','MaxPursueRange','10000'),
	('25716','Tears','MaxPursueRange','1000'),
	('25714','Kechi','MaxPursueRange','1000'),
	('25713','Darnel','MaxPursueRange','1000'),
	('18339','Dark Choir Player','MaxPursueRange','500'),
	('29162','Soldiers of Bravery','MaxPursueRange','3000'),
	('29163','Tiat','MaxPursueRange','3000'),
	('25665','Yehan Klodekus','MaxPursueRange','1500'),
	('25666','Yehan Klanikus','MaxPursueRange','1500'),

	('29002','Queen Ant Larva','ParalizeOnAttack','10'),
	('29003','Nurse Ant','ParalizeOnAttack','13'),
	('29004','Guard Ant','ParalizeOnAttack','11'),
	('29005','Royal Guard Ant','ParalizeOnAttack','9'),
	('29015','Raikel','ParalizeOnAttack','10'),
	('29017','Riba','ParalizeOnAttack','10'),
	('29018','Riba Iren','ParalizeOnAttack','9'),

	('29014','Orfen','FactionNotifyInterval','1000'),
	('29015','Raikel','FactionNotifyInterval','1000'),
	('29016','Raikel Leos','FactionNotifyInterval','1000'),
	('29017','Riba','FactionNotifyInterval','1000'),
	('29018','Riba Iren','FactionNotifyInterval','1000'),

	('22775','Sel Mahum Drill Sergeant','noRandomWalk','true'),
	('22776','Sel Mahum Training Officer','noRandomWalk','true'),	
	('22777','Sel Mahum Drill Sergeant','noRandomWalk','true'),
	('22778','Sel Mahum Drill Sergeant','noRandomWalk','true'),
	('22780','Sel Mahum Recruit','noRandomWalk','true'),	
	('22782','Sel Mahum Recruit','noRandomWalk','true'),
	('22783','Sel Mahum Soldier','noRandomWalk','true'),	
	('22784','Sel Mahum Recruit','noRandomWalk','true'),
	('22785','Sel Mahum Soldier','noRandomWalk','true'),	
	('22142','Triols Layperson','noRandomWalk','true'),
	('22143','Triols Believer','noRandomWalk','true'),
	('18344','Ancient Egg','noRandomWalk','true'),
	('18345','Sprigant','noRandomWalk','true'),
	('18346','Sprigant','noRandomWalk','true'),
	('29006','Core','noRandomWalk','true'),
	('18371','Beleth Sample','noRandomWalk','true'),
	('18372','Beleth Sample','noRandomWalk','true'),
	('18373','Beleth Sample','noRandomWalk','true'),
	('18374','Beleth Sample','noRandomWalk','true'),
	('18375','Beleth Sample','noRandomWalk','true'),
	('18376','Beleth Sample','noRandomWalk','true'),
	('18377','Beleth Sample','noRandomWalk','true'),
	('29100','Crystal Prison Guard','noRandomWalk','true'),
	('18367','Prison Guard','noRandomWalk','true'),
	('18368','Prison Guard','noRandomWalk','true'),
	('22485','Lost Watcher','noRandomWalk','true'),
	('22486','Lost Watcher','noRandomWalk','true'),
	('22487','Lost Beholder','noRandomWalk','true'),
	('22488','Lost Watcher','noRandomWalk','true'),
	('22489','Lost Watcher','noRandomWalk','true'),
	('22490','Lost Beholder','noRandomWalk','true'),
	('22491','Lost Watcher','noRandomWalk','true'),
	('22492','Lost Watcher','noRandomWalk','true'),
	('22493','Lost Beholder','noRandomWalk','true'),
	('22494','Lost Watcher','noRandomWalk','true'),
	('22495','Lost Watcher','noRandomWalk','true'),
	('22496','Lost Beholder','noRandomWalk','true'),
	('22497','Lost Watcher','noRandomWalk','true'),
	('22498','Lost Watcher','noRandomWalk','true'),
	('22499','Lost Beholder','noRandomWalk','true'),
	('22500','Lost Watcher','noRandomWalk','true'),
	('22501','Lost Watcher','noRandomWalk','true'),
	('22502','Lost Beholder','noRandomWalk','true'),
	('22503','Lost Watcher','noRandomWalk','true'),
	('22504','Lost Watcher','noRandomWalk','true'),
	('22505','Lost Beholder','noRandomWalk','true'),
	('25616','Lost Warden','noRandomWalk','true'),
	('25617','Lost Warden','noRandomWalk','true'),
	('25618','Lost Warden','noRandomWalk','true'),
	('25619','Lost Warden','noRandomWalk','true'),
	('25620','Lost Warden','noRandomWalk','true'),
	('25621','Lost Warden','noRandomWalk','true'),
	('25622','Lost Warden','noRandomWalk','true'),
	('22309','Kechis Guard','noRandomWalk','true'),
	('22310','Kechis Guard','noRandomWalk','true'),
	('22417','Kechis Guard','noRandomWalk','true'),
	('32492','Crystal Water Bottle','noRandomWalk','true'),
	('32493','Burning Brazier','noRandomWalk','true'),
	('22536','Royal Guard Captain','noRandomWalk','true'),
	('22537','Dragon Steed Troop Grand Magician','noRandomWalk','true'),
	('22538','Dragon Steed Troop Commander','noRandomWalk','true'),
	('22539','Dragon Steed Troops No 1 Battalion Commander','noRandomWalk','true'),
	('22540','White Dragon Leader','noRandomWalk','true'),
	('22541','Dragon Steed Troop Infantry','noRandomWalk','true'),
	('22542','Dragon Steed Troop Magic Leader','noRandomWalk','true'),
	('22543','Dragon Steed Troop Magician','noRandomWalk','true'),
	('22544','Dragon Steed Troop Magic Soldier','noRandomWalk','true'),
	('22546','Warrior of Light','noRandomWalk','true'),
	('22547','Dragon Steed Troop Healer','noRandomWalk','true'),
	('22548','Dragon Steed Troop Javelin Thrower','noRandomWalk','true'),
	('22549','Dragon Steed Troop Javelin Thrower','noRandomWalk','true'),
	('22596','White Dragon Leader','noRandomWalk','true'),
	('29162','Soldiers of Bravery','noRandomWalk','true'),
	('18635','Varka Silenos Recruit','noRandomWalk','true'),
	('18636','Varka Silenos Footman','noRandomWalk','true'),
	('18638','Varka Silenos Scout','noRandomWalk','true'),
	('18639','Varka Silenos Hunter','noRandomWalk','true'),
	('18640','Varka Silenos Shaman','noRandomWalk','true'),
	('18641','Varka Silenos Priest','noRandomWalk','true'),
	('18642','Varka Silenos Warrior','noRandomWalk','true'),
	('18644','Varka Silenos Medium','noRandomWalk','true'),
	('18645','Varka Silenos Magus','noRandomWalk','true'),
	('18646','Varka Silenos Officer','noRandomWalk','true'),
	('18648','Varka Silenos Seer','noRandomWalk','true'),
	('18649','Varka Silenos Great Magus','noRandomWalk','true'),
	('18650','Varka Silenos General','noRandomWalk','true'),
	('18652','Varka Silenos Great Seer','noRandomWalk','true'),
	('18653','Varka\'s Elite Guard','noRandomWalk','true'),
	('18654','Varka\'s Commander','noRandomWalk','true'),
	('18655','Varka\'s Head Guard','noRandomWalk','true'),
	('18656','Varka\'s Head Magus','noRandomWalk','true'),
	('18657','Prophet Guard','noRandomWalk','true'),
	('18658','Disciple of Prophet','noRandomWalk','true'),
	('18659','Varka\'s Prophet','noRandomWalk','true'),
	('25690', 'Aenkinel','noRandomWalk','false'),
	('25691', 'Aenkinel','noRandomWalk','false'),
	('25692', 'Aenkinel','noRandomWalk','false'),
	('25693', 'Aenkinel','noRandomWalk','false'),
	('25694', 'Aenkinel','noRandomWalk','false'),
	('25695', 'Aenkinel','noRandomWalk','false'),
	('22545', 'Wife of the Dragon of Darkness','noRandomWalk','true'),
	('25597', 'Heurel','noRandomWalk','false'),
	('25598', 'Gegei','noRandomWalk','false'),
	('25599', 'Iglas','noRandomWalk','false'),

	('29095','Gordon','isMadness','15'),
	('25126','Longhorn Golkonda','isMadness','15'),
	('29020','Baium','isMadness','15'),
	('29065','Sailren','isMadness','15'),
	('29066','Antharas','isMadness','15'),
	('29067','Antharas','isMadness','15'),
	('29068','Antharas','isMadness','15'),
	('29019','Antharas','isMadness','15'),
	('29028','Valakas','isMadness','30'),
	('29014','Orfen','isMadness','15'),
	('29022','Zaken','isMadness','15'),
	('29046','Scarlet van Halisha','isMadness','30'),
	('29047','Scarlet van Halisha','isMadness','30'),
	('25523','Plague Golem','isMadness','15'),
	('29099','Baylor','isMadness','15'),
	('29186','Baylor','isMadness','15'),
	('25305','Ketra\'s Chief Brakki','isMadness','15'),

	('29095','Gordon','SelfAggressive','100'),
	('25126','Longhorn Golkonda','SelfAggressive','100'),
	('29020','Baium','SelfAggressive','100'),
	('29065','Sailren','SelfAggressive','100'),
	('29066','Antharas','SelfAggressive','100'),
	('29067','Antharas','SelfAggressive','100'),
	('29068','Antharas','SelfAggressive','100'),
	('29019','Antharas','SelfAggressive','100'),
	('29028','Valakas','SelfAggressive','100'),
	('29014','Orfen','SelfAggressive','100'),
	('29022','Zaken','SelfAggressive','100'),
	('29046','Scarlet van Halisha','SelfAggressive','100'),
	('29047','Scarlet van Halisha','SelfAggressive','100'),
	('25523','Plague Golem','SelfAggressive','100'),
	('29099','Baylor','SelfAggressive','100'),
	('29186','Baylor','SelfAggressive','100'),
	('25305','Ketra\'s Chief Brakki','SelfAggressive','100'),
	('29100','Crystal Prison Guard','SelfAggressive','0'),
	('22270','Water Dragon Detractor','SelfAggressive','100'),
	('22271','Water Dragon Detractor','SelfAggressive','100'),
	('18329','Hall Keeper Captain','SelfAggressive','100'),
	('18330','Hall Keeper Wizard','SelfAggressive','100'),
	('18331','Hall Keeper Guard','SelfAggressive','100'),
	('18332','Hall Keeper Patrol','SelfAggressive','100'),
	('18333','Hall Keeper Suicidal Soldier','SelfAggressive','100'),
	('18334','Dark Choir Captain','SelfAggressive','100'),
	('18335','Dark Choir Prima Donna','SelfAggressive','100'),
	('18336','Dark Choir Lancer','SelfAggressive','100'),
	('18337','Dark Choir Archer','SelfAggressive','100'),
	('18338','Dark Choir Witch Doctor','SelfAggressive','100'),
	('18339','Dark Choir Player','SelfAggressive','100'),
	('18340','Hall Keeper Suicidal Soldier','SelfAggressive','100'),
	('18341','Hall Keeper Suicidal Soldier','SelfAggressive','100'),

	('13148','Lindvior','isFlying','true'),
	('18684','Red Star Stone','isFlying','true'),
	('18685','Red Star Stone','isFlying','true'),
	('18686','Red Star Stone','isFlying','true'),
	('18687','Blue Star Stone','isFlying','true'),
	('18688','Blue Star Stone','isFlying','true'),
	('18689','Blue Star Stone','isFlying','true'),
	('18690','Green Star Stone','isFlying','true'),
	('18691','Green Star Stone','isFlying','true'),
	('18692','Green Star Stone','isFlying','true'),
	('22602','Mutant - Bird','isFlying','true'),
	('22603','Mutant - Bird','isFlying','true'),
	('22604','Dra Hawk','isFlying','true'),
	('22605','Dra Hawk','isFlying','true'),
	('22606','Floating Skull','isFlying','true'),
	('22607','Floating Skull','isFlying','true'),
	('22608','Floating Zombie','isFlying','true'),
	('22609','Floating Zombie','isFlying','true'),
	('22610','Mutated Drake Wing','isFlying','true'),
	('22611','Mutated Drake Wing','isFlying','true'),
	('22612','Drak','isFlying','true'),
	('22613','Drak','isFlying','true'),
	('22614','Vulture Rider','isFlying','true'),
	('22615','Vulture Rider','isFlying','true'),
	('22616','Seed of Pain','isFlying','true'),
	('25623','Valdstone','isFlying','true'),
	('25624','Rok','isFlying','true'),
	('25625','Enira','isFlying','true'),
	('25626','Dius','isFlying','true'),
	('25627','Mutant Bird','isFlying','true'),
	('25628','Dra Hawk','isFlying','true'),
	('25629','Floating Skull','isFlying','true'),
	('25630','Floating Zombie','isFlying','true'),
	('25631','Drake Wing','isFlying','true'),
	('25632','Drak','isFlying','true'),
	('25633','Elite Rider','isFlying','true'),

	('21527','Anger of Splendor','transformOnUnderAttack','21528'),
	('21533','Alliance of Splendor','transformOnUnderAttack','21534'),
	('21524','Blade of Splendor','transformOnUnderAttack','21525'),
	('21521','Claws of Splendor','transformOnUnderAttack','21522'),
	('21537','Fang of Splendor','transformOnUnderAttack','21538'),
	('21539','Wailing of Splendor','transformOnUnderAttack','21540'),

	('20830','Guardian Angel','transformOnDead','20859'),
	('21067','Guardian Archangel','transformOnDead','21068'),
	('21062','Messenger Angel','transformOnDead','21063'),
	('20831','Seal Angel','transformOnDead','20860'),
	('21070','Seal Archangel','transformOnDead','21071'),
	
	('22536','Royal Guard Captain','transformOnDead','22545'),
	('22536','Royal Guard Captain','transformChance','3'),
	('22537','Dragon Steed Troop Grand Magician','transformOnDead','22545'),
	('22537','Dragon Steed Troop Grand Magician','transformChance','3'),
	('22538','Dragon Steed Troop Commander','transformOnDead','22545'),
	('22538','Dragon Steed Troop Commander','transformChance','3'),
	('22539','Dragon Steed Troops No 1 Battalion Commander','transformOnDead','22545'),
	('22539','Dragon Steed Troops No 1 Battalion Commander','transformChance','3'),
	('22540','White Dragon Leader','transformOnDead','22545'),
	('22540','White Dragon Leader','transformChance','3'),
	('22541','Dragon Steed Troop Infantry','transformOnDead','22545'),
	('22541','Dragon Steed Troop Infantry','transformChance','3'),
	('22542','Dragon Steed Troop Magic Leader','transformOnDead','22545'),
	('22542','Dragon Steed Troop Magic Leader','transformChance','3'),
	('22543','Dragon Steed Troop Magician','transformOnDead','22545'),
	('22543','Dragon Steed Troop Magician','transformChance','3'),
	('22544','Dragon Steed Troop Magic Soldier','transformOnDead','22545'),
	('22544','Dragon Steed Troop Magic Soldier','transformChance','3'),
	('22546','Warrior of Light','transformOnDead','22545'),
	('22546','Warrior of Light','transformChance','3'),
	('22547','Dragon Steed Troop Healer','transformOnDead','22545'),
	('22547','Dragon Steed Troop Healer','transformChance','3'),
	('22548','Dragon Steed Troop Javelin Thrower','transformOnDead','22545'),
	('22548','Dragon Steed Troop Javelin Thrower','transformChance','3'),
	('22549','Dragon Steed Troop Javelin Thrower','transformOnDead','22545'),
	('22549','Dragon Steed Troop Javelin Thrower','transformChance','3'),
	('22550','Savage Warrior','transformOnDead','22545'),
	('22550','Savage Warrior','transformChance','3'),
	('22551','Priest of Darkness','transformOnDead','22545'),
	('22551','Priest of Darkness','transformChance','3'),
	('22552','Mutation Drake','transformOnDead','22545'),
	('22552','Mutation Drake','transformChance','3'),
	
	('22621','Male Spiked Stakato','spawnOtherOnDead','22620'),
	('22621','Male Spiked Stakato','spawnOtherChance','40'),
	('22621','Male Spiked Stakato','spawnOtherCount','2'),
	('22622','Male Spiked Stakato','spawnOtherOnDead','22620'),
	('22622','Male Spiked Stakato','spawnOtherChance','40'),
	('22622','Male Spiked Stakato','spawnOtherCount','2'),		
	('22630','Spiked Stakato Nurse','spawnOtherOnDead','22631'),
	('22630','Spiked Stakato Nurse','spawnOtherChance','40'),
	('22630','Spiked Stakato Nurse','spawnOtherCount','2'),
	('22631','Spiked Stakato Nurse','spawnOtherOnDead','22619'),
	('22631','Spiked Stakato Nurse','spawnOtherChance','40'),
	('22631','Spiked Stakato Nurse','spawnOtherCount','3'),		

	('21258','Fallen Orc Shaman','transformOnUnderAttack','21259'),
	('21258','Fallen Orc Shaman','transformChance','100'),

	('29045','Frintezza','randomAnimationDisabled','true'),
	('22775','Sel Mahum Drill Sergeant','randomAnimationDisabled','true'),
	('22776','Sel Mahum Training Officer','randomAnimationDisabled','true'),	
	('22777','Sel Mahum Drill Sergeant','randomAnimationDisabled','true'),
	('22778','Sel Mahum Drill Sergeant','randomAnimationDisabled','true'),
	('22780','Sel Mahum Recruit','randomAnimationDisabled','true'),	
	('22782','Sel Mahum Recruit','randomAnimationDisabled','true'),
	('22783','Sel Mahum Soldier','randomAnimationDisabled','true'),	
	('22784','Sel Mahum Recruit','randomAnimationDisabled','true'),
	('22785','Sel Mahum Soldier','randomAnimationDisabled','true'),	
	('32705','Zaken`s Candle','randomAnimationDisabled','true'),
	('30675','Corpse Of Kamur','randomAnimationDisabled','true'),
	('30761','Corpse Of Fritz','randomAnimationDisabled','true'),
	('30762','Corpse Of Lutz','randomAnimationDisabled','true'),
	('30763','Corpse Of Kurtz','randomAnimationDisabled','true'),
	('30980','Ark Guardian\'s Corpse','randomAnimationDisabled','true'),
	('31074','Corpse of Hutaku','randomAnimationDisabled','true'),
	('31665','Corpse of Dwarf','randomAnimationDisabled','true'),
	('31752','Corpse of Angel','randomAnimationDisabled','true'),
 	('32015','Scout\'s Corpse','randomAnimationDisabled','true'),
 	('32306','Native\'s Corpse','randomAnimationDisabled','true'),
 	('32528','Medibal\'s Corpse','randomAnimationDisabled','true'),
 	('32568','Unidentified Body','randomAnimationDisabled','true'),
	('32619','','randomAnimationDisabled','true'),

	('20965','Chimera Piece','searchingMaster','true'),
	('20966','Changed Creation','searchingMaster','true'),
	('20967','Past Creature','searchingMaster','true'),
	('20968','Nonexistent Man','searchingMaster','true'),
	('20969','Giant\'s Shadow','searchingMaster','true'),
	('20970','Soldier of Ancient Times','searchingMaster','true'),
	('20971','Warrior of Ancient Times','searchingMaster','true'),
	('20972','Shaman of Ancient Times','searchingMaster','true'),
	('20973','Forgotten Ancient People','searchingMaster','true'),

	('31710','Ketra Van Commander','chatWindowDisabled','true'),
	('31711','Ketra Van Shaman','chatWindowDisabled','true'),
	('31712','Varka Raider','chatWindowDisabled','true'),
	('13002','Life Control Tower','chatWindowDisabled','true'),
	('13003','Life Control Tower','chatWindowDisabled','true'),
	('13004','Flame Control Tower','chatWindowDisabled','true'),
	('13005','Flame Control Tower','chatWindowDisabled','true'),
	('18705','Destroyed Tumor','chatWindowDisabled','true'),
	('13193','','chatWindowDisabled','true'),
	('22340','Sandstorm','chatWindowDisabled','true'),
	('13160','Snowman','chatWindowDisabled','true'),
	
('35671','Fort','isMobile','true'),
('35672','Fort','isMobile','true'),
('35673','Fort','isMobile','true'),
('35674','Fort','isMobile','true'),
('35676','Fort','isMobile','true'),
('35682','Fort','isMobile','true'),
('35683','Fort','isMobile','true'),
('35684','Fort','isMobile','true'),
('35703','Fort','isMobile','true'),
('35704','Fort','isMobile','true'),
('35705','Fort','isMobile','true'),
('35706','Fort','isMobile','true'),
('35711','Fort','isMobile','true'),
('35712','Fort','isMobile','true'),
('35718','Fort','isMobile','true'),
('35719','Fort','isMobile','true'),
('35720','Fort','isMobile','true'),
('35740','Fort','isMobile','true'),
('35741','Fort','isMobile','true'),
('35742','Fort','isMobile','true'),
('35743','Fort','isMobile','true'),
('35745','Fort','isMobile','true'),
('35751','Fort','isMobile','true'),
('35752','Fort','isMobile','true'),
('35753','Fort','isMobile','true'),
('35772','Fort','isMobile','true'),
('35773','Fort','isMobile','true'),
('35774','Fort','isMobile','true'),
('35775','Fort','isMobile','true'),
('35780','Fort','isMobile','true'),
('35781','Fort','isMobile','true'),
('35787','Fort','isMobile','true'),
('35788','Fort','isMobile','true'),
('35789','Fort','isMobile','true'),
('35809','Fort','isMobile','true'),
('35810','Fort','isMobile','true'),
('35811','Fort','isMobile','true'),
('35812','Fort','isMobile','true'),
('35814','Fort','isMobile','true'),
('35820','Fort','isMobile','true'),
('35821','Fort','isMobile','true'),
('35822','Fort','isMobile','true'),
('35840','Fort','isMobile','true'),
('35841','Fort','isMobile','true'),
('35842','Fort','isMobile','true'),
('35843','Fort','isMobile','true'),
('35845','Fort','isMobile','true'),
('35851','Fort','isMobile','true'),
('35852','Fort','isMobile','true'),
('35853','Fort','isMobile','true'),
('35872','Fort','isMobile','true'),
('35873','Fort','isMobile','true'),
('35874','Fort','isMobile','true'),
('35875','Fort','isMobile','true'),
('35880','Fort','isMobile','true'),
('35881','Fort','isMobile','true'),
('35887','Fort','isMobile','true'),
('35888','Fort','isMobile','true'),
('35889','Fort','isMobile','true'),
('35909','Fort','isMobile','true'),
('35910','Fort','isMobile','true'),
('35911','Fort','isMobile','true'),
('35912','Fort','isMobile','true'),
('35914','Fort','isMobile','true'),
('35920','Fort','isMobile','true'),
('35921','Fort','isMobile','true'),
('35922','Fort','isMobile','true'),
('35941','Fort','isMobile','true'),
('35942','Fort','isMobile','true'),
('35943','Fort','isMobile','true'),
('35944','Fort','isMobile','true'),
('35949','Fort','isMobile','true'),
('35950','Fort','isMobile','true'),
('35956','Fort','isMobile','true'),
('35957','Fort','isMobile','true'),
('35958','Fort','isMobile','true'),
('35979','Fort','isMobile','true'),
('35980','Fort','isMobile','true'),
('35981','Fort','isMobile','true'),
('35982','Fort','isMobile','true'),
('35987','Fort','isMobile','true'),
('35988','Fort','isMobile','true'),
('35994','Fort','isMobile','true'),
('35995','Fort','isMobile','true'),
('35996','Fort','isMobile','true'),
('36016','Fort','isMobile','true'),
('36017','Fort','isMobile','true'),
('36018','Fort','isMobile','true'),
('36019','Fort','isMobile','true'),
('36021','Fort','isMobile','true'),
('36027','Fort','isMobile','true'),
('36028','Fort','isMobile','true'),
('36029','Fort','isMobile','true'),
('36048','Fort','isMobile','true'),
('36049','Fort','isMobile','true'),
('36050','Fort','isMobile','true'),
('36051','Fort','isMobile','true'),
('36056','Fort','isMobile','true'),
('36057','Fort','isMobile','true'),
('36063','Fort','isMobile','true'),
('36064','Fort','isMobile','true'),
('36065','Fort','isMobile','true'),
('36086','Fort','isMobile','true'),
('36087','Fort','isMobile','true'),
('36088','Fort','isMobile','true'),
('36089','Fort','isMobile','true'),
('36094','Fort','isMobile','true'),
('36095','Fort','isMobile','true'),
('36101','Fort','isMobile','true'),
('36102','Fort','isMobile','true'),
('36103','Fort','isMobile','true'),
('36123','Fort','isMobile','true'),
('36124','Fort','isMobile','true'),
('36125','Fort','isMobile','true'),
('36126','Fort','isMobile','true'),
('36128','Fort','isMobile','true'),
('36134','Fort','isMobile','true'),
('36135','Fort','isMobile','true'),
('36136','Fort','isMobile','true'),
('36154','Fort','isMobile','true'),
('36155','Fort','isMobile','true'),
('36156','Fort','isMobile','true'),
('36157','Fort','isMobile','true'),
('36159','Fort','isMobile','true'),
('36165','Fort','isMobile','true'),
('36166','Fort','isMobile','true'),
('36167','Fort','isMobile','true'),
('36186','Fort','isMobile','true'),
('36187','Fort','isMobile','true'),
('36188','Fort','isMobile','true'),
('36189','Fort','isMobile','true'),
('36194','Fort','isMobile','true'),
('36195','Fort','isMobile','true'),
('36201','Fort','isMobile','true'),
('36202','Fort','isMobile','true'),
('36203','Fort','isMobile','true'),
('36224','Fort','isMobile','true'),
('36225','Fort','isMobile','true'),
('36226','Fort','isMobile','true'),
('36227','Fort','isMobile','true'),
('36232','Fort','isMobile','true'),
('36233','Fort','isMobile','true'),
('36239','Fort','isMobile','true'),
('36240','Fort','isMobile','true'),
('36241','Fort','isMobile','true'),
('36262','Fort','isMobile','true'),
('36263','Fort','isMobile','true'),
('36264','Fort','isMobile','true'),
('36265','Fort','isMobile','true'),
('36270','Fort','isMobile','true'),
('36271','Fort','isMobile','true'),
('36277','Fort','isMobile','true'),
('36278','Fort','isMobile','true'),
('36279','Fort','isMobile','true'),
('36299','Fort','isMobile','true'),
('36300','Fort','isMobile','true'),
('36301','Fort','isMobile','true'),
('36302','Fort','isMobile','true'),
('36304','Fort','isMobile','true'),
('36310','Fort','isMobile','true'),
('36311','Fort','isMobile','true'),
('36312','Fort','isMobile','true'),
('36331','Fort','isMobile','true'),
('36332','Fort','isMobile','true'),
('36333','Fort','isMobile','true'),
('36334','Fort','isMobile','true'),
('36339','Fort','isMobile','true'),
('36340','Fort','isMobile','true'),
('36346','Fort','isMobile','true'),
('36347','Fort','isMobile','true'),
('36348','Fort','isMobile','true'),
('36369','Fort','isMobile','true'),
('36370','Fort','isMobile','true'),
('36371','Fort','isMobile','true'),
('36372','Fort','isMobile','true'),
('36377','Fort','isMobile','true'),
('36378','Fort','isMobile','true'),
('36384','Fort','isMobile','true'),
('36385','Fort','isMobile','true'),
('36386','Fort','isMobile','true');
INSERT INTO `ai_params` (`npc_id`, `name`, `param`, `value`) VALUES
('22704','Contaminated Batur Warrior','MaxPursueRange','30000'),
('22705','Contaminated Batur Commander','MaxPursueRange','30000'),
('22706','Turka Followers Ghost','MaxPursueRange','30000'),
('22843','Maluk Sniper','canSeeInSilentMove','true');

REPLACE INTO `ai_params` VALUES ('30039', 'Guard', 'isMobile', 'true');
REPLACE INTO `ai_params` VALUES ('30040', 'Guard', 'isMobile', 'true');
REPLACE INTO `ai_params` VALUES ('30041', 'Guard', 'isMobile', 'true');
REPLACE INTO `ai_params` VALUES ('30042', 'Guard', 'isMobile', 'true');
REPLACE INTO `ai_params` VALUES ('30043', 'Guard', 'isMobile', 'true');
REPLACE INTO `ai_params` VALUES ('30044', 'Guard', 'isMobile', 'true');
REPLACE INTO `ai_params` VALUES ('30045', 'Guard', 'isMobile', 'true');
REPLACE INTO `ai_params` VALUES ('30046', 'Guard', 'isMobile', 'true');
REPLACE INTO `ai_params` VALUES ('30072', 'Guard', 'isMobile', 'true');
REPLACE INTO `ai_params` VALUES ('30073', 'Guard', 'isMobile', 'true');
REPLACE INTO `ai_params` VALUES ('30074', 'Guard', 'isMobile', 'true');
REPLACE INTO `ai_params` VALUES ('30075', 'Guard', 'isMobile', 'true');
REPLACE INTO `ai_params` VALUES ('30076', 'Guard', 'isMobile', 'true');
REPLACE INTO `ai_params` VALUES ('30121', 'Guard', 'isMobile', 'true');
REPLACE INTO `ai_params` VALUES ('30122', 'Guard', 'isMobile', 'true');
REPLACE INTO `ai_params` VALUES ('30123', 'Guard', 'isMobile', 'true');
REPLACE INTO `ai_params` VALUES ('30124', 'Guard', 'isMobile', 'true');
REPLACE INTO `ai_params` VALUES ('30125', 'Guard', 'isMobile', 'true');
REPLACE INTO `ai_params` VALUES ('30126', 'Guard', 'isMobile', 'true');
REPLACE INTO `ai_params` VALUES ('30128', 'Guard', 'isMobile', 'true');
REPLACE INTO `ai_params` VALUES ('30197', 'Guard', 'isMobile', 'true');
REPLACE INTO `ai_params` VALUES ('30198', 'Guard', 'isMobile', 'true');
REPLACE INTO `ai_params` VALUES ('30199', 'Guard', 'isMobile', 'true');
REPLACE INTO `ai_params` VALUES ('30200', 'Guard', 'isMobile', 'true');
REPLACE INTO `ai_params` VALUES ('30201', 'Guard', 'isMobile', 'true');
REPLACE INTO `ai_params` VALUES ('30216', 'Guard', 'isMobile', 'true');
REPLACE INTO `ai_params` VALUES ('30217', 'Guard', 'isMobile', 'true');
REPLACE INTO `ai_params` VALUES ('30218', 'Guard', 'isMobile', 'true');
REPLACE INTO `ai_params` VALUES ('30219', 'Guard', 'isMobile', 'true');
REPLACE INTO `ai_params` VALUES ('30220', 'Guard', 'isMobile', 'true');
REPLACE INTO `ai_params` VALUES ('30221', 'Guard', 'isMobile', 'true');
REPLACE INTO `ai_params` VALUES ('30224', 'Guard', 'isMobile', 'true');
REPLACE INTO `ai_params` VALUES ('30284', 'Guard', 'isMobile', 'true');
REPLACE INTO `ai_params` VALUES ('30285', 'Guard', 'isMobile', 'true');
REPLACE INTO `ai_params` VALUES ('30331', 'Guard', 'isMobile', 'true');
REPLACE INTO `ai_params` VALUES ('30333', 'Guard', 'isMobile', 'true');
REPLACE INTO `ai_params` VALUES ('30334', 'Guard', 'isMobile', 'true');
REPLACE INTO `ai_params` VALUES ('30335', 'Guard', 'isMobile', 'true');
REPLACE INTO `ai_params` VALUES ('30336', 'Guard', 'isMobile', 'true');
REPLACE INTO `ai_params` VALUES ('30337', 'Guard', 'isMobile', 'true');
REPLACE INTO `ai_params` VALUES ('30338', 'Guard', 'isMobile', 'true');
REPLACE INTO `ai_params` VALUES ('30346', 'Guard', 'isMobile', 'true');
REPLACE INTO `ai_params` VALUES ('30347', 'Guard', 'isMobile', 'true');
REPLACE INTO `ai_params` VALUES ('30348', 'Guard', 'isMobile', 'true');
REPLACE INTO `ai_params` VALUES ('30349', 'Guard', 'isMobile', 'true');
REPLACE INTO `ai_params` VALUES ('30355', 'Guard', 'isMobile', 'true');
REPLACE INTO `ai_params` VALUES ('30356', 'Guard', 'isMobile', 'true');
REPLACE INTO `ai_params` VALUES ('30357', 'Guard', 'isMobile', 'true');
REPLACE INTO `ai_params` VALUES ('30379', 'Guard', 'isMobile', 'true');
REPLACE INTO `ai_params` VALUES ('30380', 'Guard', 'isMobile', 'true');
REPLACE INTO `ai_params` VALUES ('30381', 'Guard', 'isMobile', 'true');
REPLACE INTO `ai_params` VALUES ('30382', 'Guard', 'isMobile', 'true');
REPLACE INTO `ai_params` VALUES ('30383', 'Guard', 'isMobile', 'true');
REPLACE INTO `ai_params` VALUES ('30384', 'Guard', 'isMobile', 'true');
REPLACE INTO `ai_params` VALUES ('30385', 'Guard', 'isMobile', 'true');
REPLACE INTO `ai_params` VALUES ('30386', 'Guard', 'isMobile', 'true');
REPLACE INTO `ai_params` VALUES ('30430', 'Guard', 'isMobile', 'true');
REPLACE INTO `ai_params` VALUES ('30431', 'Guard', 'isMobile', 'true');
REPLACE INTO `ai_params` VALUES ('30432', 'Guard', 'isMobile', 'true');
REPLACE INTO `ai_params` VALUES ('30433', 'Guard', 'isMobile', 'true');
REPLACE INTO `ai_params` VALUES ('30452', 'Guard', 'isMobile', 'true');
REPLACE INTO `ai_params` VALUES ('30465', 'Guard', 'isMobile', 'true');
REPLACE INTO `ai_params` VALUES ('30466', 'Guard', 'isMobile', 'true');
REPLACE INTO `ai_params` VALUES ('30478', 'Guard', 'isMobile', 'true');
REPLACE INTO `ai_params` VALUES ('30541', 'Guard', 'isMobile', 'true');
REPLACE INTO `ai_params` VALUES ('30542', 'Guard', 'isMobile', 'true');
REPLACE INTO `ai_params` VALUES ('30543', 'Guard', 'isMobile', 'true');
REPLACE INTO `ai_params` VALUES ('30544', 'Guard', 'isMobile', 'true');
REPLACE INTO `ai_params` VALUES ('30545', 'Guard', 'isMobile', 'true');
REPLACE INTO `ai_params` VALUES ('30546', 'Guard', 'isMobile', 'true');
REPLACE INTO `ai_params` VALUES ('30547', 'Guard', 'isMobile', 'true');
REPLACE INTO `ai_params` VALUES ('30548', 'Guard', 'isMobile', 'true');
REPLACE INTO `ai_params` VALUES ('30577', 'Guard', 'isMobile', 'true');
REPLACE INTO `ai_params` VALUES ('30578', 'Guard', 'isMobile', 'true');
REPLACE INTO `ai_params` VALUES ('30579', 'Guard', 'isMobile', 'true');
REPLACE INTO `ai_params` VALUES ('30580', 'Guard', 'isMobile', 'true');
REPLACE INTO `ai_params` VALUES ('30581', 'Guard', 'isMobile', 'true');
REPLACE INTO `ai_params` VALUES ('30582', 'Guard', 'isMobile', 'true');
REPLACE INTO `ai_params` VALUES ('30583', 'Guard', 'isMobile', 'true');
REPLACE INTO `ai_params` VALUES ('30584', 'Guard', 'isMobile', 'true');
REPLACE INTO `ai_params` VALUES ('30707', 'Guard', 'isMobile', 'true');
REPLACE INTO `ai_params` VALUES ('30708', 'Guard', 'isMobile', 'true');
REPLACE INTO `ai_params` VALUES ('30709', 'Guard', 'isMobile', 'true');
REPLACE INTO `ai_params` VALUES ('30710', 'Guard', 'isMobile', 'true');
REPLACE INTO `ai_params` VALUES ('30711', 'Guard', 'isMobile', 'true');
REPLACE INTO `ai_params` VALUES ('30712', 'Guard', 'isMobile', 'true');
REPLACE INTO `ai_params` VALUES ('30713', 'Guard', 'isMobile', 'true');
REPLACE INTO `ai_params` VALUES ('30714', 'Guard', 'isMobile', 'true');
REPLACE INTO `ai_params` VALUES ('30723', 'Guard', 'isMobile', 'true');
REPLACE INTO `ai_params` VALUES ('30724', 'Guard', 'isMobile', 'true');
REPLACE INTO `ai_params` VALUES ('30725', 'Guard', 'isMobile', 'true');
REPLACE INTO `ai_params` VALUES ('30726', 'Guard', 'isMobile', 'true');
REPLACE INTO `ai_params` VALUES ('30733', 'Guard', 'isMobile', 'true');
REPLACE INTO `ai_params` VALUES ('30870', 'Guard', 'isMobile', 'true');
REPLACE INTO `ai_params` VALUES ('30871', 'Guard', 'isMobile', 'true');
REPLACE INTO `ai_params` VALUES ('30872', 'Guard', 'isMobile', 'true');
REPLACE INTO `ai_params` VALUES ('30873', 'Guard', 'isMobile', 'true');
REPLACE INTO `ai_params` VALUES ('30874', 'Guard', 'isMobile', 'true');
REPLACE INTO `ai_params` VALUES ('30875', 'Guard', 'isMobile', 'true');
REPLACE INTO `ai_params` VALUES ('30876', 'Guard', 'isMobile', 'true');
REPLACE INTO `ai_params` VALUES ('30877', 'Guard', 'isMobile', 'true');
REPLACE INTO `ai_params` VALUES ('30880', 'Guard', 'isMobile', 'true');
REPLACE INTO `ai_params` VALUES ('30882', 'Guard', 'isMobile', 'true');
REPLACE INTO `ai_params` VALUES ('30883', 'Guard', 'isMobile', 'true');
REPLACE INTO `ai_params` VALUES ('30884', 'Guard', 'isMobile', 'true');
REPLACE INTO `ai_params` VALUES ('30885', 'Guard', 'isMobile', 'true');
REPLACE INTO `ai_params` VALUES ('30886', 'Guard', 'isMobile', 'true');
REPLACE INTO `ai_params` VALUES ('30887', 'Guard', 'isMobile', 'true');
REPLACE INTO `ai_params` VALUES ('30888', 'Guard', 'isMobile', 'true');
REPLACE INTO `ai_params` VALUES ('30889', 'Guard', 'isMobile', 'true');
REPLACE INTO `ai_params` VALUES ('30917', 'Guard', 'isMobile', 'true');
REPLACE INTO `ai_params` VALUES ('30918', 'Guard', 'isMobile', 'true');
REPLACE INTO `ai_params` VALUES ('30919', 'Guard', 'isMobile', 'true');
REPLACE INTO `ai_params` VALUES ('30920', 'Guard', 'isMobile', 'true');
REPLACE INTO `ai_params` VALUES ('30921', 'Guard', 'isMobile', 'true');
REPLACE INTO `ai_params` VALUES ('30922', 'Guard', 'isMobile', 'true');
REPLACE INTO `ai_params` VALUES ('30923', 'Guard', 'isMobile', 'true');
REPLACE INTO `ai_params` VALUES ('31292', 'Guard', 'isMobile', 'true');
REPLACE INTO `ai_params` VALUES ('31293', 'Guard', 'isMobile', 'true');
REPLACE INTO `ai_params` VALUES ('31294', 'Guard', 'isMobile', 'true');
REPLACE INTO `ai_params` VALUES ('31295', 'Guard', 'isMobile', 'true');
REPLACE INTO `ai_params` VALUES ('31296', 'Guard', 'isMobile', 'true');
REPLACE INTO `ai_params` VALUES ('31297', 'Guard', 'isMobile', 'true');
REPLACE INTO `ai_params` VALUES ('31298', 'Guard', 'isMobile', 'true');
REPLACE INTO `ai_params` VALUES ('31299', 'Guard', 'isMobile', 'true');
REPLACE INTO `ai_params` VALUES ('31341', 'Guard', 'isMobile', 'true');
REPLACE INTO `ai_params` VALUES ('31342', 'Guard', 'isMobile', 'true');
REPLACE INTO `ai_params` VALUES ('31343', 'Guard', 'isMobile', 'true');
REPLACE INTO `ai_params` VALUES ('31344', 'Guard', 'isMobile', 'true');
REPLACE INTO `ai_params` VALUES ('31345', 'Guard', 'isMobile', 'true');
REPLACE INTO `ai_params` VALUES ('31346', 'Guard', 'isMobile', 'true');
REPLACE INTO `ai_params` VALUES ('31347', 'Guard', 'isMobile', 'true');
REPLACE INTO `ai_params` VALUES ('31671', 'Guard', 'isMobile', 'true');
REPLACE INTO `ai_params` VALUES ('31672', 'Guard', 'isMobile', 'true');
REPLACE INTO `ai_params` VALUES ('31673', 'Guard', 'isMobile', 'true');
REPLACE INTO `ai_params` VALUES ('31674', 'Guard', 'isMobile', 'true');
REPLACE INTO `ai_params` VALUES ('31677', 'Guard', 'isMobile', 'true');
REPLACE INTO `ai_params` VALUES ('31678', 'Guard', 'isMobile', 'true');
REPLACE INTO `ai_params` VALUES ('31681', 'Guard', 'isMobile', 'true');
REPLACE INTO `ai_params` VALUES ('31682', 'Guard', 'isMobile', 'true');
REPLACE INTO `ai_params` VALUES ('31982', 'Guard', 'isMobile', 'true');
REPLACE INTO `ai_params` VALUES ('31983', 'Guard', 'isMobile', 'true');
REPLACE INTO `ai_params` VALUES ('31984', 'Guard', 'isMobile', 'true');
REPLACE INTO `ai_params` VALUES ('31985', 'Guard', 'isMobile', 'true');
REPLACE INTO `ai_params` VALUES ('31986', 'Guard', 'isMobile', 'true');
REPLACE INTO `ai_params` VALUES ('31987', 'Guard', 'isMobile', 'true');
REPLACE INTO `ai_params` VALUES ('31988', 'Guard', 'isMobile', 'true');
REPLACE INTO `ai_params` VALUES ('32136', 'Guard', 'isMobile', 'true');
REPLACE INTO `ai_params` VALUES ('32137', 'Guard', 'isMobile', 'true');
REPLACE INTO `ai_params` VALUES ('32173', 'Guard', 'isMobile', 'true');
REPLACE INTO `ai_params` VALUES ('32174', 'Guard', 'isMobile', 'true');
REPLACE INTO `ai_params` VALUES ('32175', 'Guard', 'isMobile', 'true');
REPLACE INTO `ai_params` VALUES ('32176', 'Guard', 'isMobile', 'true');
REPLACE INTO `ai_params` VALUES ('32177', 'Guard', 'isMobile', 'true');
REPLACE INTO `ai_params` VALUES ('32178', 'Guard', 'isMobile', 'true');
REPLACE INTO `ai_params` VALUES ('32179', 'Guard', 'isMobile', 'true');
REPLACE INTO `ai_params` VALUES ('32180', 'Guard', 'isMobile', 'true');
REPLACE INTO `ai_params` VALUES ('32185', 'Guard', 'isMobile', 'true');
REPLACE INTO `ai_params` VALUES ('32188', 'Guard', 'isMobile', 'true');
REPLACE INTO `ai_params` VALUES ('32192', 'Guard', 'isMobile', 'true');
REPLACE INTO `ai_params` VALUES ('32336', 'Guard', 'isMobile', 'true');
REPLACE INTO `ai_params` VALUES ('32479', 'Guard', 'isMobile', 'true');
REPLACE INTO `ai_params` VALUES ('32480', 'Guard', 'isMobile', 'true');
REPLACE INTO `ai_params` VALUES ('32481', 'Guard', 'isMobile', 'true');
REPLACE INTO `ai_params` VALUES ('32482', 'Guard', 'isMobile', 'true');

REPLACE INTO `ai_params` VALUES ('22854', 'Blody Karik', 'canSeeInSilentMove', 'true');
REPLACE INTO `ai_params` VALUES ('22855', 'Blody Berserker', 'canSeeInSilentMove', 'true');
REPLACE INTO `ai_params` VALUES ('22856', 'Blody Karinnes', 'canSeeInSilentMove', 'true');
