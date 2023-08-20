package l2open.gameserver.serverpackets;

import l2open.config.ConfigValue;
import l2open.database.DatabaseUtils;
import l2open.database.FiltredPreparedStatement;
import l2open.database.L2DatabaseFactory;
import l2open.database.ThreadConnection;
import l2open.gameserver.instancemanager.PlayerManager;
import l2open.gameserver.model.CharSelectInfoPackage;
import l2open.gameserver.model.base.Experience;
import l2open.gameserver.tables.CharTemplateTable;
import l2open.gameserver.templates.L2PlayerTemplate;
import l2open.util.AutoBan;
import l2open.util.GArray;

import java.sql.ResultSet;
import java.util.logging.Level;
import java.util.logging.Logger;

public class CharacterSelectionInfo extends L2GameServerPacket
{
	// d (SdSddddddddddffdQdddddddddddddddddddddddddddddddddddddddffdddchhd)
	private static Logger _log = Logger.getLogger(CharacterSelectionInfo.class.getName());

	private String _loginName;

	private int _sessionId;

	private CharSelectInfoPackage[] _characterPackages;

	public CharacterSelectionInfo(String loginName, int sessionId)
	{
		_sessionId = sessionId;
		_loginName = loginName;
		_characterPackages = loadCharacterSelectInfo(loginName);

		if(getClient() != null)
			getClient().setCharSelection(_characterPackages);
	}

	public CharSelectInfoPackage[] getCharInfo()
	{
		return _characterPackages;
	}

	@Override
	protected final void writeImpl()
	{
		int size = _characterPackages != null ? _characterPackages.length : 0;

		writeC(0x09);
		writeD(size);
		writeD(0x07); //Kamael, 0x07 ?
		writeC(0x00); //Kamael разрешает или запрещает создание игроков

		long lastAccess = 0L;
		int lastUsed = -1;
		for(int i = 0; i < size; i++)
			if(lastAccess < _characterPackages[i].getLastAccess())
			{
				lastAccess = _characterPackages[i].getLastAccess();
				lastUsed = i;
			}

		for(int i = 0; i < size; i++)
		{
			CharSelectInfoPackage charInfoPackage = _characterPackages[i];

			writeS(charInfoPackage.getName());
			writeD(charInfoPackage.getCharId()); // ?
			writeS(_loginName);
			writeD(_sessionId);
			writeD(charInfoPackage.getClanId());
			writeD(0x00); // ??

			writeD(charInfoPackage.getSex());
			writeD(charInfoPackage.getRace());
			writeD(charInfoPackage.getClassId());

			writeD(0x01); // active ??

			writeD(charInfoPackage.getX());
			writeD(charInfoPackage.getY());
			writeD(charInfoPackage.getZ());

			writeF(charInfoPackage.getCurrentHp());
			writeF(charInfoPackage.getCurrentMp());

			writeD(charInfoPackage.getSp());
			writeQ(charInfoPackage.getExp());
			writeF((float)(charInfoPackage.getExp() - Experience.LEVEL[charInfoPackage.getLevel()]) / (Experience.LEVEL[charInfoPackage.getLevel() + 1] - Experience.LEVEL[charInfoPackage.getLevel()])); // High Five exp % 
			writeD(charInfoPackage.getLevel());

			writeD(charInfoPackage.getKarma());
			writeD(charInfoPackage.getPk());
			writeD(charInfoPackage.getPvP());

			writeD(0x00);
			writeD(0x00);
			writeD(0x00);
			writeD(0x00);
			writeD(0x00);
			writeD(0x00);
			writeD(0x00);

			for(byte PAPERDOLL_ID : UserInfo.PAPERDOLL_ORDER)
				writeD(charInfoPackage.getPaperdollItemId(PAPERDOLL_ID));

			writeD(charInfoPackage.getHairStyle());
			writeD(charInfoPackage.getHairColor());
			writeD(charInfoPackage.getFace());

			writeF(charInfoPackage.getMaxHp()); // hp max
			writeF(charInfoPackage.getMaxMp()); // mp max

			writeD(charInfoPackage.getAccessLevel() > -100 ? charInfoPackage.getDeleteTimer() : -1);
			writeD(charInfoPackage.getClassId());
			writeD(i == lastUsed ? 1 : 0);

			writeC(Math.min(charInfoPackage.getEnchantEffect(), 127));
			//writeD(0x00); // TODO AugmentationId
			writeH(0);
			writeH(0);
			writeD(0x00); // TODO TransformationId

            writeD(0x00); // npdid - 16024    Tame Tiny Baby Kookaburra        A9E89C
            writeD(0x00); // level
            writeD(0x00); // ?
            writeD(0x00); // food? - 1200
            writeF(0x00); // max Hp
            writeF(0x00); // cur Hp
            writeD(charInfoPackage.getVitalityPoints() * 2);    // H5 Vitality 
		}
	}

