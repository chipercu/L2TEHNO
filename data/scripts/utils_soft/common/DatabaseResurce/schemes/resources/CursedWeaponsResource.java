package utils_soft.common.DatabaseResurce.schemes.resources;

import utils_soft.common.DatabaseResurce.anotations.Column;
import utils_soft.common.DatabaseResurce.anotations.Table;
import utils_soft.common.DatabaseResurce.DataBaseTable;
import static utils_soft.common.DatabaseResurce.schemes.resources.CursedWeaponsResource.*;
import static utils_soft.common.DatabaseResurce.anotations.IS_NULLABLE.*;
import static utils_soft.common.DatabaseResurce.anotations.DATA_TYPE.*;

@Table(
        name = "cursed_weapons",
        primary_key = {ITEM_ID},
        fields = {
                @Column(is_null = NO , type = SMALLINT , type_size = 0 , name = ITEM_ID , defValue = "null"),
                @Column(is_null = NO , type = INT , type_size = 0 , name = PLAYER_ID , defValue = "0"),
                @Column(is_null = NO , type = INT , type_size = 0 , name = PLAYER_KARMA , defValue = "0"),
                @Column(is_null = NO , type = INT , type_size = 0 , name = PLAYER_PKKILLS , defValue = "0"),
                @Column(is_null = NO , type = INT , type_size = 0 , name = NB_KILLS , defValue = "0"),
                @Column(is_null = NO , type = INT , type_size = 0 , name = X , defValue = "0"),
                @Column(is_null = NO , type = INT , type_size = 0 , name = Y , defValue = "0"),
                @Column(is_null = NO , type = INT , type_size = 0 , name = Z , defValue = "0"),
                @Column(is_null = NO , type = INT , type_size = 0 , name = END_TIME , defValue = "0"),
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

    public Integer getItemId() {
        return get(ITEM_ID);
    }
    public Integer getPlayerId() {
        return get(PLAYER_ID);
    }
    public Integer getPlayerKarma() {
        return get(PLAYER_KARMA);
    }
    public Integer getPlayerPkkills() {
        return get(PLAYER_PKKILLS);
    }
    public Integer getNbKills() {
        return get(NB_KILLS);
    }
    public Integer getX() {
        return get(X);
    }
    public Integer getY() {
        return get(Y);
    }
    public Integer getZ() {
        return get(Z);
    }
    public Integer getEndTime() {
        return get(END_TIME);
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
