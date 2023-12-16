package utils_soft.common.DatabaseResurce.schemes.generate;

import utils_soft.common.DatabaseResurce.anotations.DefValue;
import utils_soft.common.DatabaseResurce.anotations.Field;
import utils_soft.common.DatabaseResurce.anotations.Table;
import utils_soft.common.DatabaseResurce.DataBaseTable;
import static utils_soft.common.DatabaseResurce.schemes.generate.CharacterFriendsTable.*;

@Table(
        name = "character_friends",
        fields = {
                @Field(name = CHAR_ID),
                @Field(name = FRIEND_ID),
        }
)
public class CharacterFriendsTable extends DataBaseTable<CharacterFriendsTable> {

    public static final String CHAR_ID = "char_id";
    public static final String FRIEND_ID = "friend_id";

    public CharacterFriendsTable() {
super(CharacterFriendsTable.class);
}

    public Integer getCharId() {
        return get(CHAR_ID, Integer.class);
    }
    public Integer getFriendId() {
        return get(FRIEND_ID, Integer.class);
    }

    public void setCharId(Integer value) {
        set(CHAR_ID, value);
    }
    public void setFriendId(Integer value) {
        set(FRIEND_ID, value);
    }

}
