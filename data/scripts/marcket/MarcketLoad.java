package marcket;

import l2open.extensions.scripts.Functions;
import l2open.extensions.scripts.ScriptFile;
import java.util.*;

public class MarcketLoad extends Functions implements ScriptFile
{
	public static List<ItemInfo> _sellList = new ArrayList<ItemInfo>();

	public void onLoad()
	{
		_log.info("MarcketLoad Loaded...");
		load();
	}

	public void onReload()
	{}

	public void onShutdown()
	{}

	public void load()
	{
		// TODO: Подгрузку итемов из БД...	
		/*_sellList.add(new ItemInfo(2130, 10, 100, 19545, 0, -2, -2));
		_sellList.add(new ItemInfo(2131, 10, 100, 19545, 0, -2, -2));
		_sellList.add(new ItemInfo(2132, 10, 100, 19545, 0, -2, -2));
		_sellList.add(new ItemInfo(2133, 10, 100, 19545, 0, -2, -2));
		_sellList.add(new ItemInfo(2134, 10, 100, 19545, 0, -2, -2));
		_sellList.add(new ItemInfo(2135, 10, 100, 19545, 0, -2, -2));
		_sellList.add(new ItemInfo(2136, 10, 100, 19545, 0, -2, -2));
		_sellList.add(new ItemInfo(2137, 10, 100, 19545, 0, -2, -2));
		_sellList.add(new ItemInfo(2138, 10, 100, 19545, 0, -2, -2));
		_sellList.add(new ItemInfo(2139, 10, 100, 19545, 0, -2, -2));*/
	}

	public class ItemInfo
	{
		public ItemInfo(int _id, long _countMin, long _countMax, long _countAdena, int _enchant, byte _element, int _elementValue)
		{
			id=_id;
			countMin=_countMin;
			countMax=_countMax;
			countAdena=_countAdena;
			enchant=_enchant;
			element=_element;
			elementValue=_elementValue;
		}

		public int id = 0;
		public long countMin = 0L;
		public long countMax = 0L;
		public long countAdena = 0L;
		public int enchant = 0;
		public byte element = -2;
		public int elementValue = 0;
	}
}