package events.MonsterAtack;

import l2open.common.ThreadPoolManager;
import l2open.config.ConfigValue;
import l2open.extensions.scripts.Functions;
import l2open.extensions.scripts.ScriptFile;
import l2open.gameserver.Announcements;
import l2open.gameserver.instancemanager.CastleManager;
import l2open.gameserver.instancemanager.ZoneManager;
import l2open.gameserver.model.L2Character;
import l2open.gameserver.model.L2Player;
import l2open.gameserver.model.L2Zone;
import l2open.gameserver.model.entity.residence.Castle;
import l2open.gameserver.model.entity.siege.territory.TerritorySiege;
import l2open.gameserver.model.instances.L2NpcInstance;
import l2open.gameserver.serverpackets.SystemMessage;
import l2open.util.Rnd;

import java.util.ArrayList;
import java.util.Calendar;

/**
 * @author : Ragnarok
 * @modification : Nano
 * @date : 15.12.10    19:27
 */
public class MonsterAtack extends Functions implements ScriptFile 
{
    private static ArrayList<L2NpcInstance> mobs = new ArrayList<L2NpcInstance>();
    private static ArrayList<L2NpcInstance> allMobs = new ArrayList<L2NpcInstance>();
    private static boolean playerWin = false;
	private static boolean active = true;

    private enum EventTaskState 
	{
        START,
        END,
        TIME1,
        TIME2,
        TIME3,
        TIME4,
        TIME5,
        TIME6,
        DESPAWN
    }

    public class EventTask extends l2open.common.RunnableImpl 
	{
        EventTaskState state;
        ArrayList<L2NpcInstance> mb;

        public EventTask(EventTaskState state) 
		{
            this.state = state;
        }

		@SuppressWarnings("unchecked")
        public EventTask(EventTaskState state, Object clone)
		{
            this.state = state;
            this.mb = (ArrayList<L2NpcInstance>) clone;
        }

