package utils_soft.common.DatabaseResurce.schemes.generate;

import utils_soft.common.DatabaseResurce.anotations.DefValue;
import utils_soft.common.DatabaseResurce.anotations.Field;
import utils_soft.common.DatabaseResurce.anotations.Table;
import utils_soft.common.DatabaseResurce.DataBaseTable;
import static utils_soft.common.DatabaseResurce.schemes.generate.MerchantAreasListResource.*;

@Table(
        name = "merchant_areas_list",
        primary_key = {MERCHANT_AREA_ID},
        fields = {
                @Field(name = MERCHANT_AREA_ID , data_type = "int" , defValue = @DefValue(Integer = 0)),
                @Field(name = MERCHANT_AREA_NAME , data_type = "varchar" , defValue = @DefValue(String = "")),
                @Field(name = TAX , data_type = "double" , defValue = @DefValue(Double = 0.00)),
                @Field(name = CHAOTIC , data_type = "int" , defValue = @DefValue(Integer = 0)),
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
