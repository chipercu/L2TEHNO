package utils_soft.common.DatabaseResurce.schemes.generate;

import utils_soft.common.DatabaseResurce.anotations.DefValue;
import utils_soft.common.DatabaseResurce.anotations.Field;
import utils_soft.common.DatabaseResurce.anotations.Table;
import utils_soft.common.DatabaseResurce.DataBaseTable;
import static utils_soft.common.DatabaseResurce.schemes.generate.CharacterFriendsResource.*;

@Table(
        name = "character_friends",
        primary_key = {CHAR_ID,FRIEND_ID},
        fields = {
                @Field(name = CHAR_ID , data_type = "int" , defValue = @DefValue(Integer = 0)),
                @Field(name = FRIEND_ID , data_type = "int" , defValue = @DefValue(Integer = 0)),
        }
)
public class CharacterFriendsResource extends DataBaseTable<CharacterFriendsResource> {

    public static final String CHAR_ID = "char_id";
    public static final String FRIEND_ID = "friend_id";

    public CharacterFriendsResource() {
super(CharacterFriendsResource.class);
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
