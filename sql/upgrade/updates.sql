-- апдейты, которые можно применять многократно без побочных действий

UPDATE IGNORE `character_variables` SET `name` = 'KamalokaHall' WHERE `name` = 'LastEnterInstance';
DELETE FROM `character_variables` WHERE `name` = 'LastEnterInstance';
DELETE FROM `character_variables` WHERE `name` = 'HellboundConfidence';
DELETE FROM `character_quests` WHERE `name`='_1003_Valakas';

DELETE FROM `character_variables` WHERE `name` IN ('q211','q212','q213','q214','q215','q216','q217','q218','q219','q220','q221','q222','q223','q224','q225','q226','q227','q228','q229','q230','q231','q232','q233','q281','dd');

UPDATE `items` SET `item_id`=12602 WHERE `item_id`=12609;

DROP TABLE IF EXISTS zone;

DELETE FROM character_quests WHERE name='_147_PathtoBecominganEliteMercenary';
DELETE FROM character_quests WHERE name='_148_PathtoBecominganExaltedMercenary';
DELETE FROM character_quests WHERE name='_176_StepsForHonor';
DELETE FROM character_quests WHERE name='_717_ForTheSakeOfTheTerritoryGludio';
DELETE FROM character_quests WHERE name='_718_ForTheSakeOfTheTerritoryDion';
DELETE FROM character_quests WHERE name='_719_ForTheSakeOfTheTerritoryGiran';
DELETE FROM character_quests WHERE name='_720_ForTheSakeOfTheTerritoryOren';
DELETE FROM character_quests WHERE name='_721_ForTheSakeOfTheTerritoryAden';
DELETE FROM character_quests WHERE name='_722_ForTheSakeOfTheTerritoryInnadril';
DELETE FROM character_quests WHERE name='_723_ForTheSakeOfTheTerritoryGoddard';
DELETE FROM character_quests WHERE name='_724_ForTheSakeOfTheTerritoryRune';
DELETE FROM character_quests WHERE name='_725_ForTheSakeOfTheTerritorySchuttgart';
DELETE FROM character_quests WHERE name='_729_ProtectTheCatapult';
DELETE FROM character_quests WHERE name='_730_ProtectTheSupplies';
DELETE FROM character_quests WHERE name='_731_ProtectTheMilitaryLeader';
DELETE FROM character_quests WHERE name='_732_ProtectTheReligiousLeader';
DELETE FROM character_quests WHERE name='_733_ProtectTheEconomicLeader';
DELETE FROM character_quests WHERE name='_734_PierceThroughAShield';
DELETE FROM character_quests WHERE name='_735_MakeSpearsDull';
DELETE FROM character_quests WHERE name='_736_WeakenMagic';
DELETE FROM character_quests WHERE name='_737_DenyBlessings';
DELETE FROM character_quests WHERE name='_738_DestroyKeyTargets';

update npc set patk=patk*2, matk=matk*2, pdef=2*pdef, mdef=2*mdef, matkspd=2*matkspd, atkspd=2*atkspd, runspd=360 where id IN (36604,36605,36606);
update npc set patk=patk*4, matk=matk*4, pdef=2*pdef, mdef=2*mdef, matkspd=2*matkspd, atkspd=2*atkspd where id = 36606;
UPDATE npc set faction_id = 'event', aggro = 500 WHERE id in (36604,36605,36606);


