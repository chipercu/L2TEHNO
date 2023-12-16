package utils_soft.common.DatabaseResurce.schemes.generate;

import utils_soft.common.DatabaseResurce.anotations.DefValue;
import utils_soft.common.DatabaseResurce.anotations.Field;
import utils_soft.common.DatabaseResurce.anotations.Table;
import utils_soft.common.DatabaseResurce.DataBaseTable;
import static utils_soft.common.DatabaseResurce.schemes.Cursed_weaponsTable.*;

@Table(
        name = "cursed_weapons",
        fields = {
                @Field(name = ITEM_ID,
                @Field(name = PLAYER_ID,
                @Field(name = PLAYER_KARMA,
                @Field(name = PLAYER_PKKILLS,
                @Field(name = NB_KILLS,
                @Field(name = X,
                @Field(name = Y,
                @Field(name = Z,
                @Field(name = END_TIME,
        }
)
public class Cursed_weaponsTable extends DataBaseTable<Cursed_weaponsTable> {

    public static final String ITEM_ID = "item_id";
    public static final String PLAYER_ID = "player_id";
    public static final String PLAYER_KARMA = "player_karma";
    public static final String PLAYER_PKKILLS = "player_pkkills";
    public static final String NB_KILLS = "nb_kills";
    public static final String X = "x";
    public static final String Y = "y";
    public static final String Z = "z";
    public static final String END_TIME = "end_time";
    public Cursed_weaponsTable() {
super(Cursed_weaponsTable.class);
}
    public IntegergetItem_id() {
        return get(ITEM_ID, Integer.class);
    }
    public IntegergetPlayer_id() {
        return get(PLAYER_ID, Integer.class);
    }
    public IntegergetPlayer_karma() {
        return get(PLAYER_KARMA, Integer.class);
    }
    public IntegergetPlayer_pkkills() {
        return get(PLAYER_PKKILLS, Integer.class);
    }
    public IntegergetNb_kills() {
        return get(NB_KILLS, Integer.class);
    }
    public IntegergetX() {
        return get(X, Integer.class);
    }
    public IntegergetY() {
        return get(Y, Integer.class);
    }
    public IntegergetZ() {
        return get(Z, Integer.class);
    }
    public IntegergetEnd_time() {
        return get(END_TIME, Integer.class);
    }

    public void setItem_id() {
        set(ITEM_ID, value);
    }
    public void setPlayer_id() {
        set(PLAYER_ID, value);
    }
    public void setPlayer_karma() {
        set(PLAYER_KARMA, value);
    }
    public void setPlayer_pkkills() {
        set(PLAYER_PKKILLS, value);
    }
    public void setNb_kills() {
        set(NB_KILLS, value);
    }
    public void setX() {
        set(X, value);
    }
    public void setY() {
        set(Y, value);
    }
    public void setZ() {
        set(Z, value);
    }
    public void setEnd_time() {
        set(END_TIME, value);
    }

}
