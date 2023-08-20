package l2open.gameserver.geodata;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.nio.ByteOrder;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import java.util.logging.Logger;

import l2open.config.*;
import l2open.gameserver.geodata.GeoOptimizer.BlockLink;
import l2open.gameserver.model.L2Object;
import l2open.gameserver.model.L2Territory;
import l2open.gameserver.model.*;
import l2open.gameserver.model.L2Zone.ZoneType;
import l2open.gameserver.model.L2World;
import l2open.gameserver.model.instances.L2DoorInstance;
import l2open.gameserver.tables.DoorTable;
import l2open.util.Location;
import l2open.util.GArray;
import l2open.util.Log;
import l2open.util.Rnd;
import l2open.util.Util;
import l2open.util.geometry.Shape;

import l2open.gameserver.model.items.L2ItemInstance;
import l2open.gameserver.xml.ItemTemplates;
import l2open.gameserver.serverpackets.ExShowTrace;
/**
 * @Author:		Diamond
 * @CoAuthor:	DRiN
 * @Date:			01/03/2009
 */
public class GeoEngine
{
	private static Logger _log = Logger.getLogger(GeoEngine.class.getName());

	private static boolean GeodataEnabled = ConfigValue.GeodataEnabled;

	public static final byte EAST = 1, WEST = 2, SOUTH = 4, NORTH = 8, NSWE_ALL = 15, NSWE_NONE = 0;
	
	public static final byte BLOCKTYPE_FLAT = 0;
	public static final byte BLOCKTYPE_COMPLEX = 1;
	public static final byte BLOCKTYPE_MULTILEVEL = 2;
	
	public static final int BLOCKS_IN_MAP = 256 * 256;
	
	public static int MAX_LAYERS = 1; // меньше 1 быть не должно, что бы создавались временные массивы как минимум short[2]

	/**
	 * Даный массив содержит эталонную геодату. <BR>
	 * Первые 2 [][] (byte[*][*][][]) являются x и y региона.<BR>
	 */
	private static final MappedByteBuffer[][] rawgeo = new MappedByteBuffer[L2World.WORLD_SIZE_X][L2World.WORLD_SIZE_Y];
	
	/**
	 * Даный массив содержит всю геодату на сервере. <BR>
	 * Первые 2 [][] (byte[*][*][][]) являются x и y региона.<BR>
	 * Третий [] (byte[][][*][]) является блоком геодаты.<BR>
	 * Четвертый [] (byte[][][][*]) является контейнером для всех блоков в регионе.<BR>
	 */
	private static final byte[][][][][] geodata = new byte[L2World.WORLD_SIZE_X][L2World.WORLD_SIZE_Y][1][][];

	public static short getType(int x, int y, int geoIndex)
	{
		return NgetType(x - L2World.MAP_MIN_X >> 4, y - L2World.MAP_MIN_Y >> 4, geoIndex);
	}

	public static int getHeight(Location loc, int geoIndex)
	{
		return getHeight(loc.x, loc.y, loc.z, geoIndex);
	}

	public static int getHeight(int x, int y, int z, int geoIndex)
	{
		return NgetHeight(x - L2World.MAP_MIN_X >> 4, y - L2World.MAP_MIN_Y >> 4, z, geoIndex);
	}

	public static boolean canMoveToCoord(int x, int y, int z, int tx, int ty, int tz, int geoIndex)
	{
		return canMove(x, y, z, tx, ty, tz, false, geoIndex) == 0;
	}

	public static byte getNSWE(int x, int y, int z, int geoIndex)
	{
		return NgetNSWE(x - L2World.MAP_MIN_X >> 4, y - L2World.MAP_MIN_Y >> 4, z, geoIndex);
	}

	public static Location moveCheck(int x, int y, int z, int tx, int ty, int geoIndex)
	{
		return MoveCheck(x, y, z, tx, ty, false, false, false, geoIndex);
	}

	public static Location moveCheck(int x, int y, int z, int tx, int ty, boolean returnPrev, int geoIndex)
	{
		return MoveCheck(x, y, z, tx, ty, false, false, returnPrev, geoIndex);
	}

	public static Location moveCheckWithCollision(int x, int y, int z, int tx, int ty, int geoIndex)
	{
		return MoveCheck(x, y, z, tx, ty, true, false, false, geoIndex);
	}

	public static Location moveCheckWithCollision(int x, int y, int z, int tx, int ty, boolean returnPrev, int geoIndex)
	{
		return MoveCheck(x, y, z, tx, ty, true, false, returnPrev, geoIndex);
	}

	public static Location moveCheckBackwardWithCollision(int x, int y, int z, int tx, int ty, int geoIndex)
	{
		return MoveCheck(x, y, z, tx, ty, true, true, false, geoIndex);
	}

	public static Location moveCheckBackwardWithCollision(int x, int y, int z, int tx, int ty, boolean returnPrev, int geoIndex)
	{
		return MoveCheck(x, y, z, tx, ty, true, true, returnPrev, geoIndex);
	}

	public static Location moveInWaterCheck(int x, int y, int z, int tx, int ty, int tz, int waterZ, int geoIndex, boolean isPlayer)
	{
		return MoveInWaterCheck(x - L2World.MAP_MIN_X >> 4, y - L2World.MAP_MIN_Y >> 4, z, tx - L2World.MAP_MIN_X >> 4, ty - L2World.MAP_MIN_Y >> 4, tz, waterZ, geoIndex, isPlayer);
	}
 
	public static Location moveCheckForAI(Location loc1, Location loc2, int geoIndex)
	{
		return MoveCheckForAI(loc1.x - L2World.MAP_MIN_X >> 4, loc1.y - L2World.MAP_MIN_Y >> 4, loc1.z, loc2.x - L2World.MAP_MIN_X >> 4, loc2.y - L2World.MAP_MIN_Y >> 4, geoIndex);
	}

	public static Location moveCheckInAir(int x, int y, int z, int tx, int ty, int tz, double collision, int geoIndex)
	{
		int gx = x - L2World.MAP_MIN_X >> 4;
		int gy = y - L2World.MAP_MIN_Y >> 4;
		int tgx = tx - L2World.MAP_MIN_X >> 4;
		int tgy = ty - L2World.MAP_MIN_Y >> 4;

		int nz = NgetHeight(tgx, tgy, tz, geoIndex);

		// Не даем опуститься ниже, чем пол + 32
		if(tz <= nz + 32)
			tz = nz + 32;

		Location result = canSee(gx, gy, z, tgx, tgy, tz, true, geoIndex);
		if(result.equals(gx, gy, z))
			return null;

		return result.geo2world();
	}

	public static Location getFollowLocation(int x, int y, int z, int tx, int ty, int tz, int geoIndex)
	{
		int gx = x - L2World.MAP_MIN_X >> 4;
		int gy = y - L2World.MAP_MIN_Y >> 4;
		int tgx = tx - L2World.MAP_MIN_X >> 4;
		int tgy = ty - L2World.MAP_MIN_Y >> 4;

		int nz = NgetHeight(tgx, tgy, tz, geoIndex);

		// Не даем опуститься ниже, чем пол + 32
		if(tz <= nz + 32)
			tz = nz + 32;

		Location result = canSee(gx, gy, z, tgx, tgy, tz, false, geoIndex);
		return result.geo2world();
	}

	// Проверка на двухсторонюю видимость...
	public static boolean canSeeTarget(L2Object actor, L2Object target, boolean air)
	{
		if(target == null)
			return false;
		// Костыль конечно, но решает кучу проблем с дверьми
		if(target.isDoor() || actor.equals(target))
			return true;

		int mx = actor.getX() - L2World.MAP_MIN_X >> 4;
		int my = actor.getY() - L2World.MAP_MIN_Y >> 4;
		int mz = actor.getZ() + (int) actor.getColHeight() + 24;//int mz = actor.getZ() + 32;

		int tmx = target.getX() - L2World.MAP_MIN_X >> 4;
		int tmy = target.getY() - L2World.MAP_MIN_Y >> 4;
		int tmz = target.getZ() + (int) target.getColHeight() + 24;//int tmz = target.getZ() + 32;

		int geoIndex = actor.getReflection().getGeoIndex();

		return canSee(mx, my, mz, tmx, tmy, tmz, air, geoIndex).equals(tmx, tmy, tmz) && canSee(tmx, tmy, tmz, mx, my, mz, air, geoIndex).equals(mx, my, mz);
	}

	// то же самое что и canSeeTarget
	public static boolean canSeeCoord(int x, int y, int z, int tx, int ty, int tz, boolean air, int geoIndex)
	{
		int mx = x - L2World.MAP_MIN_X >> 4;
		int my = y - L2World.MAP_MIN_Y >> 4;
		int tmx = tx - L2World.MAP_MIN_X >> 4;
		int tmy = ty - L2World.MAP_MIN_Y >> 4;
		return canSee(mx, my, z, tmx, tmy, tz, air, geoIndex).equals(tmx, tmy, tz) && canSee(tmx, tmy, tz, mx, my, z, air, geoIndex).equals(mx, my, z);
	}

	// для атаки, должна быть только одностороняя видимость...
	public static boolean canAttacTarget(L2Object actor, L2Object target, boolean air)
	{
		if(target == null)
			return false;
		// Костыль конечно, но решает кучу проблем с дверьми
		if(target.isDoor() || actor.equals(target))
			return true;
		int mx = actor.getX() - L2World.MAP_MIN_X >> 4;
		int my = actor.getY() - L2World.MAP_MIN_Y >> 4;
		int mz = actor.getZ() + (int) actor.getColHeight() + 64;// было 24 //int mz = actor.getZ() + 32;

		int tmx = target.getX() - L2World.MAP_MIN_X >> 4;
		int tmy = target.getY() - L2World.MAP_MIN_Y >> 4;
		int tmz = target.getZ() + (int) target.getColHeight() + 64;// было 24 //int tmz = target.getZ() + 32;

		int geoIndex = actor.getReflection().getGeoIndex();

		//return canSee(mx, my, mz, tmx, tmy, tmz, air, geoIndex).equals(tmx, tmy, tmz) || canSee(tmx, tmy, tmz, mx, my, mz, air, geoIndex).equals(mx, my, mz);
		// на время
		if(ConfigValue.GeoEngineA1)
			return canSee(mx, my, mz, tmx, tmy, tmz, air, geoIndex, false, false).equals(tmx, tmy, tmz) || canSee(tmx, tmy, tmz, mx, my, mz, air, geoIndex, false, false).equals(mx, my, mz);
		return canSee(mx, my, mz, tmx, tmy, tmz, air, geoIndex, false, false).equals(tmx, tmy, tmz) && canSee(tmx, tmy, tmz, mx, my, mz, air, geoIndex, false, false).equals(mx, my, mz);
		//return canSee(mx, my, mz, tmx, tmy, tmz, air, geoIndex, true, actor.isPlayer()).equals(tmx, tmy, tmz) || canSee(tmx, tmy, tmz, mx, my, mz, air, geoIndex, true, actor.isPlayer()).equals(mx, my, mz);
	}

	public static boolean canMoveWithCollision(int x, int y, int z, int tx, int ty, int tz, int geoIndex)
	{
		return canMove(x, y, z, tx, ty, tz, true, geoIndex) == 0;
	}

	/**
	 * @param NSWE
	 * @param x
	 * @param y
	 * @param tx
	 * @param ty
	 * 
	 * @return True if NSWE dont block given direction
	 */
	public static boolean checkNSWE(byte NSWE, int x, int y, int tx, int ty)
	{
		if(NSWE == NSWE_ALL)
			return true;
		if(NSWE == NSWE_NONE)
			return false;
		if(tx > x)
		{
			if((NSWE & EAST) == 0)
				return false;
		}
		else if(tx < x)
			if((NSWE & WEST) == 0)
				return false;
		if(ty > y)
		{
			if((NSWE & SOUTH) == 0)
				return false;
		}
		else if(ty < y)
			if((NSWE & NORTH) == 0)
				return false;
		return true;
	}

	public static String geoXYZ2Str(int _x, int _y, int _z)
	{
		return "(" + String.valueOf((_x << 4) + L2World.MAP_MIN_X + 8) + " " + String.valueOf((_y << 4) + L2World.MAP_MIN_Y + 8) + " " + _z + ")";
	}

	public static String NSWE2Str(byte nswe)
	{
		String result = "";
		if((nswe & NORTH) == NORTH)
			result += "N";
		if((nswe & SOUTH) == SOUTH)
			result += "S";
		if((nswe & WEST) == WEST)
			result += "W";
		if((nswe & EAST) == EAST)
			result += "E";
		return result.isEmpty() ? "X" : result;
	}

	private static boolean NLOS_WATER(int x, int y, int z, int next_x, int next_y, int next_z, int geoIndex, boolean isPlayer)
	{
		short[] layers1 = new short[MAX_LAYERS + 1];
		short[] layers2 = new short[MAX_LAYERS + 1];
		NGetLayers(x, y, layers1, geoIndex);
		NGetLayers(next_x, next_y, layers2, geoIndex);

		if(layers1[0] == 0 || layers2[0] == 0)
		{
			if(isPlayer && ConfigValue.GeodataDebugWaterMoveNLos)
				_log.warning("GeoEngine: NLOS_WATER-> if(layers1[0] == 0 || layers2[0] == 0)");
			return true;
		}

		short h;

		// Находим ближайший к целевой клетке слой
		short z2 = Short.MIN_VALUE;
		for(int i = 1; i <= layers2[0]; i++)
		{
			h = (short) ((short) (layers2[i] & 0x0fff0) >> 1);
			if(Math.abs(next_z - z2) > Math.abs(next_z - h))
				z2 = h;
		}

		// Луч проходит над преградой
		if(next_z + 32 >= z2)
		{
			if(isPlayer && ConfigValue.GeodataDebugWaterMoveNLos)
				_log.warning("GeoEngine: NLOS_WATER-> next_z="+next_z+" z2="+z2);
			return true;
		}

		// Либо перед нами стена, либо над нами потолок. Ищем слой пониже, для уточнения
		short z3 = Short.MIN_VALUE;
		for(int i = 1; i <= layers2[0]; i++)
		{
			h = (short) ((short) (layers2[i] & 0x0fff0) >> 1);
			if(h < z2 + ConfigValue.MinLayerHeight && Math.abs(next_z - z3) > Math.abs(next_z - h))
				z3 = h;
		}

		// Ниже нет слоев, значит это стена
		if(z3 == Short.MIN_VALUE)
		{
			if(isPlayer && ConfigValue.GeodataDebugWaterMoveNLos)
				_log.warning("GeoEngine: NLOS_WATER-> if(z3 == Short.MIN_VALUE)");
			return false;
		}

		// Собираем данные о предыдущей клетке, игнорируя верхние слои
		short z1 = Short.MIN_VALUE;
		byte NSWE1 = NSWE_ALL;
		for(int i = 1; i <= layers1[0]; i++)
		{
			h = (short) ((short) (layers1[i] & 0x0fff0) >> 1);
			if(h < z + ConfigValue.MinLayerHeight && Math.abs(z - z1) > Math.abs(z - h))
			{
				z1 = h;
				NSWE1 = (byte) (layers1[i] & 0x0F);
			}
		}

		if(isPlayer && ConfigValue.GeodataDebugWaterMoveNLos)
			_log.warning("GeoEngine: NLOS_WATER-> checkNSWE["+checkNSWE(NSWE1, x, y, next_x, next_y)+"]");
		// Если есть NSWE, то считаем за стену
		return checkNSWE(NSWE1, x, y, next_x, next_y);
	}

	// TODO: стремный фикс1
	private static int FindNearestLowerLayer(short[] layers, int z, boolean regionEdge, boolean isPlayer, boolean isWater)
	{
		short h, nearest_layer_h = Short.MIN_VALUE;
		int nearest_layer = isWater ? z : Integer.MIN_VALUE;
		int zCheck = regionEdge ? z + ConfigValue.MaxZDiffEdge : z;
		for(int i = 1; i <= layers[0]; i++)
		{
			h = (short)((short)(layers[i] & 0x0FFF0) >> 1);
			/*if(isPlayer && isWater)
			{
				_log.info("z="+z+" size["+i+"]="+layers[0]+" h="+h+" zCheck="+zCheck+" nearest_layer_h="+nearest_layer_h+" nearest_layer="+nearest_layer);
			}*/
			if(h < zCheck && nearest_layer_h < h)
			{
				nearest_layer_h = h;
				nearest_layer = layers[i];
			}
		}
		return nearest_layer;
	}

