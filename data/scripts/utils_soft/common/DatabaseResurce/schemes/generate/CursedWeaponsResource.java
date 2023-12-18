package utils_soft.common.DatabaseResurce.schemes.generate;

import utils_soft.common.DatabaseResurce.anotations.DefValue;
import utils_soft.common.DatabaseResurce.anotations.Field;
import utils_soft.common.DatabaseResurce.anotations.Table;
import utils_soft.common.DatabaseResurce.DataBaseTable;
import static utils_soft.common.DatabaseResurce.schemes.generate.CursedWeaponsResource.*;
import static utils_soft.common.DatabaseResurce.anotations.DATA_TYPE.*;

@Table(
        name = "cursed_weapons",
        primary_key = {ITEM_ID},
        fields = {
                @Field(name = ITEM_ID , type = SMALLINT , nullable = false , defValue = @DefValue(Integer = null)),
                @Field(name = PLAYER_ID , type = INT , nullable = false , defValue = @DefValue(Integer = 0)),
                @Field(name = PLAYER_KARMA , type = INT , nullable = false , defValue = @DefValue(Integer = 0)),
                @Field(name = PLAYER_PKKILLS , type = INT , nullable = false , defValue = @DefValue(Integer = 0)),
                @Field(name = NB_KILLS , type = INT , nullable = false , defValue = @DefValue(Integer = 0)),
                @Field(name = X , type = INT , nullable = false , defValue = @DefValue(Integer = 0)),
                @Field(name = Y , type = INT , nullable = false , defValue = @DefValue(Integer = 0)),
                @Field(name = Z , type = INT , nullable = false , defValue = @DefValue(Integer = 0)),
                @Field(name = END_TIME , type = INT , nullable = false , defValue = @DefValue(Integer = 0)),
        }
)
public class CursedWeaponsResource extends DataBaseTable<CursedWeaponsResource> {

    public static final String ITEM_ID = "item_id";
    public static final String PLAYER_ID = "player_id";
    public static final String PLAYER_KARMA = "player_karma";
    public static final String PLAYER_PKKILLS = "player_pkkills";
    public static final String NB_KILLS = "nb_kills";
    public static final String X = "x";
    public static final String Y = "y";
    public static final String Z = "z";
    public static final String END_TIME = "end_time";

    public CursedWeaponsResource() {
super(CursedWeaponsResource.class);
}


    public CursedWeaponsResource(String item_id){
        super(CursedWeaponsResource.class);
        getSTAT_SET().set(ITEM_ID, item_id);
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
