package utils_soft.common.DatabaseResurce.schemes.generate;

import utils_soft.common.DatabaseResurce.anotations.DefValue;
import utils_soft.common.DatabaseResurce.anotations.Field;
import utils_soft.common.DatabaseResurce.anotations.Table;
import utils_soft.common.DatabaseResurce.DataBaseTable;
import static utils_soft.common.DatabaseResurce.schemes.generate.MerchantAreasListTable.*;

@Table(
        name = "merchant_areas_list",
        fields = {
                @Field(name = MERCHANT_AREA_ID),
                @Field(name = MERCHANT_AREA_NAME),
                @Field(name = TAX),
                @Field(name = CHAOTIC),
        }
)
public class MerchantAreasListTable extends DataBaseTable<MerchantAreasListTable> {

    public static final String MERCHANT_AREA_ID = "merchant_area_id";
    public static final String MERCHANT_AREA_NAME = "merchant_area_name";
    public static final String TAX = "tax";
    public static final String CHAOTIC = "Chaotic";

    public MerchantAreasListTable() {
super(MerchantAreasListTable.class);
}

    public Integer getMerchantAreaId() {
        return get(MERCHANT_AREA_ID, Integer.class);
    }
    public String getMerchantAreaName() {
        return get(MERCHANT_AREA_NAME, String.class);
    }
    public Double getTax() {
        return get(TAX, Double.class);
    }
    public Integer getChaotic() {
        return get(CHAOTIC, Integer.class);
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
