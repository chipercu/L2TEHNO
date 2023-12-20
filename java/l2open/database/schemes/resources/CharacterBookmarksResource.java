package l2open.database.schemes.resources;

import l2open.database.DataBaseTable;
import l2open.database.anotations.Column;
import l2open.database.anotations.DATA_TYPE;
import l2open.database.anotations.IS_NULLABLE;
import l2open.database.anotations.Table;

import static l2open.database.schemes.resources.CharacterBookmarksResource.*;

@Table(
        name = "character_bookmarks",
        primary_key = {CHAR__ID,IDX},
        fields = {
                @Column(is_null = IS_NULLABLE.NO , type = DATA_TYPE.INT , type_size = 0 , name = CHAR__ID , defValue = "null"),
                @Column(is_null = IS_NULLABLE.NO , type = DATA_TYPE.TINYINT , type_size = 0 , name = IDX , defValue = "null"),
                @Column(is_null = IS_NULLABLE.NO , type = DATA_TYPE.VARCHAR , type_size = 32 , name = NAME , defValue = "null"),
                @Column(is_null = IS_NULLABLE.NO , type = DATA_TYPE.VARCHAR , type_size = 4 , name = ACRONYM , defValue = "null"),
                @Column(is_null = IS_NULLABLE.NO , type = DATA_TYPE.TINYINT , type_size = 0 , name = ICON , defValue = "null"),
                @Column(is_null = IS_NULLABLE.NO , type = DATA_TYPE.INT , type_size = 0 , name = X , defValue = "null"),
                @Column(is_null = IS_NULLABLE.NO , type = DATA_TYPE.INT , type_size = 0 , name = Y , defValue = "null"),
                @Column(is_null = IS_NULLABLE.NO , type = DATA_TYPE.INT , type_size = 0 , name = Z , defValue = "null"),
        }
)
public class CharacterBookmarksResource extends DataBaseTable<CharacterBookmarksResource> {

    public static final String CHAR__ID = "char_Id";
    public static final String IDX = "idx";
    public static final String NAME = "name";
    public static final String ACRONYM = "acronym";
    public static final String ICON = "icon";
    public static final String X = "x";
    public static final String Y = "y";
    public static final String Z = "z";

    public CharacterBookmarksResource() {
        super(CharacterBookmarksResource.class);
}

    public Integer getCharId() {
        return get(CHAR__ID);
    }
    public Integer getIdx() {
        return get(IDX);
    }
    public String getName() {
        return get(NAME);
    }
    public String getAcronym() {
        return get(ACRONYM);
    }
    public Integer getIcon() {
        return get(ICON);
    }
    public Integer getX() {
        return get(X);
    }
    public Integer getY() {
        return get(Y);
    }
    public Integer getZ() {
        return get(Z);
    }

    public void setCharId(Integer value) {
        set(CHAR__ID, value);
    }
    public void setIdx(Integer value) {
        set(IDX, value);
    }
    public void setName(String value) {
        set(NAME, value);
    }
    public void setAcronym(String value) {
        set(ACRONYM, value);
    }
    public void setIcon(Integer value) {
        set(ICON, value);
    }
    public void setX(Integer value) {
        set(X, value);
    }
    public void setY(Integer value) {
        set(Y, value);
    }
    public void setZ(Integer value) {
        set(Z, value);
    }

}
