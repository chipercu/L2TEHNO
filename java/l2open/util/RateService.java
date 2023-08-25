package l2open.util;

import l2open.config.ConfigValue;
import l2open.gameserver.model.L2Player;
import l2open.gameserver.tables.comp.DTDEntityResolver;

import java.io.File;
import java.io.FileInputStream;
import java.util.Iterator;
import java.util.logging.Logger;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

public class RateService
{
	private static final Logger _log = Logger.getLogger(RateService.class.getName());

	private static float[] RateXp;
	private static float[] RateSp;

	private static int[] RateXpVitality;
	private static int[] RateSpVitality;

	private static double[] RateDropAdena;

	private static double[] RateDropSpoil;
	private static float[] RateCountDropSpoil;

	private static double[] RateDropItems;

	public static void loadDefaultValue()
	{
		if(ConfigValue.LevelRateServiceEnable)
		{
			RateXp = new float[ConfigValue.AltMaxLevel+1];
			RateSp = new float[ConfigValue.AltMaxLevel+1];
			RateXpVitality = new int[ConfigValue.AltMaxLevel+1];
			RateSpVitality = new int[ConfigValue.AltMaxLevel+1];
			RateDropAdena = new double[ConfigValue.AltMaxLevel+1];
			RateDropSpoil = new double[ConfigValue.AltMaxLevel+1];
			RateCountDropSpoil = new float[ConfigValue.AltMaxLevel+1];
			RateDropItems = new double[ConfigValue.AltMaxLevel+1];

			SAXReader reader = new SAXReader();

			File dtd = new File(ConfigValue.DatapackRoot + "/data/xml/", "rate_param.dtd");
			if(!dtd.exists())
			{
				_log.warning("DTD file: " + dtd.getName() + " not exists.");
				return;
			}

			File f = new File(ConfigValue.DatapackRoot + "/data/xml/", "rate_param.xml");
			if(!f.exists())
			{
				_log.warning("XML file: " + f.getName() + " not exists.");
				return;
			}

			try
			{
				Document doc = reader.read(new FileInputStream(f));
				readData(doc.getRootElement());
			}
			catch(Exception e)
			{
				_log.warning("Exception: " + e + " in file: " + f.getName());
			}

			reader.setValidation(true);
			reader.setEntityResolver(new DTDEntityResolver(dtd));
			
			_log.info("RateService: Loaded successful.");
		}
	}

	private static void readData(Element element) throws Exception
	{
		for(Iterator<Element> iterator = element.elementIterator("level"); iterator.hasNext();)
		{
			Element level = iterator.next();
			final int min_level = Integer.parseInt(level.attributeValue("min"));
			final int max_level = Integer.parseInt(level.attributeValue("max"));

			Element rate = level.element("rate");
			final float exp = Float.parseFloat(rate.attributeValue("exp", "1.0"));
			final float sp = Float.parseFloat(rate.attributeValue("sp", "1.0"));
			final float vit_exp = Float.parseFloat(rate.attributeValue("vit_exp", "1.0"));
			final float vit_sp = Float.parseFloat(rate.attributeValue("vit_sp", "1.0"));
			final float adena = Float.parseFloat(rate.attributeValue("adena", "1.0"));
			final float drop = Float.parseFloat(rate.attributeValue("drop", "1.0"));
			final float spoil = Float.parseFloat(rate.attributeValue("spoil", "1.0"));

			for(int i=min_level;i<=max_level;i++)
			{
				RateXp[i] = exp;
				RateSp[i] = sp;
				RateXpVitality[i] = (int)vit_exp;
				RateSpVitality[i] = (int)vit_sp;
				RateDropAdena[i] = adena;
				RateDropItems[i] = drop;
				RateDropSpoil[i] = spoil;
			}
		}
	}

	public static float getRateXp(L2Player player)
	{
		if(player != null && ConfigValue.LevelRateServiceEnable)
			return RateXp[player.getLevel()];
		return ConfigValue.RateXp;
	}

	public static float getRateSp(L2Player player)
	{
		if(player != null && ConfigValue.LevelRateServiceEnable)
			return RateSp[player.getLevel()];
		return ConfigValue.RateSp;
	}


	public static float getRateXpVitality(L2Player player)
	{
		if(player != null && ConfigValue.LevelRateServiceEnable)
			return RateXpVitality[player.getLevel()];
		return ConfigValue.RateXpVitality;
	}

	public static float getRateSpVitality(L2Player player)
	{
		if(player != null && ConfigValue.LevelRateServiceEnable)
			return RateSpVitality[player.getLevel()];
		return ConfigValue.RateSpVitality;
	}

	public static double getRateDropAdena(L2Player player)
	{
		if(player != null && ConfigValue.LevelRateServiceEnable)
			return RateDropAdena[player.getLevel()];
		return ConfigValue.RateDropAdena;
	}

	public static double getRateDropSpoil(L2Player player)
	{
		if(player != null && ConfigValue.LevelRateServiceEnable)
			return RateDropSpoil[player.getLevel()];
		return ConfigValue.RateDropSpoil;
	}

	public static float getRateCountDropSpoil(L2Player player)
	{
		if(player != null && ConfigValue.LevelRateServiceEnable)
			return RateCountDropSpoil[player.getLevel()];
		return ConfigValue.RateCountDropSpoil;
	}

	public static double getRateDropItems(L2Player player)
	{
		if(player != null && ConfigValue.LevelRateServiceEnable)
			return RateDropItems[player.getLevel()];
		return ConfigValue.RateDropItems;
	}
}
