package l2open.gameserver.model;

import l2open.config.ConfigValue;
import l2open.gameserver.ai.DefaultAI;
import l2open.gameserver.model.instances.L2NpcInstance;
import l2open.gameserver.serverpackets.DeleteObject;
import l2open.gameserver.serverpackets.L2GameServerPacket;
import l2open.gameserver.serverpackets.MagicSkillUse;
import l2open.gameserver.tables.SpawnTable;
import l2open.gameserver.tables.TerritoryTable;
import l2open.util.*;
import l2open.util.Location;
import l2open.util.Rnd;

import java.util.logging.Logger;
import java.util.concurrent.locks.*;

/**
 * @author Diamond
 * @Date: 15/5/2007
 * @Time: 10:06:34
 */
public final class L2WorldRegion {
    private static final Logger _log = Logger.getLogger(L2WorldRegion.class.getName());

    private L2Object[] _objects = null;
    private GArray<L2Territory> territories = null;
    //private volatile L2Territory[] territories = new L2Territory[0];

    private int tileX, tileY, tileZ, _objectsSize = 0, _playersSize = 0;
    private boolean _spawned = false;
    private final ReentrantLock objects_lock = new ReentrantLock();
    private final ReentrantLock spawn_lock = new ReentrantLock();
    private final ReentrantLock territories_lock = new ReentrantLock();

    public L2WorldRegion(int pTileX, int pTileY, int pTileZ) {
        tileX = pTileX;
        tileY = pTileY;
        tileZ = pTileZ;
    }

    public String toString() {
        return "L2WorldRegion[" + getX() + "][" + getY() + "][" + getZ() + "][" + _spawned + "][pl=" + _playersSize + ";obj=" + _objectsSize + "]";
    }

    public int getX() {
        return tileX;
    }

    public int getY() {
        return tileY;
    }

    public int getZ() {
        return tileZ;
    }