        @Override
        public void runImpl() 
		{
            switch (state) 
			{
                case START:
                    for (Castle castle : CastleManager.getInstance().getCastles().values()) 
					{
                        if (castle.getSiege() != null && castle.getSiege().isInProgress())
						{
                            ThreadPoolManager.getInstance().schedule(new EventTask(EventTaskState.START), ConfigValue.TMEventInterval);
                            return;
                        }
                    }

                    if (TerritorySiege.isInProgress()) 
					{
                        ThreadPoolManager.getInstance().schedule(new EventTask(EventTaskState.START), ConfigValue.TMEventInterval);
                        return;
                    }
                    ZoneManager.getInstance().getZoneById(L2Zone.ZoneType.peace_zone, 4600, false).setActive(false);
                    playerWin = false;
                    allMobs.clear();
                    Announcements.getInstance().announceToAll("Разведчики монстров замечены около Shuttgart! Нужно защитить жителей!");
                    ThreadPoolManager.getInstance().schedule(new EventTask(EventTaskState.TIME1), ConfigValue.TMTime1);
					active=true;
                    break;
                case TIME1:
					if(!active)
						return;
                    mobs.clear();
                    Announcements.getInstance().announceToAll("Монстры атакуют ворота Shuttgart! Нужно защитить жителей!");

                    for (int i = 0; i < ConfigValue.TMWave1Count; i++) 
					{
                        mobs.add(spawn(87368 + Rnd.get(200), -137176 + Rnd.get(100), -2288, ConfigValue.TMWave1));
                    }

                    for (int i = 0; i < ConfigValue.TMWave1Count; i++)
					{
                        mobs.add(spawn(92040 + Rnd.get(300), -139512 + Rnd.get(100), -2320, ConfigValue.TMWave1));
                    }

                    for (int i = 0; i < ConfigValue.TMWave1Count; i++) 
					{
                        mobs.add(spawn(82712 + Rnd.get(300), -139496 + Rnd.get(100), -2288, ConfigValue.TMWave1));
                    }

                    allMobs.addAll(mobs);
                    ThreadPoolManager.getInstance().schedule(new EventTask(EventTaskState.DESPAWN, mobs.clone()), ConfigValue.TMMobLife);
                    ThreadPoolManager.getInstance().schedule(new EventTask(EventTaskState.END), ConfigValue.TMTime1 + ConfigValue.TMTime2 + ConfigValue.TMTime3 + ConfigValue.TMTime4 + ConfigValue.TMTime5 + ConfigValue.TMTime6 + ConfigValue.BossLifeTime);
                    ThreadPoolManager.getInstance().schedule(new EventTask(EventTaskState.TIME2), ConfigValue.TMTime2);
                    break;
                case TIME2:
					if(!active)
						return;
                    mobs.clear();
                    for (int i = 0; i < ConfigValue.TMWave2Count; i++) 
					{
                        mobs.add(spawn(87586 + Rnd.get(300), -140366, -1541, ConfigValue.TMWave2));
                        mobs.add(spawn(87124 + Rnd.get(300), -140399, -1541, ConfigValue.TMWave2));
                        mobs.add(spawn(87345 + Rnd.get(300), -140634, -1541, ConfigValue.TMWave2));
                        mobs.add(spawn(85309 + Rnd.get(300), -141943, -1495, ConfigValue.TMWave2));
                        mobs.add(spawn(85066 + Rnd.get(300), -141654, -1541, ConfigValue.TMWave2));
                        mobs.add(spawn(84979 + Rnd.get(300), -141423, -1541, ConfigValue.TMWave2));
                        mobs.add(spawn(84951 + Rnd.get(300), -141875, -1541, ConfigValue.TMWave2));
                        mobs.add(spawn(89619 + Rnd.get(300), -141752, -1541, ConfigValue.TMWave2));
                        mobs.add(spawn(89398 + Rnd.get(300), -141956, -1487, ConfigValue.TMWave2));
                        mobs.add(spawn(89677 + Rnd.get(300), -141866, -1541, ConfigValue.TMWave2));
                        mobs.add(spawn(89712 + Rnd.get(300), -141388, -1541, ConfigValue.TMWave2));
                        mobs.add(spawn(87596 + Rnd.get(300), -140366, -1541, ConfigValue.TMWave2));
                        mobs.add(spawn(87134 + Rnd.get(300), -140399, -1541, ConfigValue.TMWave2));
                        mobs.add(spawn(87355 + Rnd.get(300), -140634, -1541, ConfigValue.TMWave2));
                        mobs.add(spawn(85319 + Rnd.get(300), -141943, -1495, ConfigValue.TMWave2));
                        mobs.add(spawn(85076 + Rnd.get(300), -141654, -1541, ConfigValue.TMWave2));
                        mobs.add(spawn(84989 + Rnd.get(300), -141423, -1541, ConfigValue.TMWave2));
                        mobs.add(spawn(84961 + Rnd.get(300), -141875, -1541, ConfigValue.TMWave2));
                        mobs.add(spawn(89629 + Rnd.get(300), -141752, -1541, ConfigValue.TMWave2));
                        mobs.add(spawn(89388 + Rnd.get(300), -141956, -1487, ConfigValue.TMWave2));
                        mobs.add(spawn(89687 + Rnd.get(300), -141866, -1541, ConfigValue.TMWave2));
                        mobs.add(spawn(89722 + Rnd.get(300), -141388, -1541, ConfigValue.TMWave2));
                    }
                    for (L2NpcInstance mob : mobs)
                        mob.setHeading(40240);
                    allMobs.addAll(mobs);
                    ThreadPoolManager.getInstance().schedule(new EventTask(EventTaskState.DESPAWN, mobs.clone()), ConfigValue.TMMobLife);
                    ThreadPoolManager.getInstance().schedule(new EventTask(EventTaskState.TIME3), ConfigValue.TMTime3);
                    break;
                case TIME3:
					if(!active)
						return;
                    Announcements.getInstance().announceToAll("Монстры уже в городе Shuttgart! Нужно защитить жителей!");
                    mobs.clear();
                    for (int i = 0; i < ConfigValue.TMWave3Count; i++) 
					{
                        mobs.add(spawn(88887 + Rnd.get(300), -142259, -1340, ConfigValue.TMWave3));
                        mobs.add(spawn(88780 + Rnd.get(300), -142220, -1340, ConfigValue.TMWave3));
                        mobs.add(spawn(88710 + Rnd.get(300), -142575, -1340, ConfigValue.TMWave3));
                        mobs.add(spawn(88503 + Rnd.get(300), -142547, -1340, ConfigValue.TMWave3));
                        mobs.add(spawn(87168 + Rnd.get(300), -141752, -1340, ConfigValue.TMWave3));
                        mobs.add(spawn(87313 + Rnd.get(300), -141630, -1340, ConfigValue.TMWave3));
                        mobs.add(spawn(87434 + Rnd.get(300), -141917, -1340, ConfigValue.TMWave3));
                        mobs.add(spawn(87204 + Rnd.get(300), -142156, -1340, ConfigValue.TMWave3));
                        mobs.add(spawn(86277 + Rnd.get(300), -142634, -1340, ConfigValue.TMWave3));
                        mobs.add(spawn(86180 + Rnd.get(300), -142421, -1340, ConfigValue.TMWave3));
                        mobs.add(spawn(85908 + Rnd.get(300), -142485, -1340, ConfigValue.TMWave3));
                        mobs.add(spawn(85943 + Rnd.get(300), -142266, -1340, ConfigValue.TMWave3));
                        mobs.add(spawn(88897 + Rnd.get(300), -142259, -1340, ConfigValue.TMWave3));
                        mobs.add(spawn(88790 + Rnd.get(300), -142220, -1340, ConfigValue.TMWave3));
                        mobs.add(spawn(88720 + Rnd.get(300), -142575, -1340, ConfigValue.TMWave3));
                        mobs.add(spawn(88513 + Rnd.get(300), -142547, -1340, ConfigValue.TMWave3));
                        mobs.add(spawn(87178 + Rnd.get(300), -141752, -1340, ConfigValue.TMWave3));
                        mobs.add(spawn(87323 + Rnd.get(300), -141630, -1340, ConfigValue.TMWave3));
                        mobs.add(spawn(87444 + Rnd.get(300), -141917, -1340, ConfigValue.TMWave3));
                        mobs.add(spawn(87214 + Rnd.get(300), -142156, -1340, ConfigValue.TMWave3));
                        mobs.add(spawn(86287 + Rnd.get(300), -142634, -1340, ConfigValue.TMWave3));
                        mobs.add(spawn(86190 + Rnd.get(300), -142421, -1340, ConfigValue.TMWave3));
                        mobs.add(spawn(85918 + Rnd.get(300), -142485, -1340, ConfigValue.TMWave3));
                        mobs.add(spawn(85953 + Rnd.get(300), -142266, -1340, ConfigValue.TMWave3));
                    }
                    for (L2NpcInstance mob : mobs)
                        mob.setHeading(40240);
                    allMobs.addAll(mobs);
                    ThreadPoolManager.getInstance().schedule(new EventTask(EventTaskState.DESPAWN, mobs.clone()), ConfigValue.TMMobLife);
                    ThreadPoolManager.getInstance().schedule(new EventTask(EventTaskState.TIME4), ConfigValue.TMTime4);
                    break;
                case TIME4:
					if(!active)
						return;
                    Announcements.getInstance().announceToAll("Монстры захватили главную площaдь Shuttgart! Нужно защитить жителей!");
                    mobs.clear();
                    for (int i = 0; i < ConfigValue.TMWave4Count; i++) 
					{
                        mobs.add(spawn(87168 + Rnd.get(300), -141752, -1340, ConfigValue.TMWave3));
                        mobs.add(spawn(87313 + Rnd.get(300), -141630, -1340, ConfigValue.TMWave3));
                        mobs.add(spawn(87434 + Rnd.get(300), -141917, -1340, ConfigValue.TMWave3));
                        mobs.add(spawn(87204 + Rnd.get(300), -142156, -1340, ConfigValue.TMWave3));
                        mobs.add(spawn(87955 + Rnd.get(300), -142804, -1340, ConfigValue.TMWave4));
                        mobs.add(spawn(87956 + Rnd.get(300), -142608, -1340, ConfigValue.TMWave4));
                        mobs.add(spawn(87642 + Rnd.get(300), -142589, -1340, ConfigValue.TMWave4));
                        mobs.add(spawn(87402 + Rnd.get(300), -142651, -1340, ConfigValue.TMWave4));
                        mobs.add(spawn(87261 + Rnd.get(300), -142558, -1340, ConfigValue.TMWave4));
                        mobs.add(spawn(87010 + Rnd.get(300), -142625, -1340, ConfigValue.TMWave4));
                        mobs.add(spawn(86771 + Rnd.get(300), -142818, -1340, ConfigValue.TMWave4));
                        mobs.add(spawn(87178 + Rnd.get(300), -141752, -1340, ConfigValue.TMWave3));
                        mobs.add(spawn(87323 + Rnd.get(300), -141630, -1340, ConfigValue.TMWave3));
                        mobs.add(spawn(87444 + Rnd.get(300), -141917, -1340, ConfigValue.TMWave3));
                        mobs.add(spawn(87214 + Rnd.get(300), -142156, -1340, ConfigValue.TMWave3));
                        mobs.add(spawn(87965 + Rnd.get(300), -142804, -1340, ConfigValue.TMWave4));
                        mobs.add(spawn(87966 + Rnd.get(300), -142608, -1340, ConfigValue.TMWave4));
                        mobs.add(spawn(87652 + Rnd.get(300), -142589, -1340, ConfigValue.TMWave4));
                        mobs.add(spawn(87412 + Rnd.get(300), -142651, -1340, ConfigValue.TMWave4));
                        mobs.add(spawn(87271 + Rnd.get(300), -142558, -1340, ConfigValue.TMWave4));
                        mobs.add(spawn(87020 + Rnd.get(300), -142625, -1340, ConfigValue.TMWave4));
                        mobs.add(spawn(86781 + Rnd.get(300), -142818, -1340, ConfigValue.TMWave4));
                    }
                    for (L2NpcInstance mob : mobs)
                        mob.setHeading(40240);
                    allMobs.addAll(mobs);
                    ThreadPoolManager.getInstance().schedule(new EventTask(EventTaskState.DESPAWN, mobs.clone()), ConfigValue.TMMobLife);
                    ThreadPoolManager.getInstance().schedule(new EventTask(EventTaskState.TIME5), ConfigValue.TMTime5);
                    break;
                case TIME5:
					if(!active)
						return;
                    Announcements.getInstance().announceToAll("Монстры прорываются в церковь Shuttgart! Нужно защитить жителей!");
                    mobs.clear();
                    for (int i = 0; i < ConfigValue.TMWave5Count; i++) 
					{
                        mobs.add(spawn(87505 + Rnd.get(300), -143049, -1292, ConfigValue.TMWave5));
                        mobs.add(spawn(87236 + Rnd.get(300), -142939, -1292, ConfigValue.TMWave5));
                        mobs.add(spawn(87202 + Rnd.get(300), -143257, -1292, ConfigValue.TMWave5));
                        mobs.add(spawn(87466 + Rnd.get(300), -143269, -1292, ConfigValue.TMWave5));
                        mobs.add(spawn(87426 + Rnd.get(300), -143537, -1292, ConfigValue.TMWave5));
                        mobs.add(spawn(87313 + Rnd.get(300), -143461, -1292, ConfigValue.TMWave5));
                        mobs.add(spawn(87358 + Rnd.get(300), -143878, -1292, ConfigValue.TMWave5));
                        mobs.add(spawn(87353 + Rnd.get(300), -144076, -1292, ConfigValue.TMWave5));
                        mobs.add(spawn(87350 + Rnd.get(300), -144355, -1292, ConfigValue.TMWave5));
                        mobs.add(spawn(87955 + Rnd.get(300), -142804, -1340, ConfigValue.TMWave4));
                        mobs.add(spawn(87956 + Rnd.get(300), -142608, -1340, ConfigValue.TMWave4));
                        mobs.add(spawn(87642 + Rnd.get(300), -142589, -1340, ConfigValue.TMWave4));
                        mobs.add(spawn(87402 + Rnd.get(300), -142651, -1340, ConfigValue.TMWave4));
                        mobs.add(spawn(87515 + Rnd.get(300), -143049, -1292, ConfigValue.TMWave5));
                        mobs.add(spawn(87236 + Rnd.get(300), -142939, -1292, ConfigValue.TMWave5));
                        mobs.add(spawn(87212 + Rnd.get(300), -143257, -1292, ConfigValue.TMWave5));
                        mobs.add(spawn(87476 + Rnd.get(300), -143269, -1292, ConfigValue.TMWave5));
                        mobs.add(spawn(87436 + Rnd.get(300), -143537, -1292, ConfigValue.TMWave5));
                        mobs.add(spawn(87323 + Rnd.get(300), -143461, -1292, ConfigValue.TMWave5));
                        mobs.add(spawn(87368 + Rnd.get(300), -143878, -1292, ConfigValue.TMWave5));
                        mobs.add(spawn(87363 + Rnd.get(300), -144076, -1292, ConfigValue.TMWave5));
                        mobs.add(spawn(87360 + Rnd.get(300), -144355, -1292, ConfigValue.TMWave5));
                        mobs.add(spawn(87965 + Rnd.get(300), -142804, -1340, ConfigValue.TMWave4));
                        mobs.add(spawn(87966 + Rnd.get(300), -142608, -1340, ConfigValue.TMWave4));
                        mobs.add(spawn(87652 + Rnd.get(300), -142589, -1340, ConfigValue.TMWave4));
                        mobs.add(spawn(87412 + Rnd.get(300), -142651, -1340, ConfigValue.TMWave4));
                    }
                    for (L2NpcInstance mob : mobs)
                        mob.setHeading(40240);
                    allMobs.addAll(mobs);
                    ThreadPoolManager.getInstance().schedule(new EventTask(EventTaskState.DESPAWN, mobs.clone()), ConfigValue.TMMobLife);
                    ThreadPoolManager.getInstance().schedule(new EventTask(EventTaskState.TIME6), ConfigValue.TMTime6);
                    break;
                case TIME6:
					if(!active)
						return;
                    Announcements.getInstance().announceToAll("Предводитель монстров и его свита захватили церковь Shuttgart! Нужно защитить жителей!");
                    mobs.clear();
                    for (int i = 0; i < ConfigValue.TMWave6Count; i++) 
					{
                        mobs.add(spawn(87466 + Rnd.get(100), -143269, -1292, ConfigValue.TMWave5));
                        mobs.add(spawn(87426 + Rnd.get(100), -143537, -1292, ConfigValue.TMWave5));
                        mobs.add(spawn(87313 + Rnd.get(100), -143461, -1292, ConfigValue.TMWave5));
                        mobs.add(spawn(87358 + Rnd.get(100), -143878, -1292, ConfigValue.TMWave5));
                        mobs.add(spawn(87353 + Rnd.get(100), -144076, -1292, ConfigValue.TMWave5));
                        mobs.add(spawn(87350 + Rnd.get(100), -144355, -1292, ConfigValue.TMWave5));
                        mobs.add(spawn(87310 + Rnd.get(100), -144725, -1292, ConfigValue.TMWave6));
                        mobs.add(spawn(87310 + Rnd.get(100), -144734, -1292, ConfigValue.TMWave6));
                        mobs.add(spawn(87361 + Rnd.get(100), -144645, -1292, ConfigValue.TMWave6));
                        mobs.add(spawn(87511 + Rnd.get(100), -144964, -1292, ConfigValue.TMWave6));
                        mobs.add(spawn(87305 + Rnd.get(100), -144697, -1292, ConfigValue.TMWave6));
                        mobs.add(spawn(87276 + Rnd.get(100), -145006, -1292, ConfigValue.TMWave6));
                        mobs.add(spawn(87114 + Rnd.get(100), -145285, -1292, ConfigValue.TMWave6));
                        mobs.add(spawn(87378 + Rnd.get(100), -145255, -1292, ConfigValue.TMWave6));
                        mobs.add(spawn(87575 + Rnd.get(100), -145295, -1292, ConfigValue.TMWave6));
                        mobs.add(spawn(87476 + Rnd.get(100), -143269, -1292, ConfigValue.TMWave5));
                        mobs.add(spawn(87436 + Rnd.get(100), -143537, -1292, ConfigValue.TMWave5));
                        mobs.add(spawn(87323 + Rnd.get(100), -143461, -1292, ConfigValue.TMWave5));
                        mobs.add(spawn(87368 + Rnd.get(100), -143878, -1292, ConfigValue.TMWave5));
                        mobs.add(spawn(87363 + Rnd.get(100), -144076, -1292, ConfigValue.TMWave5));
                        mobs.add(spawn(87360 + Rnd.get(100), -144355, -1292, ConfigValue.TMWave5));
                        mobs.add(spawn(87305 + Rnd.get(100), -144725, -1292, ConfigValue.TMWave6));
                        mobs.add(spawn(87305 + Rnd.get(100), -144734, -1292, ConfigValue.TMWave6));
                        mobs.add(spawn(87371 + Rnd.get(100), -144645, -1292, ConfigValue.TMWave6));
                        mobs.add(spawn(87521 + Rnd.get(100), -144964, -1292, ConfigValue.TMWave6));
                        mobs.add(spawn(87380 + Rnd.get(100), -144856, -1288, ConfigValue.TMWave6));
                        mobs.add(spawn(87286 + Rnd.get(100), -145006, -1292, ConfigValue.TMWave6));
                        mobs.add(spawn(87124 + Rnd.get(100), -145285, -1292, ConfigValue.TMWave6));
                        mobs.add(spawn(87388 + Rnd.get(100), -145255, -1292, ConfigValue.TMWave6));
                        mobs.add(spawn(87585 + Rnd.get(100), -145295, -1292, ConfigValue.TMWave6));
                    }
                    for (L2NpcInstance mob : mobs)
                        mob.setHeading(40240);
                    allMobs.addAll(mobs);
                    ThreadPoolManager.getInstance().schedule(new EventTask(EventTaskState.DESPAWN, mobs.clone()), ConfigValue.TMMobLife);
                    mobs.clear();
                    mobs.add(spawn(87362, -145640, -1292, ConfigValue.TMBoss));
                    allMobs.addAll(mobs);
                    ThreadPoolManager.getInstance().schedule(new EventTask(EventTaskState.DESPAWN, mobs.clone()), ConfigValue.BossLifeTime);
                    break;
                case DESPAWN:
                    for(L2NpcInstance npc : mb) 
                        if (npc != null)
                            npc.deleteMe();
                    break;
                case END:
                    if(!playerWin)
                        Announcements.getInstance().announceToAll("Игроки не сумели защитить город, Shuttgart был разграблен монстрами!");
                    ZoneManager.getInstance().getZoneById(L2Zone.ZoneType.peace_zone, 4600, false).setActive(true);
                    if(ConfigValue.TMEventInterval > 0)
                        ThreadPoolManager.getInstance().schedule(new EventTask(EventTaskState.START), ConfigValue.TMEventInterval);
            }
        }
    }

