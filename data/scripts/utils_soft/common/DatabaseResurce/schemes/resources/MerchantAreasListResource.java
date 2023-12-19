package utils_soft.common.DatabaseResurce.schemes.resources;

import utils_soft.common.DatabaseResurce.anotations.Column;
import utils_soft.common.DatabaseResurce.anotations.Table;
import utils_soft.common.DatabaseResurce.DataBaseTable;
import static utils_soft.common.DatabaseResurce.schemes.resources.MerchantAreasListResource.*;
import static utils_soft.common.DatabaseResurce.anotations.IS_NULLABLE.*;
import static utils_soft.common.DatabaseResurce.anotations.DATA_TYPE.*;

@Table(
        name = "merchant_areas_list",
        primary_key = {MERCHANT_AREA_ID},
        fields = {
                @Column(is_null = NO , type = INT , type_size = 0 , name = MERCHANT_AREA_ID , defValue = "0"),
                @Column(is_null = NO , type = VARCHAR , type_size = 25 , name = MERCHANT_AREA_NAME , defValue = ""),
                @Column(is_null = NO , type = DOUBLE , type_size = 0 , name = TAX , defValue = "0.00"),
                @Column(is_null = NO , type = INT , type_size = 0 , name = CHAOTIC , defValue = "0"),
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
