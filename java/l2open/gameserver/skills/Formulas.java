package l2open.gameserver.skills;

import l2open.config.*;
import l2open.extensions.multilang.CustomMessage;
import l2open.gameserver.cache.Msg;
import l2open.gameserver.instancemanager.CastleManager;
import l2open.gameserver.instancemanager.ClanHallManager;
import l2open.gameserver.instancemanager.FortressManager;
import l2open.gameserver.model.*;
import l2open.gameserver.model.L2Skill.Element;
import l2open.gameserver.model.L2Skill.SkillType;
import l2open.gameserver.model.base.Race;
import l2open.gameserver.model.entity.SevenSigns;
import l2open.gameserver.model.entity.residence.*;
import l2open.gameserver.model.instances.L2DoorInstance;
import l2open.gameserver.model.instances.L2NpcInstance;
import l2open.gameserver.model.instances.L2ReflectionBossInstance;
import l2open.gameserver.model.items.Inventory;
import l2open.gameserver.model.items.L2ItemInstance;
import l2open.gameserver.serverpackets.SystemMessage;
import l2open.gameserver.skills.conditions.ConditionPlayerState;
import l2open.gameserver.skills.conditions.ConditionPlayerState.CheckPlayerState;
import l2open.gameserver.skills.effects.EffectTemplate;
import l2open.gameserver.skills.funcs.Func;
import l2open.gameserver.templates.L2PlayerTemplate;
import l2open.gameserver.templates.L2Weapon;
import l2open.gameserver.templates.L2Weapon.WeaponType;
import l2open.util.Log;
import l2open.util.Rnd;
import l2open.util.Util;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.util.Map.Entry;
import java.util.TreeMap;
import java.util.logging.Logger;

import module.formulas.PtsFormulas;

public class Formulas 
{
    protected static final Logger _log = Logger.getLogger(Formulas.class.getName());

    public static int MAX_STAT_VALUE = 100;

    public static final double[] WITbonus = new double[MAX_STAT_VALUE];
    public static final double[] MENbonus = new double[MAX_STAT_VALUE];
    public static final double[] INTbonus = new double[MAX_STAT_VALUE];
    public static final double[] STRbonus = new double[MAX_STAT_VALUE];
    public static final double[] DEXbonus = new double[MAX_STAT_VALUE];
    public static final double[] CONbonus = new double[MAX_STAT_VALUE];

	private static final int[] ACCURACY_EVASION_LEVEL_BONUS = { 0,
		1, 2, 3, 4, 5, 6, 7, 8, 9, 10, //  1-10
		11, 12, 13, 14, 15, 16, 17, 18, 18, 20, // 11-20
		21, 22, 23, 24, 25, 26, 27, 28, 29, 30, // 21-30
		31, 32, 33, 34, 35, 36, 37, 38, 39, 40, // 31-40
		41, 42, 43, 44, 45, 46, 47, 48, 49, 50, // 41-50
		51, 52, 53, 54, 55, 56, 57, 58, 59, 60, // 51-60
		61, 62, 63, 64, 65, 66, 67, 68, 69, 71, // 61-70
		73, 75, 77, 79, 81, 83, 85, 89, 91, 94, // 71-80
		96, 98, 100, 103, 105, 107, 109, 112, 114, 116,  // 81-90
		118, 120, 122, 125, 127, 129, 132, 134, 136, 138  // 91-100
		};

    static 
	{
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setValidating(false);
        factory.setIgnoringComments(true);
        File file = new File(ConfigValue.AttributeBonusFile);
        Document doc = null;

        try 
		{
            doc = factory.newDocumentBuilder().parse(file);
        }
		catch (SAXException e) 
		{
            e.printStackTrace();
        } 
		catch (IOException e) 
		{
            e.printStackTrace();
        } 
		catch (ParserConfigurationException e) 
		{
            e.printStackTrace();
        }

        int i;
        double val;

        if (doc != null)
            for (Node z = doc.getFirstChild(); z != null; z = z.getNextSibling())
                for (Node n = z.getFirstChild(); n != null; n = n.getNextSibling()) 
				{
                    if (n.getNodeName().equalsIgnoreCase("str_bonus"))
                        for (Node d = n.getFirstChild(); d != null; d = d.getNextSibling())
						{
                            String node = d.getNodeName();
                            if (node.equalsIgnoreCase("set")) 
							{
                                i = Integer.valueOf(d.getAttributes().getNamedItem("attribute").getNodeValue());
                                val = Integer.valueOf(d.getAttributes().getNamedItem("val").getNodeValue());
                                STRbonus[i] = (100 + val) / 100;
                            }
                        }
                    if (n.getNodeName().equalsIgnoreCase("int_bonus"))
                        for (Node d = n.getFirstChild(); d != null; d = d.getNextSibling()) 
						{
                            String node = d.getNodeName();
                            if (node.equalsIgnoreCase("set")) 
							{
                                i = Integer.valueOf(d.getAttributes().getNamedItem("attribute").getNodeValue());
                                val = Integer.valueOf(d.getAttributes().getNamedItem("val").getNodeValue());
                                INTbonus[i] = (100 + val) / 100;
                            }
                        }
                    if (n.getNodeName().equalsIgnoreCase("con_bonus"))
                        for (Node d = n.getFirstChild(); d != null; d = d.getNextSibling()) 
						{
                            String node = d.getNodeName();
                            if (node.equalsIgnoreCase("set"))
							{
                                i = Integer.valueOf(d.getAttributes().getNamedItem("attribute").getNodeValue());
                                val = Integer.valueOf(d.getAttributes().getNamedItem("val").getNodeValue());
                                CONbonus[i] = (100 + val) / 100;
                            }
                        }
                    if (n.getNodeName().equalsIgnoreCase("men_bonus"))
                        for (Node d = n.getFirstChild(); d != null; d = d.getNextSibling()) 
						{
                            String node = d.getNodeName();
                            if (node.equalsIgnoreCase("set")) 
							{
                                i = Integer.valueOf(d.getAttributes().getNamedItem("attribute").getNodeValue());
                                val = Integer.valueOf(d.getAttributes().getNamedItem("val").getNodeValue());
                                MENbonus[i] = (100 + val) / 100;
                            }
                        }
                    if (n.getNodeName().equalsIgnoreCase("dex_bonus"))
                        for (Node d = n.getFirstChild(); d != null; d = d.getNextSibling()) 
						{
                            String node = d.getNodeName();
                            if (node.equalsIgnoreCase("set"))
							{
                                i = Integer.valueOf(d.getAttributes().getNamedItem("attribute").getNodeValue());
                                val = Integer.valueOf(d.getAttributes().getNamedItem("val").getNodeValue());
                                DEXbonus[i] = (100 + val) / 100;
                            }
                        }
                    if (n.getNodeName().equalsIgnoreCase("wit_bonus"))
                        for (Node d = n.getFirstChild(); d != null; d = d.getNextSibling()) 
						{
                            String node = d.getNodeName();
                            if (node.equalsIgnoreCase("set")) 
							{
                                i = Integer.valueOf(d.getAttributes().getNamedItem("attribute").getNodeValue());
                                val = Integer.valueOf(d.getAttributes().getNamedItem("val").getNodeValue());
                                WITbonus[i] = (100 + val) / 100;
                            }
                        }
                }
    }

    private static class FuncMultRegenResting extends Func 
	{
        static final FuncMultRegenResting[] func = new FuncMultRegenResting[Stats.NUM_STATS];

        static Func getFunc(Stats stat) 
		{
            int pos = stat.ordinal();
            if (func[pos] == null)
                func[pos] = new FuncMultRegenResting(stat);
            return func[pos];
        }

        private FuncMultRegenResting(Stats stat) 
		{
            super(stat, 0x30, null);
            setCondition(new ConditionPlayerState(CheckPlayerState.RESTING, true));
        }

        @Override
        public void calc(Env env) 
		{
            if (env.character.isPlayer() && env.character.getLevel() <= 40 && ((L2Player) env.character).getClassId().getLevel() < 3 && _stat == Stats.REGENERATE_HP_RATE)
                env.value *= 6; // TODO: переделать красивее
            else
                env.value *= 1.5;
        }
    }

    private static class FuncMultRegenStanding extends Func 
	{
        static final FuncMultRegenStanding[] func = new FuncMultRegenStanding[Stats.NUM_STATS];

        static Func getFunc(Stats stat) 
		{
            int pos = stat.ordinal();
            if (func[pos] == null)
                func[pos] = new FuncMultRegenStanding(stat);
            return func[pos];
        }

        private FuncMultRegenStanding(Stats stat) 
		{
            super(stat, 0x30, null);
            setCondition(new ConditionPlayerState(CheckPlayerState.STANDING, true));
        }

        @Override
        public void calc(Env env) 
		{
            env.value *= 1.1;
        }
    }

    private static class FuncMultRegenRunning extends Func 
	{
        static final FuncMultRegenRunning[] func = new FuncMultRegenRunning[Stats.NUM_STATS];

        static Func getFunc(Stats stat) 
		{
            int pos = stat.ordinal();
            if (func[pos] == null)
                func[pos] = new FuncMultRegenRunning(stat);
            return func[pos];
        }

        private FuncMultRegenRunning(Stats stat) 
		{
            super(stat, 0x30, null);
            setCondition(new ConditionPlayerState(CheckPlayerState.RUNNING, true));
        }

        @Override
        public void calc(Env env) 
		{
            env.value *= 0.7;
        }
    }

    private static class FuncPAtkMul extends Func 
	{
        static final FuncPAtkMul func = new FuncPAtkMul();

        private FuncPAtkMul() 
		{
            super(Stats.p_physical_attack, 0x20, null);
        }

        @Override
        public void calc(Env env) 
		{
            env.value *= STRbonus[env.character.getSTR()] * env.character.getLevelMod();
        }
    }

    private static class FuncMAtkMul extends Func 
	{
        static final FuncMAtkMul func = new FuncMAtkMul();

        private FuncMAtkMul() 
		{
            super(Stats.p_magical_attack, 0x20, null);
        }

        @Override
        public void calc(Env env) 
		{
            //{Wpn*(lvlbn^2)*[(1+INTbn)^2]+Msty}
            double ib = INTbonus[env.character.getINT()];
            double lvlb = env.character.getLevelMod();
            env.value *= lvlb * lvlb * ib * ib;
        }
    }

    private static class FuncPDefMul extends Func 
	{
        static final FuncPDefMul func = new FuncPDefMul();

        private FuncPDefMul() 
		{
            super(Stats.p_physical_defence, 0x20, null);
        }

        @Override
        public void calc(Env env) 
		{
            env.value *= env.character.getLevelMod();
        }
    }

    private static class FuncMDefMul extends Func 
	{
        static final FuncMDefMul func = new FuncMDefMul();

        private FuncMDefMul() 
		{
            super(Stats.p_magical_defence, 0x20, null);
        }

        @Override
        public void calc(Env env) 
		{
            env.value *= MENbonus[env.character.getMEN()] * env.character.getLevelMod();
        }
    }

    private static class FuncAttackRange extends Func 
	{
        static final FuncAttackRange func = new FuncAttackRange();

        private FuncAttackRange() 
		{
            super(Stats.POWER_ATTACK_RANGE, 0x20, null);
        }

        @Override
        public void calc(Env env) 
		{
            L2Weapon weapon = env.character.getActiveWeaponItem();
            if (weapon != null)
                env.value += weapon.getAttackRange();
        }
    }

    private static class FuncAccuracyAdd extends Func 
	{
        static final FuncAccuracyAdd func = new FuncAccuracyAdd();

        private FuncAccuracyAdd() 
		{
            super(Stats.p_hit, 0x10, null);
        }

        @Override
        public void calc(Env env)
		{
            if(env.character.isPet())
                return;

			env.value += Math.sqrt(env.character.getDEX()) * 6 + ACCURACY_EVASION_LEVEL_BONUS[env.character.getLevel()];

			if(env.character.isSummon())
				env.value += 4;
			//if(env.character.isSummon())
			//	env.value += env.character.getLevel() < 60 ? 4 : 5;
        }
    }

    private static class FuncEvasionAdd extends Func 
	{
        static final FuncEvasionAdd func = new FuncEvasionAdd();

        private FuncEvasionAdd() 
		{
            super(Stats.EVASION_RATE, 0x10, null);
        }

        @Override
        public void calc(Env env) 
		{
			env.value += Math.sqrt(env.character.getDEX()) * 6 + ACCURACY_EVASION_LEVEL_BONUS[env.character.getLevel()];
        }
    }

    private static class FuncMCriticalRateMul extends Func
	{
        static final FuncMCriticalRateMul func = new FuncMCriticalRateMul();

        private FuncMCriticalRateMul() 
		{
            super(Stats.MCRITICAL_RATE, 0x10, null);
        }

        @Override
        public void calc(Env env) 
		{
            env.value *= WITbonus[env.character.getWIT()];
        }
    }

    private static class FuncPCriticalRateMul extends Func 
	{
        static final FuncPCriticalRateMul func = new FuncPCriticalRateMul();

        private FuncPCriticalRateMul() 
		{
            super(Stats.CRITICAL_BASE, 0x10, null);
        }

		// p_critical_rate;{all};30;diff - используем baseCrit, значение умножаем на 10, используется только add и sub, mul нету.
		// p_critical_rate;{all};30;per - используется rCrit, используется только mul, add и sub не использывать, для этого нужно использовать параметр baseCrit.
        @Override
        public void calc(Env env) 
		{
            if (!(env.character instanceof L2Summon))
                env.value *= DEXbonus[env.character.getDEX()];
            env.value *= 0.01 * env.character.calcStat(Stats.CRITICAL_RATE, env.target, env.skill);
			if(env.character.isPlayer())
				env.value *= ((L2PlayerTemplate)env.character.getTemplate()).p_critical_rate_mod;
        }
    }

	private static class FuncPCriticalDamage extends Func 
	{
        static final FuncPCriticalDamage func = new FuncPCriticalDamage();

        private FuncPCriticalDamage() 
		{
            super(Stats.CRITICAL_DAMAGE, 0x30, null);
        }

        @Override
        public void calc(Env env) 
		{
            if(env.character.isPlayer())
				env.value *= env.character.getPlayer().getTemplate().p_critical_damage_per_mod;
        }
    }

	private static class FuncPCriticalDamageStatic extends Func 
	{
        static final FuncPCriticalDamageStatic func = new FuncPCriticalDamageStatic();

        private FuncPCriticalDamageStatic() 
		{
            super(Stats.CRITICAL_DAMAGE_STATIC, 0x30, null);
        }

        @Override
        public void calc(Env env) 
		{
            if(env.character.isPlayer())
				env.value += env.character.getPlayer().getTemplate().p_critical_damage_diff_mod;
        }
    }

    private static class FuncMoveSpeedMul extends Func 
	{
        static final FuncMoveSpeedMul func = new FuncMoveSpeedMul();

        private FuncMoveSpeedMul() 
		{
            super(Stats.p_speed, 0x20, null);
        }

        @Override
        public void calc(Env env) 
		{
            env.value *= DEXbonus[env.character.getDEX()];
        }
    }

    private static class FuncPAtkSpeedMul extends Func 
	{
        static final FuncPAtkSpeedMul func = new FuncPAtkSpeedMul();

        private FuncPAtkSpeedMul()
		{
            super(Stats.p_attack_speed, 0x20, null);
        }

        @Override
        public void calc(Env env) 
		{
            env.value *= DEXbonus[env.character.getDEX()];
        }
    }

    private static class FuncMAtkSpeedMul extends Func 
	{
        static final FuncMAtkSpeedMul func = new FuncMAtkSpeedMul();

        private FuncMAtkSpeedMul() 
		{
            super(Stats.p_magic_speed, 0x20, null);
        }

        @Override
        public void calc(Env env) 
		{
            int arg = env.character.getWIT();
            if (arg > 100)
                arg = 100;
            env.value *= WITbonus[arg];
        }
    }

    private static class FuncHennaSTR extends Func 
	{
        static final FuncHennaSTR func = new FuncHennaSTR();

        private FuncHennaSTR() 
		{
            super(Stats.STAT_STR, 0x10, null);
        }

        @Override
        public void calc(Env env) 
		{
            L2Player pc = (L2Player) env.character;
            if (pc != null)
                env.value = Math.max(1, env.value + pc.getHennaStatSTR());
        }
    }

    private static class FuncHennaDEX extends Func 
	{
        static final FuncHennaDEX func = new FuncHennaDEX();

        private FuncHennaDEX() 
		{
            super(Stats.STAT_DEX, 0x10, null);
        }

        @Override
        public void calc(Env env) 
		{
            L2Player pc = (L2Player) env.character;
            if (pc != null)
                env.value = Math.max(1, env.value + pc.getHennaStatDEX());
        }
    }

    private static class FuncHennaINT extends Func 
	{
        static final FuncHennaINT func = new FuncHennaINT();