	public static short CheckNoOneLayerInRangeAndFindNearestLowerLayer(short[] layers, int z0, int z1)
	{
		return CheckNoOneLayerInRangeAndFindNearestLowerLayer(layers, z0, z1, false);
	}
	public static short CheckNoOneLayerInRangeAndFindNearestLowerLayer(short[] layers, int z0, int z1, boolean log)
	{
		int z_min, z_max;
		if(z0 > z1)
		{
			z_min = z1;
			z_max = z0;
		}
		else
		{
			z_min = z0;
			z_max = z1;
		}
		short h, nearest_layer = Short.MIN_VALUE, nearest_layer_h = Short.MIN_VALUE;
		for(int i = 1; i <= layers[0]; i++)
		{
			h = (short) ((short) (layers[i] & 0x0fff0) >> 1);
			
			if(log)
			{
				_log.info("GeoEngine: CheckNoOneLayerInRangeAndFindNearestLowerLayer->: h["+nearest_layer_h+"]="+h+" z0="+z0+" z1="+z1);
			}
			if(!ConfigValue.GeoEngineA2 && z_min <= h && h <= z_max)
				return Short.MIN_VALUE;
			if(h < z0 && nearest_layer_h < h)
			{
				nearest_layer_h = h;
				nearest_layer = layers[i];
			}
		}
		return nearest_layer;
	}

	public static boolean canSeeWallCheck(short layer, short nearest_lower_neighbor, byte directionNSWE, int curr_z, boolean air)
	{
		short nearest_lower_neighborh = (short) ((short) (nearest_lower_neighbor & 0x0fff0) >> 1);
		if(air)
			return nearest_lower_neighborh < curr_z;
		short layerh = (short) ((short) (layer & 0x0fff0) >> 1);
		int zdiff = nearest_lower_neighborh - layerh;
		return (layer & 0x0F & directionNSWE) != 0 || zdiff > -ConfigValue.MaxZDiff && zdiff != 0;
	}
	private static List<L2ItemInstance> debugItems;
	private static void addDebugItem(int item_id, int item_count, Location loc)
	{
		L2ItemInstance item = ItemTemplates.getInstance().createItem(item_id);
		if(item_count > 1)
			item.setCount(item_count);
		debugItems.add(item);
		item.dropMe(null, loc);
	}

	private static void sendPacket(ExShowTrace est)
	{
		for(L2Player p : L2ObjectsStorage.getPlayers())
			p.sendPacket(est);
	}

