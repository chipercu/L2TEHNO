package l2open.gameserver.geodata;

import java.util.*;

import l2open.config.ConfigValue;
import l2open.gameserver.geodata.*;
import l2open.gameserver.geodata.GeoEngine;
import l2open.gameserver.geodata.PathFindBuffers.GeoNode;
import l2open.gameserver.geodata.PathFindBuffers.PathFindBuffer;
import l2open.gameserver.model.*;
import l2open.gameserver.model.items.L2ItemInstance;
import l2open.gameserver.xml.ItemTemplates;
import l2open.util.Location;

public class PathFind
{
	private static final byte NSWE_NONE = 0, EAST = 1, WEST = 2, SOUTH = 4, NORTH = 8, NSWE_ALL = 15;

	private final int geoIndex;
	private final PathFindBuffer buff;
	private final short[] hNSWE = new short[2];
	private final Location startPoint, endPoint;
	private GeoNode startNode, endNode, currentNode;

	public static List<Location> findPath(int x, int y, int z, Location target, boolean isPlayable, int geoIndex)
	{
		return findPath(x, y, z, target.x, target.y, target.z, isPlayable, geoIndex);
	}

	public static final List<Location> findPath(int x, int y, int z, int destX, int destY, int destZ, boolean isPlayable, int geoIndex)
	{
		if(Math.abs(z - destZ) > ConfigValue.PathFindDiffZ)
			return null;

		z = GeoEngine.getHeight(x, y, z, geoIndex);
		destZ = GeoEngine.getHeight(destX, destY, destZ, geoIndex);

		Location startPoint = ConfigValue.PathFindBoost == 0 ? new Location(x, y, z) : GeoEngine.moveCheckWithCollision(x, y, z, destX, destY, true, geoIndex);
		Location endPoint = (ConfigValue.PathFindBoost != 2) || (Math.abs(destZ - z) > 200) ? new Location(destX, destY, destZ) : GeoEngine.moveCheckBackwardWithCollision(destX, destY, destZ, startPoint.x, startPoint.y, true, geoIndex);

		startPoint.world2geo();
		endPoint.world2geo();

		int xdiff = Math.abs(endPoint.x - startPoint.x);
		int ydiff = Math.abs(endPoint.y - startPoint.y);

		if(xdiff == 0 && ydiff == 0)
		{
			if(Math.abs(endPoint.z - startPoint.z) < ConfigValue.PathFindMaxZDiff)
			{
				List<Location> path = new ArrayList<Location>(2);
				path.add(new Location(x, y, z));
				path.add(new Location(destX, destY, destZ));
				return path;
			}
			return null;
		}

		List<Location> path = null;

		int mapSize = ConfigValue.PathFindMapMul * Math.max(xdiff, ydiff);
		PathFindBuffer buff;
		if((buff = PathFindBuffers.alloc(mapSize)) != null)
		{
			buff.offsetX = (startPoint.x - buff.mapSize / 2);
			buff.offsetY = (startPoint.y - buff.mapSize / 2);
			/*if(debugItems != null)
			{
				for(L2ItemInstance item : debugItems)
					item.deleteMe();
				debugItems.clear();
			}*/
			buff.totalUses += 1L;
			if(isPlayable)
				buff.playableUses += 1L;
			PathFind n = new PathFind(startPoint, endPoint, buff, geoIndex);
			path = n.findPath();
			if(ConfigValue.GeodataDebug && isPlayable)
				debugPath(null, n, buff, path); // TODO дебаг не для лайва!
			buff.free();

			PathFindBuffers.recycle(buff);
		}

		if(path == null || path.isEmpty())
			return null;

		List<Location> targetRecorder = new ArrayList<Location>(path.size() + 2);

		targetRecorder.add(new Location(x, y, z));

		for(Location p : path)
			targetRecorder.add(p.geo2world());

		targetRecorder.add(new Location(destX, destY, destZ));

		if(ConfigValue.PathClean > 0)
		{
			if(isPlayable && ConfigValue.PathClean == 2)
				pathCleanNeat(targetRecorder, geoIndex);
			else
                pathClean(targetRecorder, geoIndex);
		}
		return targetRecorder;
	}