        private FuncHennaINT() 
		{
            super(Stats.STAT_INT, 0x10, null);
        }

        @Override
        public void calc(Env env) 
		{
            L2Player pc = (L2Player) env.character;
            if (pc != null)
                env.value = Math.max(1, env.value + pc.getHennaStatINT());
        }
    }

    private static class FuncHennaMEN extends Func 
	{
        static final FuncHennaMEN func = new FuncHennaMEN();

        private FuncHennaMEN() 
		{
            super(Stats.STAT_MEN, 0x10, null);
        }

        @Override
        public void calc(Env env) 
		{
            L2Player pc = (L2Player) env.character;
            if (pc != null)
                env.value = Math.max(1, env.value + pc.getHennaStatMEN());
        }
    }

    private static class FuncHennaCON extends Func 
	{
        static final FuncHennaCON func = new FuncHennaCON();

        private FuncHennaCON() 
		{
            super(Stats.STAT_CON, 0x10, null);
        }

        @Override
        public void calc(Env env) 
		{
            L2Player pc = (L2Player) env.character;
            if (pc != null)
                env.value = Math.max(1, env.value + pc.getHennaStatCON());
        }
    }

    private static class FuncHennaWIT extends Func 
	{
        static final FuncHennaWIT func = new FuncHennaWIT();

        private FuncHennaWIT() 
		{
            super(Stats.STAT_WIT, 0x10, null);
        }

        @Override
        public void calc(Env env) 
		{
            L2Player pc = (L2Player) env.character;
            if (pc != null)
                env.value = Math.max(1, env.value + pc.getHennaStatWIT());
        }
    }

    private static class FuncMaxHpAdd extends Func 
	{
        static final FuncMaxHpAdd func = new FuncMaxHpAdd();

        private FuncMaxHpAdd() 
		{
            super(Stats.p_max_hp, 0x10, null);
        }

        @Override
        public void calc(Env env) 
		{
            L2PlayerTemplate t = (L2PlayerTemplate) env.character.getTemplate();
            int lvl = Math.max(0, env.character.getLevel() - t.classBaseLevel);
            double hpmod = t.lvlHpMod * lvl;
            double hpmax = (t.lvlHpAdd + hpmod) * lvl;
            double hpmin = t.lvlHpAdd * lvl + hpmod;
            env.value += (hpmax + hpmin) / 2;
        }
    }

    private static class FuncMaxHpMul extends Func 
	{
        static final FuncMaxHpMul func = new FuncMaxHpMul();

        private FuncMaxHpMul() 
		{
            super(Stats.p_max_hp, 0x20, null);
        }

        @Override
        public void calc(Env env) 
		{
            env.value *= CONbonus[env.character.getCON()];
        }
    }

    private static class FuncMaxCpAdd extends Func 
	{
        static final FuncMaxCpAdd func = new FuncMaxCpAdd();

        private FuncMaxCpAdd() 
		{
            super(Stats.p_max_cp, 0x10, null);
        }

        @Override
        public void calc(Env env) 
		{
            L2PlayerTemplate t = (L2PlayerTemplate) env.character.getTemplate();
            int lvl = Math.max(0, env.character.getLevel() - t.classBaseLevel);
            double cpmod = t.lvlCpMod * lvl;
            double cpmax = (t.lvlCpAdd + cpmod) * lvl;
            double cpmin = t.lvlCpAdd * lvl + cpmod;
            env.value += (cpmax + cpmin) / 2;
        }
    }

    private static class FuncMaxCpMul extends Func 
	{
        static final FuncMaxCpMul func = new FuncMaxCpMul();

        private FuncMaxCpMul() 
		{
            super(Stats.p_max_cp, 0x20, null);
        }

        @Override
        public void calc(Env env) 
		{
            double cpSSmod = 1;
            int sealOwnedBy = SevenSigns.getInstance().getSealOwner(SevenSigns.SEAL_STRIFE);
            int playerCabal = SevenSigns.getInstance().getPlayerCabal((L2Player) env.character);

            if (sealOwnedBy != SevenSigns.CABAL_NULL)
                if (playerCabal == sealOwnedBy)
                    cpSSmod = 1.1;
                else
                    cpSSmod = 0.9;

            env.value *= CONbonus[env.character.getCON()] * cpSSmod;
        }
    }

    private static class FuncMaxMpAdd extends Func 
	{
        static final FuncMaxMpAdd func = new FuncMaxMpAdd();

        private FuncMaxMpAdd() 
		{
            super(Stats.p_max_mp, 0x10, null);
        }

        @Override
        public void calc(Env env) 
		{
            L2PlayerTemplate t = (L2PlayerTemplate) env.character.getTemplate();
            int lvl = Math.max(0, env.character.getLevel() - t.classBaseLevel);
            double mpmod = t.lvlMpMod * lvl;
            double mpmax = (t.lvlMpAdd + mpmod) * lvl;
            double mpmin = t.lvlMpAdd * lvl + mpmod;
            env.value += (mpmax + mpmin) / 2;
        }
    }

    private static class FuncMaxMpMul extends Func 
	{
        static final FuncMaxMpMul func = new FuncMaxMpMul();

        private FuncMaxMpMul() 
		{
            super(Stats.p_max_mp, 0x20, null);
        }

        @Override
        public void calc(Env env) 
		{
            env.value *= MENbonus[env.character.getMEN()];
        }
    }

    private static class FuncPDamageResists extends Func 
	{
        static final FuncPDamageResists func = new FuncPDamageResists();

        private FuncPDamageResists() 
		{
            super(Stats.PHYSICAL_DAMAGE, 0x30, null);
        }

        @Override
        public void calc(Env env) 
		{
            if(env.target.isRaid() && env.character.getLevel() - env.target.getLevel() > ConfigValue.RaidMaxLevelDiff) 
			{
                env.value = 1;
                return;
            }

            L2Weapon weapon = env.character.getActiveWeaponItem();
			double mod=1;
			if(weapon == null)
			{
				if(env.character.getFistWeaponType().getTrait().fullResist(env.target))
					mod = 0;
				else
					mod = Math.min(ConfigValue.MaxWeaponTraitMod, Math.max(ConfigValue.MinWeaponTraitMod, env.character.getFistWeaponType().getTrait().calcResist(env.target)));
				env.value *= mod;
			}
			else if(weapon.getItemType().getTrait() != null)
			{
				if(weapon.getItemType().getTrait().fullResist(env.target))
					mod = 0;
				else
					mod = Math.min(ConfigValue.MaxWeaponTraitMod, Math.max(ConfigValue.MinWeaponTraitMod, weapon.getItemType().getTrait().calcResist(env.target)));
				env.value *= mod;
			}
			if(env.character.getPlayer() != null && env.character.getPlayer().isGM())
				env.character.getPlayer().sendMessage("Weapon Resist: dam="+env.value+" resist="+mod+" real="+(env.value/mod));

            env.value = calcDamageResists(env.skill, env.character, env.target, env.value);
        }
    }

    private static class FuncMDamageResists extends Func 
	{
        static final FuncMDamageResists func = new FuncMDamageResists();

        private FuncMDamageResists() 
		{
            super(Stats.MAGIC_DAMAGE, 0x30, null);
        }

        @Override
        public void calc(Env env) 
		{
            if (env.target.isRaid() && Math.abs(env.character.getLevel() - env.target.getLevel()) > ConfigValue.RaidMaxLevelDiff)
			{
                env.value = 1;
                return;
            }
            env.value = calcDamageResists(env.skill, env.character, env.target, env.value);
        }
    }

    private static class FuncInventory extends Func 
	{
        static final FuncInventory func = new FuncInventory();

        private FuncInventory() 
		{
            super(Stats.INVENTORY_LIMIT, 0x01, null);
        }

        @Override
        public void calc(Env env) 
		{
            L2Player player = (L2Player) env.character;
            if (player.isGM())
                env.value = ConfigValue.MaximumSlotsForGMPlayer;
            else if (player.getTemplate().race == Race.dwarf)
                env.value = ConfigValue.MaximumSlotsForDwarf;
            else
                env.value = ConfigValue.MaximumSlotsForNoDwarf;
            env.value += player.getExpandInventory();
        }
    }

    private static class FuncWarehouse extends Func 
	{
        static final FuncWarehouse func = new FuncWarehouse();

        private FuncWarehouse() 
		{
            super(Stats.STORAGE_LIMIT, 0x01, null);
        }

        @Override
        public void calc(Env env) 
		{
            L2Player player = (L2Player) env.character;
            if (player.getTemplate().race == Race.dwarf)
                env.value = ConfigValue.BaseWarehouseSlotsForDwarf;
            else
                env.value = ConfigValue.BaseWarehouseSlotsForNoDwarf;
            env.value += player.getExpandWarehouse();
        }
    }

    private static class FuncTradeLimit extends Func 
	{
        static final FuncTradeLimit func = new FuncTradeLimit();

        private FuncTradeLimit() 
		{
            super(Stats.TRADE_LIMIT, 0x01, null);
        }

        @Override
        public void calc(Env env) 
		{
            L2Player _cha = (L2Player) env.character;
            if (_cha.getRace() == Race.dwarf)
                env.value = ConfigValue.MaxPvtStoreSlotsDwarf;
            else
                env.value = ConfigValue.MaxPvtStoreSlotsOther;
        }
    }

    private static class FuncSDefAll extends Func 
	{
        static final FuncSDefAll func = new FuncSDefAll();

        private FuncSDefAll() 
		{
            super(Stats.SHIELD_RATE, 0x20, null);
        }

        @Override
        public void calc(Env env) 
		{
            if (env.value == 0)
                return;

            L2Character target = env.target;
            if (target != null) 
			{
                L2Weapon weapon = target.getActiveWeaponItem();
                if (weapon != null)
                    switch (weapon.getItemType()) 
					{
                        case BOW:
                        case CROSSBOW:
                            env.value += 30.;
                            break;
                        case DAGGER:
                        case DUALDAGGER:
                            env.value += 12.;
                            break;
                    }
            }
        }
    }

    private static class FuncSDefPlayers extends Func 
	{
        static final FuncSDefPlayers func = new FuncSDefPlayers();

        private FuncSDefPlayers() 
		{
            super(Stats.SHIELD_RATE, 0x20, null);
        }

        @Override
        public void calc(Env env) 
		{
            if (env.value == 0)
                return;

            L2Character cha = env.character;
            L2ItemInstance shld = ((L2Player) cha).getInventory().getPaperdollItem(Inventory.PAPERDOLL_LHAND);
            if (shld == null || shld.getItemType() != WeaponType.NONE)
                return;
            env.value *= DEXbonus[cha.getDEX()];
        }
    }

    public static void addFuncsToNewCharacter(L2Character cha) 
	{
        if (cha.isPlayer()) 
		{
            cha.addStatFunc(FuncMultRegenResting.getFunc(Stats.REGENERATE_CP_RATE));
            cha.addStatFunc(FuncMultRegenStanding.getFunc(Stats.REGENERATE_CP_RATE));
            cha.addStatFunc(FuncMultRegenRunning.getFunc(Stats.REGENERATE_CP_RATE));
            cha.addStatFunc(FuncMultRegenResting.getFunc(Stats.REGENERATE_HP_RATE));
            cha.addStatFunc(FuncMultRegenStanding.getFunc(Stats.REGENERATE_HP_RATE));
            cha.addStatFunc(FuncMultRegenRunning.getFunc(Stats.REGENERATE_HP_RATE));
            cha.addStatFunc(FuncMultRegenResting.getFunc(Stats.REGENERATE_MP_RATE));
            cha.addStatFunc(FuncMultRegenStanding.getFunc(Stats.REGENERATE_MP_RATE));
            cha.addStatFunc(FuncMultRegenRunning.getFunc(Stats.REGENERATE_MP_RATE));

			if(!ConfigValue.EnablePtsPlayerStat)
			{
				cha.addStatFunc(FuncMaxCpAdd.func);
				cha.addStatFunc(FuncMaxHpAdd.func);
				cha.addStatFunc(FuncMaxMpAdd.func);
			}

            cha.addStatFunc(FuncMaxCpMul.func);
            cha.addStatFunc(FuncMaxHpMul.func);
            cha.addStatFunc(FuncMaxMpMul.func);

            cha.addStatFunc(FuncAttackRange.func);

            cha.addStatFunc(FuncHennaSTR.func);
            cha.addStatFunc(FuncHennaDEX.func);
            cha.addStatFunc(FuncHennaINT.func);
            cha.addStatFunc(FuncHennaMEN.func);
            cha.addStatFunc(FuncHennaCON.func);
            cha.addStatFunc(FuncHennaWIT.func);

            cha.addStatFunc(FuncInventory.func);
            cha.addStatFunc(FuncWarehouse.func);
            cha.addStatFunc(FuncTradeLimit.func);

            cha.addStatFunc(FuncSDefPlayers.func);

            cha.addStatFunc(FuncPCriticalDamage.func);
            cha.addStatFunc(FuncPCriticalDamageStatic.func);
        }

		// у нпс в базе инфа уже с этими модами
        if (cha.isPlayer() || cha.isPet()) 
		{
            cha.addStatFunc(FuncPAtkMul.func); // +
            cha.addStatFunc(FuncMAtkMul.func); // +
            cha.addStatFunc(FuncPDefMul.func); // +
            cha.addStatFunc(FuncMDefMul.func); // +
        }

		cha.addStatFunc(FuncMoveSpeedMul.func);

        if (!cha.isPet()) 
		{
            cha.addStatFunc(FuncAccuracyAdd.func);
            cha.addStatFunc(FuncEvasionAdd.func);
        }

        if(!cha.isPet() && !cha.isSummon() && !cha.isMonster()) 
		{
            cha.addStatFunc(FuncPAtkSpeedMul.func);
            cha.addStatFunc(FuncMAtkSpeedMul.func);
            cha.addStatFunc(FuncSDefAll.func);
        }

        cha.addStatFunc(FuncMCriticalRateMul.func);
        cha.addStatFunc(FuncPCriticalRateMul.func);
        cha.addStatFunc(FuncPDamageResists.func);
        cha.addStatFunc(FuncMDamageResists.func);
    }

    public static double calcHpRegen(final L2Character cha)
	{
        double init;
        if (cha.isPlayer())
            init = (cha.getLevel() <= 10 ? 1.95 + cha.getLevel() / 20. : 1.4 + cha.getLevel() / 10.) * cha.getLevelMod() * CONbonus[cha.getCON()];
        else if (cha.isPet())
            init = cha.getHpReg() * cha.getLevelMod() * CONbonus[cha.getCON()];
        else
            init = cha.getTemplate().baseHpReg;

        if(cha.isPlayable())
		{
            final L2Player player = cha.getPlayer();
            if (player != null && player.getClan() != null && player.getInResidence() != ResidenceType.None)
                switch (player.getInResidence()) 
				{
                    case Clanhall:
                        final int clanHallIndex = player.getClan().getHasHideout();
                        if (clanHallIndex > 0) 
						{
                            final ClanHall clansHall = ClanHallManager.getInstance().getClanHall(clanHallIndex);
                            if (clansHall != null)
                                if (clansHall.isFunctionActive(ResidenceFunction.RESTORE_HP))
                                    init *= 1. + clansHall.getFunction(ResidenceFunction.RESTORE_HP).getLevel() / 100.;
                        }
                        break;
                    case Castle:
                        final int caslteIndex = player.getClan().getHasCastle();
                        if (caslteIndex > 0) 
						{
                            final Castle castle = CastleManager.getInstance().getCastleByIndex(caslteIndex);
                            if (castle != null)
                                if (castle.isFunctionActive(ResidenceFunction.RESTORE_HP))
                                    init *= 1. + castle.getFunction(ResidenceFunction.RESTORE_HP).getLevel() / 100.;
                        }
                        break;
                    case Fortress:
                        final int fortIndex = player.getClan().getHasCastle();
                        if (fortIndex > 0) 
						{
                            final Fortress fort = FortressManager.getInstance().getFortressByIndex(fortIndex);
                            if (fort != null)
                                if (fort.isFunctionActive(ResidenceFunction.RESTORE_HP))
                                    init *= 1. + fort.getFunction(ResidenceFunction.RESTORE_HP).getLevel() / 100.;
                        }
                        break;
                }
        }

        return cha.calcStat(Stats.REGENERATE_HP_RATE, init, null, null);
    }

