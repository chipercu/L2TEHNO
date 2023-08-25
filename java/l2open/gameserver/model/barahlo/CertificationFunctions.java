package l2open.gameserver.model.barahlo;

import java.util.Arrays;
import java.util.Collection;

import l2open.config.ConfigValue;
import l2open.extensions.multilang.CustomMessage;
import l2open.extensions.scripts.Functions;
import l2open.gameserver.cache.Msg;
import l2open.gameserver.model.L2Player;
import l2open.gameserver.model.L2Skill;
import l2open.gameserver.model.L2SkillLearn;
import l2open.gameserver.model.L2SubClass;
import l2open.gameserver.model.base.ClassId;
import l2open.gameserver.model.base.ClassType2;
import l2open.gameserver.model.instances.L2NpcInstance;
import l2open.gameserver.serverpackets.SkillList;
import l2open.gameserver.tables.SkillTreeTable;
import l2open.gameserver.tables.player.PlayerData;

public class CertificationFunctions
{
	public static final String PATH = "data/html-ru/villagemaster/certification/";

	public static void showCertificationList(L2NpcInstance npc, L2Player player)
	{
		if(!checkConditions(ConfigValue.Certification65Level, npc, player, true))
			return;

		Functions.show(PATH + "certificatelist.htm", player, npc);
	}

	public static void getCertification65(L2NpcInstance npc, L2Player player)
	{
		if(!checkConditions(ConfigValue.Certification65Level, npc, player, false))
			return;

		int certification_count = player.getVarInt("certification_count", 0);
		L2SubClass clzz = player.getActiveClass();
		if(clzz.isCertificationGet(L2SubClass.CERTIFICATION_65) || certification_count >= ConfigValue.CertificationCount)
		{
			Functions.show(PATH + "certificate-already.htm", player, npc);
			return;
		}

		player.setVar("certification_count", String.valueOf(certification_count+1));
		Functions.addItem(player, 10280, 1);
		clzz.addCertification(L2SubClass.CERTIFICATION_65);
		PlayerData.getInstance().store(player, true);
	}

	public static void getCertification70(L2NpcInstance npc, L2Player player)
	{
		if(!checkConditions(ConfigValue.Certification70Level, npc, player, false))
			return;

		L2SubClass clzz = player.getActiveClass();

		// если не взят преведущий сертификат
		if(!clzz.isCertificationGet(L2SubClass.CERTIFICATION_65))
		{
			Functions.show(PATH + "certificate-fail.htm", player, npc);
			return;
		}
		else if(clzz.isCertificationGet(L2SubClass.CERTIFICATION_70))
		{
			Functions.show(PATH + "certificate-already.htm", player, npc);
			return;
		}

		Functions.addItem(player, 10280, 1);
		clzz.addCertification(L2SubClass.CERTIFICATION_70);
		PlayerData.getInstance().store(player, true);
	}

	public static void getCertification75List(L2NpcInstance npc, L2Player player)
	{
		if(!checkConditions(ConfigValue.Certification75Level, npc, player, false))
			return;

		L2SubClass clzz = player.getActiveClass();

		// если не взят преведущий сертификат
		if(!clzz.isCertificationGet(L2SubClass.CERTIFICATION_65) || !clzz.isCertificationGet(L2SubClass.CERTIFICATION_70))
		{
			Functions.show(PATH + "certificate-fail.htm", player, npc);
			return;
		}
		else if(clzz.isCertificationGet(L2SubClass.CERTIFICATION_75))
		{
			Functions.show(PATH + "certificate-already.htm", player, npc);
			return;
		}

		Functions.show(PATH + "certificate-choose.htm", player, npc, "%className%", player.getActiveClass().toString());
	}

	public static void getCertification75(L2NpcInstance npc, L2Player player, boolean classCertifi)
	{
		if(!checkConditions(ConfigValue.Certification75Level, npc, player, false))
			return;

		L2SubClass clzz = player.getActiveClass();

		// если не взят преведущий сертификат
		if(!clzz.isCertificationGet(L2SubClass.CERTIFICATION_65) || !clzz.isCertificationGet(L2SubClass.CERTIFICATION_70))
		{
			Functions.show(PATH + "certificate-fail.htm", player, npc);
			return;
		}
		else if(clzz.isCertificationGet(L2SubClass.CERTIFICATION_75))
		{
			Functions.show(PATH + "certificate-already.htm", player, npc);
			return;
		}
		else if(classCertifi)
		{
			ClassId cl = ClassId.VALUES[clzz.getClassId()];
			if(cl.getType2() == null)
				return;

			Functions.addItem(player, cl.getType2().getCertificateId(), 1);
		}
		else
			Functions.addItem(player, 10612, 1); // master ability

		clzz.addCertification(L2SubClass.CERTIFICATION_75);
		PlayerData.getInstance().store(player, true);
	}