	private static void pathClean(List<Location> path, int geoIndex)
	{
		int size = path.size();
		if(size > 2)
		{
			for(int i = 2; i < size; i++)
			{
				Location p3 = path.get(i);
				Location p2 = path.get(i - 1);
				Location p1 = path.get(i - 2);
				if(p1.equals(p2) || p3.equals(p2) || IsPointInLine(p1, p2, p3))
				{
					path.remove(i - 1);
					size--;
					i = Math.max(1, i - 1);
				}
			}
		}
		int current = 0;

		while(current < path.size() - 2)
		{
			Location one = path.get(current);
			int sub = current + 2;
			while(sub < path.size())
			{
				Location two = path.get(sub);
				if(one.equals(two) || GeoEngine.canMoveWithCollision(one.x, one.y, one.z, two.x, two.y, two.z, geoIndex))
					while(current + 1 < sub)
					{
						path.remove(current + 1);
						sub--;
					}
				sub++;
			}
			current++;
		}
	}

	private static void pathCleanNeat(List<Location> path, int geoIndex)
	{
        Location one;
        Location two;
        int size = path.size();
        int center = size / 2;
        if (size > 2)
		{
            for (int i = 2; i < size; ++i)
			{
                if (i == center) continue;
                Location p3 = path.get(i);
                Location p2 = path.get(i - 1);
                Location p1 = path.get(i - 2);
                if (!p1.equals(p2) && !p3.equals(p2) && !PathFind.IsPointInLine(p1, p2, p3)) continue;
                path.remove(i - 1);
                --size;
                i = Math.max(1, i - 1);
                if (i >= center) continue;
                --center;
            }
        }
        for (int current = 0; current < path.size() - 2; ++current)
		{
            one = path.get(current);
            for (int sub = current + 2; sub < path.size() && current != center - 2; ++sub)
			{
                two = path.get(sub);
                if (!one.equals(two) && !GeoEngine.canMoveToCoord(one.x, one.y, one.z, two.x, two.y, two.z, geoIndex)) continue;
                while (current + 1 < sub && current != center - 2) {
                    path.remove(current + 1);
                    --sub;
                    --center;
                }
            }
        }
        if (center > 1 && center < path.size() && ((one = path.get(center - 2)).equals((two = path.get(center))) || GeoEngine.canMoveToCoord(one.x, one.y, one.z, two.x, two.y, two.z, geoIndex))) {
            path.remove(center - 1);
        }
    }

	private static boolean IsPointInLine(Location p1, Location p2, Location p3)
	{
		if((p1.x == p3.x && p3.x == p2.x) || (p1.y == p3.y && p3.y == p2.y))
			return true;

		if((p1.x - p2.x) * (p1.y - p2.y) == (p2.x - p3.x) * (p2.y - p3.y))
			return true;
		return false;
	}

	public PathFind(Location _startPoint, Location _endPoint, PathFindBuffer _buff, int _geoIndex)
	{
		geoIndex = _geoIndex;
		startPoint = _startPoint;
		endPoint = _endPoint;
		buff = _buff;
	}

	private List<Location> findPath()
	{
		startNode = buff.nodes[startPoint.x - buff.offsetX][startPoint.y - buff.offsetY].set(startPoint.x, startPoint.y, (short) startPoint.z);

		GeoEngine.NgetHeightAndNSWE(startPoint.x, startPoint.y, (short) startPoint.z, hNSWE, geoIndex);
		startNode.z = hNSWE[0];
		startNode.nswe = hNSWE[1];
		startNode.costFromStart = 0f;
		startNode.state = GeoNode.OPENED;
		startNode.parent = null;

		endNode = buff.nodes[endPoint.x - buff.offsetX][endPoint.y - buff.offsetY].set(endPoint.x, endPoint.y, (short) endPoint.z);

		startNode.costToEnd = pathCostEstimate(startNode);
		startNode.totalCost = startNode.costFromStart + startNode.costToEnd;

		buff.open.add(startNode);

		long nanos = System.nanoTime();
		long searhTime = 0;
		int itr = 0;

		List<Location> path = null;
		while(((searhTime = System.nanoTime() - nanos) < ConfigValue.PathFindMaxTime) && ((currentNode = buff.open.poll()) != null))
		{
			itr++;
			if(currentNode.x == endPoint.x && currentNode.y == endPoint.y && Math.abs(currentNode.z - endPoint.z) < ConfigValue.MaxZDiff)
			{
				path = tracePath(currentNode);
				break;
			}
			handleNode(currentNode);
			currentNode.state = GeoNode.CLOSED;
		}

		buff.totalTime += searhTime;
		buff.totalItr += itr;
		if(path != null)
			buff.successUses += 1L;
		else if(searhTime > ConfigValue.PathFindMaxTime)
			buff.overtimeUses += 1L;
		return path;
	}