    public static double calcMpRegen(L2Character cha) 
	{
        double init;
        if (cha.isPlayer())
            init = (.87 + cha.getLevel() * .03) * cha.getLevelMod();
        else if (cha.isPet())
            init = cha.getMpReg() * cha.getLevelMod() * MENbonus[cha.getMEN()];
        else
            init = cha.getTemplate().baseMpReg;

        if (cha.isPlayable()) 
		{
            init *= MENbonus[cha.getMEN()];
            if (cha.isSummon())
                init *= 2;
        } 
		else if (cha.isRaid())
            init *= 3;

        if (cha.isPlayable()) 
		{
            L2Player player = cha.getPlayer();
            if (player != null) 
			{
                L2Clan clan = player.getClan();
                if (clan != null)
                    switch (player.getInResidence()) 
					{
                        case Clanhall:
                            int clanHallIndex = clan.getHasHideout();
                            if (clanHallIndex > 0) {
							
                                ClanHall clansHall = ClanHallManager.getInstance().getClanHall(clanHallIndex);
                                if (clansHall != null)
                                    if (clansHall.isFunctionActive(ResidenceFunction.RESTORE_MP))
                                        init *= 1. + clansHall.getFunction(ResidenceFunction.RESTORE_MP).getLevel() / 100.;
                            }
                            break;
                        case Castle:
                            int caslteIndex = clan.getHasCastle();
                            if (caslteIndex > 0) 
							{
                                Castle castle = CastleManager.getInstance().getCastleByIndex(caslteIndex);
                                if (castle != null)
                                    if (castle.isFunctionActive(ResidenceFunction.RESTORE_MP))
                                        init *= 1. + castle.getFunction(ResidenceFunction.RESTORE_MP).getLevel() / 100.;
                            }
                            break;
                        case Fortress:
                            int fortIndex = clan.getHasCastle();
                            if (fortIndex > 0) 
							{
                                Fortress fort = FortressManager.getInstance().getFortressByIndex(fortIndex);
                                if (fort != null)
                                    if (fort.isFunctionActive(ResidenceFunction.RESTORE_MP))
                                        init *= 1. + fort.getFunction(ResidenceFunction.RESTORE_MP).getLevel() / 100.;
                            }
                            break;
                    }
            }
        }

        return cha.calcStat(Stats.REGENERATE_MP_RATE, init, null, null);
    }

    public static double calcCpRegen(L2Character cha) 
	{
        double init = (1.5 + cha.getLevel() / 10) * cha.getLevelMod() * CONbonus[cha.getCON()];
        return cha.calcStat(Stats.REGENERATE_CP_RATE, init, null, null);
    }

	/**
	 * Подсчёт дамаг от блоу скиллов, сделано по формуле с ПТС ГФ.
	 * Цифры от блоу скиллов 1 в 1.
	 */
	public static double calcBlowDamage(L2Character attacker, L2Character target, L2Skill skill, boolean ss, boolean shield, boolean beckstab)
	{
		double pAtk = attacker.getPAtk(target);
		double weapon_random = 1 + (Rnd.get() * attacker.getRandomDamage() * 2 - attacker.getRandomDamage()) / 100;
		double soul_bonus = skill.getId() == 505 ? (1.3 + Math.min(attacker.getConsumedSouls(), 5) * 0.05) : 1;
		double p_critical_damage = 0.01 * attacker.calcStat(Stats.CRITICAL_DAMAGE, target, skill);
		double p_defence_critical_damage = 0.01 * target.calcStat(Stats.CRIT_DAMAGE_RECEPTIVE, attacker, skill);
		double pos_k2 = (attacker.calcStat(Stats.P_CRITICAL_DAMAGE_POSITION, 1, target, null) - 1) * 0.5 + 1;
		double deff_diff = attacker.calcStat(Stats.CRITICAL_DAMAGE_STATIC, target, skill) * 6;
		double crit = Rnd.chance(skill.getCriticalRate()) ? 2 : 1;
		double soulshot = ss ? 2 : 1;
		boolean isPvP = attacker.isPlayable() && target.isPlayable();
		double pvp_phys_skill_dam_bonus = isPvP ? attacker.calcStat(Stats.PVP_PHYS_SKILL_DMG_BONUS, 1, null, null) : 1;
		double p_pvp_physical_skill_defence_bonus = isPvP ? target.calcStat(Stats.p_pvp_physical_skill_defence_bonus, 1, null, null) : 1;
		double pos_k1 = 0;
		switch(Util.getDirectionTo(target, attacker))
		{
			case BEHIND:
				pos_k1 = 0.2;
				break;
			case SIDE:
				pos_k1 = 0.05;
				break;
		}
		double damage_bonus = (pAtk * soulshot + skill.getPower(target) * (ss && beckstab ? 3.5 : 1)) * weapon_random * pos_k1;
		double total_damage = ((pAtk * soulshot + skill.getPower(target)) * soul_bonus * weapon_random * p_critical_damage * p_defence_critical_damage * pos_k2 + deff_diff + damage_bonus) * 77.131 / (shield ? (target.getShldDef() + target.getPDef(attacker)) : target.getPDef(attacker)) * pvp_phys_skill_dam_bonus * crit / p_pvp_physical_skill_defence_bonus;

		if(attacker.isPlayer() && attacker.getPlayer().isGM())
		{
			attacker.sendMessage("damage_bonus="+damage_bonus+"(pAtk="+pAtk+" soulshot="+soulshot+" getPower="+skill.getPower(target)+")");
			attacker.sendMessage("total_damage="+total_damage+"()");
		}
		return calcWeaponRes(attacker, target,skill, total_damage);
	}

	// Минимум 0.05, максимум 2.0
	public static double calcWeaponRes(L2Character character, L2Character target, L2Skill skill, double value)
	{	
		L2Weapon weapon = character.getActiveWeaponItem();
		if(weapon == null)
		{
			double mod = character.getFistWeaponType().getTrait().fullResist(target) ? 0 : Math.min(ConfigValue.MaxWeaponTraitMod, Math.max(ConfigValue.MinWeaponTraitMod, character.getFistWeaponType().getTrait().calcResist(target)));
			value *= mod;
		}
		else if(weapon.getItemType().getTrait() != null)
		{
			double mod = weapon.getItemType().getTrait().fullResist(target) ? 0 : Math.min(ConfigValue.MaxWeaponTraitMod, Math.max(ConfigValue.MinWeaponTraitMod, weapon.getItemType().getTrait().calcResist(target)));
			value *= mod;
		}

		return calcDamageResists(skill, character, target, value);
	}

    public static class AttackInfo 
	{
        public double damage;
        public double defence;
        public double crit_rcpt;
        public double crit_static;
        public boolean crit;
        public boolean shld;
        public boolean miss;
    }

    /**
     * Для простых ударов
     * patk = patk
     * При крите простым ударом:
     * patk = patk * (1 + crit_damage_rcpt) * crit_damage_mod + crit_damage_static
     * Для blow скиллов
     * TODO
     * Для скилловых критов, повреждения просто удваиваются, бафы не влияют (кроме blow, для них выше)
     * patk = (1 + crit_damage_rcpt) * (patk + skill_power)
     * Для обычных атак
     * damage = patk * ss_bonus * 77 / pdef
     */
	/**
	pAtk: 1
	pDef: 1
	dam: 77
	mod70: 1,871428571428571
	mod77: 1,701298701298701
	mod_dam: 1
	---------------
	pAtk: 10
	pDef: 1
	dam: 737
	mod70: 10,52857142857143
	mod77: 9,571428571428571
	mod_dam: 5,625954198473282
	---------------
	pAtk: 1
	pDef: 10
	dam: 14
	mod70: 0,2
	mod77: 0,1818181818181818
	mod_dam: 0,1068702290076336
	---------------
	pAtk: 10
	pDef: 10
	dam: 84
	mod70: 1,2
	mod77: 1,090909090909091
	mod_dam: 0,6412213740458015
	---------------
	pAtk: 1
	pDef: 0
	dam: 151
	mod70: 
	mod77: 
	mod_dam:
 	---------------
	pAtk: 2
	pDef: 0
	dam: 230
	mod70: 
	mod77: 
	mod_dam:
	---------------
	pAtk: 10
	pDef: 0
	dam: 855
	mod70: 
	mod77: 
	mod_dam: 
	---------------
	pAtk: 0
	pDef: 0
	dam: 73
	mod70: 
	mod77: 
	mod_dam: 
	---------------
	pAtk: 0
	pDef: 1
	dam: 63
	mod70: 
	mod77: 
	mod_dam: 
  	---------------
	pAtk: 0
	pDef: 2
	dam: 34
	mod70: 
	mod77: 
	mod_dam: 
	---------------
	pAtk: 0
	pDef: 10
	dam: 7
	mod70: 
	mod77: 
	mod_dam: 
	---------------
	damage = 73-pDef
	**/
	
	/**
	  if ( v17 >= 78 || ((int (__fastcall )(__int64))(*(_QWORD )v7 + 208i64))(v7) >= 78 )
	  {
		v18 = (double)(v17 - ((int (__fastcall )(__int64))(*(_QWORD )v7 + 208i64))(v7));
		v19 = v16 + sqrt((double)v17) * v18 / 25.0;
	  }
	**/
	
	public static AttackInfo calcPhysDam(L2Character attacker, L2Character target, L2Skill skill, boolean dual, boolean blow, boolean ss, boolean onCrit, boolean onCharge, boolean onBow)
	{
		if(ConfigValue.OfflikePhysDamFormula)
			return calcPhysDam2(attacker, target, skill, dual, blow, ss, onCrit, onCharge, onBow);
        AttackInfo info = new AttackInfo();

        info.damage = attacker.getPAtk(target);
        info.defence = target.getPDef(attacker);
        info.crit_rcpt = 0.01 * target.calcStat(Stats.CRIT_DAMAGE_RECEPTIVE, attacker, skill);
        info.crit_static = attacker.calcStat(Stats.CRITICAL_DAMAGE_STATIC, target, skill);
        info.crit = Rnd.chance(calcCrit(attacker, target, skill, blow));
        info.shld = (skill == null || !skill.getShieldIgnore()) && Formulas.calcShldUse(attacker, target);
        info.miss = false;
		boolean shield = false;
		boolean noCalcBlow = false;
		boolean isPvP = attacker.isPlayable() && target.isPlayable();

        if(info.shld)
		{
			shield = true;
            info.defence += target.getShldDef();
		}

        info.defence = Math.max(info.defence, 1);

        if(skill != null)
		{
            if(skill.getPower(target) == 0) 
			{
                info.damage = 0; // РµСЃР»Рё СЃРєРёР»Р» РЅРµ РёРјРµРµС‚ СЃРІРѕРµР№ СЃРёР»С‹ РґР°Р»СЊС€Рµ РёРґС‚Рё Р±РµСЃРїРѕР»РµР·РЅРѕ, РјРѕР¶РЅРѕ СЃСЂР°Р·Сѓ РІРµСЂРЅСѓС‚СЊ РґР°РјР°Рі РѕС‚ Р»РµС‚Р°Р»Р°
                return info;
            }

			info.damage += Math.max(0., skill.getPower(target));

			//Р—Р°СЂСЏР¶Р°РµРјС‹Рµ СЃРєРёР»С‹ РёРјРµСЋС‚ РїРѕСЃС‚РѕСЏРЅРЅС‹Р№ СѓСЂРѕРЅ
			if(!skill.isChargeBoost())
				info.damage *= 1 + (Rnd.get() * attacker.getRandomDamage() * 2 - attacker.getRandomDamage()) / 100;

            if (skill.isChargeBoost())
                info.damage *= 0.8 + 0.2 * ((attacker.getIncreasedForce() + skill.getNumCharges() > 8) ? 8 : attacker.getIncreasedForce() + skill.getNumCharges());
			
            else if (skill.isSoulBoost())
                info.damage *= 1.0 + 0.06 * Math.min(attacker.getConsumedSouls(), 5);

			if(info.crit)
			{
				//Р—Р°СЂСЏР¶Р°РµРјС‹Рµ СЃРєРёР»С‹ РёРіРЅРѕСЂРёСЂСѓСЋС‚ СЃРЅРёР¶Р°СЋС‰РёРµ СЃРёР»Сѓ РєСЂРёС‚Р° СЃС‚Р°С‚С‹
			/*	if (skill.isChargeBoost() || skill.getSkillType() == SkillType.CHARGE || skill.getId() == 990 || skill.getId() == 987)
					info.damage *= 2.;
				else
					info.damage = 2 * info.crit_rcpt * info.damage;*/
				info.damage *= 2.;
			}

            // Gracia Physical Skill Damage Bonus
            info.damage *= 1.10113;
        }
		else 
		{
            info.damage *= 1 + (Rnd.get() * attacker.getRandomDamage() * 2 - attacker.getRandomDamage()) / 100;

            if(dual)
                info.damage /= 2.;

            if(info.crit) 
			{
				if(onCharge)
					info.damage *= 2;
				else
				{
					info.damage *= 0.01 * attacker.calcStat(Stats.CRITICAL_DAMAGE, target, skill);
					info.damage *= (attacker.calcStat(Stats.P_CRITICAL_DAMAGE_POSITION, 1, target, null) - 1) * 0.5 + 1;
					info.damage = 2.0 * target.calcStat(Stats.CRIT_DAMAGE_RECEPTIVE, info.damage, attacker, skill);
					info.damage += info.crit_static;
				}
            }
        }

        if(info.crit) 
		{
            // С€Р°РЅСЃ Р°Р±СЃРѕСЂР±Р°С†РёРё РґСѓС€Рё (Р±РµР· Р°РЅРёРјР°С†РёРё) РїСЂРё РєСЂРёС‚Рµ, РµСЃР»Рё Soul Mastery 4РіРѕ СѓСЂРѕРІРЅСЏ РёР»Рё Р±РѕР»РµРµ
            int chance = attacker.getSkillLevel(L2Skill.SKILL_SOUL_MASTERY);
            if(chance > 0) 
			{
                if(chance >= 21)
                    chance = 30;
                else if(chance >= 15)
                    chance = 25;
                else if(chance >= 9)
                    chance = 20;
                else if(chance >= 4)
                    chance = 15;
                if(Rnd.chance(chance))
                    attacker.setConsumedSouls(attacker.getConsumedSouls() + 1, null);
            }
        }

		switch(Util.getDirectionTo(target, attacker))
		{
			case BEHIND:
				info.damage *= 1.2;
				break;
			case SIDE:
				info.damage *= 1.1;
				break;
		}

        if(ss)
            info.damage *= blow ? 1.0 : 2.0;

        info.damage *= 70. / info.defence;
        info.damage = attacker.calcStat(Stats.PHYSICAL_DAMAGE, info.damage, target, skill);

        if(info.shld && Rnd.chance(5))
		{
            info.damage = 1;
			noCalcBlow = true;
		}

        if(target.isMonster()) 
		{
            if(attacker.getActiveWeaponItem() != null) 
			{
                final L2Weapon weapon = attacker.getActiveWeaponItem();
				if(weapon.getItemType() == WeaponType.BOW || weapon.getItemType() == WeaponType.CROSSBOW)
					info.damage = 1.0 * attacker.calcStat(Stats.PVE_BOW_DMG, info.damage, target, skill);
				else
					info.damage = 1.0 * attacker.calcStat(Stats.PVE_PHYSICAL_DMG, info.damage, target, skill);
				if(target.isRaid() || target.isBoss() || target.isEpicRaid() || target.isRefRaid())
				{
					if(weapon.getItemType() == WeaponType.BOW || weapon.getItemType() == WeaponType.CROSSBOW)
						info.damage = 1.0 * attacker.calcStat(Stats.PVR_BOW_DMG, info.damage, target, skill);
					else
						info.damage = 1.0 * attacker.calcStat(Stats.PVR_PHYSICAL_DMG, info.damage, target, skill);
				}
            }
        }

		if(isPvP)
		{
			if(skill == null)
			{
				info.damage *= attacker.calcStat(Stats.PVP_PHYS_DMG_BONUS, 1, null, null);
				info.damage /= target.calcStat(Stats.p_pvp_physical_attack_defence_bonus, 1, null, null);
			}
			else
			{
				info.damage *= attacker.calcStat(Stats.PVP_PHYS_SKILL_DMG_BONUS, 1, null, null);
				info.damage /= target.calcStat(Stats.p_pvp_physical_skill_defence_bonus, 1, null, null);
			}
		}
		
        // РўСѓС‚ РїСЂРѕРІРµСЂСЏРµРј С‚РѕР»СЊРєРѕ РµСЃР»Рё skill != null, С‚.Рє. L2Character.onHitTimer РЅРµ РѕР±СЃС‡РёС‚С‹РІР°РµС‚ РґР°РјР°Рі.
        if (skill != null) 
		{
            if (info.shld)
                if (info.damage == 1)
                    target.sendPacket(Msg.YOUR_EXCELLENT_SHIELD_DEFENSE_WAS_A_SUCCESS);
                else
                    target.sendPacket(Msg.YOUR_SHIELD_DEFENSE_HAS_SUCCEEDED);

			if(info.damage > 1 && !skill.hasEffects() && Rnd.chance(target.calcStat(Stats.PSKILL_EVASION, 0, attacker, skill)))
			{
                attacker.sendPacket(new SystemMessage(SystemMessage.C1S_ATTACK_WENT_ASTRAY).addName(attacker));
                target.sendPacket(new SystemMessage(SystemMessage.C1_HAS_EVADED_C2S_ATTACK).addName(target).addName(attacker));
                info.damage = 0;
				noCalcBlow = true;
            }

            if (target.isMonster()) 
			{
                if (attacker.getActiveWeaponItem() != null) 
				{
                    final L2Weapon weapon = attacker.getActiveWeaponItem();
					if(weapon.getItemType() == WeaponType.BOW || weapon.getItemType() == WeaponType.CROSSBOW)
						info.damage = 1.0 * attacker.calcStat(Stats.PVE_BOW_SKILL_DMG, info.damage, target, skill);
					else
						info.damage = 1.0 * attacker.calcStat(Stats.PVE_PHYS_SKILL_DMG, info.damage, target, skill);
					if(target.isRaid() || target.isBoss() || target.isEpicRaid() || target.isRefRaid())
					{
						if(weapon.getItemType() == WeaponType.BOW || weapon.getItemType() == WeaponType.CROSSBOW)
							info.damage = 1.0 * attacker.calcStat(Stats.PVR_BOW_SKILL_DMG, info.damage, target, skill);
						else
							info.damage = 1.0 * attacker.calcStat(Stats.PVR_PHYS_SKILL_DMG, info.damage, target, skill);
					}
                }
            }

            if (info.damage > 1 && skill.isDeathlink())
                info.damage *= 1.8 * (1.0 - attacker.getCurrentHpRatio());

            if (onCrit && !calcBlow(attacker, target, skill)) 
			{
                info.miss = true;
                info.damage = 0;
				noCalcBlow = true;
                attacker.sendPacket(new SystemMessage(SystemMessage.C1S_ATTACK_WENT_ASTRAY).addName(attacker));
            }

			if(blow && onCrit && !noCalcBlow)
				info.damage = calcBlowDamage(attacker, target, skill, ss, shield, skill != null && skill.getId() == 30);

			int p_skill_power_diff = (int)attacker.calcStat(Stats.p_skill_power_diff, 0);
			double p_skill_power_per = attacker.calcStat(Stats.p_skill_power_per, 1);

			info.damage *= p_skill_power_per;
			info.damage += p_skill_power_diff;

            if (info.damage > 0)
                if (attacker instanceof L2Summon)
                    ((L2Summon) attacker).displayHitMessage(target, (int) info.damage, info.crit || blow, false);
                else if (attacker.isPlayer()) 
				{
                    if (info.crit || blow)
                        attacker.sendPacket(new SystemMessage(SystemMessage.C1_HAD_A_CRITICAL_HIT).addName(attacker).addDamage(target, target, (long)info.damage));
					double trans = target.calcStat(Stats.TRANSFER_PET_DAMAGE_PERCENT, 0, attacker, skill);
					if(trans >= 1 && target.getPet() != null && !target.getPet().isDead() && target.getPet().isSummon() && target.getPet().isInRange(target, 1200) && !target.getPet().isInZonePeace() && (trans = (info.damage / 100d * trans)) < target.getPet().getCurrentHp()-1 && trans > 0)
					{
						//attacker.sendMessage("(1)Пизданул "+((long) (info.damage - trans))+" лоху и "+((long) trans)+" его носкам.");
						attacker.sendPacket(new SystemMessage(SystemMessage.YOU_HAVE_GIVEN_S1_DAMAGE_TO_YOUR_TARGET_AND_S2_DAMAGE_TO_THE_SERVITOR).addNumber((int) (info.damage - trans)).addNumber((int) trans));
					}
					else if(skill.getPower() > 0)
						attacker.sendHDmgMsg(attacker, target, skill, (int)info.damage, info.crit, false);
                }

			if(!ConfigValue.AltFormulaCastBreak && calcCastBreak(target, info.crit))
				target.abortCast(false);
			else if(ConfigValue.AltFormulaCastBreak && calcCastBreakAlt(target, Math.max(1., info.damage)))
				target.abortCast(false);
        }

		info.damage *= ConfigValue.PhysDamageMod;
		if(ConfigValue.EnableClanWarDamageBonus && target.isPlayer() && attacker.isPlayer() && attacker.getPlayer().atWarWith(target.getPlayer()))
			info.damage *= skill == null ? ConfigValue.ClanWarPhysDamageMod : ConfigValue.ClanWarPhysSkillDamageMod;

        info.damage = Math.max(1., info.damage);

        return info;
    }

