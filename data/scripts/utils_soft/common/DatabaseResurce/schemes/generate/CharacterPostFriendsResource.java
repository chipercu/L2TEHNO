package utils_soft.common.DatabaseResurce.schemes.generate;

import utils_soft.common.DatabaseResurce.anotations.DefValue;
import utils_soft.common.DatabaseResurce.anotations.Field;
import utils_soft.common.DatabaseResurce.anotations.Table;
import utils_soft.common.DatabaseResurce.DataBaseTable;
import static utils_soft.common.DatabaseResurce.schemes.generate.CharacterPostFriendsResource.*;

@Table(
        name = "character_post_friends",
        primary_key = {OBJECT_ID,POST_FRIEND},
        fields = {
                @Field(name = OBJECT_ID , data_type = "int"),
                @Field(name = POST_FRIEND , data_type = "int"),
        }
)
public class CharacterPostFriendsResource extends DataBaseTable<CharacterPostFriendsResource> {

    public static final String OBJECT_ID = "object_id";
    public static final String POST_FRIEND = "post_friend";

    public CharacterPostFriendsResource() {
super(CharacterPostFriendsResource.class);
}

    public Integer getObjectId() {
        return get(OBJECT_ID, Integer.class);
    }
    public Integer getPostFriend() {
        return get(POST_FRIEND, Integer.class);
    }

    public void setObjectId(Integer value) {
        set(OBJECT_ID, value);
    }
    public void setPostFriend(Integer value) {
        set(POST_FRIEND, value);
    }

}
