-- Перед выполнением запросов настоятельно рекомендуется выполнять полный бэкап базы даных.
-- Update Для игрового сервера (отдельно так как логин бывает разделен с гейм сервером)
-- Использовать только во время перехода на с версии 794 и меньше на 795 и выше
ALTER TABLE `character_blocklist` DROP COLUMN `target_Name`, DROP PRIMARY KEY, ADD PRIMARY KEY (`obj_Id`,`target_Id`);
CREATE TABLE `character_bookmarks` (
  `char_Id` INT NOT NULL,
  `idx` TINYINT UNSIGNED NOT NULL,
  `name` varchar(32) character set utf8 NOT NULL,
  `acronym` varchar(4) character set utf8 NOT NULL,
  `icon` TINYINT UNSIGNED NOT NULL,
  `x` INT NOT NULL,
  `y` INT NOT NULL,
  `z` INT NOT NULL,
  PRIMARY KEY  (`char_Id`,`idx`)
) ENGINE=MyISAM;
INSERT INTO character_bookmarks SELECT charId, Id, name,tag,icon,x,y,z FROM character_tpbookmark;
DROP TABLE `character_tpbookmark`;
ALTER TABLE `character_friends` DROP COLUMN `friend_name`;
-- DROP TABLE `character_premium_items`;
DROP TABLE `character_quest_global_data`;
ALTER TABLE `character_subclasses` CHANGE `sp` `sp` bigint unsigned NOT NULL DEFAULT 0;
ALTER TABLE `characters` DROP COLUMN `clan_privs`;
ALTER TABLE `characters` DROP COLUMN `wantspeace`;
ALTER TABLE `characters` DROP COLUMN `noble`;
ALTER TABLE `characters` DROP COLUMN `ketra`;
ALTER TABLE `characters` DROP COLUMN `varka`;
ALTER TABLE `characters` DROP COLUMN `ram`;
ALTER TABLE `characters` CHANGE `bookmarkslot` `bookmarks` TINYINT UNSIGNED NOT NULL DEFAULT 0;
ALTER TABLE `characters` MODIFY COLUMN `vitality` SMALLINT(5) UNSIGNED NOT NULL DEFAULT '10000';
ALTER TABLE `character_skills` MODIFY COLUMN `skill_level` SMALLINT(5) UNSIGNED NOT NULL DEFAULT '0';
ALTER TABLE `character_effects_save` DROP INDEX `order`;
ALTER TABLE `character_hennas` MODIFY COLUMN `symbol_id` TINYINT(3) UNSIGNED NOT NULL DEFAULT '0';
ALTER TABLE `clan_data` DROP COLUMN `air_licen`;
ALTER TABLE `clan_data` CHANGE `ships` `airship` SMALLINT NOT NULL DEFAULT -1;
-- Удаляем дубликаты, обновляем ключи, удаляем столбец, удаляем ключ UNIQUE
ALTER IGNORE TABLE `clan_privs` ADD UNIQUE (`clan_id`, `rank`);
ALTER TABLE `clan_privs` DROP PRIMARY KEY;
ALTER TABLE `clan_privs` ADD PRIMARY KEY (`clan_id`, `rank`);
ALTER TABLE `clan_privs` DROP COLUMN `party`;
ALTER TABLE `clan_privs` DROP INDEX `clan_id`;
--
ALTER TABLE `clanhall` MODIFY COLUMN `price` BIGINT(20) NOT NULL DEFAULT '0';
ALTER TABLE `castle` MODIFY COLUMN `siegeDayOfWeek` TINYINT(3) UNSIGNED NOT NULL DEFAULT '1';
ALTER TABLE `castle_manor_procure` MODIFY COLUMN `can_buy` BIGINT(20) NOT NULL DEFAULT '0';
ALTER TABLE `castle_manor_procure` MODIFY COLUMN `start_buy` BIGINT(20) NOT NULL DEFAULT '0';
ALTER TABLE `castle_manor_procure` MODIFY COLUMN `price` BIGINT(20) NOT NULL DEFAULT '0';
ALTER TABLE `castle_manor_production` MODIFY COLUMN `can_produce` BIGINT(20) NOT NULL DEFAULT '0';
ALTER TABLE `castle_manor_production` MODIFY COLUMN `start_produce` BIGINT(20) NOT NULL DEFAULT '0';
ALTER TABLE `castle_manor_production` MODIFY COLUMN `seed_price` BIGINT(20) NOT NULL DEFAULT '0';
ALTER TABLE `forums` ADD INDEX `forum_id_parent` (`forum_id`, `forum_parent`);
ALTER TABLE `game_log` CHANGE `etc_num9` `etc_num9` BIGINT NOT NULL default '0';
ALTER TABLE `game_log` CHANGE `etc_num10` `etc_num10` BIGINT NOT NULL default '0';
ALTER TABLE `heroes` DROP COLUMN `char_name`;
ALTER TABLE `heroes` DROP COLUMN `class_id`;
ALTER TABLE `items` CHANGE `loc` `loc` ENUM('CLANWH','CWH_BACK','FREIGHT','INVENTORY','LEASE','PAPERDOLL','VOID','WAREHOUSE','MONSTER') NOT NULL;
ALTER TABLE `items` DROP INDEX `key_loc_data`;
ALTER TABLE `items_delayed` MODIFY COLUMN `attribute` SMALLINT(6) NOT NULL DEFAULT '-1';
ALTER TABLE `items_delayed` MODIFY COLUMN `attribute_level` SMALLINT(6) NOT NULL DEFAULT '-1';
ALTER TABLE `item_attributes` MODIFY COLUMN `elemType` TINYINT(4) NOT NULL DEFAULT '-1';
ALTER TABLE `npc` MODIFY COLUMN `type` VARCHAR(30) NOT NULL DEFAULT 'L2Npc';
ALTER TABLE `posts` ADD INDEX `post_forum_id` (`post_forum_id`, `post_topic_id`);
ALTER TABLE `topic` ADD INDEX `topic_forum_id` (`topic_forum_id`);



-- DROP TABLE `pathfinder_results`;
DROP TABLE `armor`;
DROP TABLE `armor_ex`;
DROP TABLE `armorsets`;
DROP TABLE `auto_announcements`;
DROP TABLE `banned_ips`;
DROP TABLE `commons`;
DROP TABLE `etcitem`;
DROP TABLE `prices`;
DROP TABLE `too_many`;
DROP TABLE `weapon`;
DROP TABLE `weapon_ex`;

CREATE TABLE `vote` (
	`id` INT(10) NOT NULL DEFAULT '0',
	`HWID` VARCHAR(32) NOT NULL DEFAULT '',
	`vote` INT(10) NOT NULL DEFAULT '0',
	PRIMARY KEY (`id`, `HWID`, `vote`),
	INDEX `Index 2` (`id`, `vote`),
	INDEX `Index 3` (`id`),
	INDEX `Index 4` (`HWID`)
) ENGINE=MyISAM;