	/**
	207 - c руки
	415 - крит с руки
	------
	415 - с СС
	830 - крит с СС
	**/
	/**
	//----- (0000000000845648) ----------------------------------------------------

// C051D8: using guessed type void *off_C051D8;
// C1F500: using guessed type void *off_C1F500;
// 1E8EC90: using guessed type int dword_1E8EC90[];
// 226F890: using guessed type __int64 qword_226F890[];
// 299655C8: using guessed type int TlsIndex;

	**/
	// Воздействию бонуса от крита, получает только чистый дамаг, без модификаторов, после слаживается с дамагом от модификаторов...
	public static AttackInfo calcPhysDam2(L2Character attacker, L2Character target, L2Skill skill, boolean dual, boolean blow, boolean ss, boolean onCrit, boolean onCharge, boolean onBow)
	{
        AttackInfo info = new AttackInfo();

        info.damage = attacker.getPAtk(target);
		if(skill != null)
			info.damage += Math.max(0., skill.getPower(target));

        info.defence = target.getPDef(attacker);
        info.crit_rcpt = 0.01 * target.calcStat(Stats.CRIT_DAMAGE_RECEPTIVE, attacker, skill); // {p_defence_critical_damage;{armor_heavy};-15;per}
        info.crit_static = attacker.calcStat(Stats.CRITICAL_DAMAGE_STATIC, target, skill); // {p_critical_damage;{all};1000;diff}
        info.crit = Rnd.chance(calcCrit(attacker, target, skill, blow));
        info.shld = (skill == null || !skill.getShieldIgnore()) && Formulas.calcShldUse(attacker, target);
        info.miss = false;

		boolean shield = false;
		boolean noCalcBlow = false;
		double default_damage;
		double crit_damage=0;
		boolean isPvP = attacker.isPlayable() && target.isPlayable();

        if(info.shld)
		{
			shield = true;
            info.defence += target.getShldDef();
		}
        info.defence = Math.max(info.defence, 1);
		info.damage = attacker.calcStat(Stats.PHYSICAL_DAMAGE, info.damage * (onBow ? 70 : 77) / info.defence, target, skill); // Для дамага с лука, модификатор 70, а не 77.
		if(attacker.isPlayer() && attacker.getPlayer().isGM())
			attacker.sendMessage("calcPhysDam: Steep[START] damage: "+info.damage);
		if(ss)
			info.damage *= blow ? 1.0 : (dual ? 2.04 : 2.0);
		if(attacker.isPlayer() && attacker.getPlayer().isGM())
			attacker.sendMessage("calcPhysDam: Steep[0] damage: "+info.damage);
		info.damage = Math.floor(info.damage); // round
		default_damage = info.damage;
		if(attacker.isPlayer() && attacker.getPlayer().isGM())
			attacker.sendMessage("calcPhysDam: Steep[1] default_damage: "+default_damage);

        if(skill != null)
		{
			SkillTrait trait = skill.getTraitType();
			if(trait != null && trait != SkillTrait.trait_none)
			{
				if(trait.fullResist(target))
				{
					info.damage = 0.0D;
					return info;
				}
			}

			info.damage = Math.round(info.damage);
            if(skill.getPower(target) == 0) 
			{
                info.damage = 0; // если скилл не имеет своей силы дальше идти бесполезно, можно сразу вернуть дамаг от летала
                return info;
            }

			//Заряжаемые скилы имеют постоянный урон
			if(!skill.isChargeBoost())
				info.damage *= 1 + (Rnd.get() * attacker.getRandomDamage() * 2 - attacker.getRandomDamage()) / 100;

			// Правильно!!!
            if(skill.isChargeBoost())
                info.damage *= 0.8 + 0.2 * ((attacker.getIncreasedForce() + skill.getNumCharges() > 8) ? 8 : attacker.getIncreasedForce() + skill.getNumCharges());

            else if(skill.isSoulBoost())
                info.damage *= 1.0 + 0.05 * Math.min(attacker.getConsumedSouls(), 5);

			// TODO: Убрать сообщение о крите, на ПТС нету его:)
			if(info.crit)
			{
				//Заряжаемые скилы игнорируют снижающие силу крита статы
				/*if (skill.isChargeBoost() || skill.getSkillType() == SkillType.CHARGE || skill.getId() == 990 || skill.getId() == 987)
					info.damage *= 2.;
				else
					info.damage = 2 * info.crit_rcpt * info.damage;*/
				info.damage *= 2.;
			}
        }
		else 
		{
            info.damage *= 1 + (Rnd.get() * attacker.getRandomDamage() * 2 - attacker.getRandomDamage()) / 100;
			if(attacker.isPlayer() && attacker.getPlayer().isGM())
				attacker.sendMessage("calcPhysDam: Steep[2] damage: "+info.damage);
            if(info.crit) 
			{
				if(onCharge)
					info.damage *= 2;
				else
				{
					crit_damage = default_damage * 0.01 * attacker.calcStat(Stats.CRITICAL_DAMAGE, target, skill); // {p_critical_damage;{all};20;per}
					crit_damage = target.calcStat(Stats.CRIT_DAMAGE_RECEPTIVE, crit_damage, attacker, skill); // {p_defence_critical_damage;{armor_heavy};-15;per}
					crit_damage *= attacker.calcStat(Stats.P_CRITICAL_DAMAGE_POSITION, 1, target, null); // {p_critical_damage_position;front;-30;per}
					crit_damage += info.crit_static * 77.366 / info.defence + 0.5d;
					crit_damage = Math.ceil(crit_damage);
					if(attacker.isPlayer() && attacker.getPlayer().isGM())
						attacker.sendMessage("calcPhysDam: Steep[3] crit_damage: "+crit_damage);
				}
            }
        }

        if(info.crit) 
		{
            // шанс абсорбации души (без анимации) при крите, если Soul Mastery 4го уровня или более
            int chance = attacker.getSkillLevel(L2Skill.SKILL_SOUL_MASTERY);
            if(chance > 0) 
			{
                if(chance >= 21)
                    chance = 30;
                else if(chance >= 15)
                    chance = 25;
                else if(chance >= 9)
                    chance = 20;
                else if(chance >= 4)
                    chance = 15;
                if(Rnd.chance(chance))
                    attacker.setConsumedSouls(attacker.getConsumedSouls() + 1, null);
            }
        }

		/**
		 По dz вычисляем величину clamped_dz:
		 Если dz > 25 то clamped_dz = 25
		 Если dz < -25 то clamped_dz = -25
		 Если -25 < dz < 25 то clamped_dz = dz

		 crit_height_bonus = 0.008 * clamped_dz + 1.1
		**/
		/**
		hit_cond_bonus_begin
			ahead = 0%	//공격자가 피격자의 앞에 있는 경우
			side = 5%	//옆에 있는 경우
			back = 10%	//뒤에 있는 경우
			high = 3%	//높은 곳에 있는 경우
			low = -3%	//낮은 곳에 있는 경우
			dark = -10%	//조명이 없는 경우
			rain = -3%	//비가 오는 경우
		hit_cond_bonus_end
		**/
		if(skill == null)
		{
			double pos_k1 = 0;
			switch(Util.getDirectionTo(target, attacker))
			{
				case BEHIND:
					pos_k1 = 0.2;
					break;
				case SIDE:
					pos_k1 = 0.05;
					break;
			}
			info.damage += (int)(default_damage*pos_k1);
			if(attacker.isPlayer() && attacker.getPlayer().isGM())
				attacker.sendMessage("calcPhysDam: Steep[4] damage: "+info.damage);
		}

		if(onBow)
		{
			L2Weapon activeWeapon = attacker.getActiveWeaponItem();
			if(activeWeapon == null)
				return info;
			int range = activeWeapon.getAttackRange();
			int range_per = (int)((Math.min(range, attacker.getDistance(target)) / range) * 100);
			range_per = Math.min(range_per, 100);
			info.damage *= bow_damage_mod[range_per];
			// info.damage *= Math.min(range, attacker.getDistance(target)) / range * .4 + 0.76;
			if(attacker.isPlayer() && attacker.getPlayer().isGM())
				attacker.sendMessage("calcPhysDam: Steep[5] damage: "+info.damage);
		}

        if(info.shld && Rnd.chance(5))
		{
            info.damage = 1;
			noCalcBlow = true;
			if(attacker.isPlayer() && attacker.getPlayer().isGM())
				attacker.sendMessage("shld");
		}

        if(target.isMonster()) 
		{
            if(attacker.getActiveWeaponItem() != null) 
			{
                final L2Weapon weapon = attacker.getActiveWeaponItem();
				if(weapon.getItemType() == WeaponType.BOW || weapon.getItemType() == WeaponType.CROSSBOW)
					info.damage = 1.0 * attacker.calcStat(Stats.PVE_BOW_DMG, info.damage, target, skill);
				else
					info.damage = 1.0 * attacker.calcStat(Stats.PVE_PHYSICAL_DMG, info.damage, target, skill);

				if(target.isRaid() || target.isBoss() || target.isEpicRaid() || target.isRefRaid())
				{
					if(weapon.getItemType() == WeaponType.BOW || weapon.getItemType() == WeaponType.CROSSBOW)
						info.damage = 1.0 * attacker.calcStat(Stats.PVR_BOW_DMG, info.damage, target, skill);
					else
						info.damage = 1.0 * attacker.calcStat(Stats.PVR_PHYSICAL_DMG, info.damage, target, skill);
				}

				if(attacker.isPlayer() && attacker.getPlayer().isGM())
					attacker.sendMessage("calcPhysDam: Steep[6] damage: "+info.damage);
            }
        }

		if(isPvP)
		{
			if(skill == null)
			{
				
				info.damage *= attacker.calcStat(Stats.PVP_PHYS_DMG_BONUS, 1, null, null); // {p_pvp_physical_attack_dmg_bonus;30;per}
				info.damage /= target.calcStat(Stats.p_pvp_physical_attack_defence_bonus, 1, null, null); // {p_pvp_physical_attack_defence_bonus;30;per}
				if(attacker.isPlayer() && attacker.getPlayer().isGM())
					attacker.sendMessage("calcPhysDam: Steep[7] damage: "+info.damage);

			}
			else
			{
				info.damage *= attacker.calcStat(Stats.PVP_PHYS_SKILL_DMG_BONUS, 1, null, null); // {p_pvp_physical_skill_dmg_bonus;30;per}
				info.damage /= target.calcStat(Stats.p_pvp_physical_skill_defence_bonus, 1, null, null); // {p_pvp_physical_skill_defence_bonus;30;per}
				if(attacker.isPlayer() && attacker.getPlayer().isGM())
					attacker.sendMessage("calcPhysDam: Steep[8] damage: "+info.damage);
			}
		}
		else if(!attacker.isPlayable())
		{
			if(skill == null)
				info.damage /= target.calcStat(Stats.p_pve_physical_attack_defence_bonus, 1, null, null);
			else
				info.damage /= target.calcStat(Stats.p_pve_physical_skill_defence_bonus, 1, null, null);
		}

        // Тут проверяем только если skill != null, т.к. L2Character.onHitTimer не обсчитывает дамаг.
		if(skill != null)
		{
            if(info.shld)
                if(info.damage == 1)
                    target.sendPacket(Msg.YOUR_EXCELLENT_SHIELD_DEFENSE_WAS_A_SUCCESS);
                else
                    target.sendPacket(Msg.YOUR_SHIELD_DEFENSE_HAS_SUCCEEDED);

			if(info.damage > 1 && !skill.hasEffects() && Rnd.chance(target.calcStat(Stats.PSKILL_EVASION, 0, attacker, skill)))
			{
                attacker.sendPacket(new SystemMessage(SystemMessage.C1S_ATTACK_WENT_ASTRAY).addName(attacker));
                target.sendPacket(new SystemMessage(SystemMessage.C1_HAS_EVADED_C2S_ATTACK).addName(target).addName(attacker));
                info.damage = 0;
				noCalcBlow = true;
				if(attacker.isPlayer() && attacker.getPlayer().isGM())
					attacker.sendMessage("C1_HAS_EVADED_C2S_ATTACK");
            }

            if(target.isMonster()) 
			{
                if(attacker.getActiveWeaponItem() != null) 
				{
                    final L2Weapon weapon = attacker.getActiveWeaponItem();
					if(weapon.getItemType() == WeaponType.BOW || weapon.getItemType() == WeaponType.CROSSBOW)
						info.damage = 1.0 * attacker.calcStat(Stats.PVE_BOW_SKILL_DMG, info.damage, target, skill);
					else
						info.damage = 1.0 * attacker.calcStat(Stats.PVE_PHYS_SKILL_DMG, info.damage, target, skill);
					if(target.isRaid() || target.isBoss() || target.isEpicRaid() || target.isRefRaid())
					{
						if(weapon.getItemType() == WeaponType.BOW || weapon.getItemType() == WeaponType.CROSSBOW)
							info.damage = 1.0 * attacker.calcStat(Stats.PVR_BOW_SKILL_DMG, info.damage, target, skill);
						else
							info.damage = 1.0 * attacker.calcStat(Stats.PVR_PHYS_SKILL_DMG, info.damage, target, skill);
					}
                }
				if(attacker.isPlayer() && attacker.getPlayer().isGM())
					attacker.sendMessage("calcPhysDam: Steep[9] damage: "+info.damage);
            }

            if(info.damage > 1 && skill.isDeathlink())
                info.damage *= 1.8 * (1.0 - attacker.getCurrentHpRatio());

			if(attacker.isPlayer() && attacker.getPlayer().isGM())
					attacker.sendMessage("calcPhysDam: Steep[10] damage: "+info.damage);
            if(onCrit && !calcBlow(attacker, target, skill)) 
			{
                info.miss = true;
                info.damage = 0;
				noCalcBlow = true;
				if(attacker.isPlayer() && attacker.getPlayer().isGM())
					attacker.sendMessage("C1S_ATTACK_WENT_ASTRAY");
                attacker.sendPacket(new SystemMessage(SystemMessage.C1S_ATTACK_WENT_ASTRAY).addName(attacker));
            }

			if(blow && onCrit && !noCalcBlow)
				info.damage = calcBlowDamage(attacker, target, skill, ss, shield, skill != null && skill.getId() == 30);
			else if(attacker.isPlayer() && attacker.getPlayer().isGM())
				attacker.sendMessage("blow="+blow+" onCrit="+onCrit+" noCalcBlow="+(!noCalcBlow));

			if(!ConfigValue.AltFormulaCastBreak && calcCastBreak(target, info.crit))
				target.abortCast(false);
			else if(ConfigValue.AltFormulaCastBreak && calcCastBreakAlt(target, Math.max(1., info.damage)))
				target.abortCast(false);
        }

		if(attacker.isPlayer() && attacker.getPlayer().isGM())
			attacker.sendMessage("calcPhysDam: Steep[11] damage: "+info.damage);

		int p_skill_power_diff = 0;
		double p_skill_power_per = 1;
		if(skill != null)
		{
			p_skill_power_diff = (int)attacker.calcStat(Stats.p_skill_power_diff, 0);
			p_skill_power_per = attacker.calcStat(Stats.p_skill_power_per, 1);
			if(attacker.isPlayer() && attacker.getPlayer().isGM())
			{
				attacker.sendMessage("calcPhysDam: Steep[12] p_skill_power_diff: "+p_skill_power_diff);
				attacker.sendMessage("calcPhysDam: Steep[13] p_skill_power_per: "+p_skill_power_per);
			}
		}

		// Конечный дамаг...
        info.damage = Math.max(0., (info.damage+crit_damage)*p_skill_power_per+p_skill_power_diff);
		if(dual && skill == null)
			info.damage /= 2.;

		if(attacker.isPlayer() && attacker.getPlayer().isGM())
			attacker.sendMessage("calcPhysDam: Steep[FINISH] damage: "+info.damage);

		if(skill != null)
		{
			if(info.damage > 0)
				if (attacker instanceof L2Summon)
					((L2Summon) attacker).displayHitMessage(target, (int) info.damage, info.crit || blow, false);
				else if (attacker.isPlayer()) 
				{
					if (info.crit || blow)
						attacker.sendPacket(new SystemMessage(SystemMessage.C1_HAD_A_CRITICAL_HIT).addName(attacker).addDamage(target, target, (long)info.damage));
					double trans = target.calcStat(Stats.TRANSFER_PET_DAMAGE_PERCENT, 0, attacker, skill);
					if(trans >= 1 && target.getPet() != null && !target.getPet().isDead() && target.getPet().isSummon() && target.getPet().isInRange(target, 1200) && !target.getPet().isInZonePeace() && (trans = (info.damage / 100d * trans)) < target.getPet().getCurrentHp()-1 && trans > 0)
					{
						//attacker.sendMessage("(2)Пизданул "+((long) (info.damage - trans))+" лоху и "+((long) trans)+" его носкам.");
						attacker.sendPacket(new SystemMessage(SystemMessage.YOU_HAVE_GIVEN_S1_DAMAGE_TO_YOUR_TARGET_AND_S2_DAMAGE_TO_THE_SERVITOR).addNumber((int) (info.damage - trans)).addNumber((int) trans));
					}
					else if(skill.getPower() > 0)
						attacker.sendHDmgMsg(attacker, target, skill, (int)info.damage, info.crit, false);
				}
		}
		if(ConfigValue.EnableLogDamage > 0 && info.damage > ConfigValue.EnableLogDamage && attacker.isPlayer() && target.isPlayer() && attacker.getLevel() - ConfigValue.LogDamageLevelDiff <= target.getLevel())
		{
			int skill_id = 0;
			int skill_level = 0;
			if(skill != null)
			{
				skill_id = skill.getId();
				skill_level = skill.getLevel();
			}
			Log.add(attacker.getName()+" damage["+skill_id+"]["+skill_level+"] "+info.damage+" to "+target.getName(), "over_damage");
			debug_stats(attacker);
			debug_stats(target);
		}
		info.damage *= ConfigValue.PhysDamageMod;
		if(ConfigValue.EnableClanWarDamageBonus && target.isPlayer() && attacker.isPlayer() && attacker.getPlayer().atWarWith(target.getPlayer()))
			info.damage *= skill == null ? ConfigValue.ClanWarPhysDamageMod : ConfigValue.ClanWarPhysSkillDamageMod;

        return info;
    }