    public static void OnDie(L2Character self, L2Character killer) 
	{
		if (ConfigValue.TMEnabled)
		{
			if(!active)
				return;
			if (self.getNpcId() == ConfigValue.TMBoss) 
			{
				Announcements.getInstance().announceToAll("Главарь монстров повержен, игрок " + killer.getName() + " нанес последний удар!");
				Announcements.getInstance().announceToAll("Монстры отступают!");
				for (L2NpcInstance npc : allMobs) 
				{
					if (npc != null) 
					{
						npc.deleteMe();
					}
				}
				if(killer.getPlayer() != null) 
				{
					for(int i = 0; i < ConfigValue.TMItem.length; i++) 
					{
						if(Rnd.get(100) < ConfigValue.TMItemChanceBoss[i] && ConfigValue.TMItemColBoss[i] > 0) 
						{
							L2Player player = killer.getPlayer();
							player.getInventory().addItem(ConfigValue.TMItem[i], ConfigValue.TMItemColBoss[i]);
							if(ConfigValue.TMItem[i] == 57)
								player.sendPacket(new SystemMessage(SystemMessage.YOU_HAVE_OBTAINED_S1_ADENA).addNumber(ConfigValue.TMItemColBoss[i]));
							else if(ConfigValue.TMItemColBoss[i] == 1) 
							{
								final SystemMessage smsg = new SystemMessage(SystemMessage.YOU_HAVE_OBTAINED_S1);
								smsg.addItemName(ConfigValue.TMItem[i]);
								player.sendPacket(smsg);
							} 
							else 
							{
								final SystemMessage smsg = new SystemMessage(SystemMessage.YOU_HAVE_OBTAINED_S2_S1);
								smsg.addItemName(ConfigValue.TMItem[i]);
								smsg.addNumber(ConfigValue.TMItemColBoss[i]);
								player.sendPacket(smsg);
							}
						}
					}
				}
				playerWin = true;
			} 
			else if (self.getNpcId() == ConfigValue.TMWave1 || self.getNpcId() == ConfigValue.TMWave2 || self.getNpcId() == ConfigValue.TMWave3 || self.getNpcId() == ConfigValue.TMWave4 || self.getNpcId() == ConfigValue.TMWave5 || self.getNpcId() == ConfigValue.TMWave6) 
			{
				if(killer.getPlayer() != null) 
				{
					for (int i = 0; i < ConfigValue.TMItem.length; i++) 
					{
						if (Rnd.get(100) < ConfigValue.TMItemChance[i] && ConfigValue.TMItemCol[i] > 0) 
						{
							L2Player player = killer.getPlayer();
							player.getInventory().addItem(ConfigValue.TMItem[i], ConfigValue.TMItemCol[i]);
							if (ConfigValue.TMItem[i] == 57)
								player.sendPacket(new SystemMessage(SystemMessage.YOU_HAVE_OBTAINED_S1_ADENA).addNumber(ConfigValue.TMItemCol[i]));
							else if (ConfigValue.TMItemCol[i] == 1) 
							{
								final SystemMessage smsg = new SystemMessage(SystemMessage.YOU_HAVE_OBTAINED_S1);
								smsg.addItemName(ConfigValue.TMItem[i]);
								player.sendPacket(smsg);
							} 
							else
							{
								final SystemMessage smsg = new SystemMessage(SystemMessage.YOU_HAVE_OBTAINED_S2_S1);
								smsg.addItemName(ConfigValue.TMItem[i]);
								smsg.addNumber(ConfigValue.TMItemCol[i]);
								player.sendPacket(smsg);
							}
						}
					}
				}
			}
		}
    }

