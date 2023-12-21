package l2open.database.schemes.resources;

import l2open.database.DataBaseTable;
import l2open.database.anotations.Column;
import l2open.database.anotations.DATA_TYPE;
import l2open.database.anotations.IS_NULLABLE;
import l2open.database.anotations.Table;

import static l2open.database.schemes.resources.CouplesResource.*;

@Table(
        name = "couples",
        primary_key = {ID},
        fields = {
                @Column(is_null = IS_NULLABLE.NO , type = DATA_TYPE.INT , type_size = 0 , name = ID , defValue = "null"),
                @Column(is_null = IS_NULLABLE.NO , type = DATA_TYPE.INT , type_size = 0 , name = PLAYER1_ID , defValue = "0"),
                @Column(is_null = IS_NULLABLE.NO , type = DATA_TYPE.INT , type_size = 0 , name = PLAYER2_ID , defValue = "0"),
                @Column(is_null = IS_NULLABLE.YES , type = DATA_TYPE.VARCHAR , type_size = 5 , name = MARIED , defValue = "NULL"),
                @Column(is_null = IS_NULLABLE.YES , type = DATA_TYPE.BIGINT , type_size = 0 , name = AFFIANCED_DATE , defValue = "0"),
                @Column(is_null = IS_NULLABLE.YES , type = DATA_TYPE.BIGINT , type_size = 0 , name = WEDDING_DATE , defValue = "0"),
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
        return get(ID);
    }
    public Integer getPlayer1Id() {
        return get(PLAYER1_ID);
    }
    public Integer getPlayer2Id() {
        return get(PLAYER2_ID);
    }
    public String getMaried() {
        return get(MARIED);
    }
    public Long getAffiancedDate() {
        return get(AFFIANCED_DATE);
    }
    public Long getWeddingDate() {
        return get(WEDDING_DATE);
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