    public static double calcMagicDam(L2Character attacker, L2Character target, L2Skill skill, int sps, boolean isCubic) 
	{
        // Параметр ShieldIgnore для магических скиллов инвертирован
        boolean shield = skill.getShieldIgnore() && Formulas.calcShldUse(attacker, target);

		boolean isPvP = attacker.isPlayable() && target.isPlayable();
        double mAtk = attacker.getMAtk(target, skill);

        if (sps == 2)
            mAtk *= 4;
        else if (sps == 1)
            mAtk *= 2;

        double mdef = target.getMDef(null, skill);

        if (shield)
            mdef += target.getShldDef();
        if (mdef == 0)
            mdef = 1;

        double power = skill.getPower(target);
        double lethalDamage = 0;

		SkillTrait trait = skill.getTraitType();

		if(trait != null && trait != SkillTrait.trait_none)
		{
			if(trait.fullResist(target))
				return 0.;

			double traitMul = (1. + (trait.calcPower(attacker) - trait.calcResist(target)) / 100.);

			if(traitMul > 2.)
				traitMul = 2.;
			else if(traitMul < 0.05)
				traitMul = 0.05;
			power *= traitMul;
		}
        if (power == 0) 
		{
            if (lethalDamage > 0)
				attacker.sendHDmgMsg(attacker, target, skill, (int)lethalDamage, false, false);
            return lethalDamage;
        }

        if (skill.isSoulBoost())
            power *= 1.0 + 0.05 * Math.min(attacker.getConsumedSouls(), 5);

        double damage = 91 * power;
        if (isCubic) 
		{
            damage *= mAtk;
        } 
		else 
		{
            damage *= Math.sqrt(mAtk);
        }
        damage /= mdef;

        damage *= 1 + (Rnd.get() * attacker.getRandomDamage() * 2 - attacker.getRandomDamage()) / 100;

        boolean crit = calcMCrit(attacker.getMagicCriticalRate(target, skill) * (attacker.isPlayer() ? attacker.getPlayer().getTemplate().m_atk_crit_chance_mod : 1));

        if (crit)
		{
            damage *= attacker.calcStat(Stats.MCRITICAL_DAMAGE, attacker.isPlayable() && target.isPlayable() ? ConfigValue.MCritBaseDamageToPlayable : ConfigValue.MCritBaseDamage, target, skill);
			damage *= 0.01 * target.calcStat(Stats.MCRIT_DAMAGE_RECEPTIVE, attacker, skill);
		}

        damage = attacker.calcStat(Stats.MAGIC_DAMAGE, damage, target, skill);

        if (target.isMonster())
		{
			damage = attacker.calcStat(Stats.PVE_MAGICAL_DMG, damage, target, skill);
			if(target.isRaid() || target.isBoss() || target.isEpicRaid() || target.isRefRaid())
				damage = attacker.calcStat(Stats.PVR_MAGICAL_DMG, damage, target, skill);
		}

        if (shield) 
		{
			if(Rnd.chance(5))
			{
				damage = 0;
				target.sendPacket(Msg.YOUR_EXCELLENT_SHIELD_DEFENSE_WAS_A_SUCCESS);
				attacker.sendPacket(new SystemMessage(SystemMessage.C1_RESISTED_C2S_MAGIC).addName(target).addName(attacker));
			}
			else
			{
				target.sendPacket(Msg.YOUR_SHIELD_DEFENSE_HAS_SUCCEEDED);
				attacker.sendPacket(new SystemMessage(SystemMessage.YOUR_OPPONENT_HAS_RESISTANCE_TO_MAGIC_THE_DAMAGE_WAS_DECREASED));
			}
        }

		int levelDiff = target.getLevel() - attacker.getLevel();	
		double magic_rcpt = target.calcStat(Stats.p_resist_dd_magic, 1, attacker, skill) - attacker.calcStat(Stats.MAGIC_POWER, target, skill);
		double failChance = 4. * Math.max(1, levelDiff) * (1 + magic_rcpt / 100);
		//if(attacker.isPlayer())
		//	_log.info("calcMagicDam: levelDiff="+levelDiff+" magic_rcpt("+target.calcStat(Stats.p_resist_dd_magic, 1, attacker, skill)+")-("+attacker.calcStat(Stats.MAGIC_POWER, target, skill)+")="+magic_rcpt+" failChance="+failChance);
		if (Rnd.chance(failChance))
		{
			SystemMessage msg;
			if (levelDiff > ConfigValue.NoelConfig2 || (attacker.calcStat(Stats.MAGIC_POWER, target, skill) <= -900) && Rnd.chance(50))
			{
				damage = 0.0;
				msg = new SystemMessage(2269).addName(target).addName(attacker);
				attacker.sendPacket(msg);
				target.sendPacket(msg);
			}
			else
			{
				if(ConfigValue.NoelConfig1 && !target.isPlayable())
				{
					if(levelDiff >= 15 && levelDiff < 20)
						damage *= 0.85;
					else if(levelDiff >= 20 && levelDiff < 25)
						damage *= 0.80;
					else if(levelDiff >= 25 && levelDiff < 30)
						damage *= 0.75;
					else if(levelDiff >= 30 && levelDiff < 35)
						damage *= 0.70;
					else if(levelDiff >= 35 && levelDiff < 40)
						damage *= 0.65;
					else if(levelDiff >= 40 && levelDiff < 50)
						damage *= 0.50;
					else if(levelDiff >= 50 && levelDiff < 60)
						damage *= 0.30;
					else if(levelDiff >= 60 && levelDiff <= 70)
						damage *= 0.20;
					else
						damage *= 0.10;
					msg = new SystemMessage(2280).addName(target).addName(attacker);
					attacker.sendPacket(msg);
				}
				else
				{
					damage /= 2.0;
					msg = new SystemMessage(2280).addName(target).addName(attacker);
					attacker.sendPacket(msg);
					target.sendPacket(msg);
				}
			}
		}
		if(damage > 1.0D)
		{
			if(skill.isDeathlink())
				damage *= 1.8 * (1.0 - attacker.getCurrentHpRatio());
			else if(skill.isBasedOnTargetDebuff()) // 1439
				damage *= Math.min(ConfigValue.CapCurseOfDivinity, 0.3 + 0.08749999999999999 * target.getEffectList().getAllSkills(true).size());
		}
        damage += lethalDamage;

        /*if (skill.getSkillType() == SkillType.MANADAM)
            damage = Math.max(1, damage / 2.);*/

		if (isPvP && damage > 1.0)
		{
			damage *= attacker.calcStat(Stats.PVP_MAGIC_SKILL_DMG_BONUS, 1.0);
			damage /= target.calcStat(Stats.p_pvp_magical_skill_defence_bonus, 1.0);
		}
		else if (!attacker.isPlayable() && damage > 1.0)
			damage /= target.calcStat(Stats.p_pve_magical_skill_defence_bonus, 1.0);

		if(ConfigValue.EnableClanWarDamageBonus && target.isPlayer() && attacker.isPlayer() && attacker.getPlayer().atWarWith(target.getPlayer()))
			damage *= ConfigValue.ClanWarMagicDamageMod;

        if (attacker instanceof L2Summon)
            ((L2Summon) attacker).displayHitMessage(target, (int) damage, crit, false);
        else if (attacker.isPlayer()) 
		{
            if (crit)
                attacker.sendPacket(new SystemMessage(SystemMessage.MAGIC_CRITICAL_HIT).addName(attacker).addDamage(target, target, (long)damage));
			double trans = target.calcStat(Stats.TRANSFER_PET_DAMAGE_PERCENT, 0, attacker, skill);
			if(trans >= 1 && target.getPet() != null && !target.getPet().isDead() && target.getPet().isSummon() && target.getPet().isInRange(target, 1200) && !target.getPet().isInZonePeace() && (trans = (damage / 100d * trans)) < target.getPet().getCurrentHp()-1 && trans > 0)
			{
				//attacker.sendMessage("(3)Пизданул "+((long) (damage - trans))+" лоху и "+((long) trans)+" его носкам.");
				attacker.sendPacket(new SystemMessage(SystemMessage.YOU_HAVE_GIVEN_S1_DAMAGE_TO_YOUR_TARGET_AND_S2_DAMAGE_TO_THE_SERVITOR).addNumber((int) (damage - trans)).addNumber((int) trans));
			}
			else
				attacker.sendHDmgMsg(attacker, target, skill, (int)damage, crit, false);
        }

		if(!ConfigValue.AltFormulaCastBreak && calcCastBreak(target, crit))
			target.abortCast(false);
		else if(ConfigValue.AltFormulaCastBreak && calcCastBreakAlt(target, damage))
			target.abortCast(false);
        return Math.ceil(damage);
    }

	// на шанс крита влияет тоже разница маджика и таргета.
	public static double calcLethal(L2Character activeChar, L2Character target, double baseLethal, int magiclvl)
	{
		double chance = 0;
		int delta=0;
		if (magiclvl > 0)
		{
			delta = ((magiclvl + activeChar.getLevel()) / 2) - 1 - target.getLevel();

			if (delta >= -3)
				chance = (baseLethal * ((double) activeChar.getLevel() / target.getLevel()));
			else if (delta < -3 && delta >= -9)
				chance = (-3) * (baseLethal / (delta));
			else
				chance = baseLethal / 15;
		}
		else
			chance = (baseLethal * ((double) activeChar.getLevel() / target.getLevel()));
		if(activeChar.getPlayer() != null && activeChar.getPlayer().isGM())
			activeChar.sendMessage("calcLethal("+baseLethal+"): chance="+(chance*10)+" Rnd: "+ConfigValue.LethalRate+" delta: "+delta+" magiclvl: "+magiclvl);
		return 10 * chance;
	}

