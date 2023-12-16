package utils_soft.common.DatabaseResurce.schemes.generate;

import utils_soft.common.DatabaseResurce.anotations.DefValue;
import utils_soft.common.DatabaseResurce.anotations.Field;
import utils_soft.common.DatabaseResurce.anotations.Table;
import utils_soft.common.DatabaseResurce.DataBaseTable;
import static utils_soft.common.DatabaseResurce.schemes.generate.DoorsTable.*;

@Table(
        name = "doors",
        fields = {
                @Field(name = ID),
                @Field(name = NAME),
                @Field(name = PTS_NAME),
                @Field(name = HP),
                @Field(name = PDEF),
                @Field(name = MDEF),
                @Field(name = UNLOCKABLE),
                @Field(name = KEY),
                @Field(name = LEVEL),
                @Field(name = SHOW_HP),
                @Field(name = POSX),
                @Field(name = POSY),
                @Field(name = POSZ),
                @Field(name = AX),
                @Field(name = AY),
                @Field(name = BX),
                @Field(name = BY),
                @Field(name = CX),
                @Field(name = CY),
                @Field(name = DX),
                @Field(name = DY),
                @Field(name = MINZ),
                @Field(name = MAXZ),
                @Field(name = SIEGE_WEAPON),
                @Field(name = GEODATA),
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
    public static final String SHOW_HP = "showHp";
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

    public Integer getId() {
        return get(ID, Integer.class);
    }
    public String getName() {
        return get(NAME, String.class);
    }
    public String getPtsName() {
        return get(PTS_NAME, String.class);
    }
    public Integer getHp() {
        return get(HP, Integer.class);
    }
    public Integer getPdef() {
        return get(PDEF, Integer.class);
    }
    public Integer getMdef() {
        return get(MDEF, Integer.class);
    }
    public Integer getUnlockable() {
        return get(UNLOCKABLE, Integer.class);
    }
    public Integer getKey() {
        return get(KEY, Integer.class);
    }
    public Integer getLevel() {
        return get(LEVEL, Integer.class);
    }
    public Integer getShowHp() {
        return get(SHOW_HP, Integer.class);
    }
    public Integer getPosx() {
        return get(POSX, Integer.class);
    }
    public Integer getPosy() {
        return get(POSY, Integer.class);
    }
    public Integer getPosz() {
        return get(POSZ, Integer.class);
    }
    public Integer getAx() {
        return get(AX, Integer.class);
    }
    public Integer getAy() {
        return get(AY, Integer.class);
    }
    public Integer getBx() {
        return get(BX, Integer.class);
    }
    public Integer getBy() {
        return get(BY, Integer.class);
    }
    public Integer getCx() {
        return get(CX, Integer.class);
    }
    public Integer getCy() {
        return get(CY, Integer.class);
    }
    public Integer getDx() {
        return get(DX, Integer.class);
    }
    public Integer getDy() {
        return get(DY, Integer.class);
    }
    public Integer getMinz() {
        return get(MINZ, Integer.class);
    }
    public Integer getMaxz() {
        return get(MAXZ, Integer.class);
    }
    public Boolean getSiegeWeapon() {
        return get(SIEGE_WEAPON, Boolean.class);
    }
    public Boolean getGeodata() {
        return get(GEODATA, Boolean.class);
    }

    public void setId(Integer value) {
        set(ID, value);
    }
    public void setName(String value) {
        set(NAME, value);
    }
    public void setPtsName(String value) {
        set(PTS_NAME, value);
    }
    public void setHp(Integer value) {
        set(HP, value);
    }
    public void setPdef(Integer value) {
        set(PDEF, value);
    }
    public void setMdef(Integer value) {
        set(MDEF, value);
    }
    public void setUnlockable(Integer value) {
        set(UNLOCKABLE, value);
    }
    public void setKey(Integer value) {
        set(KEY, value);
    }
    public void setLevel(Integer value) {
        set(LEVEL, value);
    }
    public void setShowHp(Integer value) {
        set(SHOW_HP, value);
    }
    public void setPosx(Integer value) {
        set(POSX, value);
    }
    public void setPosy(Integer value) {
        set(POSY, value);
    }
    public void setPosz(Integer value) {
        set(POSZ, value);
    }
    public void setAx(Integer value) {
        set(AX, value);
    }
    public void setAy(Integer value) {
        set(AY, value);
    }
    public void setBx(Integer value) {
        set(BX, value);
    }
    public void setBy(Integer value) {
        set(BY, value);
    }
    public void setCx(Integer value) {
        set(CX, value);
    }
    public void setCy(Integer value) {
        set(CY, value);
    }
    public void setDx(Integer value) {
        set(DX, value);
    }
    public void setDy(Integer value) {
        set(DY, value);
    }
    public void setMinz(Integer value) {
        set(MINZ, value);
    }
    public void setMaxz(Integer value) {
        set(MAXZ, value);
    }
    public void setSiegeWeapon(Boolean value) {
        set(SIEGE_WEAPON, value);
    }
    public void setGeodata(Boolean value) {
        set(GEODATA, value);
    }

}