    /*public static void OnPlayerEnter(L2Player player) {
        if (ConfigValue.TMEnabled)
			Announcements.getInstance().announceToAll("Каждый день в 19:00 Монстры нападают на Shuttgart");
    }*/

    @Override
    public void onLoad() 
	{
        if (ConfigValue.TMEnabled)
		{
            Calendar cal = Calendar.getInstance();
            cal.set(Calendar.HOUR_OF_DAY, ConfigValue.TMStartHour);
            cal.set(Calendar.MINUTE, ConfigValue.TMStartMin);
            cal.set(Calendar.SECOND, 0);
            while (cal.getTimeInMillis() < System.currentTimeMillis())
                cal.add(Calendar.DAY_OF_YEAR, 1);
            ThreadPoolManager.getInstance().schedule(new EventTask(EventTaskState.START), cal.getTimeInMillis() - System.currentTimeMillis());
        }
    }

	public void start()
	{
		active=true;
		 ThreadPoolManager.getInstance().execute(new EventTask(EventTaskState.START));
	}
	
	public void stop()
	{
		if(!playerWin)
			Announcements.getInstance().announceToAll("Игроки не сумели защитить город, Shuttgart был разграблен монстрами!");
		ZoneManager.getInstance().getZoneById(L2Zone.ZoneType.peace_zone, 4600, false).setActive(true);
		ThreadPoolManager.getInstance().execute(new EventTask(EventTaskState.DESPAWN, allMobs));
		active=false;
	}

    @Override
    public void onReload() 
	{

    }

    @Override
    public void onShutdown() 
	{

    }
}