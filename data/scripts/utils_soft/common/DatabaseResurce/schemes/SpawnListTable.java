package utils_soft.common.DatabaseResurce.schemes;

import utils_soft.common.DatabaseResurce.DataBaseTable;

/**
 * Created by a.kiperku
 * Date: 07.12.2023
 */

public class SpawnListTable extends DataBaseTable<SpawnListTable> {

    public SpawnListTable() {
        super(SpawnListTable.class);
    }

    public String getLocation() {
        return getStat("location", "");
    }

    public int getCount() {
        return getStat("count", 1);
    }

    public int getNpcTemplateId() {
        return getStat("npc_templateid", 0);
    }

    public int getLocX() {
        return getStat("locx", 0);
    }

    public int getLocY() {
        return getStat("locy", 0);
    }

    public int getLocZ() {
        return getStat("locz", 0);
    }

    public int getHeading() {
        return getStat("heading", 0);
    }

    public int getRespawnDelay() {
        return getStat("respawn_delay", 0);
    }

    public int getRespawnDelayRnd() {
        return getStat("respawn_delay_rnd", 0);
    }

    public String getLocId() {
        return getStat("loc_id", "0");
    }

    public String getBanedLocId() {
        return getStat("baned_loc_id", "0");
    }

    public int getPeriodOfDay() {
        return getStat("periodOfDay", 0);
    }

    public int getReflection() {
        return getStat("reflection", 0);
    }

    public String getStatParam() {
        return getStat("statParam", "-1");
    }

    public String getAiParam() {
        return getStat("aiParam", "-1");
    }

}
