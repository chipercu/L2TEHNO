package utils_soft.common.DatabaseResurce.schemes.resources;

import utils_soft.common.DatabaseResurce.anotations.Column;
import utils_soft.common.DatabaseResurce.anotations.Table;
import utils_soft.common.DatabaseResurce.DataBaseTable;
import static utils_soft.common.DatabaseResurce.schemes.resources.DoorsResource.*;
import static utils_soft.common.DatabaseResurce.anotations.IS_NULLABLE.*;
import static utils_soft.common.DatabaseResurce.anotations.DATA_TYPE.*;

@Table(
        name = "doors",
        primary_key = {ID},
        fields = {
                @Column(is_null = NO , type = INT , type_size = 0 , name = ID , defValue = "0"),
                @Column(is_null = NO , type = VARCHAR , type_size = 128 , name = NAME , defValue = ""),
                @Column(is_null = NO , type = VARCHAR , type_size = 28 , name = PTS_NAME , defValue = ""),
                @Column(is_null = NO , type = INT , type_size = 0 , name = HP , defValue = "0"),
                @Column(is_null = NO , type = INT , type_size = 0 , name = PDEF , defValue = "0"),
                @Column(is_null = NO , type = INT , type_size = 0 , name = MDEF , defValue = "0"),
                @Column(is_null = NO , type = INT , type_size = 0 , name = UNLOCKABLE , defValue = "0"),
                @Column(is_null = NO , type = INT , type_size = 0 , name = KEY , defValue = "0"),
                @Column(is_null = NO , type = INT , type_size = 0 , name = LEVEL , defValue = "1"),
                @Column(is_null = NO , type = INT , type_size = 0 , name = SHOW_HP , defValue = "0"),
                @Column(is_null = NO , type = INT , type_size = 0 , name = POSX , defValue = "0"),
                @Column(is_null = NO , type = INT , type_size = 0 , name = POSY , defValue = "0"),
                @Column(is_null = NO , type = INT , type_size = 0 , name = POSZ , defValue = "0"),
                @Column(is_null = NO , type = INT , type_size = 0 , name = AX , defValue = "0"),
                @Column(is_null = NO , type = INT , type_size = 0 , name = AY , defValue = "0"),
                @Column(is_null = NO , type = INT , type_size = 0 , name = BX , defValue = "0"),
                @Column(is_null = NO , type = INT , type_size = 0 , name = BY , defValue = "0"),
                @Column(is_null = NO , type = INT , type_size = 0 , name = CX , defValue = "0"),
                @Column(is_null = NO , type = INT , type_size = 0 , name = CY , defValue = "0"),
                @Column(is_null = NO , type = INT , type_size = 0 , name = DX , defValue = "0"),
                @Column(is_null = NO , type = INT , type_size = 0 , name = DY , defValue = "0"),
                @Column(is_null = NO , type = INT , type_size = 0 , name = MINZ , defValue = "0"),
                @Column(is_null = NO , type = INT , type_size = 0 , name = MAXZ , defValue = "0"),
                @Column(is_null = NO , type = ENUM , type_size = 5 , name = SIEGE_WEAPON , defValue = "false"),
                @Column(is_null = NO , type = ENUM , type_size = 5 , name = GEODATA , defValue = "true"),
        }
)
public class DoorsResource extends DataBaseTable<DoorsResource> {

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

    public DoorsResource() {
        super(DoorsResource.class);
}

    public Integer getId() {
        return get(ID);
    }
    public String getName() {
        return get(NAME);
    }
    public String getPtsName() {
        return get(PTS_NAME);
    }
    public Integer getHp() {
        return get(HP);
    }
    public Integer getPdef() {
        return get(PDEF);
    }
    public Integer getMdef() {
        return get(MDEF);
    }
    public Integer getUnlockable() {
        return get(UNLOCKABLE);
    }
    public Integer getKey() {
        return get(KEY);
    }
    public Integer getLevel() {
        return get(LEVEL);
    }
    public Integer getShowHp() {
        return get(SHOW_HP);
    }
    public Integer getPosx() {
        return get(POSX);
    }
    public Integer getPosy() {
        return get(POSY);
    }
    public Integer getPosz() {
        return get(POSZ);
    }
    public Integer getAx() {
        return get(AX);
    }
    public Integer getAy() {
        return get(AY);
    }
    public Integer getBx() {
        return get(BX);
    }
    public Integer getBy() {
        return get(BY);
    }
    public Integer getCx() {
        return get(CX);
    }
    public Integer getCy() {
        return get(CY);
    }
    public Integer getDx() {
        return get(DX);
    }
    public Integer getDy() {
        return get(DY);
    }
    public Integer getMinz() {
        return get(MINZ);
    }
    public Integer getMaxz() {
        return get(MAXZ);
    }
    public Boolean getSiegeWeapon() {
        return get(SIEGE_WEAPON);
    }
    public Boolean getGeodata() {
        return get(GEODATA);
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
