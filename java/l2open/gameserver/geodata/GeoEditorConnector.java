package l2open.gameserver.geodata;

import java.util.ArrayList;
import java.util.List;

import l2open.gameserver.model.L2World;
import l2open.gameserver.model.L2Player;

/**
 * 
 *
 * @author  Luno
 */
public class GeoEditorConnector
{	
	private static GeoEditorConnector _instance = new GeoEditorConnector();
	public static GeoEditorConnector getInstance()
	{
		return _instance;
	}
	
	private GeoEditorThread _geThread;
	
	private List<L2Player> _gmList = new ArrayList<L2Player>();
	
	int RegionX;
	int RegionY;
	
	private GeoEditorConnector()
	{
		
	}
	public void connect(L2Player gm, int ticks)
	{
		System.out.println("GeoEditor: connect: "+gm.toString());
		if(_geThread != null)
		{
			gm.sendMessage("GeoEditor: GameServer is already connected to GeoEditor.");
			if(!_gmList.contains(gm))
				join(gm);
			return;
		}
		RegionX = getRegionX(gm);
		RegionY = getRegionY(gm);
		
		_gmList.add(gm);
		
		_geThread = new GeoEditorThread(this);
		_geThread.setTicks(ticks);
		_geThread.start();
	}
	public void leave(L2Player gm)
	{
		_gmList.remove(gm);
		gm.sendMessage("GeoEditor: You have been removed from the list");
		if(_gmList.isEmpty())
		{
			_geThread.stopRecording();
			_geThread = null;
			gm.sendMessage("GeoEditor: Connection closed.");
		}
	}
	public void join(L2Player gm)
	{
		if(_geThread == null)
		{
			gm.sendMessage("GeoEditor: GameServer is not connected to GeoEditor.");
			gm.sendMessage("GeoEditor: Use //geoeditor connect <ticks>  first.");
			return;
		}
		if(_gmList.contains(gm))
		{
			gm.sendMessage("GeoEditor: You are already on the list.");
			return;
		}
		if(getRegionX(gm) != RegionX || getRegionY(gm) != RegionY)
		{
			gm.sendMessage("GeoEditor: Only people from region: ["+RegionX+","+RegionY+"] can join.");
			return;
		}
		_gmList.add(gm);
		gm.sendMessage("GeoEditor: You have been added to the list.");
	}
	public List<L2Player> getGMs()
	{
		return _gmList;
	}
	public void sendMessage(String msg)
	{
		for(L2Player gm: _gmList)
			gm.sendMessage(msg);
	}
	public void stoppedConnection()
	{
		_geThread = null;
		_gmList.clear();
	}
	private int getRegionX(L2Player g)
	{
		int gx = (g.getX() - L2World.MAP_MIN_X) >> 4;
		gx >>= 11;
		return gx + 16;
	}
	private int getRegionY(L2Player g)
	{
		int gy = (g.getY() - L2World.MAP_MIN_Y) >> 4;
		gy >>= 11;
		return gy + 10;
	}
}