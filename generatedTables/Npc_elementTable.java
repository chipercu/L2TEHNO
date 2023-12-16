package utils_soft.common.DatabaseResurce.schemes.generate;

import utils_soft.common.DatabaseResurce.anotations.DefValue;
import utils_soft.common.DatabaseResurce.anotations.Field;
import utils_soft.common.DatabaseResurce.anotations.Table;
import utils_soft.common.DatabaseResurce.DataBaseTable;
import static utils_soft.common.DatabaseResurce.schemes.Npc_elementTable.*;

@Table(
        name = "npc_element",
        fields = {
                @Field(name = ID,
                @Field(name = ATKELEMENT,
                @Field(name = ELEMATKPOWER,
                @Field(name = FIRERES,
                @Field(name = WATERRES,
                @Field(name = WINDRES,
                @Field(name = EARTHRES,
                @Field(name = HOLYRES,
                @Field(name = DARKRES,
        }
)
public class Npc_elementTable extends DataBaseTable<Npc_elementTable> {

    public static final String ID = "id";
    public static final String ATKELEMENT = "AtkElement";
    public static final String ELEMATKPOWER = "elemAtkPower";
    public static final String FIRERES = "FireRes";
    public static final String WATERRES = "WaterRes";
    public static final String WINDRES = "WindRes";
    public static final String EARTHRES = "EarthRes";
    public static final String HOLYRES = "HolyRes";
    public static final String DARKRES = "DarkRes";
    public Npc_elementTable() {
super(Npc_elementTable.class);
}
    public IntegergetId() {
        return get(ID, Integer.class);
    }
    public IntegergetAtkElement() {
        return get(ATKELEMENT, Integer.class);
    }
    public IntegergetElemAtkPower() {
        return get(ELEMATKPOWER, Integer.class);
    }
    public IntegergetFireRes() {
        return get(FIRERES, Integer.class);
    }
    public IntegergetWaterRes() {
        return get(WATERRES, Integer.class);
    }
    public IntegergetWindRes() {
        return get(WINDRES, Integer.class);
    }
    public IntegergetEarthRes() {
        return get(EARTHRES, Integer.class);
    }
    public IntegergetHolyRes() {
        return get(HOLYRES, Integer.class);
    }
    public IntegergetDarkRes() {
        return get(DARKRES, Integer.class);
    }

    public void setId() {
        set(ID, value);
    }
    public void setAtkElement() {
        set(ATKELEMENT, value);
    }
    public void setElemAtkPower() {
        set(ELEMATKPOWER, value);
    }
    public void setFireRes() {
        set(FIRERES, value);
    }
    public void setWaterRes() {
        set(WATERRES, value);
    }
    public void setWindRes() {
        set(WINDRES, value);
    }
    public void setEarthRes() {
        set(EARTHRES, value);
    }
    public void setHolyRes() {
        set(HOLYRES, value);
    }
    public void setDarkRes() {
        set(DARKRES, value);
    }

}
