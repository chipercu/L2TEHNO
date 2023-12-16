package utils_soft.common.DatabaseResurce.schemes.generate;

import utils_soft.common.DatabaseResurce.anotations.DefValue;
import utils_soft.common.DatabaseResurce.anotations.Field;
import utils_soft.common.DatabaseResurce.anotations.Table;
import utils_soft.common.DatabaseResurce.DataBaseTable;
import static utils_soft.common.DatabaseResurce.schemes.Character_friendsTable.*;

@Table(
        name = "character_friends",
        fields = {
                @Field(name = CHAR_ID,
                @Field(name = FRIEND_ID,
        }
)
public class Character_friendsTable extends DataBaseTable<Character_friendsTable> {

    public static final String CHAR_ID = "char_id";
    public static final String FRIEND_ID = "friend_id";
    public Character_friendsTable() {
super(Character_friendsTable.class);
}
    public IntegergetChar_id() {
        return get(CHAR_ID, Integer.class);
    }
    public IntegergetFriend_id() {
        return get(FRIEND_ID, Integer.class);
    }

    public void setChar_id() {
        set(CHAR_ID, value);
    }
    public void setFriend_id() {
        set(FRIEND_ID, value);
    }

}
