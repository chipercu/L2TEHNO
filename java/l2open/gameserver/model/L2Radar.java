package l2open.gameserver.model;

import l2open.gameserver.serverpackets.RadarControl;
import l2open.util.Location;

import java.util.Vector;

public final class L2Radar
{
	private L2Player player;
	private Vector<RadarMarker> markers;

	public L2Radar(L2Player player_)
	{
		player = player_;
		markers = new Vector<RadarMarker>();
	}

	// Add a marker to player's radar
	public void addMarker(int x, int y, int z)
	{
		RadarMarker newMarker = new RadarMarker(x, y, z);
		markers.add(newMarker);
		player.sendPacket(new RadarControl(2, 2, newMarker));
		player.sendPacket(new RadarControl(0, 1, newMarker));
	}

	// Remove a marker from player's radar
	public void removeMarker(int x, int y, int z)
	{
		RadarMarker newMarker = new RadarMarker(x, y, z);
		markers.remove(newMarker);
		player.sendPacket(new RadarControl(1, 1, newMarker));
	}

	public void removeAllMarkers()
	{
		for(RadarMarker tempMarker : markers)
			player.sendPacket(new RadarControl(2, 2, tempMarker));
		markers.removeAllElements();
	}

	public void loadMarkers()
	{
		player.sendPacket(new RadarControl(2, 2, player.getX(), player.getY(), player.getZ()));
		for(RadarMarker tempMarker : markers)
			player.sendPacket(new RadarControl(0, 1, tempMarker));
	}

	@SuppressWarnings("serial")
	public class RadarMarker extends Location
	{
		// Simple class to model radar points.
		public int type;

		public RadarMarker(int type_, int x_, int y_, int z_)
		{
			super(x_, y_, z_);
			type = type_;
		}

		public RadarMarker(int x_, int y_, int z_)
		{
			super(x_, y_, z_);
			type = 1;
		}

		@Override
		public boolean equals(Object obj)
		{
			try
			{
				RadarMarker temp = (RadarMarker) obj;
				if(temp.x == x && temp.y == y && temp.z == z && temp.type == type)
					return true;
				return false;
			}
			catch(Exception e)
			{
				return false;
			}
		}
	}
}