package utils_soft.common.DatabaseResurce.schemes.resources;

import utils_soft.common.DatabaseResurce.anotations.Column;
import utils_soft.common.DatabaseResurce.anotations.Table;
import utils_soft.common.DatabaseResurce.DataBaseTable;
import static utils_soft.common.DatabaseResurce.schemes.resources.LocationsResource.*;
import static utils_soft.common.DatabaseResurce.anotations.IS_NULLABLE.*;
import static utils_soft.common.DatabaseResurce.anotations.DATA_TYPE.*;

@Table(
        name = "locations",
        primary_key = {LOC_ID,LOC_X,LOC_Y},
        fields = {
                @Column(is_null = NO , type = INT , type_size = 0 , name = LOC_ID , defValue = "0"),
                @Column(is_null = NO , type = VARCHAR , type_size = 50 , name = NAME , defValue = ""),
                @Column(is_null = NO , type = INT , type_size = 0 , name = LOC_X , defValue = "0"),
                @Column(is_null = NO , type = INT , type_size = 0 , name = LOC_Y , defValue = "0"),
                @Column(is_null = NO , type = INT , type_size = 0 , name = LOC_ZMIN , defValue = "0"),
                @Column(is_null = NO , type = INT , type_size = 0 , name = LOC_ZMAX , defValue = "0"),
                @Column(is_null = NO , type = INT , type_size = 0 , name = RADIUS , defValue = "0"),
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
        return get(LOC_ID);
    }
    public String getName() {
        return get(NAME);
    }
    public Integer getLocX() {
        return get(LOC_X);
    }
    public Integer getLocY() {
        return get(LOC_Y);
    }
    public Integer getLocZmin() {
        return get(LOC_ZMIN);
    }
    public Integer getLocZmax() {
        return get(LOC_ZMAX);
    }
    public Integer getRadius() {
        return get(RADIUS);
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
