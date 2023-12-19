package utils_soft.common.DatabaseResurce.schemes.resources;

import utils_soft.common.DatabaseResurce.anotations.Column;
import utils_soft.common.DatabaseResurce.anotations.Table;
import utils_soft.common.DatabaseResurce.DataBaseTable;
import static utils_soft.common.DatabaseResurce.schemes.resources.CharacterFriendsResource.*;
import static utils_soft.common.DatabaseResurce.anotations.IS_NULLABLE.*;
import static utils_soft.common.DatabaseResurce.anotations.DATA_TYPE.*;

@Table(
        name = "character_friends",
        primary_key = {CHAR_ID,FRIEND_ID},
        fields = {
                @Column(is_null = NO , type = INT , type_size = 0 , name = CHAR_ID , defValue = "0"),
                @Column(is_null = NO , type = INT , type_size = 0 , name = FRIEND_ID , defValue = "0"),
        }
)
public class CharacterFriendsResource extends DataBaseTable<CharacterFriendsResource> {

    public static final String CHAR_ID = "char_id";
    public static final String FRIEND_ID = "friend_id";

    public CharacterFriendsResource() {
        super(CharacterFriendsResource.class);
}

    public Integer getCharId() {
        return get(CHAR_ID);
    }
    public Integer getFriendId() {
        return get(FRIEND_ID);
    }

    public void setCharId(Integer value) {
        set(CHAR_ID, value);
    }
    public void setFriendId(Integer value) {
        set(FRIEND_ID, value);
    }

}
