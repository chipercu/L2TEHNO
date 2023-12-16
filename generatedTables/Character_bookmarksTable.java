package utils_soft.common.DatabaseResurce.schemes.generate;

import utils_soft.common.DatabaseResurce.anotations.DefValue;
import utils_soft.common.DatabaseResurce.anotations.Field;
import utils_soft.common.DatabaseResurce.anotations.Table;
import utils_soft.common.DatabaseResurce.DataBaseTable;
import static utils_soft.common.DatabaseResurce.schemes.Character_bookmarksTable.*;

@Table(
        name = "character_bookmarks",
        fields = {
                @Field(name = CHAR_ID,
                @Field(name = IDX,
                @Field(name = NAME,
                @Field(name = ACRONYM,
                @Field(name = ICON,
                @Field(name = X,
                @Field(name = Y,
                @Field(name = Z,
        }
)
public class Character_bookmarksTable extends DataBaseTable<Character_bookmarksTable> {

    public static final String CHAR_ID = "char_Id";
    public static final String IDX = "idx";
    public static final String NAME = "name";
    public static final String ACRONYM = "acronym";
    public static final String ICON = "icon";
    public static final String X = "x";
    public static final String Y = "y";
    public static final String Z = "z";
    public Character_bookmarksTable() {
super(Character_bookmarksTable.class);
}
    public IntegergetChar_Id() {
        return get(CHAR_ID, Integer.class);
    }
    public IntegergetIdx() {
        return get(IDX, Integer.class);
    }
    public StringgetName() {
        return get(NAME, String.class);
    }
    public StringgetAcronym() {
        return get(ACRONYM, String.class);
    }
    public IntegergetIcon() {
        return get(ICON, Integer.class);
    }
    public IntegergetX() {
        return get(X, Integer.class);
    }
    public IntegergetY() {
        return get(Y, Integer.class);
    }
    public IntegergetZ() {
        return get(Z, Integer.class);
    }

    public void setChar_Id() {
        set(CHAR_ID, value);
    }
    public void setIdx() {
        set(IDX, value);
    }
    public void setName() {
        set(NAME, value);
    }
    public void setAcronym() {
        set(ACRONYM, value);
    }
    public void setIcon() {
        set(ICON, value);
    }
    public void setX() {
        set(X, value);
    }
    public void setY() {
        set(Y, value);
    }
    public void setZ() {
        set(Z, value);
    }

}