	public static boolean calcLethalHit(L2Character activeChar, L2Character target, L2Skill skill)
	{
		if(skill.getMagicLevel() < (target.getLevel() - 6) || target.block_hp.get())
			return false;
		else if(!target.isLethalImmune())
		{
			// 2nd lethal effect activate (cp,hp to 1 or if target is npc then hp to 1)
			if(skill.getLethal2() > 0 && Rnd.get(ConfigValue.LethalRate) < calcLethal(activeChar, target, skill.getLethal2(),skill.getMagicLevel()))
			{
				if(target.isNpc())
				{
					double damage = target.getCurrentHp() - 1;
					if(ConfigValue.UseAltLethal2)
						damage = target.getCurrentHp()/100*ConfigValue.UseAltLethal2NpcPer-1;
					target.reduceCurrentHp(damage, activeChar, skill, false, false, false, false, false, damage, true, false, false, false);
				}
				else if(target.isPlayer()) // If is a active player set his HP and CP to 1
				{
					L2Player player = (L2Player) target;
					if(!player.isInvul())
					{
						if(activeChar.isPlayer())
						{
							if(ConfigValue.UseAltLethal2)
							{
								double damage = player.getCurrentHp()/100*ConfigValue.UseAltLethal2PlayerPer+player.getCurrentCp()-1;
								player.reduceCurrentHp(damage, activeChar, skill, false, false, false, false, false, damage, true, false, false, false);
							}
							else
							{
								player.setCurrentHp(1.1, false);
								player.setCurrentCp(1);
							}
							player.sendPacket(Msg.LETHAL_STRIKE);
						}
					}
				}
				activeChar.sendPacket(Msg.YOUR_LETHAL_STRIKE_WAS_SUCCESSFUL);
			}
			else if(skill.getLethal1() > 0 && Rnd.get(1000) < calcLethal(activeChar, target, skill.getLethal1(),skill.getMagicLevel()))
			{
				if(target.isPlayer())
				{
					L2Player player = (L2Player) target;
					if(!player.isInvul())
					{
						if(activeChar.isPlayer())
						{
							if(ConfigValue.UseAltLethal1)
								player.reduceCurrentHp(player.getCurrentHp()/8+player.getCurrentCp()-1, activeChar, skill, false, false, false, false, false, player.getCurrentHp()/8+player.getCurrentCp()-1, true, false, false, false);
							else
							{
								player.setCurrentCp(1); // Set CP to 1
								player.sendPacket(Msg.CP_DISAPPEARS_WHEN_HIT_WITH_A_HALF_KILL_SKILL);
							}
							activeChar.sendPacket(Msg.HALF_KILL);
						}
					}
				}
				else
				{
					double damage = target.getCurrentHp()/2;
					if(ConfigValue.UseAltLethal1)
						damage = target.getCurrentHp()/8-1;

					target.reduceCurrentHp(damage, activeChar, skill, false, false, false, false, false, damage, true, false, false, false);
					//target.setCurrentHp(target.getCurrentHp() / 2, false);
					activeChar.sendPacket(Msg.HALF_KILL);
				}
			}
			else
				return false;
		}
		else
			return false;
		
		return true;
	}

    public static boolean calcStunBreak(boolean crit) 
	{
        return Rnd.chance(crit ? 35 : 14);
    }
    /**
     * Returns true in case of fatal blow success
     */
	/**
	double d1 = ath.atan2(obj2Y - obj1Y, obj2X - obj1X) * 65535 / 6.283185307179586;
	 v39 = atan2(*(double *)(v38 + 8) - *(double *)(v37 + 8), *(double *)v38 - *(double *)v37) * 65535.0
        / 6.283185307179586;
    if ( (unsigned int)(unsigned __int16)abs(v35 - (unsigned __int16)(signed int)floor(v39)) - 0x4000 > 0x8000 )
	**/
    public static boolean calcBlow(L2Character activeChar, L2Character target, L2Skill skill) 
	{
        L2Weapon weapon = activeChar.getActiveWeaponItem();

        double base_weapon_crit = weapon == null ? 4. : weapon.getCritical() / 10;
        double dex_bonus = DEXbonus[activeChar.getDEX()];
		double z_diff = activeChar.getZ() - target.getZ();
		if(z_diff < -25)
			z_diff = -25;
		else if (z_diff > 25)
			z_diff = 25;
		double crit_height_bonus = 1 + (z_diff * 4 / 5 + 10) / 100;
        double fatal_blow_rate = activeChar.calcStat(Stats.FATALBLOW_RATE, target, skill);
		double crit_pos = 0;
		switch(Util.getDirectionTo(target, activeChar))
		{
			case BEHIND:
				crit_pos = 1.3;
				break;
			case SIDE:
				crit_pos = 1.1;
				break;
			case FRONT:
				if(skill.isBehind())
				{
					if (ConfigValue.SkillsShowChance && activeChar.isPlayer() && !((L2Player) activeChar).getVarB("SkillsHideChance")) // Выводим сообщение с шансом
						activeChar.sendMessage(new CustomMessage("l2open.gameserver.skills.Formulas.Chance", activeChar).addString(skill.getName()).addNumber((long) 3));
					return Rnd.chance(3);
				}
				else
				{
					crit_pos = 1;
					break;
				}
		}
		double p_critical_rate_position_bonus = activeChar.calcStat(Stats.P_CRITICAL_RATE_POSITION_BONUS, 1.0, target, skill);
		double crit_pos_bonus = crit_pos * p_critical_rate_position_bonus;
		double effect_bonus = (skill.getPower2() + 100) / 100;
        double chance = base_weapon_crit * dex_bonus * crit_height_bonus * crit_pos_bonus * effect_bonus * fatal_blow_rate;

		if(!skill.isBehind())
			chance = chance > 80 ? 80 : chance;
		else
			chance = chance > 100 ? 100 : chance;

        if (ConfigValue.SkillsShowChance && activeChar.isPlayer() && !((L2Player) activeChar).getVarB("SkillsHideChance")) // Выводим сообщение с шансом
            activeChar.sendMessage(new CustomMessage("l2open.gameserver.skills.Formulas.Chance", activeChar).addString(skill.getName()).addNumber((long) chance));

        return Rnd.chance(chance);
    }

    /**
     * Возвращает шанс крита в процентах
     */
    public static double calcCrit(L2Character attacker, L2Character target, L2Skill skill, boolean blow) 
	{
        if(skill != null)
            return skill.getCriticalRate() * (blow ? DEXbonus[attacker.getDEX()] : STRbonus[attacker.getSTR()]) * 0.01 * attacker.calcStat(Stats.SKILL_CRIT_CHANCE_MOD, target, skill);

        double rate = attacker.getCriticalHit(target, null) * 0.01 * target.calcStat(Stats.CRIT_CHANCE_RECEPTIVE, attacker, skill);

		switch(Util.getDirectionTo(target, attacker))
		{
			case BEHIND:
				rate *= 1.4;
				break;
			case SIDE:
				rate *= 1.2;
				break;
		}
		rate *= attacker.calcStat(Stats.P_CRITICAL_RATE_POSITION_BONUS, 1.0, target, skill);
		if(attacker.isPlayer())
		{
			rate *= attacker.getPlayer().getTemplate().p_atk_crit_chance_mod;
			if(attacker.getActiveWeaponItem() == null)
				return rate / 20;
		}
        return rate / 10;
    }

    public static boolean calcMCrit(double mRate) 
	{
        // floating point random gives more accuracy calculation, because argument also floating point
        return Rnd.get() * 100 <= Math.min(ConfigValue.LimitMCritical, mRate);
    }

    public static boolean calcCastBreak(L2Character target, boolean crit) 
	{
		if(target == null || target.isInvul() || target.isRaid() || !target.isCastingNow() || target.isEpicRaid())
			return false;
		L2Skill skill = target.getCastingSkill();
		if(skill == null || skill.getSkillType() == SkillType.TAKECASTLE || skill.getSkillType() == SkillType.TAKEFORTRESS || skill.getSkillType() == SkillType.TAKEFLAG || !skill.isMagic() || target.isInvul() || target.block_hp.get())
			return false;
		return Rnd.chance(target.calcStat(Stats.CAST_INTERRUPT, crit ? ConfigValue.CastInterruptCrit : ConfigValue.CastInterrupt, null, skill)*ConfigValue.CastInterruptMod);
    }

	public static final boolean calcCastBreakAlt(L2Character target, double dmg)
	{
		if(target == null || target.isInvul() || target.isRaid() || !target.isCastingNow())
			return false;
		L2Skill skill = target.getCastingSkill();
		if(skill == null || skill.getSkillType() == SkillType.TAKECASTLE || skill.getSkillType() == SkillType.TAKEFORTRESS || skill.getSkillType() == SkillType.TAKEFLAG || !skill.isMagic() || target.isInvul() || target.block_hp.get())
			return false;

		double chance;
		double preChance = chance = 500 * dmg / (target.getMaxHp() + target.getMaxCp());

		chance /= (MENbonus[target.getMEN()]);
		chance *= target.calcStat(Stats.CAST_INTERRUPT, 1, null, skill);
		//if(target.isPlayer())
		//	_log.info("calcCastBreak-> dmg: "+dmg+" HP+CP: "+(target.getMaxHp() + target.getMaxCp())+" preChance("+preChance+"): "+chance+" men: "+(MENbonus[target.getMEN()] * 100 - 100)+" resist: "+target.calcStat(Stats.CAST_INTERRUPT, 1, null, skill));

		if(chance > 99)
			chance = 99;
		else if(chance < 1)
			chance = 1;

		//if(target.isPlayer())
		//	_log.info("calcCastBreak-> chance: "+chance);
		return Rnd.get(100) < chance;
	}

    /**
     * Calculate delay (in milliseconds) for skills cast
     */
    public static int calcMAtkSpd(L2Character attacker, L2Skill skill, double skillTime) 
	{
        if (skill.isMagic())
            return (int) (skillTime * 333 / Math.max(attacker.getMAtkSpd(), 1));
        return (int) (skillTime * 333 / Math.max(attacker.getPAtkSpd(), 1));
    }

    /**
     * Calculate reuse delay (in milliseconds) for skills
     */
    public static long calcSkillReuseDelay(L2Character actor, L2Skill skill) 
	{
        long reuseDelay = skill.getReuseDelay();
        if (actor.isMonster())
            reuseDelay = skill.getReuseForMonsters();
        if (skill.isReuseDelayPermanent() || skill.isHandler() || skill.isItemSkill())
            return reuseDelay;
        if (actor.getSkillMastery(skill.getId()) == 1) 
		{
			actor.sendPacket(Msg.A_SKILL_IS_READY_TO_BE_USED_AGAIN);
            actor.removeSkillMastery(skill.getId());
            return 0;
        }
        if (skill.isMagic())
            return (long) actor.calcStat(Stats.MAGIC_REUSE_RATE, reuseDelay, null, skill);
        return (long) actor.calcStat(Stats.PHYSIC_REUSE_RATE, reuseDelay, null, skill);
    }

	// TODO: PTS
	/**
	double __cdecl CAttackAction::GetHitRatio(bool a1, double a2, double a3, const struct FVector *a4, const struct FVector *a5, const struct FAngle *a6)
{
  __m128i v6; // xmm6@0
  __int64 v7; // rax@1
  __int64 v8; // rdx@1
  double v9; // xmm3_8@1
  __int64 v10; // rbx@1
  const struct FVector *v11; // rdi@1
  __int64 v12; // r8@1
  __int64 v13; // rdx@1
  double v14; // xmm1_8@2
  double v15; // xmm2_8@4
  double v16; // xmm0_8@4
  double v17; // xmm1_8@6
  double v18; // xmm5_8@6
  double result; // xmm0_8@6
  __int128 v20; // [sp+20h] [bp-38h]@1

  v7 = *MK_FP(__GS__, 88i64);
  v8 = (unsigned int)TlsIndex;
  _mm_store_si128((__m128i *)&v20, v6);
  v9 = a2;
  v10 = *(_QWORD *)(v7 + 8 * v8);
  v11 = a4;
  v12 = *(_DWORD *)(v10 + 32024);
  v13 = dword_1E8EC90[v12 + 0x100000];
  dword_1E8EC90[v12 + 0x100000] = v13 + 1;
  qword_226F890[v13 + 1000 * v12] = (__int64)&off_B8CE00;
  if ( a1 )
    v14 = 26.0;
  else
    v14 = 29.0;
  v15 = (a3 - v14 * 0.8006559766763849 - v9) * (857375.0 / v14);
  v16 = 0.0;
  if ( -0.0 - v15 >= 0.0 )
    v16 = -0.0 - v15;
  pow(v16, 0.3333333333333333);
  CAttackAction::CalcHitLocBonus(v11, a5, a6);
  v17 = 30.0;
  result = CAttackAction::CalcHitHeightBonus(v11, a5) * v18;
  if ( result < 30.0 || (v17 = 98.0, result > 98.0) )
    result = v17;
  --dword_1E8EC90[*(_DWORD *)(v10 + 32024) + 0x100000];
  return result;
}
----
double __cdecl CAttackAction::CalcHitLocBonus(const struct FVector *a1, const struct FVector *a2, const struct FAngle *a3)
{
  __int64 v3; // rdi@1
  const struct FAngle *v4; // rbx@1
  __int64 v5; // r9@1
  __int64 v6; // r8@1
  double v7; // xmm0_8@1
  signed int v8; // ecx@1
  double result; // xmm0_8@5

  v3 = *(_QWORD *)(*MK_FP(__GS__, 88i64) + 8i64 * (unsigned int)TlsIndex);
  v4 = a3;
  v5 = *(_DWORD *)(v3 + 32024);
  v6 = dword_1E8EC90[v5 + 0x100000];
  dword_1E8EC90[v5 + 0x100000] = v6 + 1;
  qword_226F890[v6 + 1000 * v5] = (__int64)&off_B8C140;
  v7 = atan2(*((double *)a2 + 1) - *((double *)a1 + 1), *(double *)a2 - *(double *)a1);
  v8 = (unsigned __int16)abs(*(_WORD *)v4 - (unsigned __int16)(signed int)floor(v7 * 65535.0 / 6.283185307179586));
  if ( v8 >= 0x2000 && v8 <= 24576 || (unsigned int)(v8 - 40960) <= 0x4000 )
  {
    result = 1.2;
  }
  else if ( (unsigned int)(v8 - 0x2000) <= 0xC000 )
  {
    result = 1.0;
  }
  else
  {
    result = 1.3;
  }
  --dword_1E8EC90[*(_DWORD *)(v3 + 32024) + 0x100000];
  return result;
}
----
double __cdecl CAttackAction::CalcHitHeightBonus(const struct FVector *a1, const struct FVector *a2)
{
  __int64 v2; // r10@1
  __int64 v3; // r8@1
  double v4; // xmm1_8@1
  double v5; // xmm1_8@3
  double v6; // xmm1_8@5

  v2 = *(_DWORD *)(*(_QWORD *)(*MK_FP(__GS__, 88i64) + 8i64 * (unsigned int)TlsIndex) + 32024i64);
  v3 = dword_1E8EC90[v2 + 0x100000];
  dword_1E8EC90[v2 + 0x100000] = v3 + 1;
  qword_226F890[v3 + 1000 * v2] = (__int64)&off_B8C310;
  v4 = (double)((signed int)floor(*((double *)a2 + 2)) - (signed int)floor(*((double *)a1 + 2)));
  if ( v4 <= -1100.0 )
    goto LABEL_9;
  if ( v4 <= -300.0 )
  {
    v5 = (v4 * 0.125 + 37.5) / 100.0;
LABEL_8:
    v6 = v5 + 1.0;
    goto LABEL_10;
  }
  if ( v4 > 100.0 )
  {
    if ( v4 <= 500.0 )
    {
      v5 = (v4 * -0.25 + 25.0) / 100.0;
      goto LABEL_8;
    }
LABEL_9:
    v6 = 0.0;
    goto LABEL_10;
  }
  v6 = 1.0;
LABEL_10:
  --dword_1E8EC90[v2 + 0x100000];
  return v6;
}
	**/
    /**
     * Returns true if hit missed (target evaded)
     */
    public static boolean calcHitMiss(L2Character attacker, L2Character target) 
	{
        int chanceToHit = 88 + 2 * (attacker.getAccuracy() - target.getEvasionRate(attacker));

        chanceToHit = Math.max(chanceToHit, 30);
        chanceToHit = Math.min(chanceToHit, 98);

		Util.TargetDirection direction = Util.getDirectionTo(attacker, target);
		switch(direction)
		{
			case BEHIND:
				chanceToHit *= 1.2;
				break;
			case SIDE:
				chanceToHit *= 1.1;
				break;
		}

        return !Rnd.chance(chanceToHit);
    }

    /**
     * Returns true if shield defence successfull
     */
    public static boolean calcShldUse(L2Character attacker, L2Character target) 
	{
        int angle = (int) target.calcStat(Stats.SHIELD_ANGLE, attacker, null);
		if(!Util.isFacing(target, attacker, angle))
            return false;
        return Rnd.chance(target.calcStat(Stats.SHIELD_RATE, attacker, null));
    }

	public static double calcSavevsDependence(int save, L2Character cha) 
	{
        try
		{
            switch(save)
			{
                case L2Skill.SAVEVS_INT:
                    return 2. - Math.sqrt(INTbonus[cha.getINT()]);
                case L2Skill.SAVEVS_WIT:
                    return 2. - Math.sqrt(WITbonus[cha.getWIT()]);
                case L2Skill.SAVEVS_MEN:
                    return 2. - Math.sqrt(MENbonus[cha.getMEN()]);
                case L2Skill.SAVEVS_CON:
                    return 2. - Math.sqrt(CONbonus[cha.getCON()]);
                case L2Skill.SAVEVS_DEX:
                    return 2. - Math.sqrt(DEXbonus[cha.getDEX()]);
                case L2Skill.SAVEVS_STR:
                    return Math.min(2. - Math.sqrt(STRbonus[cha.getSTR()]), 1.);
            }
        } 
		catch(ArrayIndexOutOfBoundsException e) 
		{
            _log.warning("Failed calc savevs on char " + cha + " with save-stat " + save);
            e.printStackTrace();
        }
        return 1.;
    }

