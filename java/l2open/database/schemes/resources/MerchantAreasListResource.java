package l2open.database.schemes.resources;

import l2open.database.DataBaseTable;
import l2open.database.anotations.Column;
import l2open.database.anotations.DATA_TYPE;
import l2open.database.anotations.IS_NULLABLE;
import l2open.database.anotations.Table;

import static l2open.database.schemes.resources.MerchantAreasListResource.*;

@Table(
        name = "merchant_areas_list",
        primary_key = {MERCHANT_AREA_ID},
        fields = {
                @Column(is_null = IS_NULLABLE.NO , type = DATA_TYPE.INT , type_size = 0 , name = MERCHANT_AREA_ID , defValue = "0"),
                @Column(is_null = IS_NULLABLE.NO , type = DATA_TYPE.VARCHAR , type_size = 25 , name = MERCHANT_AREA_NAME , defValue = ""),
                @Column(is_null = IS_NULLABLE.NO , type = DATA_TYPE.DOUBLE , type_size = 0 , name = TAX , defValue = "0.00"),
                @Column(is_null = IS_NULLABLE.NO , type = DATA_TYPE.INT , type_size = 0 , name = CHAOTIC , defValue = "0"),
        }
)
public class MerchantAreasListResource extends DataBaseTable<MerchantAreasListResource> {

    public static final String MERCHANT_AREA_ID = "merchant_area_id";
    public static final String MERCHANT_AREA_NAME = "merchant_area_name";
    public static final String TAX = "tax";
    public static final String CHAOTIC = "Chaotic";

    public MerchantAreasListResource() {
        super(MerchantAreasListResource.class);
}

    public Integer getMerchantAreaId() {
        return get(MERCHANT_AREA_ID);
    }
    public String getMerchantAreaName() {
        return get(MERCHANT_AREA_NAME);
    }
    public Double getTax() {
        return get(TAX);
    }
    public Integer getChaotic() {
        return get(CHAOTIC);
    }

    public void setMerchantAreaId(Integer value) {
        set(MERCHANT_AREA_ID, value);
    }
    public void setMerchantAreaName(String value) {
        set(MERCHANT_AREA_NAME, value);
    }
    public void setTax(Double value) {
        set(TAX, value);
    }
    public void setChaotic(Integer value) {
        set(CHAOTIC, value);
    }

}
