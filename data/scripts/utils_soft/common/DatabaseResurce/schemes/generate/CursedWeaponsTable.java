package utils_soft.common.DatabaseResurce.schemes.generate;

import utils_soft.common.DatabaseResurce.anotations.DefValue;
import utils_soft.common.DatabaseResurce.anotations.Field;
import utils_soft.common.DatabaseResurce.anotations.Table;
import utils_soft.common.DatabaseResurce.DataBaseTable;
import static utils_soft.common.DatabaseResurce.schemes.generate.CursedWeaponsTable.*;

@Table(
        name = "cursed_weapons",
        fields = {
                @Field(name = ITEM_ID),
                @Field(name = PLAYER_ID),
                @Field(name = PLAYER_KARMA),
                @Field(name = PLAYER_PKKILLS),
                @Field(name = NB_KILLS),
                @Field(name = X),
                @Field(name = Y),
                @Field(name = Z),
                @Field(name = END_TIME),
        }
)
public class CursedWeaponsTable extends DataBaseTable<CursedWeaponsTable> {

    public static final String ITEM_ID = "item_id";
    public static final String PLAYER_ID = "player_id";
    public static final String PLAYER_KARMA = "player_karma";
    public static final String PLAYER_PKKILLS = "player_pkkills";
    public static final String NB_KILLS = "nb_kills";
    public static final String X = "x";
    public static final String Y = "y";
    public static final String Z = "z";
    public static final String END_TIME = "end_time";

    public CursedWeaponsTable() {
super(CursedWeaponsTable.class);
}

    public Integer getItemId() {
        return get(ITEM_ID, Integer.class);
    }
    public Integer getPlayerId() {
        return get(PLAYER_ID, Integer.class);
    }
    public Integer getPlayerKarma() {
        return get(PLAYER_KARMA, Integer.class);
    }
    public Integer getPlayerPkkills() {
        return get(PLAYER_PKKILLS, Integer.class);
    }
    public Integer getNbKills() {
        return get(NB_KILLS, Integer.class);
    }
    public Integer getX() {
        return get(X, Integer.class);
    }
    public Integer getY() {
        return get(Y, Integer.class);
    }
    public Integer getZ() {
        return get(Z, Integer.class);
    }
    public Integer getEndTime() {
        return get(END_TIME, Integer.class);
    }

    public void setItemId(Integer value) {
        set(ITEM_ID, value);
    }
    public void setPlayerId(Integer value) {
        set(PLAYER_ID, value);
    }
    public void setPlayerKarma(Integer value) {
        set(PLAYER_KARMA, value);
    }
    public void setPlayerPkkills(Integer value) {
        set(PLAYER_PKKILLS, value);
    }
    public void setNbKills(Integer value) {
        set(NB_KILLS, value);
    }
    public void setX(Integer value) {
        set(X, value);
    }
    public void setY(Integer value) {
        set(Y, value);
    }
    public void setZ(Integer value) {
        set(Z, value);
    }
    public void setEndTime(Integer value) {
        set(END_TIME, value);
    }

}
