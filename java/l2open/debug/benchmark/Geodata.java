package l2open.debug.benchmark;

import l2open.gameserver.geodata.GeoEngine;

public class Geodata
{
	//private static byte geo_map_x = 23, geo_map_y = 22;
	//private static int xblock_first = (geo_map_x - 15 << 11), yblock_first = (geo_map_y - 10 << 11); // 0 - 2047 blocks

	public static void main(String[] args) throws Exception
	{
		//debug: common.SetDummyUseMem(512 * 0x100000);
		common.init();
		//GeoEngine.LoadGeodataFile(geo_map_x, geo_map_y);

		GeoEngine.LoadGeodataFile((byte) 16, (byte) 19);

		//common.logMem();
		common.PromptEnterToContinue();
	}

	/*public static long bench_canSee(int iterations)
	{
		common.log.info("Benchmarking Geodata::canSee | loading geo map: " + geo_map_x + "_" + geo_map_y + "...");
		long result = System.currentTimeMillis();

		short[] curr = new short[GeoEngine.MAX_LAYERS + 1], next = new short[GeoEngine.MAX_LAYERS + 1];
		int k = 0;
		
		for(int x = xblock_first; x < xblock_first + 1920; x += 4)
		{
			System.out.print("Processing line " + (x - xblock_first ) + " / 2048\t\t\r");
			for(int y = yblock_first; y < yblock_first + 1920; y += 4)
			{
				GeoEngine.NGetLayers(x, y, curr);
				for(int i = 1; i <= curr[0]; i++)
				{
					GeoEngine.NGetLayers(x + 1, y, next);
					for(int j = 1; j <= next[0]; j++)
					{
						GeoEngine.canSee(x, y, ((curr[i] & 0x0fff0) >> 1), x + 128, y + 128, ((next[i] & 0x0fff0) >> 1));
						k++;
					}
				}
			}
			if(k >= iterations)
				break;
		}

		result = System.currentTimeMillis() - result;
		common.log.info("\t ... " + k + " canSee checks, ready in " + result + " ms");
		return result;
	}

	public static long bench_canSeeOld(int iterations)
	{

		common.log.info("Benchmarking Geodata::canSeeOld | map: " + geo_map_x + "_" + geo_map_y + "...");
		long result = System.currentTimeMillis();

		short[] curr = new short[GeoEngine.MAX_LAYERS + 1], next = new short[GeoEngine.MAX_LAYERS + 1];
		int k = 0;
		
		for(int x = xblock_first; x < xblock_first + 1920; x += 4)
		{
			System.out.print("Processing line " + (x - xblock_first ) + " / 2048\t\t\r");
			for(int y = yblock_first; y < yblock_first + 1920; y += 4)
			{
				GeoEngine.NGetLayers(x, y, curr);
				for(int i = 1; i <= curr[0]; i++)
				{
					GeoEngine.NGetLayers(x + 1, y, next);
					for(int j = 1; j <= next[0]; j++)
					{
						GeoEngine.canSeeOld(x, y, ((curr[i] & 0x0fff0) >> 1), x + 128, y + 64, ((next[i] & 0x0fff0) >> 1));
						k++;
					}
				}
			}
			if(k >= iterations)
				break;
		}

		result = System.currentTimeMillis() - result;
		common.log.info("\t ... " + k + " canSeeOld checks, ready in " + result + " ms");
		return result;
	}
	*/
}