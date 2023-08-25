package l2open.util;

import java.io.*;

public abstract class L2AbstractClientParser
{
	public final String[] names;

	public L2AbstractClientParser(File f, String charsetName) throws IOException
	{
		LineNumberReader lnr = new LineNumberReader(new InputStreamReader(new FileInputStream(f), charsetName));
		names = parseHeader(lnr.readLine().trim());

		String s;
		int idx = 0;
		while((s = lnr.readLine()) != null)
		{
			if(!onParseLine(idx, s.trim().split("\t")))
				break;
			idx++;
		}
	}

	public L2AbstractClientParser(File f) throws IOException
	{
		this(f, "UTF-8");
	}

	public L2AbstractClientParser(String s, String charsetName) throws IOException
	{
		this(new File(s), charsetName);
	}

	public L2AbstractClientParser(String s) throws IOException
	{
		this(new File(s));
	}

	private String[] parseHeader(String s)
	{
		return s.split("\t");
	}

	protected abstract boolean onParseLine(int index, String[] s);
}