	public static Location canSee(int _x, int _y, int _z, int _tx, int _ty, int _tz, boolean air, int geoIndex)
	{
		return canSee(_x, _y, _z, _tx, _ty, _tz, air, geoIndex, false, false);
	}
	public static Location canSee(int _x, int _y, int _z, int _tx, int _ty, int _tz, boolean air, int geoIndex, boolean is_attack, boolean log)
	{
		if(log)
		{
			if(debugItems == null)
				debugItems = new ArrayList<L2ItemInstance>();
			else
			{
				for(L2ItemInstance item : debugItems)
					item.deleteMe();
				debugItems.clear();
			}
		}
		boolean log2=log;
		if(log)
			_log.info("GeoEngine: ->canSee: ----------------------------------");
		//log=false;
		int diff_x = _tx - _x, diff_y = _ty - _y, diff_z = _tz - _z;
		int dx = Math.abs(diff_x), dy = Math.abs(diff_y);

		float steps = Math.max(dx, dy);
		int curr_x = _x, curr_y = _y, curr_z = _z;
		short[] curr_layers = new short[MAX_LAYERS + 1];
		NGetLayers(curr_x, curr_y, curr_layers, geoIndex);

		Location result = new Location(_x, _y, _z, -1);

		if(steps == 0)
		{
			if(CheckNoOneLayerInRangeAndFindNearestLowerLayer(curr_layers, curr_z, curr_z + diff_z, log) != Short.MIN_VALUE)
				result.set(_tx, _ty, _tz, 1);
			return result;
		}

		float step_x = diff_x / steps, step_y = diff_y / steps, step_z = diff_z / steps;
		float half_step_z = step_z / 2.0f;
		float next_x = curr_x, next_y = curr_y, next_z = curr_z;
		int i_next_x, i_next_y, i_next_z, middle_z;
		short[] tmp_layers = new short[MAX_LAYERS + 1];
		short src_nearest_lower_layer, dst_nearest_lower_layer, tmp_nearest_lower_layer;

		for(int i = 0; i < steps; i++)
		{
			if(curr_layers[0] == 0)
			{
				result.set(_tx, _ty, _tz, 0);
				return result; // Здесь нет геодаты, разрешаем
			}

			next_x += step_x;
			next_y += step_y;
			next_z += step_z;
			i_next_x = (int) Math.ceil(next_x);
			i_next_y = (int) Math.ceil(next_y);
			i_next_z = (int) Math.ceil (next_z);
			middle_z = (int) (curr_z + half_step_z);

			if(log2)
			{
				int l_x = ((curr_x << 4) + L2World.MAP_MIN_X + 8);
				int l_y = ((curr_y << 4) + L2World.MAP_MIN_Y + 8);

				int i_l_x = ((i_next_x << 4) + L2World.MAP_MIN_X + 8);
				int i_l_y = ((i_next_y << 4) + L2World.MAP_MIN_Y + 8);

				ExShowTrace result2 = new ExShowTrace(5000);
				result2.addTrace(i_l_x, i_l_y, i_next_z);

				sendPacket(result2);
				
				_log.info("GeoEngine: ->canSee: x["+l_x+"]="+i_l_x+" y["+l_y+"]="+i_l_y+" z["+curr_z+"]["+middle_z+"]="+i_next_z);
				addDebugItem(57, i+1, new Location(l_x+ConfigValue.DG1, l_y+ConfigValue.DG2, curr_z));
				addDebugItem(65, i+1, new Location(i_l_x+ConfigValue.DG1, i_l_y+ConfigValue.DG2, i_next_z));
			}
			if((src_nearest_lower_layer = CheckNoOneLayerInRangeAndFindNearestLowerLayer(curr_layers, curr_z, middle_z, log)) == Short.MIN_VALUE)
				return result.setH(-10); // либо есть преграждающая поверхность, либо нет снизу слоя и значит это "пустота", то что за стеной или за колоной

			NGetLayers(curr_x, curr_y, curr_layers, geoIndex);
			if(curr_layers[0] == 0)
			{
				result.set(_tx, _ty, _tz, 0);
				return result; // Здесь нет геодаты, разрешаем
			}

			if((dst_nearest_lower_layer = CheckNoOneLayerInRangeAndFindNearestLowerLayer(curr_layers, i_next_z, middle_z, log)) == Short.MIN_VALUE)
			{
				if(log2)
				{
					int l_x = ((curr_x << 4) + L2World.MAP_MIN_X + 8);
					int l_y = ((curr_y << 4) + L2World.MAP_MIN_Y + 8);

					int i_l_x = ((i_next_x << 4) + L2World.MAP_MIN_X + 8);
					int i_l_y = ((i_next_y << 4) + L2World.MAP_MIN_Y + 8);

					//addDebugItem(65, i+1, new Location(l_x, l_y, curr_z));
					//addDebugItem(57, i+1, new Location(i_l_x, i_l_y, i_next_z));

					_log.info("GeoEngine: ->canSee: NSWE1="+NSWE2Str((byte)(dst_nearest_lower_layer & 0x0F))+" ["+l_x+","+l_y+","+curr_z+"]["+i_l_x+","+i_l_y+","+i_next_z+"]");
				}
				return result.setH(-11); // либо есть преграда, либо нет снизу слоя и значит это "пустота", то что за стеной или за колоной
			}

			if(log2)
			{
				int l_x = ((curr_x << 4) + L2World.MAP_MIN_X + 8);
				int l_y = ((curr_y << 4) + L2World.MAP_MIN_Y + 8);

				int i_l_x = ((i_next_x << 4) + L2World.MAP_MIN_X + 8);
				int i_l_y = ((i_next_y << 4) + L2World.MAP_MIN_Y + 8);

				//addDebugItem(65, i+1, new Location(l_x, l_y, curr_z));
				//addDebugItem(57, i+1, new Location(i_l_x, i_l_y, i_next_z));

				_log.info("GeoEngine: ->canSee: NSWE2Str="+NSWE2Str((byte)(src_nearest_lower_layer & 0x0F))+" ["+l_x+","+l_y+","+curr_z+"]["+i_l_x+","+i_l_y+","+i_next_z+"]");
			}
			if(curr_x == i_next_x)
			{
				//движемся по вертикали
				if(!canSeeWallCheck(src_nearest_lower_layer, dst_nearest_lower_layer, i_next_y > curr_y ? SOUTH : NORTH, is_attack ? _z : curr_z, air))
				{
					/*short nearest_lower_neighborh = (short) ((short) (dst_nearest_lower_layer & 0x0fff0) >> 1);
					short layerh = (short) ((short) (src_nearest_lower_layer & 0x0fff0) >> 1);
					int zdiff = nearest_lower_neighborh - layerh;

					if(log2)
						_log.info("GeoEngine: ->canSee: ["+((layerh & 0x0F & (i_next_y > curr_y ? SOUTH : NORTH)) != 0)+"]["+(zdiff > -ConfigValue.MaxZDiff && zdiff != 0)+"]:["+nearest_lower_neighborh+"]["+layerh+"]["+zdiff+"]");
				*/
					return result.setH(-20);
				}
			}
			else if(curr_y == i_next_y)
			{
				//движемся по горизонтали
				if(!canSeeWallCheck(src_nearest_lower_layer, dst_nearest_lower_layer, i_next_x > curr_x ? EAST : WEST, is_attack ? _z : curr_z, air))
					return result.setH(-21);
			}
			else
			{
				//движемся по диагонали
				NGetLayers(curr_x, i_next_y, tmp_layers, geoIndex);
				if(tmp_layers[0] == 0)
				{
					result.set(_tx, _ty, _tz, 0);
					return result; // Здесь нет геодаты, разрешаем
				}
				if((tmp_nearest_lower_layer = CheckNoOneLayerInRangeAndFindNearestLowerLayer(tmp_layers, i_next_z, middle_z, log)) == Short.MIN_VALUE)
					return result.setH(-30); // либо есть преграда, либо нет снизу слоя и значит это "пустота", то что за стеной или за колоной

				if(!(canSeeWallCheck(src_nearest_lower_layer, tmp_nearest_lower_layer, i_next_y > curr_y ? SOUTH : NORTH, is_attack ? _z : curr_z, air) && canSeeWallCheck(tmp_nearest_lower_layer, dst_nearest_lower_layer, i_next_x > curr_x ? EAST : WEST, is_attack ? _z : curr_z, air)))
				{
					if(!ConfigValue.NewGeoEngineTest)
						return result.setH(-22);
					NGetLayers(i_next_x, curr_y, tmp_layers, geoIndex);
					if(tmp_layers[0] == 0)
					{
						result.set(_tx, _ty, _tz, 0);
						return result; // Здесь нет геодаты, разрешаем
					}
					if((tmp_nearest_lower_layer = CheckNoOneLayerInRangeAndFindNearestLowerLayer(tmp_layers, i_next_z, middle_z)) == Short.MIN_VALUE)
						return result.setH(-31); // либо есть преграда, либо нет снизу слоя и значит это "пустота", то что за стеной или за колоной
					if(!canSeeWallCheck(src_nearest_lower_layer, tmp_nearest_lower_layer, i_next_x > curr_x ? EAST : WEST, curr_z, air))
						return result.setH(-32);
					if(!canSeeWallCheck(tmp_nearest_lower_layer, dst_nearest_lower_layer, i_next_y > curr_y ? SOUTH : NORTH, curr_z, air))
						return result.setH(-33);
				}
			}

			result.set(curr_x, curr_y, curr_z);
			curr_x = i_next_x;
			curr_y = i_next_y;
			curr_z = i_next_z;
		}

		result.set(_tx, _ty, _tz, 0xFF);
		return result;
	}
	/**
	 * проверка видимости
	 * @return возвращает последнюю точку которую видно (в формате геокоординат)
	 * в результате (Location) h является кодом, если >= 0 то успешно достигли последней точки, если меньше то не последней
	 */
	public static Location canSee1(int _x, int _y, int _z, int _tx, int _ty, int _tz, boolean air, int geoIndex)
	{
		return canSee1(_x, _y, _z, _tx, _ty, _tz, air, geoIndex, false);
	}
	public static Location canSee1(int _x, int _y, int _z, int _tx, int _ty, int _tz, boolean air, int geoIndex, boolean log)
	{
		if(log)
		{
			if(debugItems == null)
				debugItems = new ArrayList<L2ItemInstance>();
			else
			{
				for(L2ItemInstance item : debugItems)
					item.deleteMe();
				debugItems.clear();
			}
		}
		boolean log2=log;
		if(log)
			_log.info("GeoEngine: ->canSee: ----------------------------------");
		log=false;
		int diff_x = _tx - _x, diff_y = _ty - _y, diff_z = _tz - _z;
		int dx = Math.abs(diff_x), dy = Math.abs(diff_y);

		float steps = Math.max(dx, dy);
		int curr_x = _x, curr_y = _y, curr_z = _z;
		short[] curr_layers = new short[MAX_LAYERS + 1];
		NGetLayers(curr_x, curr_y, curr_layers, geoIndex, log ? 1 : -1);

		Location result = new Location(_x, _y, _z, -1);

		if(steps == 0)
		{
			if(CheckNoOneLayerInRangeAndFindNearestLowerLayer(curr_layers, curr_z, curr_z + diff_z) != Short.MIN_VALUE)
				result.set(_tx, _ty, _tz, 1);
			return result;
		}

		float step_x = diff_x / steps, step_y = diff_y / steps, step_z = diff_z / steps;
		float half_step_z = step_z / 2.0f;
		float next_x = curr_x, next_y = curr_y, next_z = curr_z;
		int i_next_x, i_next_y, i_next_z, middle_z;
		short[] tmp_layers = new short[MAX_LAYERS + 1];
		short src_nearest_lower_layer, dst_nearest_lower_layer, tmp_nearest_lower_layer;

		for(int i = 0; i < steps; i++)
		{
			if(curr_layers[0] == 0)
			{
				result.set(_tx, _ty, _tz, 2);
				return result; // Здесь нет геодаты, разрешаем
			}

			next_x += step_x;
			next_y += step_y;
			next_z += step_z;
			i_next_x = (int) Math.round(next_x); // round ??? ceil
			i_next_y = (int) Math.round(next_y); // round ??? ceil
			i_next_z = (int) Math.round (next_z); // round ??? ceil
			middle_z = (int) (curr_z + half_step_z);

			if(log2)
			{
				int l_x = ((curr_x << 4) + L2World.MAP_MIN_X + 8);
				int l_y = ((curr_y << 4) + L2World.MAP_MIN_Y + 8);

				int i_l_x = ((i_next_x << 4) + L2World.MAP_MIN_X + 8);
				int i_l_y = ((i_next_y << 4) + L2World.MAP_MIN_Y + 8);

				addDebugItem(57, i+1, new Location(l_x+ConfigValue.DG1, l_y+ConfigValue.DG2, curr_z));
				addDebugItem(65, i+1, new Location(i_l_x+ConfigValue.DG1, i_l_y+ConfigValue.DG2, i_next_z));
			}
			// попадаем ли мы в текущий слой, по Z координате, если все ок, возвращаем NSWE проходимость.
			if((src_nearest_lower_layer = CheckNoOneLayerInRangeAndFindNearestLowerLayer(curr_layers, curr_z, middle_z)) == Short.MIN_VALUE)
				return result.setH(-10); // либо есть преграждающая поверхность, либо нет снизу слоя и значит это "пустота", то что за стеной или за колоной

			// сделали шах на кледку, берем соответсвующий блок гео
			NGetLayers(curr_x, curr_y, curr_layers, geoIndex, log ? 2 : -1);
			if(curr_layers[0] == 0)
			{
				result.set(_tx, _ty, _tz, 3);
				return result; // Здесь нет геодаты, разрешаем
			}

			// попадаем ли мы в следующую Z координату на текущем шаге, если все ок, возвращаем NSWE проходимость.
			if((dst_nearest_lower_layer = CheckNoOneLayerInRangeAndFindNearestLowerLayer(curr_layers, i_next_z, middle_z)) == Short.MIN_VALUE)
			{
					if(log2)
					{
						int l_x = ((curr_x << 4) + L2World.MAP_MIN_X + 8);
						int l_y = ((curr_y << 4) + L2World.MAP_MIN_Y + 8);

						int i_l_x = ((i_next_x << 4) + L2World.MAP_MIN_X + 8);
						int i_l_y = ((i_next_y << 4) + L2World.MAP_MIN_Y + 8);

						//addDebugItem(65, i+1, new Location(l_x, l_y, curr_z));
						//addDebugItem(57, i+1, new Location(i_l_x, i_l_y, i_next_z));

						_log.info("GeoEngine: ->canSee: NSWE1="+NSWE2Str((byte)(dst_nearest_lower_layer & 0x0F))+" ["+l_x+","+l_y+","+curr_z+"]["+i_l_x+","+i_l_y+","+i_next_z+"]");
					}
				return result.setH(-11); // либо есть преграда, либо нет снизу слоя и значит это "пустота", то что за стеной или за колоной	
			}
			if(log2)
			{
				int l_x = ((curr_x << 4) + L2World.MAP_MIN_X + 8);
				int l_y = ((curr_y << 4) + L2World.MAP_MIN_Y + 8);

				int i_l_x = ((i_next_x << 4) + L2World.MAP_MIN_X + 8);
				int i_l_y = ((i_next_y << 4) + L2World.MAP_MIN_Y + 8);

				//addDebugItem(65, i+1, new Location(l_x, l_y, curr_z));
				//addDebugItem(57, i+1, new Location(i_l_x, i_l_y, i_next_z));

				_log.info("GeoEngine: ->canSee: NSWE2Str="+NSWE2Str((byte)(src_nearest_lower_layer & 0x0F))+" ["+l_x+","+l_y+","+curr_z+"]["+i_l_x+","+i_l_y+","+i_next_z+"]");
			}
			if(curr_x == i_next_x)
			{
				if(log2)
					_log.info("GeoEngine: ->canSee1: geoXYZ2Str="+geoXYZ2Str(curr_x, i_next_y, curr_z));
				//движемся по вертикали
				if(!canSeeWallCheck(src_nearest_lower_layer, dst_nearest_lower_layer, i_next_y > curr_y ? SOUTH : NORTH, curr_z, air))
				{
					if(log2)
						_log.info("GeoEngine: ->canSee2: geoXYZ2Str="+geoXYZ2Str(curr_x, i_next_y, curr_z));
					NGetLayers(i_next_x, i_next_y, tmp_layers, geoIndex, log ? 3 : -1);
					if((tmp_nearest_lower_layer = CheckNoOneLayerInRangeAndFindNearestLowerLayer(tmp_layers, i_next_z, middle_z)) == Short.MIN_VALUE)
						return result.setH(-100); // либо есть преграда, либо нет снизу слоя и значит это "пустота", то что за стеной или за колоной

					//движемся по вертикали
					if(!canSeeWallCheck(src_nearest_lower_layer, tmp_nearest_lower_layer, i_next_y > curr_y ? SOUTH : NORTH, curr_z, air))
						return result.setH(-20);
				}
			}
			else if(curr_y == i_next_y)
			{
				//движемся по горизонтали
				if(!canSeeWallCheck(src_nearest_lower_layer, dst_nearest_lower_layer, i_next_x > curr_x ? EAST : WEST, curr_z, air))
				{
					NGetLayers(i_next_x, i_next_y, tmp_layers, geoIndex, log ? 3 : -1);
					if((tmp_nearest_lower_layer = CheckNoOneLayerInRangeAndFindNearestLowerLayer(tmp_layers, i_next_z, middle_z)) == Short.MIN_VALUE)
						return result.setH(-101); // либо есть преграда, либо нет снизу слоя и значит это "пустота", то что за стеной или за колоной

					//движемся по горизонтали
					if(!canSeeWallCheck(src_nearest_lower_layer, tmp_nearest_lower_layer, i_next_x > curr_x ? EAST : WEST, curr_z, air))
						return result.setH(-21);
				}
			}
			else
			{
				if(!canSeeWallCheck(src_nearest_lower_layer, dst_nearest_lower_layer, i_next_y > curr_y ? SOUTH : NORTH, curr_z, air))
				{
					NGetLayers(i_next_x, i_next_y, tmp_layers, geoIndex, log ? 3 : -1);
					if((tmp_nearest_lower_layer = CheckNoOneLayerInRangeAndFindNearestLowerLayer(tmp_layers, i_next_z, middle_z)) == Short.MIN_VALUE)
						return result.setH(-200); // либо есть преграда, либо нет снизу слоя и значит это "пустота", то что за стеной или за колоной

					//движемся по вертикали
					if(!canSeeWallCheck(src_nearest_lower_layer, tmp_nearest_lower_layer, i_next_x > curr_x ? EAST : WEST, curr_z, air))
						return result.setH(-220);
				}
				else if(!canSeeWallCheck(src_nearest_lower_layer, dst_nearest_lower_layer, i_next_x > curr_x ? EAST : WEST, curr_z, air))
				{
					NGetLayers(i_next_x, i_next_y, tmp_layers, geoIndex, log ? 3 : -1);
					if((tmp_nearest_lower_layer = CheckNoOneLayerInRangeAndFindNearestLowerLayer(tmp_layers, i_next_z, middle_z)) == Short.MIN_VALUE)
						return result.setH(-201); // либо есть преграда, либо нет снизу слоя и значит это "пустота", то что за стеной или за колоной

					//движемся по горизонтали
					if(!canSeeWallCheck(src_nearest_lower_layer, tmp_nearest_lower_layer, i_next_y > curr_y ? SOUTH : NORTH, curr_z, air))
						return result.setH(-221);
				}
				else
				{
					//движемся по диагонали
					NGetLayers(i_next_x, i_next_y, tmp_layers, geoIndex);
					if(tmp_layers[0] == 0)
					{
						result.set(_tx, _ty, _tz, 0);
						return result; // Здесь нет геодаты, разрешаем
					}
					if((tmp_nearest_lower_layer = CheckNoOneLayerInRangeAndFindNearestLowerLayer(tmp_layers, i_next_z, middle_z)) == Short.MIN_VALUE)
					{
						if(log2)
						{
							int l_x = ((curr_x << 4) + L2World.MAP_MIN_X + 8);
							int l_y = ((curr_y << 4) + L2World.MAP_MIN_Y + 8);

							int i_l_x = ((i_next_x << 4) + L2World.MAP_MIN_X + 8);
							int i_l_y = ((i_next_y << 4) + L2World.MAP_MIN_Y + 8);

							//addDebugItem(65, i+1, new Location(l_x, l_y, curr_z));
							//addDebugItem(57, i+1, new Location(i_l_x, i_l_y, i_next_z));

							// NSWE=X [-75336,-252584,-7702][-75320,-252600,-7703]
							_log.info("GeoEngine: ->canSee: NSWE="+NSWE2Str((byte)(tmp_nearest_lower_layer & 0x0F))+" ["+l_x+","+l_y+","+curr_z+"]["+i_l_x+","+i_l_y+","+i_next_z+"]");
						}
						return result.setH(-30); // либо есть преграда, либо нет снизу слоя и значит это "пустота", то что за стеной или за колоной
					}

					if(!(canSeeWallCheck(src_nearest_lower_layer, tmp_nearest_lower_layer, i_next_y > curr_y ? SOUTH : NORTH, curr_z, air) && canSeeWallCheck(tmp_nearest_lower_layer, dst_nearest_lower_layer, i_next_x > curr_x ? EAST : WEST, curr_z, air)))
					{
						NGetLayers(i_next_x, curr_y, tmp_layers, geoIndex);
						if(tmp_layers[0] == 0)
						{
							result.set(_tx, _ty, _tz, 0);
							return result; // Здесь нет геодаты, разрешаем
						}
						if((tmp_nearest_lower_layer = CheckNoOneLayerInRangeAndFindNearestLowerLayer(tmp_layers, i_next_z, middle_z)) == Short.MIN_VALUE)
							return result.setH(-31); // либо есть преграда, либо нет снизу слоя и значит это "пустота", то что за стеной или за колоной
						if(!canSeeWallCheck(src_nearest_lower_layer, tmp_nearest_lower_layer, i_next_x > curr_x ? EAST : WEST, curr_z, air))
							return result.setH(-32);
						if(!canSeeWallCheck(tmp_nearest_lower_layer, dst_nearest_lower_layer, i_next_x > curr_x ? EAST : WEST, curr_z, air))
							return result.setH(-33);
					}
				}
			}
			/*{
				if(!canSeeWallCheck(src_nearest_lower_layer, dst_nearest_lower_layer, i_next_y > curr_y ? SOUTH : NORTH, curr_z, air))
					return result.setH(-50);
				if(!canSeeWallCheck(src_nearest_lower_layer, dst_nearest_lower_layer, i_next_x > curr_x ? EAST : WEST, curr_z, air))
					return result.setH(-55);

				//движемся по диагонали
				NGetLayers(curr_x, i_next_y, tmp_layers, geoIndex, log ? 3 : -1);
				if(tmp_layers[0] == 0)
				{
					result.set(_tx, _ty, _tz, 4);
					return result; // Здесь нет геодаты, разрешаем
				}
				if((tmp_nearest_lower_layer = CheckNoOneLayerInRangeAndFindNearestLowerLayer(tmp_layers, i_next_z, middle_z)) == Short.MIN_VALUE)
					return result.setH(-30); // либо есть преграда, либо нет снизу слоя и значит это "пустота", то что за стеной или за колоной

				boolean w1 = canSeeWallCheck(src_nearest_lower_layer, tmp_nearest_lower_layer, i_next_y > curr_y ? SOUTH : NORTH, curr_z, air);
				boolean w3 = canSeeWallCheck(tmp_nearest_lower_layer, src_nearest_lower_layer, i_next_x > curr_x ? EAST : WEST, curr_z, air);
				boolean w5 = canSeeWallCheck(src_nearest_lower_layer, dst_nearest_lower_layer, i_next_x > curr_x ? EAST : WEST, curr_z, air);
				boolean w6 = canSeeWallCheck(dst_nearest_lower_layer, src_nearest_lower_layer, i_next_x > curr_x ? EAST : WEST, curr_z, air);
				boolean w2 = canSeeWallCheck(tmp_nearest_lower_layer, dst_nearest_lower_layer, i_next_x > curr_x ? EAST : WEST, curr_z, air);
				boolean w4 = canSeeWallCheck(dst_nearest_lower_layer, tmp_nearest_lower_layer, i_next_x > curr_x ? EAST : WEST, curr_z, air);
				if(log2)
				{
					int l_x = ((curr_x << 4) + L2World.MAP_MIN_X + 8);
					int l_y = ((curr_y << 4) + L2World.MAP_MIN_Y + 8);

					int i_l_x = ((i_next_x << 4) + L2World.MAP_MIN_X + 8);
					int i_l_y = ((i_next_y << 4) + L2World.MAP_MIN_Y + 8);

					//addDebugItem(65, i+1, new Location(l_x, l_y, curr_z));
					//addDebugItem(57, i+1, new Location(i_l_x, i_l_y, i_next_z));

					_log.info("GeoEngine: ->canSee: NSWE2Str["+w1+"]["+w2+"]["+w3+"]["+w4+"]["+w5+"]["+w6+"]="+NSWE2Str((byte)(src_nearest_lower_layer & 0x0F))+" ["+l_x+","+l_y+","+curr_z+"]["+i_l_x+","+i_l_y+","+i_next_z+"]");
				}
				if(!(w1 && w2))
				//if(!(canSeeWallCheck(src_nearest_lower_layer, tmp_nearest_lower_layer, i_next_y > curr_y ? SOUTH : NORTH, curr_z, air) && canSeeWallCheck(tmp_nearest_lower_layer, dst_nearest_lower_layer, i_next_x > curr_x ? EAST : WEST, curr_z, air)))
				{
					if(log2)
						_log.info("GeoEngine: ->canSee: src_nearest_lower_layer="+NSWE2Str((byte)(src_nearest_lower_layer & 0x0F))+" tmp_nearest_lower_layer="+NSWE2Str((byte)(tmp_nearest_lower_layer & 0x0F))+" dst_nearest_lower_layer="+NSWE2Str((byte)(dst_nearest_lower_layer & 0x0F))+" ["+((curr_x << 4) + L2World.MAP_MIN_X + 8)+","+((curr_y << 4) + L2World.MAP_MIN_Y + 8)+","+curr_z+"]["+((i_next_x << 4) + L2World.MAP_MIN_X + 8)+","+((i_next_y << 4) + L2World.MAP_MIN_Y + 8)+","+i_next_z+"]");

					NGetLayers(i_next_x, curr_y, tmp_layers, geoIndex, log ? 4 : -1);
					if(tmp_layers[0] == 0)
					{
						result.set(_tx, _ty, _tz, 5);
						return result; // Здесь нет геодаты, разрешаем
					}
					if((tmp_nearest_lower_layer = CheckNoOneLayerInRangeAndFindNearestLowerLayer(tmp_layers, i_next_z, middle_z)) == Short.MIN_VALUE)
						return result.setH(-31); // либо есть преграда, либо нет снизу слоя и значит это "пустота", то что за стеной или за колоной
					if(!canSeeWallCheck(src_nearest_lower_layer, tmp_nearest_lower_layer, i_next_x > curr_x ? EAST : WEST, curr_z, air))
						return result.setH(-32);
					if(!canSeeWallCheck(tmp_nearest_lower_layer, dst_nearest_lower_layer, i_next_x > curr_x ? EAST : WEST, curr_z, air))
						return result.setH(-33);

					if(!canSeeWallCheck(src_nearest_lower_layer, tmp_nearest_lower_layer, i_next_y > curr_y ? SOUTH : NORTH, curr_z, air))
						return result.setH(-34);
					if(!canSeeWallCheck(tmp_nearest_lower_layer, dst_nearest_lower_layer, i_next_y > curr_y ? SOUTH : NORTH, curr_z, air))
						return result.setH(-35);
				}
				if(!canSeeWallCheck(src_nearest_lower_layer, tmp_nearest_lower_layer, i_next_y > curr_y ? SOUTH : NORTH, curr_z, air) || !canSeeWallCheck(tmp_nearest_lower_layer, dst_nearest_lower_layer, i_next_x > curr_x ? EAST : WEST, curr_z, air))
					return result.setH(-40);
			}*/

			result.set(curr_x, curr_y, curr_z, 6);
			curr_x = i_next_x;
			curr_y = i_next_y;
			curr_z = i_next_z;
		}

		result.set(_tx, _ty, _tz, 0xFF);
		return result;
	}

