package utils_soft.common.DatabaseResurce.schemes.resources;

import utils_soft.common.DatabaseResurce.anotations.Column;
import utils_soft.common.DatabaseResurce.anotations.Table;
import utils_soft.common.DatabaseResurce.DataBaseTable;
import static utils_soft.common.DatabaseResurce.schemes.resources.CharacterBookmarksResource.*;
import static utils_soft.common.DatabaseResurce.anotations.IS_NULLABLE.*;
import static utils_soft.common.DatabaseResurce.anotations.DATA_TYPE.*;

@Table(
        name = "character_bookmarks",
        primary_key = {CHAR__ID,IDX},
        fields = {
                @Column(is_null = NO , type = INT , type_size = 0 , name = CHAR__ID , defValue = "null"),
                @Column(is_null = NO , type = TINYINT , type_size = 0 , name = IDX , defValue = "null"),
                @Column(is_null = NO , type = VARCHAR , type_size = 32 , name = NAME , defValue = "null"),
                @Column(is_null = NO , type = VARCHAR , type_size = 4 , name = ACRONYM , defValue = "null"),
                @Column(is_null = NO , type = TINYINT , type_size = 0 , name = ICON , defValue = "null"),
                @Column(is_null = NO , type = INT , type_size = 0 , name = X , defValue = "null"),
                @Column(is_null = NO , type = INT , type_size = 0 , name = Y , defValue = "null"),
                @Column(is_null = NO , type = INT , type_size = 0 , name = Z , defValue = "null"),
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
