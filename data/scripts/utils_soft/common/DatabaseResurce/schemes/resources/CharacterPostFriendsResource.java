package utils_soft.common.DatabaseResurce.schemes.resources;

import utils_soft.common.DatabaseResurce.anotations.Column;
import utils_soft.common.DatabaseResurce.anotations.Table;
import utils_soft.common.DatabaseResurce.DataBaseTable;
import static utils_soft.common.DatabaseResurce.schemes.resources.CharacterPostFriendsResource.*;
import static utils_soft.common.DatabaseResurce.anotations.IS_NULLABLE.*;
import static utils_soft.common.DatabaseResurce.anotations.DATA_TYPE.*;

@Table(
        name = "character_post_friends",
        primary_key = {OBJECT_ID,POST_FRIEND},
        fields = {
                @Column(is_null = NO , type = INT , type_size = 0 , name = OBJECT_ID , defValue = "null"),
                @Column(is_null = NO , type = INT , type_size = 0 , name = POST_FRIEND , defValue = "null"),
        }
)
public class CharacterPostFriendsResource extends DataBaseTable<CharacterPostFriendsResource> {

    public static final String OBJECT_ID = "object_id";
    public static final String POST_FRIEND = "post_friend";

    public CharacterPostFriendsResource() {
        super(CharacterPostFriendsResource.class);
}

    public Integer getObjectId() {
        return get(OBJECT_ID);
    }
    public Integer getPostFriend() {
        return get(POST_FRIEND);
    }

    public void setObjectId(Integer value) {
        set(OBJECT_ID, value);
    }
    public void setPostFriend(Integer value) {
        set(POST_FRIEND, value);
    }

}
