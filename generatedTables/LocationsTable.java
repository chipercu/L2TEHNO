package utils_soft.common.DatabaseResurce.schemes.generate;

import utils_soft.common.DatabaseResurce.anotations.DefValue;
import utils_soft.common.DatabaseResurce.anotations.Field;
import utils_soft.common.DatabaseResurce.anotations.Table;
import utils_soft.common.DatabaseResurce.DataBaseTable;
import static utils_soft.common.DatabaseResurce.schemes.LocationsTable.*;

@Table(
        name = "locations",
        fields = {
                @Field(name = LOC_ID,
                @Field(name = NAME,
                @Field(name = LOC_X,
                @Field(name = LOC_Y,
                @Field(name = LOC_ZMIN,
                @Field(name = LOC_ZMAX,
                @Field(name = RADIUS,
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
    public IntegergetLoc_id() {
        return get(LOC_ID, Integer.class);
    }
    public StringgetName() {
        return get(NAME, String.class);
    }
    public IntegergetLoc_x() {
        return get(LOC_X, Integer.class);
    }
    public IntegergetLoc_y() {
        return get(LOC_Y, Integer.class);
    }
    public IntegergetLoc_zmin() {
        return get(LOC_ZMIN, Integer.class);
    }
    public IntegergetLoc_zmax() {
        return get(LOC_ZMAX, Integer.class);
    }
    public IntegergetRadius() {
        return get(RADIUS, Integer.class);
    }

    public void setLoc_id() {
        set(LOC_ID, value);
    }
    public void setName() {
        set(NAME, value);
    }
    public void setLoc_x() {
        set(LOC_X, value);
    }
    public void setLoc_y() {
        set(LOC_Y, value);
    }
    public void setLoc_zmin() {
        set(LOC_ZMIN, value);
    }
    public void setLoc_zmax() {
        set(LOC_ZMAX, value);
    }
    public void setRadius() {
        set(RADIUS, value);
    }

}
