package l2open.extensions.multilang;

import java.io.File;
import java.io.FilenameFilter;

/**
 * @Author: Death
 * @Date: 25/9/2007
 * @Time: 21:02:26
 */
public class PropertiesFilter implements FilenameFilter
{
	@Override
	public boolean accept(File dir, String name)
	{
		return name.endsWith(".properties");
	}
}