	private List<Location> tracePath(GeoNode f)
	{
		LinkedList<Location> locations = new LinkedList<Location>();
		do
		{
			locations.addFirst(f.getLoc());
			f = f.parent;
		}
		while(f.parent != null);
		return locations;
	}

	private void handleNode(GeoNode node)
	{
		int clX = node.x;
		int clY = node.y;
		short clZ = node.z;

		getHeightAndNSWE(clX, clY, clZ);
		short NSWE = hNSWE[1];

		if(ConfigValue.PathFindDiagonal)
		{
			// Юго-восток
			if((NSWE & SOUTH) == SOUTH && (NSWE & EAST) == EAST)
			{
				getHeightAndNSWE(clX + 1, clY, clZ);
				if((hNSWE[1] & SOUTH) == SOUTH)
				{
					getHeightAndNSWE(clX, clY + 1, clZ);
					if((hNSWE[1] & EAST) == EAST)
						handleNeighbour(clX + 1, clY + 1, node, true);
				}
			}

			// Юго-запад
			if((NSWE & SOUTH) == SOUTH && (NSWE & WEST) == WEST)
			{
				getHeightAndNSWE(clX - 1, clY, clZ);
				if((hNSWE[1] & SOUTH) == SOUTH)
				{
					getHeightAndNSWE(clX, clY + 1, clZ);
					if((hNSWE[1] & WEST) == WEST)
						handleNeighbour(clX - 1, clY + 1, node, true);
				}
			}

			// Северо-восток
			if((NSWE & NORTH) == NORTH && (NSWE & EAST) == EAST)
			{
				getHeightAndNSWE(clX + 1, clY, clZ);
				if((hNSWE[1] & NORTH) == NORTH)
				{
					getHeightAndNSWE(clX, clY - 1, clZ);
					if((hNSWE[1] & EAST) == EAST)
						handleNeighbour(clX + 1, clY - 1, node, true);
				}
			}

			// Северо-запад
			if((NSWE & NORTH) == NORTH && (NSWE & WEST) == WEST)
			{
				getHeightAndNSWE(clX - 1, clY, clZ);
				if((hNSWE[1] & NORTH) == NORTH)
				{
					getHeightAndNSWE(clX, clY - 1, clZ);
					if((hNSWE[1] & WEST) == WEST)
						handleNeighbour(clX - 1, clY - 1, node, true);
				}
			}
		}

		// Восток
		if((NSWE & EAST) == EAST)
			handleNeighbour(clX + 1, clY, node, false);

		// Запад
		if((NSWE & WEST) == WEST)
			handleNeighbour(clX - 1, clY, node, false);

		// Юг
		if((NSWE & SOUTH) == SOUTH)
			handleNeighbour(clX, clY + 1, node, false);

		// Север
		if((NSWE & NORTH) == NORTH)
			handleNeighbour(clX, clY - 1, node, false);
	}

	private float pathCostEstimate(GeoNode n)
	{
		int diffx = endNode.x - n.x;
		int diffy = endNode.y - n.y;
		int diffz = endNode.z - n.z;

		return (float) Math.sqrt(diffx * diffx + diffy * diffy + diffz * diffz / 256);
	}

	private float traverseCost(GeoNode from, GeoNode n, boolean d)
	{
		if(n.nswe != NSWE_ALL || Math.abs(n.z - from.z) > 16)
			return 3.2f;
		else
		{
			getHeightAndNSWE(n.x + 1, n.y, n.z);
			if(hNSWE[1] != NSWE_ALL || Math.abs(n.z - hNSWE[0]) > 16)
				return d ? 1.98f : 1.4f;

			getHeightAndNSWE(n.x - 1, n.y, n.z);
			if(hNSWE[1] != NSWE_ALL || Math.abs(n.z - hNSWE[0]) > 16)
				return d ? 1.98f : 1.4f;

			getHeightAndNSWE(n.x, n.y + 1, n.z);
			if(hNSWE[1] != NSWE_ALL || Math.abs(n.z - hNSWE[0]) > 16)
				return d ? 1.98f : 1.4f;

			getHeightAndNSWE(n.x, n.y - 1, n.z);
			if(hNSWE[1] != NSWE_ALL || Math.abs(n.z - hNSWE[0]) > 16)
				return d ? 1.98f : 1.4f;
		}

		return d ? 1.414f : 1f;
	}

