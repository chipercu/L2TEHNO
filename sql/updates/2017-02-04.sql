ALTER TABLE `skills` ADD COLUMN `affect_range` int(5) NOT NULL DEFAULT '-1' AFTER `next_action`;
ALTER TABLE `skills` ADD COLUMN `affect_limit` varchar(7) NOT NULL DEFAULT '0;0' AFTER `affect_range`;
ALTER TABLE `skills` ADD COLUMN `fan_range` varchar(16) NOT NULL DEFAULT '0;0;0;0' AFTER `affect_limit`;
ALTER TABLE `skills` ADD COLUMN `affect_scope` enum('balakas_scope','dead_pledge','dead_union','fan','none','party','party_pledge','pledge','point_blank','range','range_sort_by_hp','ring_range','single','square','square_pb','static_object_scope','wyvern_scope') NOT NULL DEFAULT 'none' AFTER `target_type`;
ALTER TABLE `skills` ADD COLUMN `target_type` enum('advance_base','artillery','door_treasure','enemy','enemy_not','enemy_only','fortress_flagpole','ground','holything','item','none','npc_body','others','pc_body','self','summon','target','wyvern_target') NOT NULL DEFAULT 'none' AFTER `next_action`;
ALTER TABLE `skills` ADD COLUMN `affect_object` enum('all','clan','friend','hidden_place','invisible','none','not_friend','object_dead_npc_body','undead_real_enemy','wyvern_object') NOT NULL DEFAULT 'none' AFTER `affect_scope`;