	public static double calcSavevsDependence2(int save, L2Character cha) 
	{
        try
		{
            switch (save) 
			{
                case L2Skill.SAVEVS_INT:
                    return cha.getINT();
                case L2Skill.SAVEVS_WIT:
                    return cha.getWIT();
                case L2Skill.SAVEVS_MEN:
                    return cha.getMEN();
                case L2Skill.SAVEVS_CON:
                    return cha.getCON();
                case L2Skill.SAVEVS_DEX:
                    return cha.getDEX();
                case L2Skill.SAVEVS_STR:
                    return cha.getSTR();
            }
        } 
		catch (ArrayIndexOutOfBoundsException e) 
		{
            _log.warning("Failed calc savevs on char " + cha + " with save-stat " + save);
            e.printStackTrace();
        }
        return 1.;
    }

	public static boolean calcSkillSuccess(Env env, int spiritshot, boolean mag_calc)
	{
		final L2Skill skill = env.skill;
		final L2Character caster = env.character;
		final L2Character target = env.target;

		boolean isGM = caster.isPlayer() && (((L2Player) caster).isGM() && !((L2Player) caster).getVarB("SkillsHideChance") || ConfigValue.SkillsShowChanceFull && ((L2Player) caster).getVarB("SkillsShowChanceFull", true));

		if(ConfigValue.UseOldDebuffFormula || ConfigValue.DebuffFormulaType == 2)
			return calcSkillSuccess2(env, spiritshot, mag_calc);
		else if(ConfigValue.DebuffFormulaType == 1)
			return calcSkillSuccess1(env, spiritshot, mag_calc);
		if(env.value == -1)
			return true;

		Env copy = new Env();
        copy.character = env.character;
        copy.target = env.target;
        copy.skill = env.skill;
        copy.value = env.skill.getActivateRate();

		env.value = Math.max(Math.min(env.value, 100), 1); // На всякий случай
		final double base = env.value; // Запоминаем базовый шанс (нужен позже)

		if(isGM) 
		{
			caster.sendMessage("skill: " + skill);
			//caster.sendMessage("magic level: " + mLevel);
			caster.sendMessage("chance: " + env.value);
		}

		if(!skill.isOffensive())
			return Rnd.chance(env.value);

		if(skill.getSaveVs() > 0)
		{
			env.value *= calcSavevsDependence(skill.getSaveVs(), target);
			 if(isGM)
                caster.sendMessage("chance w/savevs: " + env.value);
		}

		env.value = Math.max(env.value, 1);

		double mAtkMod = 1.;
		int ssMod = 0;
		if(skill.isMagic() && mag_calc) // Этот блок только для магических скиллов
		{
			int mdef = Math.max(1, target.getMDef(target, skill)); // Вычисляем mDef цели
			double matk = caster.getMAtk(target, skill);

			if(skill.isSSPossible() && spiritshot > 0) // Считаем бонус от шотов
                matk *= spiritshot * 2;

			//mAtkMod = 14 * Math.sqrt(matk)/mdef;
			if(target.isPlayer())
                mAtkMod = ConfigValue.SkillsChanceModPvP * Math.pow(matk, ConfigValue.SkillsChancePowPvP) / mdef;
            else
                mAtkMod = ConfigValue.SkillsChanceMod * Math.pow(matk, ConfigValue.SkillsChancePow) / mdef;

			env.value *= mAtkMod;
			env.value = Math.max(env.value, 1);
			if(isGM)
				caster.sendMessage("chance mAtkMod["+spiritshot+"]("+mAtkMod+"): " + env.value);
		}

		double lvlDependMod = skill.getLevelModifier();
		if(lvlDependMod != 0)
		{
			final int attackLevel =  skill.getMagicLevel() > 0 ? skill.getMagicLevel() : caster.getLevel();

			// Это выглядит прмиерно вот так :)
			final int delta = attackLevel - target.getLevel();
			lvlDependMod = 1. + delta * 0.032 * lvlDependMod;
			if(lvlDependMod < 0)
				lvlDependMod = 0.1;
			env.value *= lvlDependMod;

			if(isGM)
				caster.sendMessage("chance lvlDependMod("+lvlDependMod+"): " + env.value);
		}

		double resMod = 0;
		double powMod = 0;
		double traitMod = 1.;
		double debuffMod = 1.;

		double debRes = target.calcStat(Stats.DEBUFF_RECEPTIVE, caster, skill);
		debuffMod = 1. - debRes / 120.;

		if(debuffMod != 1 && mag_calc)
		{
			if(debRes == 100) // Если у нас 100% резиста от дебафа, то он не пройдет в любом случае.
				return false;
			debuffMod = Math.max(debuffMod, 0);
			env.value *= debuffMod;
		}
		if(isGM)
		{
			//caster.sendMessage("chance debuffRec("+target.calcStat(Stats.DEBUFF_RECEPTIVE, caster, skill)+")");
			caster.sendMessage("chance debuffMod("+debuffMod+"): " + env.value);
		}

		SkillTrait trait = skill.getTraitType();
		if(trait != null && trait != SkillTrait.trait_none)
		{
			if(trait.fullResist(target))
			{
				if(isGM)
					caster.sendMessage("fullResist("+trait+")");
				return false;
			}
			resMod = trait.calcResist(target); // Трейт Резист
			powMod = trait.calcPower(caster); // Трейт Повер

			// На оффе делается кап в 100 на разницу между резистами...
			double maxResist = powMod - resMod; // Не, кап не делается, там ваще улет все...Math.min(Math.max(powMod - resMod, -100), 100);
			if(maxResist < 0)
				traitMod = 1 / (1 - 0.075 * maxResist);

			if(maxResist >= 0)
				traitMod = 1 + 0.02 * maxResist;
			if(isGM)
			{
				caster.sendMessage("chance("+trait+") resMod("+resMod+")");
				caster.sendMessage("chance("+trait+") powMod("+powMod+")");
				//caster.sendMessage("chance trait("+traitMod+"): " + env.value);
			}
			if(traitMod != 1)
			{
				traitMod = Math.abs(traitMod);
				env.value *= traitMod;
			}
		}

		if(isGM)
			caster.sendMessage("chance trait("+traitMod+"): " + env.value);

		/*double elementMod = 0;
		final Element element = skill.getElement();
		if(element != Element.NONE)
		{
			elementMod = skill.getElementPower();
			Element attackElement = getAttackElement(caster, target);
			if(attackElement == element)
				elementMod += caster.calcStat(element.getAttack(), 0);

			elementMod -= target.calcStat(element.getDefence(), 0);
			elementMod = Math.round(elementMod / 10);

			env.value += elementMod;
		}*/

		double attrMod = 1.;
		final Element element = skill.getElement();
		if(isGM)
		{
			caster.sendMessage("===========================");
			caster.sendMessage("chance element("+element.getId()+"): " + element);
		}
		if(element != Element.NONE)
		{
			//ElementalAttribute elAtr = skill.getElementalAttribute();
			double elementMod = skill.getElementPower();
			if(isGM)
				caster.sendMessage("chance ElementPower: " + elementMod);
			elementMod += target.calcStat(element.getDefence(), 0);//target.getStat().getElementalDef(elAtr);
			if(isGM)
				caster.sendMessage("chance TargetDef: " + target.calcStat(element.getDefence(), 0));
			if(caster.isCharacter() && caster.getAttackElementValue()[0] == element.getId())
				elementMod += caster.getAttackElementValue()[1];
			if(isGM)
				caster.sendMessage("chance Atack["+caster.getAttackElementValue()[0]+"]["+caster.getAttackElementValue()[1]+"]");
			if(isGM)
			{
				caster.sendMessage("chance elementMod: " + elementMod);
				caster.sendMessage("chance attr: " + attrMod);
			}
			attrMod *= (100f + 0.2f * elementMod) / 100f;
		}
		if(attrMod < 0)
			attrMod = 0.01;

		if(isGM)
		{
			caster.sendMessage("chance attrMod: " + attrMod);
			caster.sendMessage("===========================");
		}
		env.value *= attrMod;

		if(isGM)
			caster.sendMessage("chance finish: " + env.value);
		//if(effect_chance == -1) // Вот это я не знаю, кап должен работать только на скилы или и на шансы самих эффектов...
		if(mag_calc)
		{
			env.value = Math.max(env.value, Math.min(base, ConfigValue.SkillsChanceMin)); // Если базовый шанс более ConfigValue.SkillsChanceMin, то при небольшой разнице в уровнях, делаем кап снизу.
			env.value = Math.max(Math.min(env.value, ConfigValue.SkillsChanceCap), 1); // Применяем кап
		}
		else // Что бы сопляки не плакались мамочке, что шанс выше 100% подымается...
		{
			env.value = Math.max(env.value, Math.min(base, 0));
			env.value = Math.max(Math.min(env.value, 100), 1);
		}
		final boolean result = Rnd.chance((int)env.value);
		if(ConfigValue.SkillsShowChance || isGM) 
		{
            L2Player player = caster.getPlayer();
            if(player != null && !player.getVarB("SkillsHideChance"))
                player.sendMessage(new CustomMessage("l2open.gameserver.skills.Formulas.Chance", player).addString(skill.getName()).addNumber(Math.round(env.value)));
        }
		double result3 = PtsFormulas.calcSkillSuccess(mag_calc, copy, spiritshot);

		if(isGM)
			caster.getPlayer().sendMessage("SkillChance: ["+env.value+"]["+result3+"]");
		return result;
	}

	public static boolean calcSkillSuccess1(Env env, int spiritshot, boolean mag_calc)
	{
		if(env.value == -1)
			return true;

		env.value = Math.max(Math.min(env.value, 100), 1); // На всякий случай
		final double base = env.value; // Запоминаем базовый шанс (нужен позже)

		final L2Skill skill = env.skill;
		if(!skill.isOffensive())
			return Rnd.chance(env.value);

		final L2Character caster = env.character;
		final L2Character target = env.target;

		boolean isGM = caster.isPlayer() && ((L2Player) caster).isGM() && !((L2Player) caster).getVarB("SkillsHideChance");

		if(isGM) 
		{
			caster.sendMessage("skill: " + skill);
			caster.sendMessage("magic level: " + skill.getMagicLevel());
			caster.sendMessage("chance: " + env.value);
		}

		int lvlmodifier = (skill.getMagicLevel() - target.getLevel()) * skill.getLevelModifier();
		double stat_modifier = 1;

		switch(skill.getSaveVs())
		{
			case L2Skill.SAVEVS_INT:
				stat_modifier = INTbonus[target.getINT()];
				break;
			case L2Skill.SAVEVS_WIT:
				stat_modifier = WITbonus[target.getWIT()];
				break;
			case L2Skill.SAVEVS_MEN:
				stat_modifier = MENbonus[target.getMEN()];
				break;
			case L2Skill.SAVEVS_CON:
				stat_modifier = CONbonus[target.getCON()];
				break;
			case L2Skill.SAVEVS_DEX:
				stat_modifier = DEXbonus[target.getDEX()];
				break;
			case L2Skill.SAVEVS_STR:
				stat_modifier = STRbonus[target.getSTR()];
				break;
		}

		double rate = env.value / stat_modifier + lvlmodifier;

		double mAtkMod = 1.;
		int ssMod = 0;
		if(skill.isMagic() && mag_calc) // Этот блок только для магических скиллов
		{
			int mdef = Math.max(1, target.getMDef(target, skill)); // Вычисляем mDef цели
			double matk = caster.getMAtk(target, skill);

			if(skill.isSSPossible() && spiritshot > 0) // Считаем бонус от шотов
                matk *= spiritshot * 2;

			mAtkMod = Math.sqrt(matk/ mdef);

			rate *= mAtkMod;
			rate = Math.max(rate, 1);
			if(isGM)
				caster.sendMessage("chance mAtkMod("+mAtkMod+"): " + rate);
		}

		
		double traitMod = 1.;
		double debuffMod = 1.;

		double debRes = target.calcStat(Stats.DEBUFF_RECEPTIVE, caster, skill);
		debuffMod = 1. - debRes / 120.;

		if(debuffMod != 1 && mag_calc)
		{
			if(debRes == 100) // Если у нас 100% резиста от дебафа, то он не пройдет в любом случае.
				return false;
			debuffMod = Math.max(debuffMod, 0);
			rate *= debuffMod;
		}
		if(isGM)
			caster.sendMessage("chance debuffMod("+target.calcStat(Stats.DEBUFF_RECEPTIVE, caster, skill)+")("+debuffMod+"): " + rate);

		SkillTrait trait = skill.getTraitType();
		if(trait != null && trait != SkillTrait.trait_none)
		{
			if(trait.fullResist(target))
			{
				if(isGM)
					caster.sendMessage("fullResist("+trait+")");
				return false;
			}

			double resMod = trait.calcResist(target); // Трейт Резист
			double powMod = trait.calcPower(caster); // Трейт Повер

			rate *= resMod * powMod;
			if(isGM)
			{
				caster.sendMessage("chance("+trait+") resMod("+resMod+")");
				caster.sendMessage("chance("+trait+") powMod("+powMod+")");
				caster.sendMessage("chance trait("+resMod*powMod+"): " + rate);
			}
		}

		// TODO: MODFIX
		double attrMod = 1.;
		final Element element = skill.getElement();
		if(isGM)
		{
			caster.sendMessage("===========================");
			caster.sendMessage("chance element("+element.getId()+"): " + element);
		}
		if(element != Element.NONE)
		{
			double elementMod = skill.getElementPower();
			if(isGM)
				caster.sendMessage("chance ElementPower: " + elementMod);
			elementMod += target.calcStat(element.getDefence(), 0);
			if(isGM)
				caster.sendMessage("chance TargetDef: " + target.calcStat(element.getDefence(), 0));
			if(caster.isCharacter() && caster.getAttackElementValue()[0] == element.getId())
				elementMod += caster.getAttackElementValue()[1];
			if(isGM)
				caster.sendMessage("chance Atack["+caster.getAttackElementValue()[0]+"]["+caster.getAttackElementValue()[1]+"]");
			if(isGM)
			{
				caster.sendMessage("chance elementMod: " + elementMod);
				caster.sendMessage("chance attr: " + attrMod);
			}
			attrMod *= (100f + 0.2f * elementMod) / 100f;
		}
		if(attrMod < 0)
			attrMod = 0.01;

		if(isGM)
		{
			caster.sendMessage("chance attrMod: " + attrMod);
			caster.sendMessage("===========================");
		}
		rate *= attrMod;

		if(isGM)
			caster.sendMessage("chance finish: " + rate);

		if(mag_calc)
		{
			if(rate > 90)
				rate = ConfigValue.SkillsChanceCap;
			else if (rate < 10 && target.getLevel() - skill.getMagicLevel() <= 25)
				rate = ConfigValue.SkillsChanceMin;
		}
		else // Что бы сопляки не плакались мамочке, что шанс выше 100% подымается...
		{
			rate = Math.max(rate, Math.min(base, 0));
			rate = Math.max(Math.min(rate, 100), 1);
		}

		if(ConfigValue.SkillsShowChance || isGM) 
		{
            L2Player player = caster.getPlayer();
            if(player != null && !player.getVarB("SkillsHideChance"))
                player.sendMessage(new CustomMessage("l2open.gameserver.skills.Formulas.Chance", player).addString(skill.getName()).addNumber(Math.round(rate)));
        }
		return Rnd.chance((int)rate);
	}