	@Override
	protected boolean writeImplLindvior()
	{
		int size = _characterPackages != null ? _characterPackages.length : 0;

		writeC(0x09);
		writeD(size);
		writeD(0x07); //Kamael, 0x07 ?
		writeC(0x00); //Kamael разрешает или запрещает создание игроков

		writeC(0x01);
		writeC(0x02);// хз у гв всегда 2 поступало
		writeD(0x00);

		long lastAccess = 0L;
		int lastUsed = -1;
		for(int i = 0; i < size; i++)
			if(lastAccess < _characterPackages[i].getLastAccess())
			{
				lastAccess = _characterPackages[i].getLastAccess();
				lastUsed = i;
			}

		for(int i = 0; i < size; i++)
		{
			CharSelectInfoPackage charInfoPackage = _characterPackages[i];

			writeS(charInfoPackage.getName());
			writeD(charInfoPackage.getCharId()); // ?
			writeS(_loginName);
			writeD(_sessionId);
			writeD(charInfoPackage.getClanId());
			writeD(0x00); // ??

			writeD(charInfoPackage.getSex());
			writeD(charInfoPackage.getRace());
			writeD(charInfoPackage.getClassId());

			writeD(0x01); // active ??

			writeD(charInfoPackage.getX());
			writeD(charInfoPackage.getY());
			writeD(charInfoPackage.getZ());

			writeF(charInfoPackage.getCurrentHp());
			writeF(charInfoPackage.getCurrentMp());

			writeD(charInfoPackage.getSp());
			writeQ(charInfoPackage.getExp());
			writeF((float)(charInfoPackage.getExp() - Experience.LEVEL[charInfoPackage.getLevel()]) / (Experience.LEVEL[charInfoPackage.getLevel() + 1] - Experience.LEVEL[charInfoPackage.getLevel()])); // High Five exp % 
			writeD(charInfoPackage.getLevel());

			writeD(-charInfoPackage.getKarma());
			writeD(charInfoPackage.getPk());
			writeD(charInfoPackage.getPvP());

			writeD(0x00);
			writeD(0x00);
			writeD(0x00);
			writeD(0x00);
			writeD(0x00);
			writeD(0x00);
			writeD(0x00);

			for(byte PAPERDOLL_ID : UserInfo.PAPERDOLL_ORDER)
				writeD(charInfoPackage.getPaperdollItemId(PAPERDOLL_ID));

			// New Protocol 411
			writeD(0x00); // writeD(charInfoPackage.getVisualItemId(Inventory.PAPERDOLL_RHAND));
			writeD(0x00); // writeD(charInfoPackage.getVisualItemId(Inventory.PAPERDOLL_LHAND));
			writeD(0x00); // writeD(charInfoPackage.getVisualItemId(Inventory.PAPERDOLL_GLOVES));
			writeD(0x00); // writeD(charInfoPackage.getVisualItemId(Inventory.PAPERDOLL_CHEST));
			writeD(0x00); // writeD(charInfoPackage.getVisualItemId(Inventory.PAPERDOLL_LEGS));
			writeD(0x00); // writeD(charInfoPackage.getVisualItemId(Inventory.PAPERDOLL_FEET));
			writeD(0x00);
			writeD(0x00); // writeD(charInfoPackage.getVisualItemId(Inventory.PAPERDOLL_HAIR));
			writeD(0x00); // writeD(charInfoPackage.getVisualItemId(Inventory.PAPERDOLL_DHAIR));
			// End Protocol 411

			writeD(charInfoPackage.getHairStyle());
			writeD(charInfoPackage.getHairColor());
			writeD(charInfoPackage.getFace());

			writeF(charInfoPackage.getMaxHp()); // hp max
			writeF(charInfoPackage.getMaxMp()); // mp max

			writeD(charInfoPackage.getAccessLevel() > -100 ? charInfoPackage.getDeleteTimer() : -1);
			writeD(charInfoPackage.getClassId());
			writeD(i == lastUsed ? 1 : 0);

			writeC(Math.min(charInfoPackage.getEnchantEffect(), 127));
			writeD(0x00); // writeD(charInfoPackage.getPaperdollAugmentationId(Inventory.PAPERDOLL_RHAND));
			writeD(0x00); // TODO TransformationId

            writeD(0x00); // npdid - 16024    Tame Tiny Baby Kookaburra        A9E89C
            writeD(0x00); // level
            writeD(0x00); // ?
            writeD(0x00); // food? - 1200
            writeF(0x00); // max Hp
            writeF(0x00); // cur Hp
            writeD(charInfoPackage.getVitalityPoints() * 2);    // H5 Vitality 

			writeD(200); // writeD(charSelectionInfo.getPremiumAccess() > 0 ? 300 : 200); // Vitality percent
			writeD(5); // Vitaliti items count
			writeD(charInfoPackage.getAccessLevel() > -100 ? 0x01 : 0x00);
			writeC(0x00);
			writeC(0x00);
		}
		return true;
	}

