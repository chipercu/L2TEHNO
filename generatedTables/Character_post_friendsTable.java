package utils_soft.common.DatabaseResurce.schemes.generate;

import utils_soft.common.DatabaseResurce.anotations.DefValue;
import utils_soft.common.DatabaseResurce.anotations.Field;
import utils_soft.common.DatabaseResurce.anotations.Table;
import utils_soft.common.DatabaseResurce.DataBaseTable;
import static utils_soft.common.DatabaseResurce.schemes.Character_post_friendsTable.*;

@Table(
        name = "character_post_friends",
        fields = {
                @Field(name = OBJECT_ID,
                @Field(name = POST_FRIEND,
        }
)
public class Character_post_friendsTable extends DataBaseTable<Character_post_friendsTable> {

    public static final String OBJECT_ID = "object_id";
    public static final String POST_FRIEND = "post_friend";
    public Character_post_friendsTable() {
super(Character_post_friendsTable.class);
}
    public IntegergetObject_id() {
        return get(OBJECT_ID, Integer.class);
    }
    public IntegergetPost_friend() {
        return get(POST_FRIEND, Integer.class);
    }

    public void setObject_id() {
        set(OBJECT_ID, value);
    }
    public void setPost_friend() {
        set(POST_FRIEND, value);
    }

}
