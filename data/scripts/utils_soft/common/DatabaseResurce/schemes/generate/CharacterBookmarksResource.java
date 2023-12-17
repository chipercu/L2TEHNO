package utils_soft.common.DatabaseResurce.schemes.generate;

import utils_soft.common.DatabaseResurce.anotations.DefValue;
import utils_soft.common.DatabaseResurce.anotations.Field;
import utils_soft.common.DatabaseResurce.anotations.Table;
import utils_soft.common.DatabaseResurce.DataBaseTable;
import static utils_soft.common.DatabaseResurce.schemes.generate.CharacterBookmarksResource.*;

@Table(
        name = "character_bookmarks",
        primary_key = {CHAR__ID,IDX},
        fields = {
                @Field(name = CHAR__ID , data_type = "int"),
                @Field(name = IDX , data_type = "tinyint"),
                @Field(name = NAME , data_type = "varchar"),
                @Field(name = ACRONYM , data_type = "varchar"),
                @Field(name = ICON , data_type = "tinyint"),
                @Field(name = X , data_type = "int"),
                @Field(name = Y , data_type = "int"),
                @Field(name = Z , data_type = "int"),
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
        return get(CHAR__ID, Integer.class);
    }
    public Integer getIdx() {
        return get(IDX, Integer.class);
    }
    public String getName() {
        return get(NAME, String.class);
    }
    public String getAcronym() {
        return get(ACRONYM, String.class);
    }
    public Integer getIcon() {
        return get(ICON, Integer.class);
    }
    public Integer getX() {
        return get(X, Integer.class);
    }
    public Integer getY() {
        return get(Y, Integer.class);
    }
    public Integer getZ() {
        return get(Z, Integer.class);
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
