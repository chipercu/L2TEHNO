package npc.model;

import l2open.gameserver.ai.CtrlEvent;
import l2open.gameserver.geodata.GeoEngine;
import l2open.gameserver.idfactory.IdFactory;
import l2open.gameserver.model.L2Character;
import l2open.gameserver.model.L2Skill;
import l2open.gameserver.model.Reflection;
import l2open.gameserver.model.entity.KamalokaNightmare;
import l2open.gameserver.model.instances.L2MonsterInstance;
import l2open.gameserver.tables.NpcTable;
import l2open.gameserver.templates.L2NpcTemplate;
import l2open.util.Rnd;

public class KanabionInstance extends L2MonsterInstance
{
	private boolean canSpawn = false; // Изначально спавн доплеров и войдов запрещен.
	public KanabionInstance(int objectId, L2NpcTemplate template)
	{
		super(objectId, template);
	}

	@Override
	public void reduceCurrentHp(double i, L2Character attacker, L2Skill skill, boolean awake, boolean standUp, boolean directHp, boolean canReflect, boolean isDot, double i2, boolean sendMesseg, boolean bow, boolean crit, boolean tp)
	{
		if(Rnd.chance(i * 50 / getMaxHp())) // Шанс появления.
			canSpawn = true; // Если шанс успешный, разрешаем спавн.
		super.reduceCurrentHp(i, attacker, skill, awake, standUp, directHp, canReflect, isDot, i2, sendMesseg, bow, crit, tp);
		if (canSpawn && isDead()) // Если канабион умер и спавн разрешен, спавним.
		{
			KanabionInstance npc = new KanabionInstance(IdFactory.getInstance().getNextId(), NpcTable.getTemplate(getNext()));
			npc.setSpawnedLoc(GeoEngine.findPointToStay(getX(), getY(), getZ(), 100, 120, getReflection().getGeoIndex()));
			npc.setReflection(getReflection().getId());
			npc.onSpawn();
			npc.spawnMe(npc.getSpawnedLoc());
			npc.getAI().notifyEvent(CtrlEvent.EVT_AGGRESSION, attacker, Rnd.get(1, 100));
			canSpawn = false; // Закрываем спавн. Если этого не сделать, то тригер останется открытым для всех остальных мобов.
		}
	}

	@Override
	public void doDie(L2Character killer)
	{
		Reflection r = getReflection();
		if(r instanceof KamalokaNightmare)
			((KamalokaNightmare) r).registerKilled(getTemplate());
		super.doDie(killer);
	}

	private int getNext()
	{
		switch(getNpcId())
		{
			case 22452: // White Skull Kanabion
				return 22453; // Doppler
			case 22453:
			case 22454:
				return 22454; // Void

			case 22455: // Begrudged Kanabion
				return 22456; // Doppler
			case 22456:
			case 22457:
				return 22457; // Void

			case 22458: // Rotten Kanabion
				return 22459; // Doppler
			case 22459:
			case 22460:
				return 22460; // Void

			case 22461: // Gluttonous Kanabion
				return 22462; // Doppler
			case 22462:
			case 22463:
				return 22463; // Void

			case 22464: // Callous Kanabion
				return 22465; // Doppler
			case 22465:
			case 22466:
				return 22466; // Void

			case 22467: // Savage Kanabion
				return 22468; // Doppler
			case 22468:
			case 22469:
				return 22469; // Void

			case 22470: // Peerless Kanabion
				return 22471; // Doppler
			case 22471:
			case 22472:
				return 22472; // Void

			case 22473: // Massive Kanabion
				return 22474; // Doppler
			case 22474:
			case 22475:
				return 22475; // Void

			case 22476: // Fervent Kanabion
				return 22477; // Doppler
			case 22477:
			case 22478:
				return 22478; // Void

			case 22479: // Ruptured Kanabion
				return 22480; // Doppler
			case 22480:
			case 22481:
				return 22481; // Void

			case 22482: // Sword Kanabion
				return 22483; // Doppler
			case 22483:
			case 22484:
				return 22484; // Void

			default:
				return 0; // такого быть не должно
		}
	}
}