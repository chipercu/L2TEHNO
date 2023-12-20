package l2open.database.schemes.resources;

import l2open.database.DataBaseTable;
import l2open.database.anotations.Column;
import l2open.database.anotations.DATA_TYPE;
import l2open.database.anotations.IS_NULLABLE;
import l2open.database.anotations.Table;

import static l2open.database.schemes.resources.LocationsResource.*;

@Table(
        name = "locations",
        primary_key = {LOC_ID,LOC_X,LOC_Y},
        fields = {
                @Column(is_null = IS_NULLABLE.NO , type = DATA_TYPE.INT , type_size = 0 , name = LOC_ID , defValue = "0"),
                @Column(is_null = IS_NULLABLE.NO , type = DATA_TYPE.VARCHAR , type_size = 50 , name = NAME , defValue = ""),
                @Column(is_null = IS_NULLABLE.NO , type = DATA_TYPE.INT , type_size = 0 , name = LOC_X , defValue = "0"),
                @Column(is_null = IS_NULLABLE.NO , type = DATA_TYPE.INT , type_size = 0 , name = LOC_Y , defValue = "0"),
                @Column(is_null = IS_NULLABLE.NO , type = DATA_TYPE.INT , type_size = 0 , name = LOC_ZMIN , defValue = "0"),
                @Column(is_null = IS_NULLABLE.NO , type = DATA_TYPE.INT , type_size = 0 , name = LOC_ZMAX , defValue = "0"),
                @Column(is_null = IS_NULLABLE.NO , type = DATA_TYPE.INT , type_size = 0 , name = RADIUS , defValue = "0"),
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