	public static boolean calcSkillSuccess2(Env env, int spiritshot, boolean mag_calc)
	{
		if(env.value == -1)
			return true;
        L2Skill skill = env.skill;
        if(!skill.isOffensive())
            return Rnd.chance(env.value);

        L2Character character = env.character;
        L2Character target = env.target;

		env.value = Math.max(Math.min(env.value, 100), 1); // На всякий случай
        double base = env.value; // Запоминаем базовый шанс (нужен позже)

        double mLevel = skill.getMagicLevel() <= 0 || !character.isPlayer() ? character.getLevel() : skill.getMagicLevel(); // Разница в уровнях

		if(skill.getLevelModifier() > 0)
			mLevel = (mLevel - target.getLevel() + 3) * skill.getLevelModifier(); //Не пойму, зачем у них +3 стоит...

        env.value += mLevel >= 0 ? 0 : mLevel;

        boolean isGM = character.isPlayer() && ((L2Player) character).isGM();

        if(isGM) 
		{
            character.sendMessage("magic level: " + mLevel);
            character.sendMessage("chance: " + env.value);
        }

        if(skill.getSaveVs() > 0) 
		{
            if(isGM) 
			{
                character.sendMessage("skill: " + skill);
                character.sendMessage("chance: " + env.value);
                character.sendMessage("save type: " + skill.getSaveVs());
            }

            env.value += 30 - calcSavevsDependence2(skill.getSaveVs(), target);
            // В принципе я считаю можно даже не лезть в формулу, а просто снизить множитель шанса прохождения в конфиге.

            if(isGM)
                character.sendMessage("chance w/savevs: " + env.value);
        }

        env.value = Math.max(env.value, 1);

        if(skill.isMagic() && mag_calc) // Этот блок только для магических скиллов
        {
            int mdef = Math.max(1, target.getMDef(target, skill)); // Вычисляем mDef цели
            double matk = character.getMAtk(target, skill);
            if (skill.isSSPossible() && spiritshot > 0) // Считаем бонус от шотов
                matk *= spiritshot * 2;
            if (target.isPlayer())
                env.value *= ConfigValue.SkillsChanceModPvP * Math.pow(matk, ConfigValue.SkillsChancePowPvP) / mdef;
            else
                env.value *= ConfigValue.SkillsChanceMod * Math.pow(matk, ConfigValue.SkillsChancePow) / mdef;
        }

		double res = 0;
		SkillTrait trait = skill.getTraitType();
		if(trait != null && trait != SkillTrait.trait_none)
		{
			if(trait.fullResist(target))
			{
				if(isGM)
					character.sendMessage("fullResist("+trait+")");
				return false;
			}
			res += trait.calcResist(target);
			res -= trait.calcPower(character);
		}
		res += target.calcStat(Stats.DEBUFF_RECEPTIVE, character, skill);
		if(res != 0) 
		{
			double mod = Math.abs(0.02 * res) + 1;
			env.value = res > 0 ? env.value / mod : env.value * mod;
			if(isGM && trait != null && trait != SkillTrait.trait_none) 
			{
				character.sendMessage("resist: " + trait);
				character.sendMessage("defense: " + (int) trait.calcResist(target));
				character.sendMessage("attack: " + (int) trait.calcPower(character));
				character.sendMessage("chance w/resist: " + env.value);
			}
		}

        env.value = character.calcStat(Stats.ACTIVATE_RATE, env.value, target, skill); // Учитываем общий бонус к шансам, если есть

        //if(skill.isSoulBoost()) // Бонус от душ камаелей
        //	env.value *= 0.85 + 0.06 * Math.min(character.getConsumedSouls(), 5);

		if(mag_calc)
		{
			env.value = Math.max(env.value, Math.min(base, ConfigValue.SkillsChanceMin)); // Если базовый шанс более ConfigValue.SkillsChanceMin, то при небольшой разнице в уровнях, делаем кап снизу.
			env.value = Math.max(Math.min(env.value, ConfigValue.SkillsChanceCap), 1); // Применяем кап
		}

        if(target.isPlayer()) 
		{
            L2Player player = (L2Player) target;
            if((ConfigValue.SkillsShowChance && character.isMonster() || player.isGM()) && player.getVarB("SkillsMobChance"))
                target.sendMessage(character.getName() + ": " + new CustomMessage("l2open.gameserver.skills.Formulas.Chance", target).addString(skill.getName()).addNumber(Math.round(env.value)).toString());
        }

        if(ConfigValue.SkillsShowChance || isGM) 
		{
            L2Player player = character.getPlayer();
            if(player != null && !player.getVarB("SkillsHideChance"))
                player.sendMessage(new CustomMessage("l2open.gameserver.skills.Formulas.Chance", player).addString(skill.getName()).addNumber(Math.round(env.value)));
        }
        return Rnd.chance(env.value);
    }

	/**
	 * Возвращает максимально эффективный атрибут, при атаке цели
	 * @param attacker
	 * @param target
	 * @return
	 */
	public static Element getAttackElement(L2Character attacker, L2Character target)
	{
		double val, max = Double.MIN_VALUE;
		Element result = Element.NONE;
		for(Element e : Element.VALUES)
		{
			val = attacker.calcStat(e.getAttack(), 0., null, null);
			if(val <= 0.)
				continue;

			if(target != null)
				val -= target.calcStat(e.getDefence(), 0., null, null);

			if(val > max)
			{
				result = e;
				max = val;
			}
		}

		return result;
	}

    public static boolean calcSkillSuccess(L2Character player, L2Character target, L2Skill skill, int spiritshot) 
	{
        Env env = new Env();
        env.character = player;
        env.target = target;
        env.skill = skill;
        env.value = skill.getActivateRate();
		return calcSkillSuccess(env, spiritshot, true);
    }

    public static void calcSkillMastery(L2Skill skill, L2Character activeChar) 
	{
        if (skill.isHandler())
            return;

		if (activeChar.getSkillLevel(331) > 0 && (activeChar.calcStat(Stats.SKILL_MASTERY, activeChar.getINT(), null, skill) / 2) >= Rnd.get(1000) || activeChar.getSkillLevel(330) > 0 && (activeChar.calcStat(Stats.SKILL_MASTERY, activeChar.getSTR(), null, skill) / 2) >= Rnd.get(1000))
		{
            byte masteryLevel;
            L2Skill.SkillType type = skill.getSkillType();
			//if(skill.isMusic() || type == L2Skill.SkillType.BUFF || type == L2Skill.SkillType.HOT || type == L2Skill.SkillType.HEAL_PERCENT)
			if(skill.getAbnormalTime() > 0)
				masteryLevel = 2; // время действия в 2 раза
			// это фигня, нету такого...
			//else if(type == L2Skill.SkillType.HEAL)
			//	masteryLevel = 3; // увеличивает силу хила в 3 раза
			else
				masteryLevel = 1; // время отката 0 
            if (masteryLevel > 0)
                activeChar.setSkillMastery(skill.getId(), masteryLevel);
        }
    }

    public static double calcDamageResists(L2Skill skill, L2Character attacker, L2Character defender, double value) 
	{
        if (attacker == defender) // это дамаг от местности вроде ожога в лаве, наносится от своего имени
            return value; // TODO: по хорошему надо учитывать защиту, но поскольку эти скиллы немагические то надо делать отдельный механизм

        if (attacker.isBoss())
            value *= ConfigValue.RateEpicAttack;
        else if (attacker.isRaid() || attacker.isRefRaid())
            value *= ConfigValue.RateRaidAttack;

        if (defender.isBoss())
            value /= ConfigValue.RateEpicDefense;
        else if (defender.isRaid() || defender.isRefRaid())
            value /= ConfigValue.RateRaidDefense;

        // модификатор для расчёта атрибутов для суммонов
        double attackMod = 1.0;

        // Передача атрибутов от мастера к самону работает тока у саммонеров (фантом суммонер, элементал суммонер, варлок и классов, производных от этих трех)
        // Случай 1: атакует суммонер с призванным суммоном
		if(attacker.isPlayer() && attacker.getPlayer().getClassId().isAllowAttributeTransfer() && attacker.getPet() != null && attacker.getPet().isSummon() && attacker.getPlayer().getWeaponsExpertisePenalty() <= 0)
            attackMod = 0.2; // 20% остаток у мастера
		// Случай 2: атакует суммон
        else if(attacker.isSummon() && attacker.getPlayer() != null && attacker.getPlayer().getClassId().isAllowAttributeTransfer() && attacker.getPlayer().getWeaponsExpertisePenalty() <= 0)
            attacker = attacker.getPlayer(); // меняем, чтоб тупо всё считалось с хозяина
        // Случай 3: атакуют суммона
        if (defender.isSummon() && defender.getPlayer() != null && defender.getPlayer().getClassId().isAllowAttributeTransfer())
            defender = defender.getPlayer(); // защитн. атрибуты 100% суммону, 100% мастеру

        L2Player pAttacker = attacker.getPlayer();

        // если уровень игрока ниже чем на 2 и более уровней моба 78+, то его урон по мобу снижается
        int diff = defender.getLevel() - (pAttacker != null ? pAttacker.getLevel() : attacker.getLevel());
        if (attacker.isPlayable() && defender.isMonster() && defender.getLevel() >= 78 && diff > 2)
            value *= .7 / Math.pow(diff - 2, .25);

        if (skill != null)
		{
            if (pAttacker != null && pAttacker.isGM()) 
			{
                attacker.sendMessage("skill element: " + skill.getElement());
                attacker.sendMessage("skill element power: " + skill.getElement().getAttack());
            }
            if (skill.getElement() == Element.NONE)
                return value;
            double attack = attacker.calcStat(skill.getElement().getAttack(), skill.getElementPower());
            double defense = -defender.calcStat(skill.getElement().getDefence(), 0);
            return applyDefense(attacker, defender, defense, attack * attackMod, value, skill);
        }

        TreeMap<Double, Stats> sort_attibutes = new TreeMap<Double, Stats>();
        for (Element e : Element.values())
            if (e != Element.NONE)
                sort_attibutes.put(attacker.calcStat(e.getAttack(), 0), e.getDefence());

        int attack = sort_attibutes.lastEntry().getKey().intValue();
        if (attack <= 0)
            return value;

        double defense = -defender.calcStat(sort_attibutes.lastEntry().getValue(), 0);
        return applyDefense(attacker, defender, defense, attack * attackMod, value, null);
    }

	public static double applyDefense(L2Character attacker, L2Character defender, double defense, double attack, double value, L2Skill skill)
	{
		if(skill == null || !skill.isMagic())
		{
			if(attacker.isPlayer() && ((L2Player) attacker).isGM() || ConfigValue.AttribShowCalc)
			{
				double mod = getElementMod(defense, attack);
				attacker.sendMessage("--- element calc ---");
				attacker.sendMessage("mod: " + mod);
				attacker.sendMessage("defense: " + (int) defense);
				attacker.sendMessage("attack: " + (int) attack);
				attacker.sendMessage("skill: " + (skill == null ? "null" : skill));
				attacker.sendMessage("old value: " + (int) value);
				attacker.sendMessage("new value: " + (int) (value * mod));
				attacker.sendMessage("--------------------");
			}
			else if(attacker.isSummon() && attacker.getPlayer() != null && attacker.getPlayer().isGM()) 
			{ 
				double mod = getElementMod(defense, attack); 
				attacker.getPlayer().sendMessage("--- element calc ---"); 
				attacker.getPlayer().sendMessage("mod: " + mod); 
				attacker.getPlayer().sendMessage("defense: " + (int) defense); 
				attacker.getPlayer().sendMessage("attack: " + (int) attack); 
				attacker.getPlayer().sendMessage("skill: " + (skill == null ? "null" : skill)); 
				attacker.getPlayer().sendMessage("old value: " + (int) value);
				attacker.getPlayer().sendMessage("new value: " + (int) (value * mod)); 
				attacker.getPlayer().sendMessage("--------------------"); 
			} 
			return value * getElementMod(defense, attack);
		}
		double defenseFirst60 = Math.min(60, defense);

		value *= getElementMod(defense, attack);

		if(defense <= defenseFirst60)
			return value;

		defense -= defenseFirst60;

		if(defense > 0 && Rnd.chance(2))
		{
			value /= 2.;
			attacker.sendPacket(new SystemMessage(SystemMessage.DAMAGE_IS_DECREASED_BECAUSE_C1_RESISTED_AGAINST_C2S_MAGIC).addName(defender).addName(attacker));
		}
		return value;
	}

    /**
     * Возвращает множитель для атаки из значений атакующего и защитного элемента. Только для простых атак и немагических скиллов.
     * <br /><br />
     * Для простых атак диапазон от 1.0 до 1.7
     * <br /><br />
     * Для скиллов от 1.0 до 2.0
     * <br /><br />
     *
     * @param defense значение защиты
     * @param attack  значение атаки
	 */
	private static double getElementMod(double defense, double attack)
	{
		double diff = attack - defense;
		if (diff < 0)
			return 1.0;
		if (diff >= 0 && diff < 45)
			return (1.0 + diff / 45 * 0.2);
		else if (diff >= 45 && diff < 150)
			return 1.2;
		else if (diff >= 150 && diff < 300)
			return 1.4;
		else if (diff >= 300 && diff < 450)
			return 1.7;
		else if (diff >= 450)
			return 1.9;
		return diff;
	}

    /**
     * Используется только для отображения в окне информации
     */
    public static int[] calcAttackElement(L2Character attacker) 
	{
        TreeMap<Double, Integer> sort_attibutes = new TreeMap<Double, Integer>();
        for (Element e : Element.values())
            if (e != Element.NONE)
                sort_attibutes.put(attacker.calcStat(e.getAttack(), 0), e.getId());

        Entry<Double, Integer> element = sort_attibutes.lastEntry();
        if (element.getKey().intValue() <= 0)
            return null;

        return new int[]{element.getValue(), element.getKey().intValue()};
    }

	private static void log(L2Character attacker, String txt)
	{
		if(attacker.isPlayer() && attacker.getPlayer().isGM())
			attacker.sendMessage(txt);
	}

	private static void debug_stats(L2Character target)
	{
		Calculator[] calculators = target.getCalculators();

		String log_str = "--- Debug for " + target.getName() + " ---\r\n";

		for(Calculator calculator : calculators)
		{
			if(calculator == null/* || calculator.getBase() == null*/)
				continue;
			Env env = new Env(target, target, null);
			env.value = calculator.getBase() == null ? 1 : calculator.getBase();
			log_str += "Stat: " + calculator._stat.getValue() + ", limit: " + calculator._stat._max + ", prevValue: " + calculator.getLast() + "\r\n";
			Func[] funcs = calculator.getFunctions();
			for(int i = 0; i < funcs.length; i++)
			{
				String order = Integer.toHexString(funcs[i]._order).toUpperCase();
				if(order.length() == 1)
					order = "0" + order;
				log_str += "\tFunc #" + i + "@ [0x" + order + "]" + funcs[i].getClass().getSimpleName() + "\t" + env.value;
				if(funcs[i].getCondition() == null || funcs[i].getCondition().test(env))
					funcs[i].calc(env);
				log_str += " -> " + env.value + (funcs[i]._funcOwner != null ? "; owner: " + funcs[i]._funcOwner.toString() : "; no owner") + "\r\n";
			}
		}

		Log.addMy(log_str, "debug_stats_player" , target.getName());
	}

	private static double[] bow_damage_mod = 
	{
		0.8385012919896625d,
		0.8385012919896625d,
		0.8385012919896625d,
		0.8385012919896625d,
		0.8385012919896625d,
		0.8385012919896625d,
		0.8385012919896625d,
		0.8385012919896625d,
		0.8385012919896625d,
		0.8385012919896625d,
		0.8385012919896625d,
		0.8488372093023256d,
		0.8591731266149871d,
		0.8682170542635659d,
		0.8772609819121447d,
		0.8863049095607235d,
		0.8940568475452196d,
		0.9018087855297158d,
		0.9095607235142119d,
		0.9160206718346253d,
		0.9211886304909561d,
		0.9263565891472868d,
		0.9315245478036176d,
		0.9354005167958656d,
		0.9392764857881137d,
		0.9418604651162791d,
		0.9444444444444444d,
		0.9470284237726098d,
		0.9483204134366925d,
		0.9483204134366925d,
		0.9496124031007752d,
		0.9496124031007752d,
		0.9509043927648578d,
		0.9521963824289405d,
		0.9534883720930232d,
		0.9547803617571059d,
		0.9547803617571059d,
		0.9560723514211886d,
		0.9573643410852713d,
		0.9586563307493540d,
		0.9599483204134367d,
		0.9612403100775194d,
		0.9625322997416021d,
		0.9638242894056848d,
		0.9651162790697674d,
		0.9664082687338501d,
		0.9677002583979328d,
		0.9702842377260982d,
		0.9715762273901809d,
		0.9728682170542636d,
		0.9741602067183463d,
		0.9767441860465116d,
		0.9780361757105943d,
		0.9806201550387597d,
		0.9819121447028424d,
		0.9832041343669251d,
		0.9857881136950904d,
		0.9883720930232558d,
		0.9896640826873385d,
		0.9922480620155038d,
		1d,
		1.0167958656330751d,
		1.0322997416020670d,
		1.0490956072351420d,
		1.0633074935400520d,
		1.0775193798449610d,
		1.0904392764857880d,
		1.1020671834625320d,
		1.1136950904392760d,
		1.1240310077519380d,
		1.1330749354005170d,
		1.1421188630490960d,
		1.1485788113695090d,
		1.1563307493540050d,
		1.1614987080103360d,
		1.1666666666666670d,
		1.1705426356589150d,
		1.1731266149870800d,
		1.1757105943152450d,
		1.1770025839793280d,
		1.1782945736434110d,
		1.1782945736434110d,
		1.1782945736434110d,
		1.1782945736434110d,
		1.1782945736434110d,
		1.1782945736434110d,
		1.1782945736434110d,
		1.1782945736434110d,
		1.1782945736434110d,
		1.1782945736434110d,
		1.1782945736434110d,
		1.1782945736434110d,
		1.1782945736434110d,
		1.1782945736434110d,
		1.1782945736434110d,
		1.1782945736434110d,
		1.1782945736434110d,
		1.1782945736434110d,
		1.1782945736434110d,
		1.1782945736434110d,
		1.1782945736434110d
	};
}