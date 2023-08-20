package l2open.gameserver.model.instances;

import l2open.gameserver.templates.L2Henna;

/**
 * This class represents a Non-Player-Character in the world. it can be
 * a monster or a friendly character.
 * it also uses a template to fetch some static values.
 * the templates are hardcoded in the client, so we can rely on them.
 *
 * @version $Revision$ $Date$
 */
public class L2HennaInstance
{
	private L2Henna _template;
	private short _symbolId;
	private short _itemIdDye;
	private long _price, _amountDyeRequire;
	private byte _statINT;
	private byte _statSTR;
	private byte _statCON;
	private byte _statMEM;
	private byte _statDEX;
	private byte _statWIT;

	public L2HennaInstance(L2Henna template)
	{
		_template = template;
		_symbolId = _template.symbol_id;
		_itemIdDye = _template.dye;
		_amountDyeRequire = _template.amount;
		_price = _template.price;
		_statINT = _template.stat_INT;
		_statSTR = _template.stat_STR;
		_statCON = _template.stat_CON;
		_statMEM = _template.stat_MEM;
		_statDEX = _template.stat_DEX;
		_statWIT = _template.stat_WIT;
	}

	public String getName()
	{
		String res = "";
		if(_statINT > 0)
			res = res + "INT +" + _statINT;
		else if(_statSTR > 0)
			res = res + "STR +" + _statSTR;
		else if(_statCON > 0)
			res = res + "CON +" + _statCON;
		else if(_statMEM > 0)
			res = res + "MEN +" + _statMEM;
		else if(_statDEX > 0)
			res = res + "DEX +" + _statDEX;
		else if(_statWIT > 0)
			res = res + "WIT +" + _statWIT;

		if(_statINT < 0)
			res = res + ", INT " + _statINT;
		else if(_statSTR < 0)
			res = res + ", STR " + _statSTR;
		else if(_statCON < 0)
			res = res + ", CON " + _statCON;
		else if(_statMEM < 0)
			res = res + ", MEN " + _statMEM;
		else if(_statDEX < 0)
			res = res + ", DEX " + _statDEX;
		else if(_statWIT < 0)
			res = res + ", WIT " + _statWIT;

		return res;
	}

	public L2Henna getTemplate()
	{
		return _template;
	}

	public short getSymbolId()
	{
		return _symbolId;
	}

	public void setSymbolId(short SymbolId)
	{
		_symbolId = SymbolId;
	}

	public short getItemIdDye()
	{
		return _itemIdDye;
	}

	public void setItemIdDye(short ItemIdDye)
	{
		_itemIdDye = ItemIdDye;
	}

	public long getAmountDyeRequire()
	{
		return _amountDyeRequire;
	}

	public void setAmountDyeRequire(long AmountDyeRequire)
	{
		_amountDyeRequire = AmountDyeRequire;
	}

	public long getPrice()
	{
		return _price;
	}

	public void setPrice(long Price)
	{
		_price = Price;
	}

	public byte getStatINT()
	{
		return _statINT;
	}

	public void setStatINT(byte StatINT)
	{
		_statINT = StatINT;
	}

	public byte getStatSTR()
	{
		return _statSTR;
	}

	public void setStatSTR(byte StatSTR)
	{
		_statSTR = StatSTR;
	}

	public byte getStatCON()
	{
		return _statCON;
	}

	public void setStatCON(byte StatCON)
	{
		_statCON = StatCON;
	}

	public byte getStatMEM()
	{
		return _statMEM;
	}

	public void setStatMEM(byte StatMEM)
	{
		_statMEM = StatMEM;
	}

	public byte getStatDEX()
	{
		return _statDEX;
	}

	public void setStatDEX(byte StatDEX)
	{
		_statDEX = StatDEX;
	}

	public byte getStatWIT()
	{
		return _statWIT;
	}

	public void setStatWIT(byte StatWIT)
	{
		_statWIT = StatWIT;
	}
}