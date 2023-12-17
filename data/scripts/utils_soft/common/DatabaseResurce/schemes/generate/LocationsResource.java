package utils_soft.common.DatabaseResurce.schemes.generate;

import utils_soft.common.DatabaseResurce.anotations.DefValue;
import utils_soft.common.DatabaseResurce.anotations.Field;
import utils_soft.common.DatabaseResurce.anotations.Table;
import utils_soft.common.DatabaseResurce.DataBaseTable;
import static utils_soft.common.DatabaseResurce.schemes.generate.LocationsResource.*;

@Table(
        name = "locations",
        primary_key = {LOC_ID,LOC_X,LOC_Y},
        fields = {
                @Field(name = LOC_ID , data_type = "int" , defValue = @DefValue(Integer = 0)),
                @Field(name = NAME , data_type = "varchar" , defValue = @DefValue(String = "")),
                @Field(name = LOC_X , data_type = "int" , defValue = @DefValue(Integer = 0)),
                @Field(name = LOC_Y , data_type = "int" , defValue = @DefValue(Integer = 0)),
                @Field(name = LOC_ZMIN , data_type = "int" , defValue = @DefValue(Integer = 0)),
                @Field(name = LOC_ZMAX , data_type = "int" , defValue = @DefValue(Integer = 0)),
                @Field(name = RADIUS , data_type = "int" , defValue = @DefValue(Integer = 0)),
        }
)
public class LocationsResource extends DataBaseTable<LocationsResource> {

    public static final String LOC_ID = "loc_id";
    public static final String NAME = "name";
    public static final String LOC_X = "loc_x";
    public static final String LOC_Y = "loc_y";
    public static final String LOC_ZMIN = "loc_zmin";
    public static final String LOC_ZMAX = "loc_zmax";
    public static final String RADIUS = "radius";

    public LocationsResource() {
super(LocationsResource.class);
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