	private static Location MoveInWaterCheck(int x, int y, int z, int tx, int ty, int tz, int waterZ, int geoIndex, boolean isPlayer)
	{
		waterZ-=ConfigValue.GeoWaterZ;
		int z_p = NgetHeight(x, y, z, geoIndex)-ConfigValue.GeoWaterZ_Diff;
		int dx = tx - x;
		int dy = ty - y;
		int dz = tz - z;
		int inc_x = sign(dx);
		int inc_y = sign(dy);
		dx = Math.abs(dx);
		dy = Math.abs(dy);
		if(dx + dy == 0)
		{
			if(isPlayer && ConfigValue.GeodataDebugWaterMove)
				_log.warning("GeoEngine: MoveInWaterCheck-> if(isPlayer)");
			return new Location(x, y, Math.min(waterZ, Math.max(tz, z_p))).geo2world();
			//return new Location(x, y, z).geo2world();
		}
		float inc_z_for_x = dx == 0 ? 0 : dz / dx;
		float inc_z_for_y = dy == 0 ? 0 : dz / dy;

		int next_x = x;
		int next_y = y;
		float next_z = z;
		if (dx >= dy)
		{
			int delta_A = 2 * dy;
			int d = delta_A - dx;
			int delta_B = delta_A - 2 * dx;
			for (int i = 0; i < dx; i++)
			{
				int prev_x = x;
				int prev_y = y;
				int prev_z = z;
				x = next_x;
				y = next_y;
				z = (int)next_z;
				if (d > 0)
				{
					d += delta_B;
					next_x += inc_x;
					next_z += inc_z_for_x;
					next_y += inc_y;
					next_z += inc_z_for_y;
				}
				else
				{
					d += delta_A;
					next_x += inc_x;
					next_z += inc_z_for_x;
				}
				if(!NLOS_WATER(x, y, z, next_x, next_y, (int)next_z, geoIndex, isPlayer))
				{
					if(isPlayer && ConfigValue.GeodataDebugWaterMove)
						_log.warning("GeoEngine: MoveInWaterCheck-> NLOS_WATER 1");
					return new Location(prev_x, prev_y, Math.min(waterZ, Math.max(tz, z_p))).geo2world();
					//return new Location(prev_x, prev_y, prev_z).geo2world();
				}
			}
		}
		else
		{
			int delta_A = 2 * dx;
			int d = delta_A - dy;
			int delta_B = delta_A - 2 * dy;
			for (int i = 0; i < dy; i++)
			{
				int prev_x = x;
				int prev_y = y;
				int prev_z = z;
				x = next_x;
				y = next_y;
				z = (int)next_z;
				if (d > 0)
				{
					d += delta_B;
					next_x += inc_x;
					next_z += inc_z_for_x;
					next_y += inc_y;
					next_z += inc_z_for_y;
				}
				else
				{
					d += delta_A;
					next_y += inc_y;
					next_z += inc_z_for_y;
				}
				if(!NLOS_WATER(x, y, z, next_x, next_y, (int)next_z, geoIndex, isPlayer))
				{
					if(isPlayer && ConfigValue.GeodataDebugWaterMove)
						_log.warning("GeoEngine: MoveInWaterCheck-> NLOS_WATER 2");
					return new Location(prev_x, prev_y, Math.min(waterZ, Math.max(tz, z_p))).geo2world();
					//return new Location(prev_x, prev_y, prev_z).geo2world();
				}
			}
		}
		if(isPlayer && ConfigValue.GeodataDebugWaterMove)
			_log.warning("GeoEngine: MoveInWaterCheck-> Finish");
		return new Location(next_x, next_y, Math.min(waterZ, Math.max((int)next_z, z_p))).geo2world();
		//return new Location(next_x, next_y, (int)next_z).geo2world();
	}

	/**
	 * проверка проходимости по прямой
	 * @return 0 - проходимо, в ином случае код причины непроходимости (используется при отладке)
	 */
	private static int canMove(int __x, int __y, int _z, int __tx, int __ty, int _tz, boolean withCollision, int geoIndex)
	{
		boolean isWater = false;
		int _x = __x - L2World.MAP_MIN_X >> 4;
		int _y = __y - L2World.MAP_MIN_Y >> 4;
		int _tx = __tx - L2World.MAP_MIN_X >> 4;
		int _ty = __ty - L2World.MAP_MIN_Y >> 4;

		int diff_x = _tx - _x; int diff_y = _ty - _y;
		int incx = sign(diff_x); int incy = sign(diff_y);
		boolean overRegionEdge = (_x >> 11 != _tx >> 11) || (_y >> 11 != _ty >> 11);

		if(diff_x < 0)
			diff_x = -diff_x;
		if(diff_y < 0)
			diff_y = -diff_y;
		int el;
		int pdx;
		int pdy;
		int es;

		if(diff_x > diff_y)
		{
			pdx = incx;
			pdy = 0;
			es = diff_y;
			el = diff_x;
		}
		else
		{
			pdx = 0;
			pdy = incy;
			es = diff_x;
			el = diff_y;
		}

		int err = el / 2;

		int curr_x = _x; int curr_y = _y; int curr_z = _z;
		int next_x = curr_x; int next_y = curr_y; int next_z = curr_z;

		short[] next_layers = new short[MAX_LAYERS + 1];
		short[] temp_layers = new short[MAX_LAYERS + 1];
		short[] curr_layers = new short[MAX_LAYERS + 1];

		NGetLayers(curr_x, curr_y, curr_layers, geoIndex);
		if(curr_layers[0] == 0)
			return 0;

		for(int i = 0; i < el; i++)
		{
			err -= es;
			if(err < 0)
			{
				err += el;
				next_x += incx;
				next_y += incy;
			}
			else
			{
				next_x += pdx;
				next_y += pdy;
			}
			boolean regionEdge = (overRegionEdge) && ((next_x >> 11 != curr_x >> 11) || (next_y >> 11 != curr_y >> 11));

			NGetLayers(next_x, next_y, next_layers, geoIndex);
			if((next_z = NcanMoveNext(curr_x, curr_y, curr_z, curr_layers, next_x, next_y, next_layers, temp_layers, withCollision, regionEdge, geoIndex, false, isWater)) == Integer.MIN_VALUE)
				return 1;
			short[] t = curr_layers;
			curr_layers = next_layers;
			next_layers = t;

			curr_x = next_x;
			curr_y = next_y;
			curr_z = next_z;
		}

		int diff_z = curr_z - _tz;
		if(ConfigValue.AllowFallFromWalls)
			return diff_z < ConfigValue.MaxZDiff ? 0 : diff_z * 10000;

		if(diff_z < 0)
			diff_z = -diff_z;
		return diff_z <= ConfigValue.MaxZDiff ? 0 : diff_z * 10000;
	}

	public static Location MoveCheck(int __x, int __y, int _z, int __tx, int __ty, boolean withCollision, boolean backwardMove, boolean returnPrev, int geoIndex)
	{
		boolean isWater=false;

		int _x = __x - L2World.MAP_MIN_X >> 4;
		int _y = __y - L2World.MAP_MIN_Y >> 4;
		int _tx = __tx - L2World.MAP_MIN_X >> 4;
		int _ty = __ty - L2World.MAP_MIN_Y >> 4;

		int diff_x = _tx - _x;
		int diff_y = _ty - _y;

		int incx = sign(diff_x);
		int incy = sign(diff_y);

		boolean overRegionEdge = (_x >> 11 != _tx >> 11) || (_y >> 11 != _ty >> 11);

		if(diff_x < 0)
			diff_x = -diff_x;
		if(diff_y < 0)
			diff_y = -diff_y;
		int el;
		int pdx;
		int pdy;
		int es;

		if(diff_x > diff_y)
		{
			pdx = incx;
			pdy = 0;
			es = diff_y;
			el = diff_x;
		}
		else
		{
			pdx = 0;
			pdy = incy;
			es = diff_x;
			el = diff_y;
		}

		int err = el / 2;

		int curr_x = _x; int curr_y = _y; int curr_z = _z;
		int next_x = curr_x; int next_y = curr_y; int next_z = curr_z;
		int prev_x = curr_x; int prev_y = curr_y; int prev_z = curr_z;

		short[] next_layers = new short[MAX_LAYERS + 1];
		short[] temp_layers = new short[MAX_LAYERS + 1];
		short[] curr_layers = new short[MAX_LAYERS + 1];

		NGetLayers(curr_x, curr_y, curr_layers, geoIndex);
		for(int i = 0; i < el; i++)
		{
			err -= es;
			if(err < 0)
			{
				err += el;
				next_x += incx;
				next_y += incy;
			}
			else
			{
				next_x += pdx;
				next_y += pdy;
			}
			boolean regionEdge = (overRegionEdge) && ((next_x >> 11 != curr_x >> 11) || (next_y >> 11 != curr_y >> 11));

			NGetLayers(next_x, next_y, next_layers, geoIndex);
			if((next_z = NcanMoveNext(curr_x, curr_y, curr_z, curr_layers, next_x, next_y, next_layers, temp_layers, withCollision, regionEdge, geoIndex, false, isWater)) == Integer.MIN_VALUE)
				break;
			if(backwardMove && (NcanMoveNext(next_x, next_y, next_z, next_layers, curr_x, curr_y, curr_layers, temp_layers, withCollision, regionEdge, geoIndex, false, isWater) == Integer.MIN_VALUE))
				break;
			short[] t = curr_layers;
			curr_layers = next_layers;
			next_layers = t;

			if(returnPrev)
			{
				prev_x = curr_x;
				prev_y = curr_y;
				prev_z = curr_z;
			}

			curr_x = next_x;
			curr_y = next_y;
			curr_z = next_z;
		}
		if(returnPrev)
		{
			curr_x = prev_x;
			curr_y = prev_y;
			curr_z = prev_z;
		}
		return new Location(curr_x, curr_y, curr_z).geo2world();
	}
	public static List<Location> MoveList(int __x, int __y, int _z, int __tx, int __ty, int __tz, int geoIndex, boolean onlyFullPath)
	{
		return MoveList(__x, __y, _z, __tx, __ty, __tz, geoIndex, onlyFullPath, false, -1, Integer.MIN_VALUE);
	}

	/** Аналогичен CanMove, но возвращает весь пройденный путь. В гео координатах. */
	public static List<Location> MoveList(int __x, int __y, int _z, int __tx, int __ty, int __tz, int geoIndex, boolean onlyFullPath, boolean isPlayer, int id, int waterZ)
	{
		boolean isWater = (waterZ != Integer.MIN_VALUE);

		int _x = __x - L2World.MAP_MIN_X >> 4;
		int _y = __y - L2World.MAP_MIN_Y >> 4;
		int _tx = __tx - L2World.MAP_MIN_X >> 4;
		int _ty = __ty - L2World.MAP_MIN_Y >> 4;

		int z_p = NgetHeight(_x, _y, _z, geoIndex)-ConfigValue.GeoWaterZ_Diff;

		//waterZ = Math.min(waterZ, Math.max(__tz, z_p));

		int diff_x = _tx - _x; int diff_y = _ty - _y;
		int incx = sign(diff_x); int incy = sign(diff_y);
		boolean overRegionEdge = (_x >> 11 != _tx >> 11) || (_y >> 11 != _ty >> 11);

		if(diff_x < 0)
			diff_x = -diff_x;
		if(diff_y < 0)
			diff_y = -diff_y;
		int el;
		int pdx;
		int pdy;
		int es;

		if(diff_x > diff_y)
		{
			pdx = incx;
			pdy = 0;
			es = diff_y;
			el = diff_x;
		}
		else
		{
			pdx = 0;
			pdy = incy;
			es = diff_x;
			el = diff_y;
		}

		int err = el / 2;

		int curr_x = _x; int curr_y = _y; int curr_z = _z;
		int next_x = curr_x; int next_y = curr_y; int next_z = curr_z;

		short[] next_layers = new short[MAX_LAYERS + 1];
		short[] temp_layers = new short[MAX_LAYERS + 1];
		short[] curr_layers = new short[MAX_LAYERS + 1];

		NGetLayers(curr_x, curr_y, curr_layers, geoIndex);
		if(curr_layers[0] == 0)
			return null;

		List<Location> result = new ArrayList<Location>(el + 1);

		int x2 = (curr_x << 4) + L2World.MAP_MIN_X;
		int y2 = (curr_y << 4) + L2World.MAP_MIN_Y;
		if(isPlayer && ConfigValue.GeodataDebugMoveList)
			_log.info("GeoEngine: MoveList[start] -> curr_x="+x2+" curr_y="+y2+" z["+isWater+"]["+curr_z+"]["+z_p+"]["+(waterZ-ConfigValue.GeoWaterZ)+"]="+(isWater ? Math.min((waterZ-ConfigValue.GeoWaterZ), Math.max(curr_z, z_p)) : curr_z));

		/*int curr_z = -5528;
		int z_p = -5792;
		int waterZ = -5477;

		Math.min(waterZ, Math.max(curr_z, z_p))
		GeoEngine: MoveList[start] -> curr_x=20957 curr_y=25377 z[true][-5528][-5792][-5477]=-5528
		*/
		result.add(new Location(curr_x, curr_y, isWater ? Math.min((waterZ-ConfigValue.GeoWaterZ), Math.max(__tz, z_p)) : curr_z));

		for(int i = 0;i < el;i++)
		{
			err -= es;
			if(err < 0)
			{
				err += el;
				next_x += incx;
				next_y += incy;
			}
			else
			{
				next_x += pdx;
				next_y += pdy;
			}
			boolean regionEdge = (overRegionEdge) && ((next_x >> 11 != curr_x >> 11) || (next_y >> 11 != curr_y >> 11));

			NGetLayers(next_x, next_y, next_layers, geoIndex);

			if(isPlayer && ConfigValue.GeodataDebugMoveList2)
			{
				for(short cl : curr_layers)
				{
					int h = (short) ((short) (cl & 0x0fff0) >> 1);
					_log.info("GeoEngine: MoveList -> curr_layers="+h);
				}
				for(short nl : next_layers)
				{
					int h = (short) ((short) (nl & 0x0fff0) >> 1);
					_log.info("GeoEngine: MoveList -> next_layers="+h);
				}
			}

			int x3 = (next_x << 4) + L2World.MAP_MIN_X;
			int y3 = (next_y << 4) + L2World.MAP_MIN_Y;

			//isWater = L2World.isWater(x3, y3, curr_z);
			/*if(isWater && waterZ == Integer.MIN_VALUE)
			{
				int water_z = Integer.MIN_VALUE;

				GArray<L2Territory> terrlist = L2World.getTerritories(x3, y3, curr_z);
				if(terrlist != null)
					for(L2Territory terr : terrlist)
						if(terr != null && terr.getZone() != null && terr.getZone().getType() == ZoneType.water && (water_z == Integer.MIN_VALUE || water_z < terr.getZmax()))
							water_z = terr.getZmax();

				waterZ=water_z;
			}*/
			if(/*!isWater && */(next_z = NcanMoveNext(curr_x, curr_y, curr_z, curr_layers, next_x, next_y, next_layers, temp_layers, false, regionEdge, geoIndex, isPlayer, isWater)) == Integer.MIN_VALUE)
			{
				if(!onlyFullPath)
				{
					if(isPlayer && ConfigValue.GeodataDebugMoveList)
						_log.info("GeoEngine: MoveList[break]");
					break;
				}
				if(isPlayer && ConfigValue.GeodataDebugMoveList)
					_log.info("GeoEngine: MoveList[return null]");
				return null;
			}

			short[] t = curr_layers;
			curr_layers = next_layers;
			next_layers = t;

			curr_x = next_x;
			curr_y = next_y;

			int x1 = (curr_x << 4) + L2World.MAP_MIN_X;
			int y1 = (curr_y << 4) + L2World.MAP_MIN_Y;
			if(isPlayer && ConfigValue.GeodataDebugMoveList)
				_log.info("GeoEngine: MoveList["+i+"] -> curr_x="+x1+" curr_y="+y1+" z["+isWater+"]["+curr_z+"]["+next_z+"]["+z_p+"]["+(waterZ-ConfigValue.GeoWaterZ)+"]="+(isWater ? Math.min((waterZ-ConfigValue.GeoWaterZ), Math.max(next_z, z_p)) : next_z));

			//if(!isWater)
				curr_z = next_z;

			result.add(new Location(curr_x, curr_y, isWater ? Math.min((waterZ-ConfigValue.GeoWaterZ), Math.max(__tz, z_p)) : curr_z));
		}
		return result;
	}
	public static Location[] MoveLoc(int __x, int __y, int _z, int __tx, int __ty, int __tz, int geoIndex, boolean isPlayer, int id, int waterZ, boolean geo2world)
	{
		boolean isWater = (waterZ != Integer.MIN_VALUE);

		int _x = __x - L2World.MAP_MIN_X >> 4;
		int _y = __y - L2World.MAP_MIN_Y >> 4;
		int _tx = __tx - L2World.MAP_MIN_X >> 4;
		int _ty = __ty - L2World.MAP_MIN_Y >> 4;

		waterZ-=ConfigValue.GeoWaterZ;
		int z_p = NgetHeight(_x, _y, _z, geoIndex)-ConfigValue.GeoWaterZ_Diff;

		//waterZ = Math.min(waterZ, Math.max(__tz, z_p));

		int diff_x = _tx - _x; int diff_y = _ty - _y;
		int incx = sign(diff_x); int incy = sign(diff_y);
		boolean overRegionEdge = (_x >> 11 != _tx >> 11) || (_y >> 11 != _ty >> 11);

		if(diff_x < 0)
			diff_x = -diff_x;
		if(diff_y < 0)
			diff_y = -diff_y;
		int el;
		int pdx;
		int pdy;
		int es;

		if(diff_x > diff_y)
		{
			pdx = incx;
			pdy = 0;
			es = diff_y;
			el = diff_x;
		}
		else
		{
			pdx = 0;
			pdy = incy;
			es = diff_x;
			el = diff_y;
		}

		int err = el / 2;

		int curr_x = _x; int curr_y = _y; int curr_z = _z;
		int next_x = curr_x; int next_y = curr_y; int next_z = curr_z;

		short[] next_layers = new short[MAX_LAYERS + 1];
		short[] temp_layers = new short[MAX_LAYERS + 1];
		short[] curr_layers = new short[MAX_LAYERS + 1];

		NGetLayers(curr_x, curr_y, curr_layers, geoIndex);
		if(curr_layers[0] == 0)
			return null;

		int x2 = (curr_x << 4) + L2World.MAP_MIN_X;
		int y2 = (curr_y << 4) + L2World.MAP_MIN_Y;
		if(isPlayer && ConfigValue.GeodataDebugMoveList)
			_log.info("GeoEngine: MoveList[start] -> curr_x="+x2+" curr_y="+y2+" z["+isWater+"]["+curr_z+"]["+z_p+"]["+waterZ+"]="+(isWater ? Math.min(waterZ, Math.max(curr_z, z_p)) : curr_z));

		/*int curr_z = -5528;
		int z_p = -5792;
		int waterZ = -5477;

		Math.min(waterZ, Math.max(curr_z, z_p))
		GeoEngine: MoveList[start] -> curr_x=20957 curr_y=25377 z[true][-5528][-5792][-5477]=-5528
		*/

		Location[] result = new Location[2];
		for(int i = 0;i < el;i++)
		{
			err -= es;
			if(err < 0)
			{
				err += el;
				next_x += incx;
				next_y += incy;
			}
			else
			{
				next_x += pdx;
				next_y += pdy;
			}
			boolean regionEdge = (overRegionEdge) && ((next_x >> 11 != curr_x >> 11) || (next_y >> 11 != curr_y >> 11));

			NGetLayers(next_x, next_y, next_layers, geoIndex);

			if(isPlayer && ConfigValue.GeodataDebugMoveList2)
			{
				for(short cl : curr_layers)
				{
					int h = (short) ((short) (cl & 0x0fff0) >> 1);
					_log.info("GeoEngine: MoveList -> curr_layers="+h);
				}
				for(short nl : next_layers)
				{
					int h = (short) ((short) (nl & 0x0fff0) >> 1);
					_log.info("GeoEngine: MoveList -> next_layers="+h);
				}
			}

			if(/*!isWater && */(next_z = NcanMoveNext(curr_x, curr_y, curr_z, curr_layers, next_x, next_y, next_layers, temp_layers, false, regionEdge, geoIndex, isPlayer, isWater)) == Integer.MIN_VALUE)
			{
				if(isPlayer && ConfigValue.GeodataDebugMoveList)
					_log.info("GeoEngine: MoveList[return null]");
				return result;
			}
			result[1] = new Location(next_x, next_y, isWater ? Math.min(waterZ, Math.max(__tz, z_p)) : next_z, geo2world);

			short[] t = curr_layers;
			curr_layers = next_layers;
			next_layers = t;

			curr_x = next_x;
			curr_y = next_y;

			int x1 = (curr_x << 4) + L2World.MAP_MIN_X;
			int y1 = (curr_y << 4) + L2World.MAP_MIN_Y;
			if(isPlayer && ConfigValue.GeodataDebugMoveList)
				_log.info("GeoEngine: MoveList["+i+"] -> curr_x="+x1+" curr_y="+y1+" z["+isWater+"]["+curr_z+"]["+next_z+"]["+z_p+"]["+waterZ+"]="+(isWater ? Math.min(waterZ, Math.max(next_z, z_p)) : next_z));

			//if(!isWater)
				curr_z = next_z;
		}
		result[0] = new Location(curr_x, curr_y, isWater ? Math.min(waterZ, Math.max(__tz, z_p)) : curr_z, geo2world);
		return result;
	}

