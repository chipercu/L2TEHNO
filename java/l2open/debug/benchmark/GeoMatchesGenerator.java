package l2open.debug.benchmark;

import l2open.config.ConfigSystem;
import l2open.config.ConfigValue;
import l2open.gameserver.geodata.GeoEngine;

public class GeoMatchesGenerator
{
	public static void main(String[] args) throws Exception
	{
		common.init();
		ConfigSystem.load();
		ConfigValue.GeoFilesPattern = "(\\d{2}_\\d{2})\\.l2j";
		ConfigValue.AllowDoors = false;
		ConfigValue.CompactGeoData = false;
		GeoEngine.load();
		common.log.info("Goedata loaded");
		common.GC();
		GeoEngine.genBlockMatches(0); //TODO
		if(common.YesNoPrompt("Do you want to delete temproary geo checksums files?"))
			GeoEngine.deleteChecksumFiles();
		common.PromptEnterToContinue();
	}
}