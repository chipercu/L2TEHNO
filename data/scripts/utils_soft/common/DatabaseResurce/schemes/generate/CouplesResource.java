package utils_soft.common.DatabaseResurce.schemes.generate;

import utils_soft.common.DatabaseResurce.anotations.DefValue;
import utils_soft.common.DatabaseResurce.anotations.Field;
import utils_soft.common.DatabaseResurce.anotations.Table;
import utils_soft.common.DatabaseResurce.DataBaseTable;
import static utils_soft.common.DatabaseResurce.schemes.generate.CouplesResource.*;

@Table(
        name = "couples",
        primary_key = {ID},
        fields = {
                @Field(name = ID , data_type = "int"),
                @Field(name = PLAYER1_ID , data_type = "int" , defValue = @DefValue(Integer = 0)),
                @Field(name = PLAYER2_ID , data_type = "int" , defValue = @DefValue(Integer = 0)),
                @Field(name = MARIED , data_type = "varchar"),
                @Field(name = AFFIANCED_DATE , data_type = "bigint" , defValue = @DefValue(Long = 0)),
                @Field(name = WEDDING_DATE , data_type = "bigint" , defValue = @DefValue(Long = 0)),
        }
)
public class CouplesResource extends DataBaseTable<CouplesResource> {

    public static final String ID = "id";
    public static final String PLAYER1_ID = "player1Id";
    public static final String PLAYER2_ID = "player2Id";
    public static final String MARIED = "maried";
    public static final String AFFIANCED_DATE = "affiancedDate";
    public static final String WEDDING_DATE = "weddingDate";

    public CouplesResource() {
super(CouplesResource.class);
}

    public Integer getId() {
        return get(ID, Integer.class);
    }
    public Integer getPlayer1Id() {
        return get(PLAYER1_ID, Integer.class);
    }
    public Integer getPlayer2Id() {
        return get(PLAYER2_ID, Integer.class);
    }
    public String getMaried() {
        return get(MARIED, String.class);
    }
    public Long getAffiancedDate() {
        return get(AFFIANCED_DATE, Long.class);
    }
    public Long getWeddingDate() {
        return get(WEDDING_DATE, Long.class);
    }

    public void setId(Integer value) {
        set(ID, value);
    }
    public void setPlayer1Id(Integer value) {
        set(PLAYER1_ID, value);
    }
    public void setPlayer2Id(Integer value) {
        set(PLAYER2_ID, value);
    }
    public void setMaried(String value) {
        set(MARIED, value);
    }
    public void setAffiancedDate(Long value) {
        set(AFFIANCED_DATE, value);
    }
    public void setWeddingDate(Long value) {
        set(WEDDING_DATE, value);
    }

}