	public static void getCertification80(L2NpcInstance npc, L2Player player)
	{
		if(!checkConditions(ConfigValue.Certification80Level, npc, player, false))
			return;

		L2SubClass clzz = player.getActiveClass();

		// если не взят(ы) преведущий сертификат(ы)
		if(!clzz.isCertificationGet(L2SubClass.CERTIFICATION_65) || !clzz.isCertificationGet(L2SubClass.CERTIFICATION_70) || !clzz.isCertificationGet(L2SubClass.CERTIFICATION_75))
		{
			Functions.show(PATH + "certificate-fail.htm", player, npc);
			return;
		}
		else if(clzz.isCertificationGet(L2SubClass.CERTIFICATION_80))
		{
			Functions.show(PATH + "certificate-already.htm", player, npc);
			return;
		}

		ClassId cl = ClassId.VALUES[clzz.getClassId()];
		if(cl.getType2() == null)
			return;

		Functions.addItem(player, cl.getType2().getTransformationId(), 1);
		clzz.addCertification(L2SubClass.CERTIFICATION_80);
		PlayerData.getInstance().store(player, true);
	}

	public static void cancelCertification(L2NpcInstance npc, L2Player player)
	{
		if(player.getInventory().getAdena() < ConfigValue.CertificationRemovePrice)
		{
			player.sendPacket(Msg.YOU_DO_NOT_HAVE_ENOUGH_ADENA);
			return;
		}
		else if(player.isSubClassActive())
		{
			player.sendMessage("Нельзя удалить сертификацию с активированным подклассом.");
			return;
		}

		player.getInventory().destroyItemByItemId(57, ConfigValue.CertificationRemovePrice, true);

		for(ClassType2 classType2 : ClassType2.VALUES)
		{
			if(player.getInventory().getCountOf(classType2.getCertificateId()) > 0)
				player.getInventory().destroyItemByItemId(classType2.getCertificateId(), player.getInventory().getCountOf(classType2.getCertificateId()), true);
			if(player.getInventory().getCountOf(classType2.getTransformationId()) > 0)
				player.getInventory().destroyItemByItemId(classType2.getTransformationId(), player.getInventory().getCountOf(classType2.getTransformationId()), true);
		}

		L2SkillLearn[] skillLearnList = SkillTreeTable.getAllCertificationSkills();
		for(L2SkillLearn learn : skillLearnList)
		{
			L2Skill skill = player.getKnownSkill(learn.getId());
			if(skill != null)
				player.removeSkill(skill, true, true);
		}

		for(L2SubClass subClass : player.getSubClasses().values())
		{
			if(!subClass.isBase()) // isSubClassActive()
				subClass.setCertification(0);
		}

		player.unsetVar("certification_count");
		player.sendPacket(new SkillList(player));
		Functions.show(new CustomMessage("scripts.services.SubclassSkills.SkillsDeleted", player), player);
	}

	public static boolean checkConditions(int level, L2NpcInstance npc, L2Player player, boolean first)
	{
		if(player.getLevel() < level)
		{
			Functions.show(PATH + "certificate-nolevel.htm", player, npc, "%level%", level);
			return false;
		}
		else if(!player.isSubClassActive())
		{
			Functions.show(PATH + "certificate-nosub.htm", player, npc);
			return false;
		}
		else if(first)
			return true;

		// Эта проверка полный бред, за 1 рас можно взять все книги на оффе.
		/*for (ClassType2 type : ClassType2.VALUES)
		{
			if (player.getInventory().getCountOf(type.getCertificateId()) > 0 || player.getInventory().getCountOf(type.getTransformationId()) > 0)
			{
				Functions.show(PATH + "certificate-already.htm", player, npc);
				return false;
			}
		}*/

		return true;
	}
}
