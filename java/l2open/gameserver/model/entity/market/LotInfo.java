package l2open.gameserver.model.entity.market;

import l2open.gameserver.model.items.L2ItemInstance;
import l2open.gameserver.model.L2Player;

/**
 * @author Diagod
 */
public class LotInfo
{
	public int objectId; // ID продавца.
	public int itemObjectId; // ID обьекта итема.
	public int itemId; // ID итема.
	public long itemCount = 1; // количество продаваемых итемов.
	public String itemName; // имя итема.
	public int cashValue; // тип валюты за которую продают.
	public long cashCount; // стоимость.
	public long data; // дата подачи итема на продажу.
	public long dataAdd; // дополнительное время на продажу итема.
	public int status; // состояние, 0 - продается, 1 - продано.
	public int type; // тип продажи, 0 - рынок, налог плотит продавец, 1 - рынок, налог плотит покупатель, 2 - Аукцион, налог плотит продавец, 3 - Аукцион, налог плотит покупатель.
	public long lastBetValue = -1; // последняя ставка на аукционе.
	public int lastBetObjectId = -1; // ID чара который сделал последнюю ставку на аукционе.
	public String buyrName;
	public int buyrId;

	public LotInfo()
	{}

	public LotInfo(int objectId, int itemObjectId, int itemId, long itemCount, String itemName, int cashValue, long cashCount, long data, long dataAdd, int status, int type, long lastBetValue, int lastBetObjectId)
	{
		this.objectId = objectId; // ID продавца.
		this.itemObjectId = itemObjectId; // ID обьекта итема.
		this.itemId = itemId; // ID итема.
		this.itemCount = itemCount; // количество продаваемых итемов.
		this.itemName = itemName; // имя итема.
		this.cashValue = cashValue; // тип валюты за которую продают.
		this.cashCount = cashCount; // стоимость.
		this.data = data; // дата подачи итема на продажу.
		this.dataAdd = dataAdd; // дополнительное время на продажу итема.
		this.status = status; // состояние, 0 - продается, 1 - продано.
		this.type = type; // тип продажи, 0 - рынок, налог плотит продавец, 1 - рынок, налог плотит покупатель, 2 - Аукцион, налог плотит продавец, 3 - Аукцион, налог плотит покупатель.
		this.lastBetValue = lastBetValue; // последняя ставка на аукционе.
		this.lastBetObjectId = lastBetObjectId; // ID чара который сделал последнюю ставку на аукционе.
	}
}
