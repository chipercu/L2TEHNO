package utils_soft.common.DatabaseResurce.schemes;

import utils_soft.common.DatabaseResurce.anotations.DefValue;
import utils_soft.common.DatabaseResurce.anotations.Field;
import utils_soft.common.DatabaseResurce.anotations.Table;
import utils_soft.common.DatabaseResurce.DataBaseTable;

import static utils_soft.common.DatabaseResurce.schemes.NpcElementTable.*;

@Table(
        name = "npc_element",
        fields = {
                @Field(name = Id_COLUMN),
                @Field(name = AtkElement_COLUMN, defValue = @DefValue(INT = -1)),
                @Field(name = ElemAtkPower_COLUMN),
                @Field(name = FireRes_COLUMN),
                @Field(name = WaterRes_COLUMN),
                @Field(name = WindRes_COLUMN),
                @Field(name = EarthRes_COLUMN),
                @Field(name = HolyRes_COLUMN),
                @Field(name = DarkRes_COLUMN),
        }
)
public class NpcElementTable extends DataBaseTable<NpcElementTable> {

    public static final String Id_COLUMN = "id";
    public static final String AtkElement_COLUMN = "AtkElement";
    public static final String ElemAtkPower_COLUMN = "elemAtkPower";
    public static final String FireRes_COLUMN = "FireRes";
    public static final String WaterRes_COLUMN = "WaterRes";
    public static final String WindRes_COLUMN = "WindRes";
    public static final String EarthRes_COLUMN = "EarthRes";
    public static final String HolyRes_COLUMN = "HolyRes";
    public static final String DarkRes_COLUMN = "DarkRes";

    public NpcElementTable() {
        super(NpcElementTable.class);
    }

    public Integer getId() {
        return get(Id_COLUMN, Integer.class);
    }
    public Integer getAtkElement() {
        return get(AtkElement_COLUMN, Integer.class);
    }
    public Integer getElemAtkPower() {
        return get(ElemAtkPower_COLUMN, Integer.class);
    }

    public Integer getFireRes() {
        return get(FireRes_COLUMN, Integer.class);
    }
    public Integer getWaterRes() {
        return get(WaterRes_COLUMN, Integer.class);
    }
    public Integer getWindRes() {
        return get(WindRes_COLUMN, Integer.class);
    }
    public Integer getEarthRes() {
        return get(EarthRes_COLUMN, Integer.class);
    }
    public Integer getHolyRes() {
        return get(HolyRes_COLUMN, Integer.class);
    }
    public Integer getDarkRes() {
        return get(DarkRes_COLUMN, Integer.class);
    }

    public void setAtkElement(int value) {
        set(AtkElement_COLUMN, value);
    }
    public void setElemAtkPower(int value) {
        set(ElemAtkPower_COLUMN, value);
    }
    public void setFireRes(int value) {
        set(FireRes_COLUMN, value);
    }
    public void setWaterRes(int value) {
        set(WaterRes_COLUMN, value);
    }
    public void setWindRes(int value) {
        set(WindRes_COLUMN, value);
    }
    public void setEarthRes(int value) {
        set(EarthRes_COLUMN, value);
    }
    public void setHolyRes(int value) {
        set(HolyRes_COLUMN, value);
    }
    public void setDarkRes(int value) {
        set(DarkRes_COLUMN, value);
    }

}
