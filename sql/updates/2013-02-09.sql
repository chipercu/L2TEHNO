ALTER TABLE `character_subclasses` DROP COLUMN `skills`;
ALTER TABLE `character_subclasses` ADD COLUMN `certification` smallint(5) default '0' AFTER `death_penalty`;