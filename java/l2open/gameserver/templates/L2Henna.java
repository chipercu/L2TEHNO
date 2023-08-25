package l2open.gameserver.templates;

public class L2Henna
{
	public final short symbol_id;
	public final String symbol_name;
	public final short dye;
	public final long price, amount;
	public final byte stat_INT;
	public final byte stat_STR;
	public final byte stat_CON;
	public final byte stat_MEM;
	public final byte stat_DEX;
	public final byte stat_WIT;

	public L2Henna(StatsSet set)
	{
		symbol_id = set.getShort("symbol_id");
		symbol_name = ""; //set.getString("symbol_name");
		dye = set.getShort("dye");
		price = set.getLong("price");
		amount = set.getLong("amount");
		stat_INT = set.getByte("stat_INT");
		stat_STR = set.getByte("stat_STR");
		stat_CON = set.getByte("stat_CON");
		stat_MEM = set.getByte("stat_MEM");
		stat_DEX = set.getByte("stat_DEX");
		stat_WIT = set.getByte("stat_WIT");
	}

	public short getSymbolId()
	{
		return symbol_id;
	}

	public short getDyeId()
	{
		return dye;
	}

	public long getPrice()
	{
		return price;
	}

	public long getAmountDyeRequire()
	{
		return amount;
	}

	public byte getStatINT()
	{
		return stat_INT;
	}

	public byte getStatSTR()
	{
		return stat_STR;
	}

	public byte getStatCON()
	{
		return stat_CON;
	}

	public byte getStatMEM()
	{
		return stat_MEM;
	}

	public byte getStatDEX()
	{
		return stat_DEX;
	}

	public byte getStatWIT()
	{
		return stat_WIT;
	}
}