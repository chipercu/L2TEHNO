package utils_soft.NpcEditor;

/**
 * Created by a.kiperku
 * Date: 07.12.2023
 */

public class SpawnModel {
    private String location;
    private int count;
    private int npc_templateid;
    private int locx;
    private int locy;
    private int locz;
    private int heading;
    private int respawn_delay;
    private int respawn_delay_rnd;
    private int loc_id;
    private int baned_loc_id;
    private int periodOfDay;
    private int reflection;
    private int statParam;
    private String aiParam;

    public SpawnModel(String location, int count, int npc_templateid, int locx, int locy, int locz, int heading, int respawn_delay, int respawn_delay_rnd, int loc_id, int baned_loc_id, int periodOfDay, int reflection, int statParam, String aiParam) {
        this.location = location;
        this.count = count;
        this.npc_templateid = npc_templateid;
        this.locx = locx;
        this.locy = locy;
        this.locz = locz;
        this.heading = heading;
        this.respawn_delay = respawn_delay;
        this.respawn_delay_rnd = respawn_delay_rnd;
        this.loc_id = loc_id;
        this.baned_loc_id = baned_loc_id;
        this.periodOfDay = periodOfDay;
        this.reflection = reflection;
        this.statParam = statParam;
        this.aiParam = aiParam;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public int getNpc_templateid() {
        return npc_templateid;
    }

    public void setNpc_templateid(int npc_templateid) {
        this.npc_templateid = npc_templateid;
    }

    public int getLocx() {
        return locx;
    }

    public void setLocx(int locx) {
        this.locx = locx;
    }

    public int getLocy() {
        return locy;
    }

    public void setLocy(int locy) {
        this.locy = locy;
    }

    public int getLocz() {
        return locz;
    }

    public void setLocz(int locz) {
        this.locz = locz;
    }

    public int getHeading() {
        return heading;
    }

    public void setHeading(int heading) {
        this.heading = heading;
    }

    public int getRespawn_delay() {
        return respawn_delay;
    }

    public void setRespawn_delay(int respawn_delay) {
        this.respawn_delay = respawn_delay;
    }

    public int getRespawn_delay_rnd() {
        return respawn_delay_rnd;
    }

    public void setRespawn_delay_rnd(int respawn_delay_rnd) {
        this.respawn_delay_rnd = respawn_delay_rnd;
    }

    public int getLoc_id() {
        return loc_id;
    }

    public void setLoc_id(int loc_id) {
        this.loc_id = loc_id;
    }

    public int getBaned_loc_id() {
        return baned_loc_id;
    }

    public void setBaned_loc_id(int baned_loc_id) {
        this.baned_loc_id = baned_loc_id;
    }

    public int getPeriodOfDay() {
        return periodOfDay;
    }

    public void setPeriodOfDay(int periodOfDay) {
        this.periodOfDay = periodOfDay;
    }

    public int getReflection() {
        return reflection;
    }

    public void setReflection(int reflection) {
        this.reflection = reflection;
    }

    public int getStatParam() {
        return statParam;
    }

    public void setStatParam(int statParam) {
        this.statParam = statParam;
    }

    public String getAiParam() {
        return aiParam;
    }

    public void setAiParam(String aiParam) {
        this.aiParam = aiParam;
    }
}
