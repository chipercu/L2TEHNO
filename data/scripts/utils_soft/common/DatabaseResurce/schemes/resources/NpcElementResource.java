package utils_soft.common.DatabaseResurce.schemes.resources;

import utils_soft.common.DatabaseResurce.anotations.Column;
import utils_soft.common.DatabaseResurce.anotations.Table;
import utils_soft.common.DatabaseResurce.DataBaseTable;
import static utils_soft.common.DatabaseResurce.schemes.resources.NpcElementResource.*;
import static utils_soft.common.DatabaseResurce.anotations.IS_NULLABLE.*;
import static utils_soft.common.DatabaseResurce.anotations.DATA_TYPE.*;

@Table(
        name = "npc_element",
        primary_key = {ID},
        fields = {
                @Column(is_null = NO , type = INT , type_size = 0 , name = ID , defValue = "0"),
                @Column(is_null = NO , type = TINYINT , type_size = 0 , name = ATK_ELEMENT , defValue = "-1"),
                @Column(is_null = NO , type = INT , type_size = 0 , name = ELEM_ATK_POWER , defValue = "0"),
                @Column(is_null = NO , type = INT , type_size = 0 , name = FIRE_RES , defValue = "0"),
                @Column(is_null = NO , type = INT , type_size = 0 , name = WATER_RES , defValue = "0"),
                @Column(is_null = NO , type = INT , type_size = 0 , name = WIND_RES , defValue = "0"),
                @Column(is_null = NO , type = INT , type_size = 0 , name = EARTH_RES , defValue = "0"),
                @Column(is_null = NO , type = INT , type_size = 0 , name = HOLY_RES , defValue = "0"),
                @Column(is_null = NO , type = INT , type_size = 0 , name = DARK_RES , defValue = "0"),
        }
)
public class NpcElementResource extends DataBaseTable<NpcElementResource> {

    public static final String ID = "id";
    public static final String ATK_ELEMENT = "AtkElement";
    public static final String ELEM_ATK_POWER = "elemAtkPower";
    public static final String FIRE_RES = "FireRes";
    public static final String WATER_RES = "WaterRes";
    public static final String WIND_RES = "WindRes";
    public static final String EARTH_RES = "EarthRes";
    public static final String HOLY_RES = "HolyRes";
    public static final String DARK_RES = "DarkRes";

    public NpcElementResource() {
        super(NpcElementResource.class);
}

    public Integer getId() {
        return get(ID);
    }
    public Integer getAtkElement() {
        return get(ATK_ELEMENT);
    }
    public Integer getElemAtkPower() {
        return get(ELEM_ATK_POWER);
    }
    public Integer getFireRes() {
        return get(FIRE_RES);
    }
    public Integer getWaterRes() {
        return get(WATER_RES);
    }
    public Integer getWindRes() {
        return get(WIND_RES);
    }
    public Integer getEarthRes() {
        return get(EARTH_RES);
    }
    public Integer getHolyRes() {
        return get(HOLY_RES);
    }
    public Integer getDarkRes() {
        return get(DARK_RES);
    }

    public void setId(Integer value) {
        set(ID, value);
    }
    public void setAtkElement(Integer value) {
        set(ATK_ELEMENT, value);
    }
    public void setElemAtkPower(Integer value) {
        set(ELEM_ATK_POWER, value);
    }
    public void setFireRes(Integer value) {
        set(FIRE_RES, value);
    }
    public void setWaterRes(Integer value) {
        set(WATER_RES, value);
    }
    public void setWindRes(Integer value) {
        set(WIND_RES, value);
    }
    public void setEarthRes(Integer value) {
        set(EARTH_RES, value);
    }
    public void setHolyRes(Integer value) {
        set(HOLY_RES, value);
    }
    public void setDarkRes(Integer value) {
        set(DARK_RES, value);
    }

}
