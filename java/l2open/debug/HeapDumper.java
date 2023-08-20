package l2open.debug;

import com.sun.management.HotSpotDiagnosticMXBean;

import javax.management.MBeanServer;
import java.io.File;
import java.lang.management.ManagementFactory;
import java.text.SimpleDateFormat;
import java.util.Date;

public class HeapDumper
{
	/**
	 * Делает дамп памяти, который кстати умеет открывать YJP
	 * @param dumpsDir - директория с дампами
	 * @param live - определяет дампить все или только "живые" объекты
	 */
	public static String dumpHeap(String dumpsDir, boolean live)
	{
		synchronized (HeapDumper.class)
		{
			if(hotspotMBean == null)
				hotspotMBean = getHotspotMBean();
		}
		try
		{
			String fullPath = dumpsDir + "/";
			new File(fullPath).mkdirs();
			fullPath += formatter.format(new Date());
			if(live)
				fullPath += ".live";
			fullPath += ".hprof";
			hotspotMBean.dumpHeap(fullPath, live);
			return fullPath;
		}
		catch(RuntimeException re)
		{
			throw re;
		}
		catch(Exception exp)
		{
			throw new RuntimeException(exp);
		}
	}

	private static SimpleDateFormat formatter = new SimpleDateFormat("yyyy.MM.dd_HH-mm-ss.SSS");

	private static HotSpotDiagnosticMXBean hotspotMBean;

	private static HotSpotDiagnosticMXBean getHotspotMBean()
	{
		try
		{
			MBeanServer server = ManagementFactory.getPlatformMBeanServer();
			HotSpotDiagnosticMXBean bean = ManagementFactory.newPlatformMXBeanProxy(server, "com.sun.management:type=HotSpotDiagnostic", HotSpotDiagnosticMXBean.class);
			return bean;
		}
		catch(RuntimeException re)
		{
			throw re;
		}
		catch(Exception exp)
		{
			throw new RuntimeException(exp);
		}
	}
}