package l2open.debug.benchmark;

import l2open.config.ConfigValue;
import l2open.gameserver.geodata.GeoEngine;

public class GeoCheckOptimized
{
	public static void main(String[] args) throws Exception
	{
		common.init();
		ConfigValue.GeoFilesPattern = "(\\d{2}_\\d{2})\\.l2j";
		ConfigValue.AllowDoors = false;
		ConfigValue.CompactGeoData = true;
		GeoEngine.load();
		common.PromptEnterToContinue();
	}
}