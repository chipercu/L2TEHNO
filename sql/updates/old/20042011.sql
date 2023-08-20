ALTER TABLE `clan_skills`
ADD COLUMN `squad_index`  smallint(6) NOT NULL DEFAULT '-1' AFTER `skill_name`,
DROP PRIMARY KEY,
ADD PRIMARY KEY (`clan_id`, `skill_id`, `squad_index`);