	public static Location MoveLoc(int __x, int __y, int _z, int __tx, int __ty, int __tz, int geoIndex, boolean onlyFullPath, boolean isPlayer, int id, int waterZ, boolean geo2world)
	{
		boolean isWater = (waterZ != Integer.MIN_VALUE);

		int _x = __x - L2World.MAP_MIN_X >> 4;
		int _y = __y - L2World.MAP_MIN_Y >> 4;
		int _tx = __tx - L2World.MAP_MIN_X >> 4;
		int _ty = __ty - L2World.MAP_MIN_Y >> 4;

		waterZ-=ConfigValue.GeoWaterZ;
		int z_p = NgetHeight(_x, _y, _z, geoIndex)-ConfigValue.GeoWaterZ_Diff;

		//waterZ = Math.min(waterZ, Math.max(__tz, z_p));

		int diff_x = _tx - _x; int diff_y = _ty - _y;
		int incx = sign(diff_x); int incy = sign(diff_y);
		boolean overRegionEdge = (_x >> 11 != _tx >> 11) || (_y >> 11 != _ty >> 11);

		if(diff_x < 0)
			diff_x = -diff_x;
		if(diff_y < 0)
			diff_y = -diff_y;
		int el;
		int pdx;
		int pdy;
		int es;

		if(diff_x > diff_y)
		{
			pdx = incx;
			pdy = 0;
			es = diff_y;
			el = diff_x;
		}
		else
		{
			pdx = 0;
			pdy = incy;
			es = diff_x;
			el = diff_y;
		}

		int err = el / 2;

		int curr_x = _x; int curr_y = _y; int curr_z = _z;
		int next_x = curr_x; int next_y = curr_y; int next_z = curr_z;

		short[] next_layers = new short[MAX_LAYERS + 1];
		short[] temp_layers = new short[MAX_LAYERS + 1];
		short[] curr_layers = new short[MAX_LAYERS + 1];

		NGetLayers(curr_x, curr_y, curr_layers, geoIndex);
		if(curr_layers[0] == 0)
			return null;

		int x2 = (curr_x << 4) + L2World.MAP_MIN_X;
		int y2 = (curr_y << 4) + L2World.MAP_MIN_Y;
		if(isPlayer && ConfigValue.GeodataDebugMoveList)
			_log.info("GeoEngine: MoveList[start] -> curr_x="+x2+" curr_y="+y2+" z["+isWater+"]["+curr_z+"]["+z_p+"]["+waterZ+"]="+(isWater ? Math.min(waterZ, Math.max(curr_z, z_p)) : curr_z));

		/*int curr_z = -5528;
		int z_p = -5792;
		int waterZ = -5477;

		Math.min(waterZ, Math.max(curr_z, z_p))
		GeoEngine: MoveList[start] -> curr_x=20957 curr_y=25377 z[true][-5528][-5792][-5477]=-5528
		*/

		for(int i = 0;i < el;i++)
		{
			err -= es;
			if(err < 0)
			{
				err += el;
				next_x += incx;
				next_y += incy;
			}
			else
			{
				next_x += pdx;
				next_y += pdy;
			}
			boolean regionEdge = (overRegionEdge) && ((next_x >> 11 != curr_x >> 11) || (next_y >> 11 != curr_y >> 11));

			NGetLayers(next_x, next_y, next_layers, geoIndex);

			if(isPlayer && ConfigValue.GeodataDebugMoveList2)
			{
				for(short cl : curr_layers)
				{
					int h = (short) ((short) (cl & 0x0fff0) >> 1);
					_log.info("GeoEngine: MoveList -> curr_layers="+h);
				}
				for(short nl : next_layers)
				{
					int h = (short) ((short) (nl & 0x0fff0) >> 1);
					_log.info("GeoEngine: MoveList -> next_layers="+h);
				}
			}

			if(/*!isWater && */(next_z = NcanMoveNext(curr_x, curr_y, curr_z, curr_layers, next_x, next_y, next_layers, temp_layers, false, regionEdge, geoIndex, isPlayer, isWater)) == Integer.MIN_VALUE)
			{
				if(!onlyFullPath)
				{
					if(isPlayer && ConfigValue.GeodataDebugMoveList)
						_log.info("GeoEngine: MoveList[break]");
					break;
				}
				if(isPlayer && ConfigValue.GeodataDebugMoveList)
					_log.info("GeoEngine: MoveList[return null]");
				return null;
			}

			short[] t = curr_layers;
			curr_layers = next_layers;
			next_layers = t;

			curr_x = next_x;
			curr_y = next_y;

			int x1 = (curr_x << 4) + L2World.MAP_MIN_X;
			int y1 = (curr_y << 4) + L2World.MAP_MIN_Y;
			if(isPlayer && ConfigValue.GeodataDebugMoveList)
				_log.info("GeoEngine: MoveList["+i+"] -> curr_x="+x1+" curr_y="+y1+" z["+isWater+"]["+curr_z+"]["+next_z+"]["+z_p+"]["+waterZ+"]="+(isWater ? Math.min(waterZ, Math.max(next_z, z_p)) : next_z));

			//if(!isWater)
				curr_z = next_z;
		}
		return new Location(curr_x, curr_y, isWater ? Math.min(waterZ, Math.max(__tz, z_p)) : curr_z, geo2world);
	}

	/**
	 * Используется только для антипаровоза в AI
	 */
	private static Location MoveCheckForAI(int x, int y, int z, int tx, int ty, int geoIndex)
	{
		int dx = tx - x;
		int dy = ty - y;
		int inc_x = sign(dx);
		int inc_y = sign(dy);
		dx = Math.abs(dx);
		dy = Math.abs(dy);
		if(dx + dy < 2 || dx == 2 && dy == 0 || dx == 0 && dy == 2)
			return new Location(x, y, z).geo2world();
		int prev_x = x;
		int prev_y = y;
		int prev_z = z;
		int next_x = x;
		int next_y = y;
		int next_z = z;
		if(dx >= dy) // dy/dx <= 1
		{
			int delta_A = 2 * dy;
			int d = delta_A - dx;
			int delta_B = delta_A - 2 * dx;
			for(int i = 0; i < dx; i++)
			{
				prev_x = x;
				prev_y = y;
				prev_z = z;
				x = next_x;
				y = next_y;
				z = next_z;
				if(d > 0)
				{
					d += delta_B;
					next_x += inc_x;
					next_y += inc_y;
				}
				else
				{
					d += delta_A;
					next_x += inc_x;
				}
				next_z = NcanMoveNextForAI(x, y, z, next_x, next_y, geoIndex);
				if(next_z == 0)
					return new Location(prev_x, prev_y, prev_z).geo2world();
			}
		}
		else
		{
			int delta_A = 2 * dx;
			int d = delta_A - dy;
			int delta_B = delta_A - 2 * dy;
			for(int i = 0; i < dy; i++)
			{
				prev_x = x;
				prev_y = y;
				prev_z = z;
				x = next_x;
				y = next_y;
				z = next_z;
				if(d > 0)
				{
					d += delta_B;
					next_x += inc_x;
					next_y += inc_y;
				}
				else
				{
					d += delta_A;
					next_y += inc_y;
				}
				next_z = NcanMoveNextForAI(x, y, z, next_x, next_y, geoIndex);
				if(next_z == 0)
					return new Location(prev_x, prev_y, prev_z).geo2world();
			}
		}
		return new Location(next_x, next_y, next_z).geo2world();
	}

	private static boolean NcanMoveNextExCheck(int x, int y, int h, int nextx, int nexty, int hexth, short[] temp_layers, boolean regionEdge, int geoIndex)
	{
		NGetLayers(x, y, temp_layers, geoIndex);
		if(temp_layers[0] == 0)
			return true;

		int temp_layer;
		if((temp_layer = FindNearestLowerLayer(temp_layers, h + ConfigValue.MinLayerHeight, regionEdge, false, false)) == Integer.MIN_VALUE)
			return false;
		short temp_layer_h = (short)((short)(temp_layer & 0x0FFF0) >> 1);
		int maxDeltaZ = regionEdge ? ConfigValue.MaxZDiffEdge : ConfigValue.MaxZDiff;
		if(Math.abs(temp_layer_h - hexth) >= maxDeltaZ || Math.abs(temp_layer_h - h) >= maxDeltaZ)
			return false;
		return checkNSWE((byte)(temp_layer & 0x0F), x, y, nextx, nexty);
	}

	// Возвращает координату Z, по координатам X и Y.
	public static int getNextZ(int _x, int _y)
	{
		short[] next_layers = new short[MAX_LAYERS + 1];
		NGetLayers(_x - L2World.MAP_MIN_X >> 4, _y - L2World.MAP_MIN_Y >> 4, next_layers, 0);
		int next_layer = 0;
		short h, nearest_layer_h = Short.MIN_VALUE;
		for(int i = 1; i <= next_layers[0]; i++)
		{
			h = (short) ((short) (next_layers[i] & 0x0fff0) >> 1);
			if(nearest_layer_h < h)
			{
				nearest_layer_h = h;
				next_layer = next_layers[i];
			}
		}
		return (short) ((short) (next_layer & 0x0fff0) >> 1);
	}

	/**
	 * 
	 * @return возвращает высоту следующего блока, либо Integer.MIN_VALUE если двигатся нельзя
	 */
	public static int NcanMoveNext(int x, int y, int z, short[] layers, int next_x, int next_y, short[] next_layers, short[] temp_layers, boolean withCollision, boolean regionEdge, int geoIndex, boolean isPlayer, boolean isWater)
	{
		if(layers[0] == 0 || next_layers[0] == 0)
			return z;

		int layer, next_layer;
		if((layer = FindNearestLowerLayer(layers, z + ConfigValue.MinLayerHeight, regionEdge, isPlayer, isWater)) == Integer.MIN_VALUE)
		{
			if(isPlayer && ConfigValue.GeodataDebugNcanMoveNext)
				_log.warning("NcanMoveNext: Integer.MIN_VALUE == 1");
			return Integer.MIN_VALUE;
		}

		//----
		short z1 = Short.MIN_VALUE;
		byte NSWE1 = NSWE_ALL;
		for(int i = 1; i <= layers[0]; i++)
		{
			short h = (short) ((short) (layers[i] & 0x0fff0) >> 1);
			if(h < z + ConfigValue.MinLayerHeight && Math.abs(z - z1) > Math.abs(z - h))
			{
				z1 = h;
				NSWE1 = (byte) (layers[i] & 0x0F);
			}
		}
		//----
		
		byte layer_nswe = isWater ? NSWE1 : (byte) (layer & 0x0F);
		if(!checkNSWE(layer_nswe, x, y, next_x, next_y))
		{
			if(isPlayer && ConfigValue.GeodataDebugNcanMoveNext)
			{
				_log.warning("NcanMoveNext: Integer.MIN_VALUE == 2 layer == "+layer+" layer_nswe == "+layer_nswe+" x="+x+" y="+y+" next_x="+next_x+" next_y="+next_y);
				l2open.util.Util.test();
			}
			return Integer.MIN_VALUE;
		}

		short layer_h = (short) ((short) (layer & 0x0fff0) >> 1);
		if((next_layer = FindNearestLowerLayer(next_layers, layer_h + ConfigValue.MinLayerHeight, regionEdge, isPlayer, isWater)) == Integer.MIN_VALUE)
		{
			if(isPlayer && ConfigValue.GeodataDebugNcanMoveNext)
				_log.warning("NcanMoveNext: Integer.MIN_VALUE == 3");
			return Integer.MIN_VALUE;
		}

		short next_layer_h = (short) ((short) (next_layer & 0x0fff0) >> 1);
		/*if(withCollision && next_layer_h + ConfigValue.MaxZDiff < layer_h)
			return Integer.MIN_VALUE;*/

		// если движение не по диагонали
		if(x == next_x || y == next_y)
		{
			if(withCollision)
			{
				short[] heightNSWE = temp_layers;
				if(x == next_x)
				{
					NgetHeightAndNSWE(x - ConfigValue.CollisionSize, y, layer_h, temp_layers, geoIndex);
					if(Math.abs(temp_layers[0] - layer_h) > 15 || !checkNSWE(layer_nswe, x - ConfigValue.CollisionSize, y, x, y) || !checkNSWE((byte) temp_layers[1], x - ConfigValue.CollisionSize, y, x - ConfigValue.CollisionSize, next_y))
					{
						if(isPlayer && ConfigValue.GeodataDebugNcanMoveNext)
							_log.warning("NcanMoveNext: Integer.MIN_VALUE == 4");
						return Integer.MIN_VALUE;
					}

					NgetHeightAndNSWE(x + ConfigValue.CollisionSize, y, layer_h, temp_layers, geoIndex);
					if(Math.abs(temp_layers[0] - layer_h) > 15 || !checkNSWE(layer_nswe, x + ConfigValue.CollisionSize, y, x, y) || !checkNSWE((byte) temp_layers[1], x + ConfigValue.CollisionSize, y, x + ConfigValue.CollisionSize, next_y))
					{
						if(isPlayer && ConfigValue.GeodataDebugNcanMoveNext)
							_log.warning("NcanMoveNext: Integer.MIN_VALUE == 5");
						return Integer.MIN_VALUE;
					}

					return next_layer_h;
				}

				int maxDeltaZ = regionEdge ? ConfigValue.MaxZDiffEdge : ConfigValue.MaxZDiff;
				NgetHeightAndNSWE(x, y - ConfigValue.CollisionSize, layer_h, temp_layers, geoIndex);
				if(Math.abs(temp_layers[0] - layer_h) >= maxDeltaZ || !checkNSWE(layer_nswe, x, y - ConfigValue.CollisionSize, x, y) || !checkNSWE((byte) temp_layers[1], x, y - ConfigValue.CollisionSize, next_x, y - ConfigValue.CollisionSize))
				{
					if(isPlayer && ConfigValue.GeodataDebugNcanMoveNext)
						_log.warning("NcanMoveNext: Integer.MIN_VALUE == 6");
					return Integer.MIN_VALUE;
				}

				NgetHeightAndNSWE(x, y + ConfigValue.CollisionSize, layer_h, temp_layers, geoIndex);
				if(Math.abs(temp_layers[0] - layer_h) >= maxDeltaZ || !checkNSWE(layer_nswe, x, y + ConfigValue.CollisionSize, x, y) || !checkNSWE((byte) temp_layers[1], x, y + ConfigValue.CollisionSize, next_x, y + ConfigValue.CollisionSize))
				{
					if(isPlayer && ConfigValue.GeodataDebugNcanMoveNext)
						_log.warning("NcanMoveNext: Integer.MIN_VALUE == 7");
					return Integer.MIN_VALUE;
				}
			}

			return next_layer_h;
		}

		if(!NcanMoveNextExCheck(x, next_y, layer_h, next_x, next_y, next_layer_h, temp_layers, regionEdge, geoIndex))
		{
			if(isPlayer && ConfigValue.GeodataDebugNcanMoveNext)
				_log.warning("NcanMoveNext: Integer.MIN_VALUE == 8");
			return Integer.MIN_VALUE;
		}
		if(!NcanMoveNextExCheck(next_x, y, layer_h, next_x, next_y, next_layer_h, temp_layers, regionEdge, geoIndex))
		{
			if(isPlayer && ConfigValue.GeodataDebugNcanMoveNext)
				_log.warning("NcanMoveNext: Integer.MIN_VALUE == 9");
			return Integer.MIN_VALUE;
		}

		//FIXME if(withCollision)

		return next_layer_h;
	}

