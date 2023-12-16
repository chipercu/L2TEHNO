package utils_soft.common.DatabaseResurce.schemes.generate;

import utils_soft.common.DatabaseResurce.anotations.DefValue;
import utils_soft.common.DatabaseResurce.anotations.Field;
import utils_soft.common.DatabaseResurce.anotations.Table;
import utils_soft.common.DatabaseResurce.DataBaseTable;
import static utils_soft.common.DatabaseResurce.schemes.CouplesTable.*;

@Table(
        name = "couples",
        fields = {
                @Field(name = ID,
                @Field(name = PLAYER1ID,
                @Field(name = PLAYER2ID,
                @Field(name = MARIED,
                @Field(name = AFFIANCEDDATE,
                @Field(name = WEDDINGDATE,
        }
)
public class CouplesTable extends DataBaseTable<CouplesTable> {

    public static final String ID = "id";
    public static final String PLAYER1ID = "player1Id";
    public static final String PLAYER2ID = "player2Id";
    public static final String MARIED = "maried";
    public static final String AFFIANCEDDATE = "affiancedDate";
    public static final String WEDDINGDATE = "weddingDate";
    public CouplesTable() {
super(CouplesTable.class);
}
    public IntegergetId() {
        return get(ID, Integer.class);
    }
    public IntegergetPlayer1Id() {
        return get(PLAYER1ID, Integer.class);
    }
    public IntegergetPlayer2Id() {
        return get(PLAYER2ID, Integer.class);
    }
    public StringgetMaried() {
        return get(MARIED, String.class);
    }
    public ObjectgetAffiancedDate() {
        return get(AFFIANCEDDATE, Object.class);
    }
    public ObjectgetWeddingDate() {
        return get(WEDDINGDATE, Object.class);
    }

    public void setId() {
        set(ID, value);
    }
    public void setPlayer1Id() {
        set(PLAYER1ID, value);
    }
    public void setPlayer2Id() {
        set(PLAYER2ID, value);
    }
    public void setMaried() {
        set(MARIED, value);
    }
    public void setAffiancedDate() {
        set(AFFIANCEDDATE, value);
    }
    public void setWeddingDate() {
        set(WEDDINGDATE, value);
    }

}