	public static CharSelectInfoPackage[] loadCharacterSelectInfo(String loginName)
	{
		CharSelectInfoPackage charInfopackage;
		GArray<CharSelectInfoPackage> characterList = new GArray<CharSelectInfoPackage>();

		ThreadConnection con = null;
		FiltredPreparedStatement statement = null;
		ResultSet pl_rset = null;
		try
		{
			con = L2DatabaseFactory.getInstance().getConnection();
			statement = con.prepareStatement("SELECT * FROM characters AS c LEFT JOIN character_subclasses AS cs ON (c.obj_Id=cs.char_obj_id AND cs.isBase=1) WHERE account_name=? LIMIT 7");
			statement.setString(1, loginName);
			pl_rset = statement.executeQuery();

			while(pl_rset.next()) // fills the package
			{
				charInfopackage = restoreChar(pl_rset, pl_rset);
				if(charInfopackage != null)
					characterList.add(charInfopackage);
			}
		}
		catch(Exception e)
		{
			_log.log(Level.WARNING, "could not restore charinfo:", e);
		}
		finally
		{
			DatabaseUtils.closeDatabaseCSR(con, statement, pl_rset);
		}

		return characterList.toArray(new CharSelectInfoPackage[characterList.size()]);
	}

	private static CharSelectInfoPackage restoreChar(ResultSet chardata, ResultSet charclass)
	{
		CharSelectInfoPackage charInfopackage = null;
		try
		{
			int objectId = chardata.getInt("obj_Id");
			int classid = charclass.getInt("class_id");
			boolean female = chardata.getInt("sex") == 1;
			L2PlayerTemplate templ = CharTemplateTable.getInstance().getTemplate(classid, female);
			if(templ == null)
			{
				_log.log(Level.WARNING, "restoreChar fail | templ == null | objectId: " + objectId + " | classid: " + classid + " | female: " + female);
				return null;
			}
			String name = chardata.getString("char_name");
			charInfopackage = new CharSelectInfoPackage(objectId, name);
			charInfopackage.setLevel(charclass.getInt("level"));
			charInfopackage.setMaxHp(charclass.getInt("maxHp"));
			charInfopackage.setCurrentHp(charclass.getDouble("curHp"));
			charInfopackage.setMaxMp(charclass.getInt("maxMp"));
			charInfopackage.setCurrentMp(charclass.getDouble("curMp"));

			charInfopackage.setX(chardata.getInt("x"));
			charInfopackage.setY(chardata.getInt("y"));
			charInfopackage.setZ(chardata.getInt("z"));
			charInfopackage.setPk(chardata.getInt("pkkills"));
			charInfopackage.setPvP(chardata.getInt("pvpkills"));

			charInfopackage.setFace(chardata.getInt("face"));
			charInfopackage.setHairStyle(chardata.getInt("hairstyle"));
			charInfopackage.setHairColor(chardata.getInt("haircolor"));
			charInfopackage.setSex(female ? 1 : 0);

			charInfopackage.setExp(charclass.getLong("exp"));
			charInfopackage.setSp(charclass.getInt("sp"));
			charInfopackage.setClanId(chardata.getInt("clanid"));

			charInfopackage.setKarma(chardata.getInt("karma"));
			charInfopackage.setRace(templ.race.ordinal());
			charInfopackage.setClassId(classid);
			long deletetime = chardata.getLong("deletetime");
			int deletedays = 0;
			if(ConfigValue.DeleteCharAfterMin != -1)
			{
				if(deletetime > 0)
				{
					deletetime = (int) (System.currentTimeMillis() / 1000 - deletetime);
					deletedays = (int) (deletetime / 60);
					if(deletedays >= ConfigValue.DeleteCharAfterMin)
					{
						PlayerManager.deleteFromClan(objectId, charInfopackage.getClanId());
						PlayerManager.deleteCharByObjId(objectId);
						return null;
					}
					deletetime = ConfigValue.DeleteCharAfterMin * 60 - deletetime;
				}
				else
					deletetime = 0;
			}
			else if(ConfigValue.DeleteCharAfterDays > 0)
				if(deletetime > 0)
				{
					deletetime = (int) (System.currentTimeMillis() / 1000 - deletetime);
					deletedays = (int) (deletetime / 3600 / 24);
					if(deletedays >= ConfigValue.DeleteCharAfterDays)
					{
						PlayerManager.deleteFromClan(objectId, charInfopackage.getClanId());
						PlayerManager.deleteCharByObjId(objectId);
						return null;
					}
					deletetime = ConfigValue.DeleteCharAfterDays * 3600 * 24 - deletetime;
				}
				else
					deletetime = 0;
			charInfopackage.setDeleteTimer((int) deletetime);
			charInfopackage.setLastAccess(chardata.getLong("lastAccess") * 1000L);
			charInfopackage.setAccessLevel(chardata.getInt("accesslevel"));

			charInfopackage.setVitalityPoints(chardata.getInt("vitality"));
			if(charInfopackage.getAccessLevel() < 0 && !AutoBan.isBanned(objectId))
				charInfopackage.setAccessLevel(0);
		}
		catch(Exception e)
		{
			_log.log(Level.INFO, "", e);
		}

		return charInfopackage;
	}
}