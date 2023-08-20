package l2open.util;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class L2ClientParser extends L2AbstractClientParser implements Iterable<Map<String, String>>
{
	private ArrayList<String[]> elements;

	public L2ClientParser(File f) throws IOException
	{
		super(f);
	}

	public L2ClientParser(File f, String charsetName) throws IOException
	{
		super(f, charsetName);
	}

	public L2ClientParser(String s) throws IOException
	{
		super(s);
	}

	public L2ClientParser(String s, String charsetName) throws IOException
	{
		super(s, charsetName);
	}

	@Override
	protected boolean onParseLine(int index, String[] s)
	{
		if(elements == null)
			elements = new ArrayList<String[]>();
		elements.add(s);
		return true;
	}

	public String[] get(int index)
	{
		return elements == null ? null : elements.get(index);
	}

	public int size()
	{
		return elements == null ? 0 : elements.size();
	}

	public Map<String, String> getAssoc(int index)
	{
		String[] line = get(index);
		if(line == null)
			return null;
		HashMap<String, String> ret = new HashMap<String, String>();
		for(int i = 0; i < names.length; i++)
			ret.put(names[i], line[i]);
		return ret;
	}

	public String getField(int index, String name)
	{
		String[] line = get(index);
		if(line == null)
			return null;
		for(int i = 0; i < names.length; i++)
			if(names[i].equalsIgnoreCase(name))
				return line[i];
		return null;
	}

	@Override
	public Iterator<Map<String, String>> iterator()
	{
		return new Itr();
	}

	private class Itr implements Iterator<Map<String, String>>
	{
		int cursor = 0;

		@Override
		public boolean hasNext()
		{
			return cursor < size();
		}

		@Override
		public Map<String, String> next()
		{
			return getAssoc(cursor++);
		}

		@Override
		public void remove()
		{
			throw new IllegalStateException();
		}
	}
}