    private void spawn() {
        if (!_spawned && ConfigValue.DelayedSpawn) {
            spawn_lock.lock();
            try {
                if (_spawned)
                    return;
                _spawned = true;

                for (L2Spawn spawn : SpawnTable.getInstance().getSpawnTable()) {
                    Location loc = null;
                    if (spawn.getLocx() == 0) {
                        if (spawn.getLocation() <= 0) {
                            if (spawn.getLocation2() == null)
                                continue;
                            L2Territory terr = spawn.getLocation2()[Rnd.get(spawn.getLocation2().length)];
                            if (terr == null)
                                continue;
                            spawn.setLocation(terr.getId() * -1);
                            loc = terr.getCenter();
                        }
                        L2Territory terr = TerritoryTable.getInstance().getLocation(spawn.getLocation());
                        if (terr == null)
                            continue;
                        loc = terr.getCenter();
                    } else
                        loc = spawn.getLoc();

                    int x = loc.x / L2World.DIV_BY + L2World.OFFSET_X;
                    int y = loc.y / L2World.DIV_BY + L2World.OFFSET_Y;

                    if (L2World.validRegion(x, y, 0)) {
                        int z = 0;
                        if (L2World.getRegions()[x][y].length > 1) {
                            z = loc.z / L2World.DIV_BY_FOR_Z + L2World.OFFSET_Z;
                            if (!L2World.validRegion(x, y, z))
                                continue;
                        }

                        if (x == tileX && y == tileY && z == tileZ)
                            spawn.init();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                spawn_lock.unlock();
            }
        }
    }

    public void addToPlayers(L2Object object, L2Character dropper) {
        if (_objects == null) {
            _objectsSize = 0;
            _playersSize = 0;
            return;
        }

        L2Player player = null;
        if (object.isPlayer())
            player = (L2Player) object;

        // Если object - игрок, показать ему все видимые обьекты в регионе
        if (player != null) {
            GArray<L2GameServerPacket> result = new GArray<L2GameServerPacket>(_objectsSize);
            for (L2Object obj : getObjectsList(new GArray<L2Object>(_objectsSize), object.getObjectId(), object.getReflection())) {
                // Если это фэйк обсервера - не показывать.
                if (obj.inObserverMode() && !obj.isInOlympiadMode() && (obj.getCurrentRegion() == null || !obj.getCurrentRegion().equals(this)))
                    continue;
                result.addAll(player.addVisibleObject(obj, dropper));
            }
            player.sendPackets(result);
        }

        // Показать обьект всем игрокам в регионе
        for (L2Player pl : getPlayersList(new GArray<L2Player>(_playersSize), object.getObjectId(), object.getReflection(), -1, -1))
            pl.sendPackets(pl.addVisibleObject(object, dropper));
    }

    public void removeFromPlayers(L2Object object, boolean move) {
        if (_objects == null) {
            _objectsSize = 0;
            _playersSize = 0;
            return;
        }

        L2Player player = null;
        if (object.isPlayer())
            player = (L2Player) object;

        // Если object - игрок, убрать у него все видимые обьекты в регионе
        if (player != null) {
            GArray<L2GameServerPacket> result = new GArray<L2GameServerPacket>(_objectsSize);
            for (L2Object obj : getObjectsList(new GArray<L2Object>(_objectsSize), object.getObjectId(), object.getReflection())) {
                L2GameServerPacket p = player.removeVisibleObject(obj, null, move);
                if (p != null)
                    result.add(p);
            }
            player.sendPackets(result);
        }

        GArray<L2Player> p_list = getPlayersList(new GArray<L2Player>(_playersSize), object.getObjectId(), object.getReflection(), -1, -1);
        if (p_list != null && p_list.size() > 0) {
            DeleteObject p = new DeleteObject(object);
            // Убрать обьект у всех игроков в регионе
            for (L2Player pl : p_list) {
                L2GameServerPacket d = pl.removeVisibleObject(object, p, move);
                if (d != null)
                    pl.sendPacket(d);
            }
        }
    }

    public L2Object[] getObjects() {
        objects_lock.lock();
        try {
            if (_objects == null) {
                _objects = new L2Object[50];
                _objectsSize = 0;
                _playersSize = 0;
            }
            return _objects;
        } finally {
            objects_lock.unlock();
        }
    }

    public void addObject(L2Object obj) {
        if (obj == null)
            return;

        objects_lock.lock();
        try {
            if (_objects == null) {
                _objects = new L2Object[50];
                _objectsSize = 0;
            } else if (_objectsSize >= _objects.length) {
                L2Object[] temp = new L2Object[_objects.length * 2];
                for (int i = 0; i < _objectsSize; i++)
                    temp[i] = _objects[i];
                _objects = temp;
            }

            _objects[_objectsSize] = obj;
            _objectsSize++;

            if (obj.isPlayer()) {
                _playersSize++;
                for (L2WorldRegion neighbor : getNeighbors())
                    neighbor.spawn();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            objects_lock.unlock();
        }

        if (obj.isNpc() && obj.getAI() instanceof DefaultAI && obj.getAI().isGlobalAI() && !obj.getAI().isActive())
            obj.getAI().startAITask();
    }

    public void removeObject(L2Object obj, boolean move) {
        if (obj == null)
            return;

        objects_lock.lock();
        try {
            if (_objects == null) {
                _objectsSize = 0;
                _playersSize = 0;
                return;
            }

            if (_objectsSize > 1) {
                int k = -1;
                for (int i = 0; i < _objectsSize; i++)
                    if (_objects[i] == obj) {
                        k = i;
                        break;
                    }
                if (k > -1) {
                    _objects[k] = _objects[_objectsSize - 1];
                    _objects[_objectsSize - 1] = null;
                    _objectsSize--;
                }
            } else if (_objectsSize == 1 && _objects[0] == obj) {
                _objects[0] = null;
                _objects = null;
                _objectsSize = 0;
                _playersSize = 0;
            }

            if (obj.isPlayer()) {
                _playersSize--;
                if (_playersSize <= 0)
                    _playersSize = 0;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            objects_lock.unlock();
        }

        if (!move && obj.isNpc() && obj.getAI() instanceof DefaultAI && !obj.getAI().isGlobalAI())
            obj.getAI().stopAITask();
    }

    public GArray<L2Object> getObjectsList(GArray<L2Object> result, int exclude, Reflection reflection) {
        objects_lock.lock();
        try {
            if (_objects == null || _objectsSize == 0)
                return result;
            for (int i = 0; i < _objectsSize; i++) {
                L2Object obj = _objects[i];
                if (obj != null && obj.getObjectId() != exclude && (reflection.getId() == -1 || obj.getReflection() == reflection))
                    result.add(obj);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            objects_lock.unlock();
        }
        return result;
    }

    public GArray<L2Object> getObjectsList(GArray<L2Object> result, int exclude, Reflection reflection, int x, int y, int z, long sqrad, int height) {
        objects_lock.lock();
        try {
            if (_objects == null || _objectsSize == 0)
                return result;
            for (int i = 0; i < _objectsSize; i++) {
                L2Object obj = _objects[i];
                if (obj == null || obj.getObjectId() == exclude || reflection.getId() != -1 && obj.getReflection() != reflection)
                    continue;
                if (Math.abs(obj.getZ() - z) > height)
                    continue;
                long dx = obj.getX() - x;
                dx *= dx;
                if (dx > sqrad)
                    continue;
                long dy = obj.getY() - y;
                dy *= dy;
                if (dx + dy < sqrad)
                    result.add(obj);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            objects_lock.unlock();
        }
        return result;
    }

    public GArray<L2Character> getCharactersList(GArray<L2Character> result, int exclude, int reflection) {
        objects_lock.lock();
        try {
            if (_objects == null || _objectsSize == 0)
                return result;
            for (int i = 0; i < _objectsSize; i++) {
                L2Object obj = _objects[i];
                if (obj != null && obj.isCharacter() && obj.getObjectId() != exclude && (reflection == -1 || obj.getReflection().getId() == reflection))
                    result.add((L2Character) obj);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            objects_lock.unlock();
        }
        return result;
    }

    public GArray<L2Character> getCharactersList(GArray<L2Character> result, int exclude, int reflection, int x, int y, int z, long sqrad, int height) {
        objects_lock.lock();
        try {
            if (_objects == null || _objectsSize == 0)
                return result;
            for (int i = 0; i < _objectsSize; i++) {
                L2Object obj = _objects[i];
                if (obj == null || !obj.isCharacter() || obj.getObjectId() == exclude || reflection != -1 && obj.getReflection().getId() != reflection)
                    continue;
                if (Math.abs(obj.getZ() - z) > height)
                    continue;
                long dx = obj.getX() - x;
                dx *= dx;
                if (dx > sqrad)
                    continue;
                long dy = obj.getY() - y;
                dy *= dy;
                if (dx + dy < sqrad)
                    result.add((L2Character) obj);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            objects_lock.unlock();
        }
        return result;
    }

    public GArray<L2NpcInstance> getNpcsList(GArray<L2NpcInstance> result, int exclude, int reflection) {
        objects_lock.lock();
        try {
            if (_objects == null || _objectsSize == 0)
                return result;
            for (int i = 0; i < _objectsSize; i++) {
                L2Object obj = _objects[i];
                if (obj != null && obj.isNpc() && obj.getObjectId() != exclude && (reflection == -1 || obj.getReflection().getId() == reflection))
                    result.add((L2NpcInstance) obj);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            objects_lock.unlock();
        }
        return result;
    }

    public GArray<L2NpcInstance> getNpcsList(GArray<L2NpcInstance> result, int exclude, int reflection, int x, int y, int z, long sqrad, int height) {
        objects_lock.lock();
        try {
            if (_objects == null || _objectsSize == 0)
                return result;
            for (int i = 0; i < _objectsSize; i++) {
                L2Object obj = _objects[i];
                if (obj == null || !obj.isNpc() || obj.getObjectId() == exclude || reflection != -1 && obj.getReflection().getId() != reflection)
                    continue;
                if (Math.abs(obj.getZ() - z) > height)
                    continue;
                long dx = obj.getX() - x;
                dx *= dx;
                if (dx > sqrad)
                    continue;
                long dy = obj.getY() - y;
                dy *= dy;
                if (dx + dy < sqrad)
                    result.add((L2NpcInstance) obj);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            objects_lock.unlock();
        }
        return result;
    }

    public GArray<L2Player> getPlayersList(GArray<L2Player> result, int exclude, Reflection reflection, int z, int height) {
        objects_lock.lock();
        try {
            if (_objects == null || _objectsSize == 0)
                return result;
            for (int i = 0; i < _objectsSize; i++) {
                L2Object obj = _objects[i];
                if (obj != null && obj.isPlayer() && obj.getObjectId() != exclude && (reflection.getId() == -1 || obj.getReflection() == reflection) && (height == -1 || Math.abs(obj.getZ() - z) <= height))
                    result.add((L2Player) obj);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            objects_lock.unlock();
        }
        return result;
    }

    public GArray<L2Player> getPlayersList(GArray<L2Player> result, int exclude, Reflection reflection, int x, int y, int z, long sqrad, int height, boolean alowObserve) {
        objects_lock.lock();
        try {
            if (_objects == null || _objectsSize == 0)
                return result;
            for (int i = 0; i < _objectsSize; i++) {
                L2Object obj = _objects[i];
                if (obj == null || !obj.isPlayer() || obj.getObjectId() == exclude || reflection.getId() != -1 && obj.getReflection().getId() != reflection.getId())
                    continue;
                if (alowObserve && ((L2Player) obj).inObserverMode()) {
                    result.add((L2Player) obj);
                    continue;
                }
                if (Math.abs(obj.getZ() - z) > height)
                    continue;
                long dx = obj.getX() - x;
                dx *= dx;
                if (dx > sqrad)
                    continue;
                long dy = obj.getY() - y;
                dy *= dy;
                if (dx + dy < sqrad)
                    result.add((L2Player) obj);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            objects_lock.unlock();
        }
        return result;
    }

    public GArray<L2Playable> getPlayablesList(GArray<L2Playable> result, int exclude, Reflection reflection) {
        objects_lock.lock();
        try {
            if (_objects == null || _objectsSize == 0)
                return result;
            for (int i = 0; i < _objectsSize; i++) {
                L2Object obj = _objects[i];
                if (obj != null && obj.isPlayable() && obj.getObjectId() != exclude && (reflection.getId() == -1 || obj.getReflection() == reflection))
                    result.add((L2Playable) obj);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            objects_lock.unlock();
        }
        return result;
    }

    public GArray<L2Playable> getPlayablesList(GArray<L2Playable> result, int exclude, Reflection reflection, int x, int y, int z, long sqrad, int height) {
        objects_lock.lock();
        try {
            if (_objects == null || _objectsSize == 0)
                return result;
            for (int i = 0; i < _objectsSize; i++) {
                L2Object obj = _objects[i];
                if (obj == null || !obj.isPlayable() || obj.getObjectId() == exclude || reflection.getId() != -1 && obj.getReflection().getId() != reflection.getId())
                    continue;
                if (Math.abs(obj.getZ() - z) > height)
                    continue;
                long dx = obj.getX() - x;
                dx *= dx;
                if (dx > sqrad)
                    continue;
                long dy = obj.getY() - y;
                dy *= dy;
                if (dx + dy < sqrad)
                    result.add((L2Playable) obj);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            objects_lock.unlock();
        }
        return result;
    }

    /**
     * Удаляет весь видимый спаун из региона.
     */
    public void deleteVisibleNpcSpawns() {
        GArray<L2NpcInstance> toRemove = new GArray<L2NpcInstance>(_objectsSize);

        objects_lock.lock();
        try {
            if (_objects != null)
                for (int i = 0; i < _objectsSize; i++) {
                    L2Object obj = _objects[i];
                    if (obj != null && obj.isNpc())
                        toRemove.add((L2NpcInstance) obj);
                }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            objects_lock.unlock();
        }

        for (L2NpcInstance npc : toRemove) {
            L2Spawn spawn = npc.getSpawn();
            if (spawn != null) {
                npc.deleteMe();
                spawn.stopRespawn();
            }
        }
    }

    /**
     * Показывает игроку все видимые обьекты в регионе
     */
    public void showObjectsToPlayer(L2Player player, boolean only_transform) {
        if (player != null && _objects != null) {
            GArray<L2GameServerPacket> result = new GArray<L2GameServerPacket>(_objectsSize);
            for (L2Object obj : getObjectsList(new GArray<L2Object>(_objectsSize), player.getObjectId(), player.getReflection()))
                if (!only_transform || obj.isPlayer() && obj.getPlayer().isTransformed())
                    result.addAll(player.addVisibleObject(obj, null));
            player.sendPackets(result);
        }
    }

    /**
     * Убирает у игрока все видимые обьекты в регионе
     */
    public void removeObjectsFromPlayer(L2Player player) {
        if (player != null && _objects != null) {
            GArray<L2GameServerPacket> result = new GArray<L2GameServerPacket>(_objectsSize);
            for (L2Object obj : getObjectsList(new GArray<L2Object>(_objectsSize), player.getObjectId(), player.getReflection())) {
                L2GameServerPacket p = player.removeVisibleObject(obj, null, true);
                if (p != null)
                    result.add(p);
            }
            player.sendPackets(result);
        }
    }

    /**
     * Убирает обьект у всех игроков в регионе
     */
    public void removePlayerFromOtherPlayers(L2Object object) {
        if (object != null && _objects != null) {

            GArray<L2Player> p_list = getPlayersList(new GArray<L2Player>(_playersSize), object.getObjectId(), object.getReflection(), -1, -1);
            if (p_list != null && p_list.size() > 0) {
                DeleteObject p = new DeleteObject(object);
                for (L2Player pl : p_list) {
                    L2Character flt = pl.getFollowTarget();
                    if (flt != null && flt == object) {
                        pl.setFollowTarget(null);
                        pl.stopMove(true, true);
                    }
                    if (pl.getPet() != null) {
                        flt = pl.getPet().getFollowTarget();
                        if (flt != null && flt == object) {
                            pl.getPet().setFollowTarget(null);
                            pl.getPet().stopMove(true, true);
                        }
                    }
                    L2GameServerPacket d = pl.removeVisibleObject(object, p, true);
                    if (d != null)
                        pl.sendPacket(d);
                }
            }
        }
    }

    public boolean areNeighborsEmpty() {
        if (!isEmpty())
            return false;
        for (L2WorldRegion neighbor : getNeighbors())
            if (!neighbor.isEmpty())
                return false;
        return true;
    }

    public GArray<L2WorldRegion> getNeighbors() {
        return L2World.getNeighbors(tileX, tileY, tileZ);
    }

    public int getObjectsSize() {
        return _objectsSize;
    }

    public int getPlayersSize() {
        return _playersSize;
    }

    public boolean isEmpty() {
        return _playersSize <= 0;
    }

    public boolean isNull() {
        return _objectsSize <= 0;
    }

    public String getName() {
        return "(" + tileX + ", " + tileY + ", " + tileZ + ")";
    }

    public void addTerritory(L2Territory territory) {
        territories_lock.lock();
        try {
            //territories = ArrayUtils.add(territories, territory);
            if (territories == null)
                territories = new GArray<L2Territory>(5);
            if (!territories.contains(territory))
                territories.add(territory);
            else
                _log.info("L2WorldRegion[" + tileX + "][" + tileY + "][" + tileZ + "]: DUBLICAT[" + territory + "] ");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            territories_lock.unlock();
            //_log.info("L2WorldRegion["+tileX+"]["+tileY+"]["+tileZ+"]: addTerritory["+territory+"] ");
        }
        //_log.info("L2WorldRegion["+tileX+"]["+tileY+"]["+tileZ+"]: addTerritory["+territory+"] ");
    }

    public void removeTerritory(L2Territory territory) {
        territories_lock.lock();
        try {
            //territories = ArrayUtils.remove(territories, territory);
            if (territories == null)
                return;
            territories.remove(territory);
            if (territories.isEmpty())
                territories = null;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            territories_lock.unlock();
        }
    }

    public GArray<L2Territory> getTerritories(int x, int y, int z) {
        territories_lock.lock();
        try {
            if (territories == null)
                return null;
            GArray<L2Territory> result = new GArray<L2Territory>(territories.size());
            for (L2Territory terr : territories)
                if (terr != null && terr.isInside(x, y, z))
                    result.add(terr);
            return result.isEmpty() ? null : result;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            territories_lock.unlock();
        }
        return null;
    }

	/*private final Lock lock = new ReentrantLock();
	private volatile L2Zone[] _zones = new L2Zone[0];
	void addZone(L2Zone zone)
	{
		lock.lock();
		try
		{
			_zones = ArrayUtils.add(_zones, zone);
		}
		finally
		{
			lock.unlock();
		}
	}

	void removeZone(L2Zone zone)
	{
		lock.lock();
		try
		{
			_zones = ArrayUtils.remove(_zones, zone);
		}
		finally
		{
			lock.unlock();
		}
	}

	L2Zone[] getZones()
	{
		// Без синхронизации и копирования, т.к. удаление/добавление зон происходит достаточно редко
		return _zones;
	}*/
}