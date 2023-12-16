package utils_soft.common.DatabaseResurce.schemes.generate;

import utils_soft.common.DatabaseResurce.anotations.DefValue;
import utils_soft.common.DatabaseResurce.anotations.Field;
import utils_soft.common.DatabaseResurce.anotations.Table;
import utils_soft.common.DatabaseResurce.DataBaseTable;
import static utils_soft.common.DatabaseResurce.schemes.DoorsTable.*;

@Table(
        name = "doors",
        fields = {
                @Field(name = ID,
                @Field(name = NAME,
                @Field(name = PTS_NAME,
                @Field(name = HP,
                @Field(name = PDEF,
                @Field(name = MDEF,
                @Field(name = UNLOCKABLE,
                @Field(name = KEY,
                @Field(name = LEVEL,
                @Field(name = SHOWHP,
                @Field(name = POSX,
                @Field(name = POSY,
                @Field(name = POSZ,
                @Field(name = AX,
                @Field(name = AY,
                @Field(name = BX,
                @Field(name = BY,
                @Field(name = CX,
                @Field(name = CY,
                @Field(name = DX,
                @Field(name = DY,
                @Field(name = MINZ,
                @Field(name = MAXZ,
                @Field(name = SIEGE_WEAPON,
                @Field(name = GEODATA,
        }
)
public class DoorsTable extends DataBaseTable<DoorsTable> {

    public static final String ID = "id";
    public static final String NAME = "name";
    public static final String PTS_NAME = "pts_name";
    public static final String HP = "hp";
    public static final String PDEF = "pdef";
    public static final String MDEF = "mdef";
    public static final String UNLOCKABLE = "unlockable";
    public static final String KEY = "key";
    public static final String LEVEL = "level";
    public static final String SHOWHP = "showHp";
    public static final String POSX = "posx";
    public static final String POSY = "posy";
    public static final String POSZ = "posz";
    public static final String AX = "ax";
    public static final String AY = "ay";
    public static final String BX = "bx";
    public static final String BY = "by";
    public static final String CX = "cx";
    public static final String CY = "cy";
    public static final String DX = "dx";
    public static final String DY = "dy";
    public static final String MINZ = "minz";
    public static final String MAXZ = "maxz";
    public static final String SIEGE_WEAPON = "siege_weapon";
    public static final String GEODATA = "geodata";
    public DoorsTable() {
super(DoorsTable.class);
}
    public IntegergetId() {
        return get(ID, Integer.class);
    }
    public StringgetName() {
        return get(NAME, String.class);
    }
    public StringgetPts_name() {
        return get(PTS_NAME, String.class);
    }
    public IntegergetHp() {
        return get(HP, Integer.class);
    }
    public IntegergetPdef() {
        return get(PDEF, Integer.class);
    }
    public IntegergetMdef() {
        return get(MDEF, Integer.class);
    }
    public IntegergetUnlockable() {
        return get(UNLOCKABLE, Integer.class);
    }
    public IntegergetKey() {
        return get(KEY, Integer.class);
    }
    public IntegergetLevel() {
        return get(LEVEL, Integer.class);
    }
    public IntegergetShowHp() {
        return get(SHOWHP, Integer.class);
    }
    public IntegergetPosx() {
        return get(POSX, Integer.class);
    }
    public IntegergetPosy() {
        return get(POSY, Integer.class);
    }
    public IntegergetPosz() {
        return get(POSZ, Integer.class);
    }
    public IntegergetAx() {
        return get(AX, Integer.class);
    }
    public IntegergetAy() {
        return get(AY, Integer.class);
    }
    public IntegergetBx() {
        return get(BX, Integer.class);
    }
    public IntegergetBy() {
        return get(BY, Integer.class);
    }
    public IntegergetCx() {
        return get(CX, Integer.class);
    }
    public IntegergetCy() {
        return get(CY, Integer.class);
    }
    public IntegergetDx() {
        return get(DX, Integer.class);
    }
    public IntegergetDy() {
        return get(DY, Integer.class);
    }
    public IntegergetMinz() {
        return get(MINZ, Integer.class);
    }
    public IntegergetMaxz() {
        return get(MAXZ, Integer.class);
    }
    public ObjectgetSiege_weapon() {
        return get(SIEGE_WEAPON, Object.class);
    }
    public ObjectgetGeodata() {
        return get(GEODATA, Object.class);
    }

    public void setId() {
        set(ID, value);
    }
    public void setName() {
        set(NAME, value);
    }
    public void setPts_name() {
        set(PTS_NAME, value);
    }
    public void setHp() {
        set(HP, value);
    }
    public void setPdef() {
        set(PDEF, value);
    }
    public void setMdef() {
        set(MDEF, value);
    }
    public void setUnlockable() {
        set(UNLOCKABLE, value);
    }
    public void setKey() {
        set(KEY, value);
    }
    public void setLevel() {
        set(LEVEL, value);
    }
    public void setShowHp() {
        set(SHOWHP, value);
    }
    public void setPosx() {
        set(POSX, value);
    }
    public void setPosy() {
        set(POSY, value);
    }
    public void setPosz() {
        set(POSZ, value);
    }
    public void setAx() {
        set(AX, value);
    }
    public void setAy() {
        set(AY, value);
    }
    public void setBx() {
        set(BX, value);
    }
    public void setBy() {
        set(BY, value);
    }
    public void setCx() {
        set(CX, value);
    }
    public void setCy() {
        set(CY, value);
    }
    public void setDx() {
        set(DX, value);
    }
    public void setDy() {
        set(DY, value);
    }
    public void setMinz() {
        set(MINZ, value);
    }
    public void setMaxz() {
        set(MAXZ, value);
    }
    public void setSiege_weapon() {
        set(SIEGE_WEAPON, value);
    }
    public void setGeodata() {
        set(GEODATA, value);
    }

}
