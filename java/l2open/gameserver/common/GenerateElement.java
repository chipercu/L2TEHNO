package l2open.gameserver.common;

public class GenerateElement
{
	public static String button(String value, String bypass, int width, int height, String back, String fore)
	{
		StringBuilder sb = new StringBuilder();
		sb.append("<button value=\"" + value + "\" action=\"bypass -h " + bypass + "\" width=" + width + " height=" + height + " back=\"" + back + "\" fore=\"" + fore + "\">");
		return sb.toString();
	}

	public static String buttonTD(String value, String bypass, int width, int height, String back, String fore)
	{
		StringBuilder sb = new StringBuilder();
		sb.append("<td>");
		sb.append(button(value, bypass, width, height, back, fore));
		sb.append("</td>");
		return sb.toString();
	}

	public static String buttonTR(String value, String bypass, int width, int height, String back, String fore)
	{
		StringBuilder sb = new StringBuilder();
		sb.append("<tr>");
		sb.append(buttonTD(value, bypass, width, height, back, fore));
		sb.append("</tr>");
		return sb.toString();
	}
}