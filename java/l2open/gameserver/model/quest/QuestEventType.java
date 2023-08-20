package l2open.gameserver.model.quest;

public enum QuestEventType
{
	MOB_TARGETED_BY_SKILL, // onSkillUse action triggered when a character uses a skill on a mob
	MOBGOTATTACKED, // onAttack action triggered when a mob attacked by someone
	MOBKILLED, // onKill action triggered when a mob killed.
	QUEST_START, // onTalk action from start npcs
	QUEST_TALK, // onTalk action from npcs participating in a quest
	NPC_FIRST_TALK;
}