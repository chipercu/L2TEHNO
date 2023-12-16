package utils_soft.common.DatabaseResurce.schemes.generate;

import utils_soft.common.DatabaseResurce.anotations.DefValue;
import utils_soft.common.DatabaseResurce.anotations.Field;
import utils_soft.common.DatabaseResurce.anotations.Table;
import utils_soft.common.DatabaseResurce.DataBaseTable;
import static utils_soft.common.DatabaseResurce.schemes.Merchant_areas_listTable.*;

@Table(
        name = "merchant_areas_list",
        fields = {
                @Field(name = MERCHANT_AREA_ID,
                @Field(name = MERCHANT_AREA_NAME,
                @Field(name = TAX,
                @Field(name = CHAOTIC,
        }
)
public class Merchant_areas_listTable extends DataBaseTable<Merchant_areas_listTable> {

    public static final String MERCHANT_AREA_ID = "merchant_area_id";
    public static final String MERCHANT_AREA_NAME = "merchant_area_name";
    public static final String TAX = "tax";
    public static final String CHAOTIC = "Chaotic";
    public Merchant_areas_listTable() {
super(Merchant_areas_listTable.class);
}
    public IntegergetMerchant_area_id() {
        return get(MERCHANT_AREA_ID, Integer.class);
    }
    public StringgetMerchant_area_name() {
        return get(MERCHANT_AREA_NAME, String.class);
    }
    public DoublegetTax() {
        return get(TAX, Double.class);
    }
    public IntegergetChaotic() {
        return get(CHAOTIC, Integer.class);
    }

    public void setMerchant_area_id() {
        set(MERCHANT_AREA_ID, value);
    }
    public void setMerchant_area_name() {
        set(MERCHANT_AREA_NAME, value);
    }
    public void setTax() {
        set(TAX, value);
    }
    public void setChaotic() {
        set(CHAOTIC, value);
    }

}