	/**
	 * Используется только для антипаровоза в AI
	 */
	public static int NcanMoveNextForAI(int x, int y, int z, int next_x, int next_y, int geoIndex)
	{
		short[] layers1 = new short[MAX_LAYERS + 1];
		short[] layers2 = new short[MAX_LAYERS + 1];
		NGetLayers(x, y, layers1, geoIndex);
		NGetLayers(next_x, next_y, layers2, geoIndex);

		if(layers1[0] == 0 || layers2[0] == 0)
			return z == 0 ? 1 : z;

		short h;

		short z1 = Short.MIN_VALUE;
		byte NSWE1 = NSWE_ALL;
		for(int i = 1; i <= layers1[0]; i++)
		{
			h = (short) ((short) (layers1[i] & 0x0fff0) >> 1);
			if(Math.abs(z - z1) > Math.abs(z - h))
			{
				z1 = h;
				NSWE1 = (byte) (layers1[i] & 0x0F);
			}
		}

		if(z1 == Short.MIN_VALUE)
			return 0;

		short z2 = Short.MIN_VALUE;
		byte NSWE2 = NSWE_ALL;
		for(int i = 1; i <= layers2[0]; i++)
		{
			h = (short) ((short) (layers2[i] & 0x0fff0) >> 1);
			if(Math.abs(z - z2) > Math.abs(z - h))
			{
				z2 = h;
				NSWE2 = (byte) (layers2[i] & 0x0F);
			}
		}

		if(z2 == Short.MIN_VALUE)
			return 0;

		if(z1 > z2 && z1 - z2 > ConfigValue.MaxZDiff)
			return 0;

		if(!checkNSWE(NSWE1, x, y, next_x, next_y) || !checkNSWE(NSWE2, next_x, next_y, x, y))
			return 0;

		return z2 == 0 ? 1 : z2;
	}

	/**
	 * в нулевую ячейку кладется длина
	 * @param geoX
	 * @param geoY
	 * @param result
	 */
	public static void NGetLayers(int geoX, int geoY, short[] result, int geoIndex)
	{
		NGetLayers(geoX, geoY, result, geoIndex, -1);
	}

	public static void NGetLayers(int geoX, int geoY, short[] result, int geoIndex, int log)
	{
		result[0] = 0;
		byte[] block = getGeoBlockFromGeoCoords(geoX, geoY, geoIndex, log >= 0);
		if(block == null)
		{
			if(log >= 0)
				_log.info("GeoEngine: ->NGetLayers["+log+"]: Error["+geoX+"]["+geoY+"]["+geoIndex+"] 1");
			return;
		}

		int cellX, cellY;
		int index = 0;
		// Read current block type: 0 - flat, 1 - complex, 2 - multilevel
		byte type = block[index];
		index++;

		if(log >= 0)
			_log.info("GeoEngine: ->NGetLayers["+log+"]: type["+type+"]="+(type == 0 ? "BLOCKTYPE_FLAT" : type == 1 ? "BLOCKTYPE_COMPLEX" : "BLOCKTYPE_MULTILEVEL"));

		switch(type)
		{
			case BLOCKTYPE_FLAT:
				short height = makeShort(block[index + 1], block[index]);
				height = (short) (height & 0x0fff0);
				result[0]++;
				result[1] = (short) ((short) (height << 1) | NSWE_ALL);
				return;
			case BLOCKTYPE_COMPLEX:
				cellX = getCell(geoX);
				cellY = getCell(geoY);
				index += (cellX << 3) + cellY << 1;
				height = makeShort(block[index + 1], block[index]);
				result[0]++;
				result[1] = height;
				return;
			case BLOCKTYPE_MULTILEVEL:
				cellX = getCell(geoX);
				cellY = getCell(geoY);
				int offset = (cellX << 3) + cellY;

				if(log >= 0)
					_log.info("GeoEngine: ->NGetLayers["+log+"]0: offset="+offset+" index="+index);
				while(offset > 0)
				{
					byte lc = block[index];
					index += (lc << 1) + 1;
					offset--;
				}
				byte layer_count = block[index];
				if(log >= 0)
					_log.info("GeoEngine: ->NGetLayers["+log+"]1: offset="+offset+" index="+index+" layer_count="+layer_count+" MAX_LAYERS="+MAX_LAYERS);
				index++;
				if(layer_count <= 0 || layer_count > MAX_LAYERS)
					return;
				result[0] = layer_count;
				while(layer_count > 0)
				{
					result[layer_count] = makeShort(block[index + 1], block[index]);
					layer_count--;
					index += 2;
				}
				return;
			default:
				_log.warning("GeoEngine: Unknown block type");
				return;
		}
	}

	private static short NgetType(int geoX, int geoY, int geoIndex)
	{
		byte[] block = getGeoBlockFromGeoCoords(geoX, geoY, geoIndex);

		if(block == null)
			return 0;

		return block[0];
	}

	public static int NgetHeight(int geoX, int geoY, int z, int geoIndex)
	{
		byte[] block = null;
		try
		{
			block = getGeoBlockFromGeoCoords(geoX, geoY, geoIndex);
		}
		catch(NullPointerException e)
		{
			_log.warning("GeoEnogine(1099) getGeoBlockFromGeoCoords(1428) return NPE: geoX=" + geoX+" geoY="+geoY+" z="+z+" geoIndex="+geoIndex);
		}
		if(block == null)
			return z;

		int cellX, cellY, index = 0;

		// Read current block type: 0 - flat, 1 - complex, 2 - multilevel
		byte type = block[index];
		index++;

		short height;
		switch(type)
		{
			case BLOCKTYPE_FLAT:
				height = makeShort(block[index + 1], block[index]);
				return (short) (height & 0x0fff0);
			case BLOCKTYPE_COMPLEX:
				cellX = getCell(geoX);
				cellY = getCell(geoY);
				index += (cellX << 3) + cellY << 1;
				height = makeShort(block[index + 1], block[index]);
				return (short) ((short) (height & 0x0fff0) >> 1); // height / 2
			case BLOCKTYPE_MULTILEVEL:
				cellX = getCell(geoX);
				cellY = getCell(geoY);
				int offset = (cellX << 3) + cellY;
				while(offset > 0)
				{
					byte lc = block[index];
					index += (lc << 1) + 1;
					offset--;
				}
				byte layers = block[index];
				index++;
				if(layers <= 0 || layers > MAX_LAYERS)
					return (short) z;

				int z_nearest_lower_limit = z + ConfigValue.MinLayerHeight;
				int z_nearest_lower = Integer.MIN_VALUE;
				int z_nearest = Integer.MIN_VALUE;

				while(layers > 0)
				{
					height = (short) ((short) (makeShort(block[index + 1], block[index]) & 0x0fff0) >> 1);
					if(height < z_nearest_lower_limit)
						z_nearest_lower = Math.max(z_nearest_lower, height);
					else if(Math.abs(z - height) < Math.abs(z - z_nearest))
						z_nearest = height;
					layers--;
					index += 2;
				}

				return z_nearest_lower != Integer.MIN_VALUE ? z_nearest_lower : z_nearest;
			default:
				_log.warning("GeoEngine: Unknown blockType");
				return z;
		}
	}

	/**
	 * @param geoX позиция геодаты
	 * @param geoY позиция геодаты
	 * @param z координата без изменений
	 * 
	 * @return NSWE: 0-15
	 */
	public static byte NgetNSWE(int geoX, int geoY, int z, int geoIndex)
	{
		byte[] block = getGeoBlockFromGeoCoords(geoX, geoY, geoIndex);

		if(block == null)
			return NSWE_ALL;

		int cellX, cellY;
		int index = 0;

		// Read current block type: 0 - flat, 1 - complex, 2 - multilevel
		byte type = block[index];
		index++;

		switch(type)
		{
			case BLOCKTYPE_FLAT:
				return NSWE_ALL;
			case BLOCKTYPE_COMPLEX:
				cellX = getCell(geoX);
				cellY = getCell(geoY);
				index += (cellX << 3) + cellY << 1;
				short height = makeShort(block[index + 1], block[index]);
				return (byte) (height & 0x0F);
			case BLOCKTYPE_MULTILEVEL:
				cellX = getCell(geoX);
				cellY = getCell(geoY);
				int offset = (cellX << 3) + cellY;
				while(offset > 0)
				{
					byte lc = block[index];
					index += (lc << 1) + 1;
					offset--;
				}
				byte layers = block[index];
				index++;
				if(layers <= 0 || layers > MAX_LAYERS)
					return NSWE_ALL;

				short tempz1 = Short.MIN_VALUE;
				short tempz2 = Short.MIN_VALUE;
				int index_nswe1 = NSWE_NONE;
				int index_nswe2 = NSWE_NONE;
				int z_nearest_lower_limit = z + ConfigValue.MinLayerHeight;

				while(layers > 0)
				{
					height = (short) ((short) (makeShort(block[index + 1], block[index]) & 0x0fff0) >> 1); // height / 2

					if(height < z_nearest_lower_limit)
					{
						if(height > tempz1)
						{
							tempz1 = height;
							index_nswe1 = index;
						}
					}
					else if(Math.abs(z - height) < Math.abs(z - tempz2))
					{
						tempz2 = height;
						index_nswe2 = index;
					}

					layers--;
					index += 2;
				}

				if(index_nswe1 > 0)
					return (byte) (makeShort(block[index_nswe1 + 1], block[index_nswe1]) & 0x0F);
				if(index_nswe2 > 0)
					return (byte) (makeShort(block[index_nswe2 + 1], block[index_nswe2]) & 0x0F);

				return NSWE_ALL;
			default:
				_log.warning("GeoEngine: Unknown block type.");
				return NSWE_ALL;
		}
	}

	public static void NgetHeightAndNSWE(int geoX, int geoY, short z, short[] result, int geoIndex)
	{
		byte[] block = getGeoBlockFromGeoCoords(geoX, geoY, geoIndex);

		if(block == null)
		{
			result[0] = z;
			result[1] = NSWE_ALL;
			return;
		}

		int cellX, cellY, index = 0;
		short height, NSWE = NSWE_ALL;

		// Read current block type: 0 - flat, 1 - complex, 2 - multilevel
		byte type = block[index];
		index++;

		switch(type)
		{
			case BLOCKTYPE_FLAT:
				height = makeShort(block[index + 1], block[index]);
				result[0] = (short) (height & 0x0fff0);
				result[1] = NSWE_ALL;
				return;
			case BLOCKTYPE_COMPLEX:
				cellX = getCell(geoX);
				cellY = getCell(geoY);
				index += (cellX << 3) + cellY << 1;
				height = makeShort(block[index + 1], block[index]);
				result[0] = (short) ((short) (height & 0x0fff0) >> 1); // height / 2
				result[1] = (short) (height & 0x0F);
				return;
			case BLOCKTYPE_MULTILEVEL:
				cellX = getCell(geoX);
				cellY = getCell(geoY);
				int offset = (cellX << 3) + cellY;
				while(offset > 0)
				{
					byte lc = block[index];
					index += (lc << 1) + 1;
					offset--;
				}
				byte layers = block[index];
				index++;
				if(layers <= 0 || layers > MAX_LAYERS)
				{
					result[0] = z;
					result[1] = NSWE_ALL;
					return;
				}

				short tempz1 = Short.MIN_VALUE;
				short tempz2 = Short.MIN_VALUE;
				int index_nswe1 = 0;
				int index_nswe2 = 0;
				int z_nearest_lower_limit = z + ConfigValue.MinLayerHeight;

				while(layers > 0)
				{
					height = (short) ((short) (makeShort(block[index + 1], block[index]) & 0x0fff0) >> 1); // height / 2

					if(height < z_nearest_lower_limit)
					{
						if(height > tempz1)
						{
							tempz1 = height;
							index_nswe1 = index;
						}
					}
					else if(Math.abs(z - height) < Math.abs(z - tempz2))
					{
						tempz2 = height;
						index_nswe2 = index;
					}

					layers--;
					index += 2;
				}

				if(index_nswe1 > 0)
				{
					NSWE = makeShort(block[index_nswe1 + 1], block[index_nswe1]);
					NSWE = (short) (NSWE & 0x0F);
				}
				else if(index_nswe2 > 0)
				{
					NSWE = makeShort(block[index_nswe2 + 1], block[index_nswe2]);
					NSWE = (short) (NSWE & 0x0F);
				}
				result[0] = tempz1 > Short.MIN_VALUE ? tempz1 : tempz2;
				result[1] = NSWE;
				return;
			default:
				_log.warning("GeoEngine: Unknown block type.");
				result[0] = z;
				result[1] = NSWE_ALL;
				return;
		}
	}

	protected static short makeShort(byte b1, byte b0)
	{
		return (short) (b1 << 8 | b0 & 0xff);
	}

	/**
	 * @param geoPos позиция геодаты
	 * 
	 * @return Block Index: 0-255
	 */
	protected static int getBlock(int geoPos)
	{
		return (geoPos >> 3) % 256;
	}

	/**
	 * @param geoPos позиция геодаты
	 * 
	 * @return Cell Index: 0-7
	 */
	protected static int getCell(int geoPos)
	{
		return geoPos % 8;
	}

	/**
	 * Создает индекс блока геодаты по заданым координатам блока.
	 * 
	 * @param blockX блок по geoX
	 * @param blockY блок по geoY
	 * 
	 * @return индекс блока
	 */
	protected static int getBlockIndex(int blockX, int blockY)
	{
		return (blockX << 8) + blockY;
	}

	private static byte sign(int x)
	{
		if(x >= 0)
			return +1;
		return -1;
	}

