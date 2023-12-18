package utils_soft.common.DatabaseResurce.schemes.generate;

import utils_soft.common.DatabaseResurce.anotations.DefValue;
import utils_soft.common.DatabaseResurce.anotations.Field;
import utils_soft.common.DatabaseResurce.anotations.Table;
import utils_soft.common.DatabaseResurce.DataBaseTable;
import static utils_soft.common.DatabaseResurce.schemes.generate.NpcElementResource.*;
import static utils_soft.common.DatabaseResurce.anotations.DATA_TYPE.*;

@Table(
        name = "npc_element",
        primary_key = {ID},
        fields = {
                @Field(name = ID , type = INT , nullable = false , defValue = @DefValue(Integer = 0)),
                @Field(name = ATK_ELEMENT , type = TINYINT , nullable = false , defValue = @DefValue(Integer = -1)),
                @Field(name = ELEM_ATK_POWER , type = INT , nullable = false , defValue = @DefValue(Integer = 0)),
                @Field(name = FIRE_RES , type = INT , nullable = false , defValue = @DefValue(Integer = 0)),
                @Field(name = WATER_RES , type = INT , nullable = false , defValue = @DefValue(Integer = 0)),
                @Field(name = WIND_RES , type = INT , nullable = false , defValue = @DefValue(Integer = 0)),
                @Field(name = EARTH_RES , type = INT , nullable = false , defValue = @DefValue(Integer = 0)),
                @Field(name = HOLY_RES , type = INT , nullable = false , defValue = @DefValue(Integer = 0)),
                @Field(name = DARK_RES , type = INT , nullable = false , defValue = @DefValue(Integer = 0)),
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


    public NpcElementResource(String id){
        super(NpcElementResource.class);
        getSTAT_SET().set(ID, id);
    }

    public Integer getId() {
        return get(ID, Integer.class);
    }
    public Integer getAtkElement() {
        return get(ATK_ELEMENT, Integer.class);
    }
    public Integer getElemAtkPower() {
        return get(ELEM_ATK_POWER, Integer.class);
    }
    public Integer getFireRes() {
        return get(FIRE_RES, Integer.class);
    }
    public Integer getWaterRes() {
        return get(WATER_RES, Integer.class);
    }
    public Integer getWindRes() {
        return get(WIND_RES, Integer.class);
    }
    public Integer getEarthRes() {
        return get(EARTH_RES, Integer.class);
    }
    public Integer getHolyRes() {
        return get(HOLY_RES, Integer.class);
    }
    public Integer getDarkRes() {
        return get(DARK_RES, Integer.class);
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
