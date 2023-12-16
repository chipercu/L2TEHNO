package utils_soft.common.DatabaseResurce.schemes.generate;

import utils_soft.common.DatabaseResurce.anotations.DefValue;
import utils_soft.common.DatabaseResurce.anotations.Field;
import utils_soft.common.DatabaseResurce.anotations.Table;
import utils_soft.common.DatabaseResurce.DataBaseTable;
import static utils_soft.common.DatabaseResurce.schemes.generate.LocationsTable.*;

@Table(
        name = "locations",
        fields = {
                @Field(name = LOC_ID),
                @Field(name = NAME),
                @Field(name = LOC_X),
                @Field(name = LOC_Y),
                @Field(name = LOC_ZMIN),
                @Field(name = LOC_ZMAX),
                @Field(name = RADIUS),
        }
)
public class LocationsTable extends DataBaseTable<LocationsTable> {

    public static final String LOC_ID = "loc_id";
    public static final String NAME = "name";
    public static final String LOC_X = "loc_x";
    public static final String LOC_Y = "loc_y";
    public static final String LOC_ZMIN = "loc_zmin";
    public static final String LOC_ZMAX = "loc_zmax";
    public static final String RADIUS = "radius";

    public LocationsTable() {
super(LocationsTable.class);
}

    public Integer getLocId() {
        return get(LOC_ID, Integer.class);
    }
    public String getName() {
        return get(NAME, String.class);
    }
    public Integer getLocX() {
        return get(LOC_X, Integer.class);
    }
    public Integer getLocY() {
        return get(LOC_Y, Integer.class);
    }
    public Integer getLocZmin() {
        return get(LOC_ZMIN, Integer.class);
    }
    public Integer getLocZmax() {
        return get(LOC_ZMAX, Integer.class);
    }
    public Integer getRadius() {
        return get(RADIUS, Integer.class);
    }

    public void setLocId(Integer value) {
        set(LOC_ID, value);
    }
    public void setName(String value) {
        set(NAME, value);
    }
    public void setLocX(Integer value) {
        set(LOC_X, value);
    }
    public void setLocY(Integer value) {
        set(LOC_Y, value);
    }
    public void setLocZmin(Integer value) {
        set(LOC_ZMIN, value);
    }
    public void setLocZmax(Integer value) {
        set(LOC_ZMAX, value);
    }
    public void setRadius(Integer value) {
        set(RADIUS, value);
    }

}