	/**
	 * Возвращает актуальный блок для текущих геокоординат.<BR>
	 * Является заготовкой для возвращения отдельніх блоков с дверьми
	 * 
	 * @param geoX геокоордината
	 * @param geoY геокоордината
	 * 
	 * @return текущий блок геодаты, или null если нет геодаты.
	 */
	private static byte[] getGeoBlockFromGeoCoords(int geoX, int geoY, int geoIndex)
	{
		return getGeoBlockFromGeoCoords(geoX, geoY, geoIndex, false);
	}
	private static byte[] getGeoBlockFromGeoCoords(int geoX, int geoY, int geoIndex, boolean log)
	{
		if(!GeodataEnabled)
		{
			if(log)
				_log.info("GeoEngine: ->getGeoBlockFromGeoCoords: Error 1");
			return null;
		}
		int ix = geoX >> 11;
		int iy = geoY >> 11;

		if(ix < 0 || ix >= L2World.WORLD_SIZE_X || iy < 0 || iy >= L2World.WORLD_SIZE_Y)
		{
			if(log)
				_log.info("GeoEngine: ->getGeoBlockFromGeoCoords: Error 2");
			return null;
		}

		byte[][][] region = geodata[ix][iy];

		int blockX = getBlock(geoX);
		int blockY = getBlock(geoY);

		int regIndex = 0;
		
		//Рефлект с геодатой
		if((geoIndex & 0x0f000000) == 0x0f000000)
		{
			int x = (geoIndex & 0x00ff0000) >> 16;
			int y = (geoIndex & 0x0000ff00) >> 8;
		
			//Проверяем регион
			if(ix == x && iy == y)
				regIndex = (geoIndex & 0x000000ff);
		}

		// return 0x0f000000 | (ix << 16) | (iy << 8) | regIndex;
		try
		{
			return region[regIndex][getBlockIndex(blockX, blockY)];
		}
		catch(Exception e)
		{
			//_log.warning("GeoEnogine(1433) getGeoBlockFromGeoCoords(1429) return NPE: geoX=" + geoX+" geoY="+geoY+" z="+0+" geoIndex="+geoIndex);
			if(log)
				_log.info("GeoEngine: ->getGeoBlockFromGeoCoords: Error 3");
			return null;
		}
	}

	/**
	 * Загрузка геодаты в память
	 */
	public static void load()
	{
		if(!GeodataEnabled)
			return;
		
		_log.info("GeoEngine: Loading Geodata...");

		File f = new File(ConfigValue.GeodataRoot, "geodata");

		if(!f.exists() || !f.isDirectory())
		{
			_log.info("GeoEngine: Files missing, loading aborted.");
			GeodataEnabled = false;
			ConfigValue.GeodataEnabled = false;
			return;
		}

		int counter = 0;
		Pattern p = Pattern.compile(ConfigValue.GeoFilesPattern);

		for(File q : f.listFiles())
		{
			if(q.isDirectory())
				continue;

			String fn = q.getName();
			Matcher m = p.matcher(fn);
			if(m.matches())
			{
				fn = fn.substring(0, 5); // обрезаем .l2j
				String[] xy = fn.split("_");
				byte rx = Byte.parseByte(xy[0]);
				byte ry = Byte.parseByte(xy[1]);

				LoadGeodataFile(rx, ry);
				LoadGeodata(rx, ry, 0);

				counter++;
			}
		}

		if(counter == 0)
		{
			GeodataEnabled = false;
			ConfigValue.GeodataEnabled = false;
		}

		_log.info("GeoEngine: Loaded " + counter + " map(s), max layers: " + MAX_LAYERS);

		if(ConfigValue.CompactGeoData)
			compact();

		if(ConfigValue.AllowDoors)
			for(L2DoorInstance door : DoorTable.getInstance().getDoors())
				if(!door.isOpen())
				{
					applyGeoCollision(door, 0);
					door.geoOpen = false;
				}
	}

	public static void DumpGeodata(String dir)
	{
		new File(dir).mkdirs();
		for(int mapX = 0; mapX < L2World.WORLD_SIZE_X; mapX++)
			for(int mapY = 0; mapY < L2World.WORLD_SIZE_Y; mapY++)
			{
				if(geodata[mapX][mapY] == null)
					continue;
				int rx = mapX + ConfigValue.GeoFirstX;
				int ry = mapY + ConfigValue.GeoFirstY;
				String fName = dir + "/" + rx + "_" + ry + ".l2j";
				_log.info("Dumping geo: " + fName);
				DumpGeodataFile(fName, (byte) rx, (byte) ry);
			}
	}

	public static boolean DumpGeodataFile(int cx, int cy)
	{
		return DumpGeodataFileMap((byte) (Math.floor((float) cx / (float) 32768) + 20), (byte) (Math.floor((float) cy / (float) 32768) + 18));
	}

	public static boolean DumpGeodataFileMap(byte rx, byte ry)
	{
		String name = "log/" + rx + "_" + ry + ".l2j";
		return DumpGeodataFile(name, rx, ry);
	}

	public static boolean DumpGeodataFile(String name, byte rx, byte ry)
	{
		int ix = rx - ConfigValue.GeoFirstX;
		int iy = ry - ConfigValue.GeoFirstY;

		byte[][] geoblocks = geodata[ix][iy][0];
		if(geoblocks == null)
			return false;

		File f = new File(name);
		if(f.exists())
			f.delete();
		OutputStream os = null;
		try
		{
			os = new BufferedOutputStream(new FileOutputStream(f));
			for(byte[] geoblock : geoblocks)
				os.write(geoblock);
		}
		catch(IOException e)
		{
			_log.warning(""+e);
			return false;
		}
		finally
		{
			if(os != null)
				try
				{
					os.close();
				}
				catch(Exception e)
				{

				}
		}

		return true;
	}

	/**
	 * Загрузка региона геодаты.
	 * 
	 * @param rx регион x
	 * @param ry регион y
	 */
	public static boolean LoadGeodataFile(byte rx, byte ry)
	{
		String fname = "geodata/" + rx + "_" + ry + ".l2j";
		int ix = rx - ConfigValue.GeoFirstX;
		int iy = ry - ConfigValue.GeoFirstY;

		if(ix < 0 || iy < 0 || ix > (L2World.MAP_MAX_X >> 15) + Math.abs(L2World.MAP_MIN_X >> 15) || iy > (L2World.MAP_MAX_Y >> 15) + Math.abs(L2World.MAP_MIN_Y >> 15))
		{
			_log.info("GeoEngine: File " + fname + " was not loaded!!! ");
			return false;
		}

		//_log.info("GeoEngine: Loading: " + fname);

		File geoFile = new File(ConfigValue.GeodataRoot, fname);
		FileChannel roChannel = null;

		try
		{
			roChannel = new RandomAccessFile(geoFile, "r").getChannel();
			long size = roChannel.size();
			MappedByteBuffer buf = roChannel.map(FileChannel.MapMode.READ_ONLY, 0, size);
			buf.order(ByteOrder.LITTLE_ENDIAN);
			rawgeo[ix][iy] = buf;
			
			if(size < BLOCKS_IN_MAP * 3)
				throw new RuntimeException("Invalid geodata : " + fname + "!");
			
			return true;
		}
		catch(IOException e)
		{
			_log.warning(""+e);			
		}
		finally
		{
			try
			{
				if(roChannel != null)
					roChannel.close();
			}
			catch(IOException e2)
			{
				_log.warning(""+e2);			
			}
		}

		return false;
	}

	public static void LoadGeodata(int rx, int ry, int regIndex)
	{
		int ix = rx - ConfigValue.GeoFirstX;
		int iy = ry - ConfigValue.GeoFirstY;
		
		MappedByteBuffer geo = rawgeo[ix][iy];

		int index = 0, orgIndex, block = 0, floor = 0;

		byte[][] blocks;

		synchronized(geodata)
		{
			if((blocks = geodata[ix][iy][regIndex]) == null)
				geodata[ix][iy][regIndex] = (blocks = new byte[BLOCKS_IN_MAP][]); // 256 * 256 блоков в регионе геодаты
		}

		// Indexing geo files, so we will know where each block starts
		for(block = 0; block < BLOCKS_IN_MAP; block++)
		{
			byte type = geo.get(index);
			index++;

			byte[] geoBlock;
			switch(type)
			{
				case BLOCKTYPE_FLAT:

					// Создаем блок геодаты
					geoBlock = new byte[2 + 1];

					// Читаем нужные даные с геодаты
					geoBlock[0] = type;
					geoBlock[1] = geo.get(index);
					geoBlock[2] = geo.get(index + 1);

					// Увеличиваем индекс
					index += 2;

					// Добавляем блок геодаты
					blocks[block] = geoBlock;
					break;

				// else if(type == 0x0040)
				case BLOCKTYPE_COMPLEX:

					// Создаем блок геодаты
					geoBlock = new byte[128 + 1];

					// Читаем данные с геодаты
					geoBlock[0] = type;

					geo.position(index);
					geo.get(geoBlock, 1, 128);

					// Увеличиваем индекс
					index += 128;

					// Добавляем блок геодаты
					blocks[block] = geoBlock;
					break;

				case BLOCKTYPE_MULTILEVEL:
					// Оригинальный индекс
					orgIndex = index;

					// Считаем длину блока геодаты
					for(int b = 0; b < 64; b++)
					{
						byte layers = geo.get(index);
						MAX_LAYERS = Math.max(MAX_LAYERS, layers);
						index += (layers << 1) + 1;
						if(layers > floor)
							floor = layers;
					}

					// Получаем длину
					int diff = index - orgIndex;

					// Создаем массив геодаты
					geoBlock = new byte[diff + 1];

					// Читаем даные с геодаты
					geoBlock[0] = type;

					geo.position(orgIndex);
					geo.get(geoBlock, 1, diff);

					// Добавляем блок геодаты
					blocks[block] = geoBlock;
					break;
				default:
					throw new RuntimeException("Invalid geodata: " + rx + "_" + ry + "!");
			}
		}
	}

	public static int NextGeoIndex(int rx, int ry, int refId)
	{
		return NextGeoIndex(rx, ry, refId, 0);
	}

	public static int NextGeoIndex(int rx, int ry, int refId, int instancedZoneId)
	{
		if (!GeodataEnabled)
			return 0;

		int ix = rx - ConfigValue.GeoFirstX;
		int iy = ry - ConfigValue.GeoFirstY;

		int regIndex = -1;

		synchronized(geodata)
		{
			byte[][][] region = geodata[ix][iy];

			//Ищем свободный блок
			for(int i = 0; i < region.length; i++)
			{
				if(region[i] == null)
				{
					regIndex = i;
					break;
				}
			}

			//Свободного блока нет, создаем новый
			if(regIndex == -1)
			{
				byte[][][] resizedRegion = new byte[(regIndex = region.length) + 1][][];
				for(int i = 0; i < region.length; i++)
					resizedRegion[i] = region[i];
				geodata[ix][iy] = resizedRegion;
			}
			
			LoadGeodata(rx, ry, regIndex);
		}

		Log.add("NextGeoIndex: ["+rx+"_"+ry+"]["+instancedZoneId+"]["+refId+"]["+regIndex+"]["+(0x0f000000 | (ix << 16) | (iy << 8) | regIndex)+"]", "reflection");
		return 0x0f000000 | (ix << 16) | (iy << 8) | regIndex;
	}
	
	/**
	 * Освободить занятый рефлектом индекс геодаты.
	 * 
	 * @param geoIndex
	 */
	public static void FreeGeoIndex(int rx, int ry, int refId, int instancedZoneId, int geoIndex)
	{
		if (!GeodataEnabled)
			return;

		//Рефлект без геодаты
		if((geoIndex & 0x0f000000) != 0x0f000000)
			return;
		
		int ix = (geoIndex & 0x00ff0000) >> 16;
		int iy = (geoIndex & 0x0000ff00) >> 8;
		int regIndex = geoIndex & 0x000000ff;  
		
		synchronized(geodata)
		{
			try
			{
				geodata[ix][iy][regIndex] = null;
			}
			catch(Exception e)
			{
				_log.info("GeoEngine(1792): FreeGeoIndex: ix="+(ix+ConfigValue.GeoFirstX)+" iy="+(iy+ConfigValue.GeoFirstY)+" regIndex="+regIndex+" geoIndex="+geoIndex);
				Util.test();
			}
		}
		Log.add("FreeGeoIndex: ["+rx+"_"+ry+"]["+(ix+ConfigValue.GeoFirstX)+"_"+(iy+ConfigValue.GeoFirstY)+"]["+instancedZoneId+"]["+refId+"]["+regIndex+"]["+geoIndex+"]", "reflection");
	}

	public static void removeGeoCollision(GeoCollision collision, int geoIndex)
	{
		L2Territory shape = collision.getGeoPos();

		byte[][] around = collision.getGeoAround();
		if(around == null)
			throw new RuntimeException("Attempt to remove unitialized collision: " + collision);

		//Размер коллизии в клетках геодаты	
		int minX = shape.getXmin() - L2World.MAP_MIN_X - 16 >> 4;
		int minY = shape.getYmin() - L2World.MAP_MIN_Y - 16 >> 4;
		int minZ = shape.getZmin();
		int maxZ = shape.getZmax();

		short height;
		byte old_nswe;

		for(int gX = 0; gX < around.length; gX++)
			for(int gY = 0; gY < around[gX].length; gY++)
			{
				int geoX = minX + gX;
				int geoY = minY + gY;

				byte[] block = getGeoBlockFromGeoCoords(geoX, geoY, geoIndex);
				if(block == null)
					continue;
				
				int cellX = getCell(geoX);
				int cellY = getCell(geoY);

				int index = 0;
				byte blockType = block[index];
				index++;

				switch(blockType)
				{
					case BLOCKTYPE_FLAT:
					case BLOCKTYPE_COMPLEX:
						index += (cellX << 3) + cellY << 1;

						// Получаем высоту клетки
						height = makeShort(block[index + 1], block[index]);
						old_nswe = (byte) (height & 0x0F);
						height &= 0xfff0;
						height >>= 1;

						// подходящий слой не найден
						if(height < minZ || height > maxZ)
							break;

						// around
						height <<= 1;
						height &= 0xfff0;
						height |= old_nswe;
						if(collision.isConcrete())
							height |= around[gX][gY];
						else
							height &= ~around[gX][gY];

						// Записываем высоту в массив
						block[index + 1] = (byte) (height >> 8);
						block[index] = (byte) (height & 0x00ff);
						break;
					case BLOCKTYPE_MULTILEVEL:
						// Последний валидный индекс для двери
						int neededIndex = -1;

						// Далее следует стандартный механизм получения высоты
						int offset = (cellX << 3) + cellY;
						while(offset > 0)
						{
							byte lc = block[index];
							index += (lc << 1) + 1;
							offset--;
						}
						byte layers = block[index];
						index++;
						if(layers <= 0 || layers > MAX_LAYERS)
							break;
						short temph = Short.MIN_VALUE;
						old_nswe = NSWE_ALL;
						while(layers > 0)
						{
							height = makeShort(block[index + 1], block[index]);
							byte tmp_nswe = (byte) (height & 0x0F);
							height &= 0xfff0;
							height >>= 1;
							int z_diff_last = Math.abs(minZ - temph);
							int z_diff_curr = Math.abs(maxZ - height);
							if(z_diff_last > z_diff_curr)
							{
								old_nswe = tmp_nswe;
								temph = height;
								neededIndex = index;
							}
							layers--;
							index += 2;
						}

						// подходящий слой не найден						
						if(temph == Short.MIN_VALUE || (temph < minZ || temph > maxZ))
							break;

						// around
						temph <<= 1;
						temph &= 0xfff0;
						temph |= old_nswe;
						if(collision.isConcrete())
							temph |= around[gX][gY];
						else
							temph &= ~around[gX][gY];

						// записываем высоту
						block[neededIndex + 1] = (byte) (temph >> 8);
						block[neededIndex] = (byte) (temph & 0x00ff);
						break;
				}
			}
	}

