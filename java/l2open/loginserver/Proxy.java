package l2open.loginserver;

public class Proxy
{
	public int id;
	public int max_player;
	public int region=-1;
	public int bit_mask=0;

	public String ip;
	public String ip_in;

	public Proxy(int i1, int i2, int i3, String s, String s2, int i4)
	{
		id = i1;
		max_player = i2;
		region = i3;
		ip = s;
		ip_in = s2;
		bit_mask = i4;
	}
}