	private void handleNeighbour(int x, int y, GeoNode from, boolean d)
	{
		int nX = x - buff.offsetX, nY = y - buff.offsetY;
		if(nX >= buff.mapSize || nX < 0 || nY >= buff.mapSize || nY < 0)
			return;

		GeoNode n = buff.nodes[nX][nY];
		float newCost;

		if(!n.isSet())
		{
			n = n.set(x, y, from.z);
			GeoEngine.NgetHeightAndNSWE(x, y, from.z, hNSWE, geoIndex);
			n.z = hNSWE[0];
			n.nswe = hNSWE[1];
		}

		int height = Math.abs(n.z - from.z);
		if(height > ConfigValue.PathFindMaxZDiff || n.nswe == NSWE_NONE)
			return;

		newCost = from.costFromStart + traverseCost(from, n, d);
		if((n.state == GeoNode.OPENED || n.state == GeoNode.CLOSED) && n.costFromStart <= newCost)
			return;

		if(n.state == GeoNode.NONE)
			n.costToEnd = pathCostEstimate(n);

		n.parent = from;
		n.costFromStart = newCost;
		n.totalCost = n.costFromStart + n.costToEnd;

		if(n.state == GeoNode.OPENED)
			return;

		n.state = GeoNode.OPENED;
		buff.open.add(n);
	}

	private void getHeightAndNSWE(int x, int y, short z)
	{
		int nX = x - buff.offsetX, nY = y - buff.offsetY;
		if(nX >= buff.mapSize || nX < 0 || nY >= buff.mapSize || nY < 0)
		{
			hNSWE[1] = NSWE_NONE; // Затычка
			return;
		}

		GeoNode n = buff.nodes[nX][nY];
		if(!n.isSet())
		{
			n = n.set(x, y, z);
			GeoEngine.NgetHeightAndNSWE(x, y, z, hNSWE, geoIndex);
			n.z = hNSWE[0];
			n.nswe = hNSWE[1];
		}
		else
		{
			hNSWE[0] = n.z;
			hNSWE[1] = n.nswe;
		}
	}

	// ---------------------------
	private static List<L2ItemInstance> debugItems;

	private static void addDebugItem(int item_id, int item_count, Location loc)
	{
		L2ItemInstance item = ItemTemplates.getInstance().createItem(item_id);
		if(item_count > 1)
			item.setCount(item_count);
		debugItems.add(item);
		item.dropMe(null, loc);
	}

	protected static void debugPath(L2Player player, PathFind pf, PathFindBuffer buff, List<Location> _path)
	{
		//if(!player.isGM())
		//	return;

		double dist = pf.startPoint.clone().geo2world().distance(pf.endPoint.clone().geo2world());
		System.out.println(String.format("Path for %s, distance: %.0f, MapSize: %s, size: %s", /*player.getName()*/"GM", dist, /*pf.info.MapSize*/0, (_path == null ? "null" : _path.size())));

		if(debugItems == null)
			debugItems = new ArrayList<L2ItemInstance>();
		else
		{
			for(L2ItemInstance item : debugItems)
				item.deleteMe();
			debugItems.clear();
		}

		for(int i = 0; i < buff.nodes.length; i++)
			for(int j = 0; j < buff.nodes[i].length; j++)
			{
				if(/*GeoNode.isNull(buff.nodes[i][j]) || */buff.nodes[i][j] == pf.startNode || _path == null && buff.nodes[i][j].state == GeoNode.CLOSED)
					continue;

				addDebugItem(buff.nodes[i][j].state == GeoNode.CLOSED ? 65 : 57, /*(int) buff.nodes[i][j].moveCost*/1, new Location(buff.nodes[i][j].x, buff.nodes[i][j].y, buff.nodes[i][j].z).clone().geo2world());
			}

		addDebugItem(3433, 1, pf.startPoint.clone().geo2world());
		addDebugItem(3436, 1, pf.endPoint.clone().geo2world());
	}

}