	public static void applyGeoCollision(GeoCollision collision, int geoIndex)
	{
		L2Territory shape = collision.getGeoPos();
		if(shape.getXmax() == shape.getYmax() && shape.getXmax() == 0)
			throw new RuntimeException("Attempt to add incorrect collision: " + collision);
		
		boolean isFirstTime = false;

		//Размер коллизии в клетках геодаты	
		int minX = shape.getXmin() - L2World.MAP_MIN_X - 16 >> 4;
		int maxX = shape.getXmax() - L2World.MAP_MIN_X + 16 >> 4;
		int minY = shape.getYmin() - L2World.MAP_MIN_Y - 16 >> 4;
		int maxY = shape.getYmax() - L2World.MAP_MIN_Y + 16 >> 4;
		int minZ = shape.getZmin();
		int maxZ = shape.getZmax();

		byte[][] around = collision.getGeoAround();
		if(around == null)
		{
			isFirstTime = true;

			//Сформируем коллизию
			byte[][] cells = new byte[maxX - minX + 1][maxY - minY + 1];
			for(int gX = minX; gX <= maxX; gX++)
				for(int gY = minY; gY <= maxY; gY++)
				{
					int x = (gX << 4) + L2World.MAP_MIN_X;
					int y = (gY << 4) + L2World.MAP_MIN_Y;

					loop: for(int ax = x; ax < x + 16; ax++)
						for(int ay = y; ay < y + 16; ay++)
							if(shape.isInside(ax, ay))
							{
								cells[gX - minX][gY - minY] = 1;
								break loop;
							}
				}

			around = new byte[maxX - minX + 1][maxY - minY + 1];
			for(int gX = 0; gX < cells.length; gX++)
				for(int gY = 0; gY < cells[gX].length; gY++)
				{
					if(cells[gX][gY] == 1)
					{
						around[gX][gY] = NSWE_ALL;

						byte _nswe;
						if(gY > 0)
							if(cells[gX][gY - 1] == 0)
							{
								_nswe = around[gX][gY - 1];
								_nswe |= SOUTH;
								around[gX][gY - 1] = _nswe;
							}
						if(gY + 1 < cells[gX].length)
							if(cells[gX][gY + 1] == 0)
							{
								_nswe = around[gX][gY + 1];
								_nswe |= NORTH;
								around[gX][gY + 1] = _nswe;
							}
						if(gX > 0)
							if(cells[gX - 1][gY] == 0)
							{
								_nswe = around[gX - 1][gY];
								_nswe |= EAST;
								around[gX - 1][gY] = _nswe;
							}
						if(gX + 1 < cells.length)
							if(cells[gX + 1][gY] == 0)
							{
								_nswe = around[gX + 1][gY];
								_nswe |= WEST;
								around[gX + 1][gY] = _nswe;
							}
					}
				}

			collision.setGeoAround(around);
		}

		short height;
		byte old_nswe, close_nswe;

		for(int gX = 0; gX < around.length; gX++)
			for(int gY = 0; gY < around[gX].length; gY++)
			{
				int geoX = minX + gX;
				int geoY = minY + gY;

				// Попытка скопировать блок геодаты, если уже существует, то не скопируется
				//TODO: if(first_time)
				//	copyBlock(ix, iy, blockIndex);

				byte[] block = getGeoBlockFromGeoCoords(geoX, geoY, geoIndex);
				if(block == null)
					continue;
				
				int cellX = getCell(geoX);
				int cellY = getCell(geoY);

				int index = 0;
				byte blockType = block[index];
				index++;

				//_log.info("block["+(blockType == 0 ? "BLOCKTYPE_FLAT" : blockType == 1 ? "BLOCKTYPE_COMPLEX" : "BLOCKTYPE_MULTILEVEL")+"]: "+block.length+" cellX="+cellX+" cellY="+cellY);
				switch(blockType)
				{
					case BLOCKTYPE_FLAT:
						height = makeShort(block[index + 1], block[index]);

						// заменяем FLAT блок на COMPLEX
						// ------------------------------------------------------------------------------------------------------
						int ix = geoX >> 11;
						int iy = geoY >> 11;

						byte[][][] region = geodata[ix][iy];

						int blockX = getBlock(geoX);
						int blockY = getBlock(geoY);

						int regIndex = 0;
						
						//Рефлект с геодатой
						if((geoIndex & 0x0f000000) == 0x0f000000)
						{
							int x = (geoIndex & 0x00ff0000) >> 16;
							int y = (geoIndex & 0x0000ff00) >> 8;
						
							//Проверяем регион
							if(ix == x && iy == y)
								regIndex = (geoIndex & 0x000000ff);
						}

						block = (region[regIndex][getBlockIndex(blockX, blockY)] = new byte[128 + 1]);
						block[0] = BLOCKTYPE_COMPLEX;
						// ------------------------------------------------------------------------------------------------------
						// нужно записать высоту полученную с FLAT блока и статус ячеек...

						//_log.info("block[BLOCKTYPE_FLAT]["+height+"]["+(height >> 8)+"]["+(height & 0x00ff)+"]: "+block.length+" cellX="+cellX+" cellY="+cellY);

						height <<= 1;
						height &= 0xfff0;
						height |= NSWE_ALL;
						//_log.info("block[BLOCKTYPE_FLAT]["+height+"]["+(height >> 8)+"]["+(height & 0x00ff)+"]:");

						for(int i=1;i<=128;i=i+2)
						{
							block[i + 1] = (byte) (height >> 8);
							block[i] = (byte) (height & 0x00ff);
						}
						// -----------------------------------------------------
						index += (cellX << 3) + cellY << 1;

						// Получаем высоту клетки
						height = makeShort(block[index + 1], block[index]);
						old_nswe = (byte) (height & 0x0F);
						height &= 0xfff0;
						height >>= 1;
						//_log.info("block[BLOCKTYPE_COMPLEX]["+height+"]["+(height >> 8)+"]["+(height & 0x00ff)+"]:");

						// подходящий слой не найден
						if(height < minZ || height > maxZ)
							break;

						close_nswe = around[gX][gY];

						if(isFirstTime)
						{
							if(collision.isConcrete())
								close_nswe &= old_nswe;
							else
								close_nswe &= ~old_nswe;
							around[gX][gY] = close_nswe;
						}

						// around
						height <<= 1;
						height &= 0xfff0;
						height |= old_nswe;
						//_log.info("block[BLOCKTYPE_COMPLEX]["+height+"]["+(height >> 8)+"]["+(height & 0x00ff)+"]:");

						if(collision.isConcrete())
							height &= ~close_nswe;
						else
							height |= close_nswe;

						// Записываем высоту в массив
						block[index + 1] = (byte) (height >> 8);
						block[index] = (byte) (height & 0x00ff);
						break;
					case BLOCKTYPE_COMPLEX:
						index += (cellX << 3) + cellY << 1;

						// Получаем высоту клетки
						height = makeShort(block[index + 1], block[index]);
						old_nswe = (byte) (height & 0x0F);
						height &= 0xfff0;
						height >>= 1;
						//_log.info("block[BLOCKTYPE_COMPLEX]["+height+"]["+(height >> 8)+"]["+(height & 0x00ff)+"]:");

						// подходящий слой не найден
						if(height < minZ || height > maxZ)
							break;

						close_nswe = around[gX][gY];

						if(isFirstTime)
						{
							if(collision.isConcrete())
								close_nswe &= old_nswe;
							else
								close_nswe &= ~old_nswe;
							around[gX][gY] = close_nswe;
						}

						// around
						height <<= 1;
						height &= 0xfff0;
						height |= old_nswe;
						//_log.info("block[BLOCKTYPE_COMPLEX]["+height+"]["+(height >> 8)+"]["+(height & 0x00ff)+"]:");

						if(collision.isConcrete())
							height &= ~close_nswe;
						else
							height |= close_nswe;

						// Записываем высоту в массив
						block[index + 1] = (byte) (height >> 8);
						block[index] = (byte) (height & 0x00ff);
						break;
					case BLOCKTYPE_MULTILEVEL:
						// Последний валидный индекс для двери
						int neededIndex = -1;

						// Далее следует стандартный механизм получения высоты
						int offset = (cellX << 3) + cellY;
						while(offset > 0)
						{
							byte lc = block[index];
							index += (lc << 1) + 1;
							offset--;
						}
						byte layers = block[index];
						index++;
						if(layers <= 0 || layers > MAX_LAYERS)
							break;
						short temph = Short.MIN_VALUE;
						old_nswe = NSWE_ALL;
						while(layers > 0)
						{
							height = makeShort(block[index + 1], block[index]);
							byte tmp_nswe = (byte) (height & 0x0F);
							height &= 0xfff0;
							height >>= 1;
							int z_diff_last = Math.abs(minZ - temph);
							int z_diff_curr = Math.abs(maxZ - height);
							if(z_diff_last > z_diff_curr)
							{
								old_nswe = tmp_nswe;
								temph = height;
								neededIndex = index;
							}
							layers--;
							index += 2;
						}

						// подходящий слой не найден
						if(temph == Short.MIN_VALUE || (temph < minZ || temph > maxZ))
							break;

						close_nswe = around[gX][gY];

						if(isFirstTime)
						{
							if(collision.isConcrete())
								close_nswe &= old_nswe;
							else
								close_nswe &= ~old_nswe;
							around[gX][gY] = close_nswe;
						}

						// around
						temph <<= 1;
						temph &= 0xfff0;
						temph |= old_nswe;
						if(collision.isConcrete())
							temph &= ~close_nswe;
						else
							temph |= close_nswe;

						// записываем высоту
						block[neededIndex + 1] = (byte) (temph >> 8);
						block[neededIndex] = (byte) (temph & 0x00ff);
						break;
				}
			}
	}

	/**
	 * загружает заранее сгенерированые карты соовпадений в блоках и благодаря им оптимизирует размещение геодаты в памяти
	 * @return количество оптимизированых блоков
	 */
	public static void compact()
	{
		long total = 0, optimized = 0;
		BlockLink[] links;
		byte[][][] link_region;

		for(int mapX = 0; mapX < L2World.WORLD_SIZE_X; mapX++)
			for(int mapY = 0; mapY < L2World.WORLD_SIZE_Y; mapY++)
			{
				if(geodata[mapX][mapY] == null)
					continue;
				total += BLOCKS_IN_MAP;
				links = GeoOptimizer.loadBlockMatches("geodata/matches/" + (mapX + ConfigValue.GeoFirstX) + "_" + (mapY + ConfigValue.GeoFirstY) + ".matches");
				if(links == null)
					continue;
				for(int i = 0; i < links.length; i++)
				{
					link_region = geodata[links[i].linkMapX][links[i].linkMapY];
					if(link_region == null)
						continue;
					link_region[links[i].linkBlockIndex][0] = geodata[mapX][mapY][links[i].blockIndex][0];
					optimized++;
				}
			}

		_log.info(String.format("GeoEngine: - Compacted %d of %d blocks...", optimized, total));
	}

	/**
	 * сравнение двух байт-массивов
	 * @param a1
	 * @param a2
	 * @return
	 */
	public static boolean equalsData(byte[] a1, byte[] a2)
	{
		if(a1.length != a2.length)
			return false;
		for(int i = 0; i < a1.length; i++)
			if(a1[i] != a2[i])
				return false;
		return true;
	}

	/**
	 * сравнение двух блоков геодаты
	 * @param mapX1
	 * @param mapY1
	 * @param blockIndex1
	 * @param mapX2
	 * @param mapY2
	 * @param blockIndex2
	 * @return
	 */
	public static boolean compareGeoBlocks(int mapX1, int mapY1, int blockIndex1, int mapX2, int mapY2, int blockIndex2)
	{
		return equalsData(geodata[mapX1][mapY1][blockIndex1][0], geodata[mapX2][mapY2][blockIndex2][0]);
	}

	private static void initChecksums()
	{
		_log.info("GeoEngine: - Generating Checksums...");
		new File(ConfigValue.GeodataRoot, "geodata/checksum").mkdirs();
		ExecutorService executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
		GeoOptimizer.checkSums = new int[L2World.WORLD_SIZE_X][L2World.WORLD_SIZE_Y][];
		for(int mapX = 0; mapX < L2World.WORLD_SIZE_X; mapX++)
			for(int mapY = 0; mapY < L2World.WORLD_SIZE_Y; mapY++)
				if(geodata[mapX][mapY] != null)
					executor.execute(new GeoOptimizer.CheckSumLoader(mapX, mapY, geodata[mapX][mapY]));
		try
		{
			executor.awaitTermination(Long.MAX_VALUE, TimeUnit.SECONDS);
		}
		catch(InterruptedException e)
		{
			_log.warning(""+e);
		}
	}

	private static void initBlockMatches(int maxScanRegions)
	{
		_log.info("GeoEngine: Generating Block Matches...");
		new File(ConfigValue.GeodataRoot, "geodata/matches").mkdirs();
		ExecutorService executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
		for(int mapX = 0; mapX < L2World.WORLD_SIZE_X; mapX++)
			for(int mapY = 0; mapY < L2World.WORLD_SIZE_Y; mapY++)
				if(geodata[mapX][mapY] != null && GeoOptimizer.checkSums != null && GeoOptimizer.checkSums[mapX][mapY] != null)
					executor.execute(new GeoOptimizer.GeoBlocksMatchFinder(mapX, mapY, maxScanRegions));
		try
		{
			executor.awaitTermination(Long.MAX_VALUE, TimeUnit.SECONDS);
		}
		catch(InterruptedException e)
		{
			_log.warning(""+e);
		}
	}

	public static void deleteChecksumFiles()
	{
		for(int mapX = 0; mapX < L2World.WORLD_SIZE_X; mapX++)
			for(int mapY = 0; mapY < L2World.WORLD_SIZE_Y; mapY++)
			{
				if(geodata[mapX][mapY] == null)
					continue;
				new File(ConfigValue.GeodataRoot, "geodata/checksum/" + (mapX + ConfigValue.GeoFirstX) + "_" + (mapY + ConfigValue.GeoFirstY) + ".crc").delete();
			}
	}

	public static void genBlockMatches(int maxScanRegions)
	{
		initChecksums();
		initBlockMatches(maxScanRegions);
	}

	public static void unload()
	{
		for(int mapX = 0; mapX < L2World.WORLD_SIZE_X; mapX++)
			for(int mapY = 0; mapY < L2World.WORLD_SIZE_Y; mapY++)
				geodata[mapX][mapY] = null;
	}

	public static Location findPointToStay(int x, int y, int z, int radiusmin, int radiusmax, int geoIndex)
	{
		for(int i = 0; i < radiusmax; ++i)
		{
			if(radiusmin > i)
				radiusmin = radiusmin - i;
			else
				radiusmin = 0;

			Location pos = Location.coordsRandomize(x, y, z, 0, radiusmin, radiusmax-i);
			int tempz = getHeight(pos.x, pos.y, pos.z, geoIndex);
			if(Math.abs(pos.z - tempz) >= 200 || getNSWE(pos.x, pos.y, tempz, geoIndex) != 15)
				continue;
			pos.z = tempz;
			return pos;
		}
		return new Location(x, y, z);
	}

	public static Location findPointToStayPet(L2Player activeChar, int radiusmin, int radiusmax, int geoIndex)
	{
		
		for(int i = 0; i < radiusmax; ++i)
		{
			if(radiusmin > i)
				radiusmin = radiusmin - i;
			else
				radiusmin = 0;

			Location pos = Location.coordsRandomize(activeChar.getX(), activeChar.getY(), activeChar.getZ(), 0, radiusmin, radiusmax-i);
			int tempz = getHeight(pos.x, pos.y, pos.z, geoIndex);
			if(Math.abs(pos.z - tempz) >= 50 || getNSWE(pos.x, pos.y, tempz, geoIndex) != 15)
				continue;
			if(!canSeeCoord(activeChar.getX(), activeChar.getY(), activeChar.getZ() + 32, pos.x, pos.y, pos.z + 32, false, activeChar.getReflection().getGeoIndex()) || !canMoveToCoord(activeChar.getX(), activeChar.getY(), activeChar.getZ(), pos.x, pos.y, pos.z, activeChar.getReflection().getGeoIndex()))
				continue;
			pos.z = tempz;
			return pos;
		}
		return new Location(activeChar.getX(), activeChar.getY(), activeChar.getZ());
	}

	public static boolean isTheSameBlock(Location p1, Location p2)
	{
        if(p1 == null || p2 == null)
            return false;

        int _x = p1.x - L2World.MAP_MIN_X >> 4;
        int _y = p1.y - L2World.MAP_MIN_Y >> 4;
        int _tx = p2.x - L2World.MAP_MIN_X >> 4;
        int _ty = p2.y - L2World.MAP_MIN_Y >> 4;
        if(_x == _tx && _y == _ty)
            return true;

        return false;
    }
}