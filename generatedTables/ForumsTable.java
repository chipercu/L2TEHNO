package utils_soft.common.DatabaseResurce.schemes.generate;

import utils_soft.common.DatabaseResurce.anotations.DefValue;
import utils_soft.common.DatabaseResurce.anotations.Field;
import utils_soft.common.DatabaseResurce.anotations.Table;
import utils_soft.common.DatabaseResurce.DataBaseTable;
import static utils_soft.common.DatabaseResurce.schemes.ForumsTable.*;

@Table(
        name = "forums",
        fields = {
                @Field(name = FORUM_ID,
                @Field(name = FORUM_NAME,
                @Field(name = FORUM_PARENT,
                @Field(name = FORUM_POST,
                @Field(name = FORUM_TYPE,
                @Field(name = FORUM_PERM,
                @Field(name = FORUM_OWNER_ID,
        }
)
public class ForumsTable extends DataBaseTable<ForumsTable> {

    public static final String FORUM_ID = "forum_id";
    public static final String FORUM_NAME = "forum_name";
    public static final String FORUM_PARENT = "forum_parent";
    public static final String FORUM_POST = "forum_post";
    public static final String FORUM_TYPE = "forum_type";
    public static final String FORUM_PERM = "forum_perm";
    public static final String FORUM_OWNER_ID = "forum_owner_id";
    public ForumsTable() {
super(ForumsTable.class);
}
    public IntegergetForum_id() {
        return get(FORUM_ID, Integer.class);
    }
    public StringgetForum_name() {
        return get(FORUM_NAME, String.class);
    }
    public IntegergetForum_parent() {
        return get(FORUM_PARENT, Integer.class);
    }
    public IntegergetForum_post() {
        return get(FORUM_POST, Integer.class);
    }
    public IntegergetForum_type() {
        return get(FORUM_TYPE, Integer.class);
    }
    public IntegergetForum_perm() {
        return get(FORUM_PERM, Integer.class);
    }
    public IntegergetForum_owner_id() {
        return get(FORUM_OWNER_ID, Integer.class);
    }

    public void setForum_id() {
        set(FORUM_ID, value);
    }
    public void setForum_name() {
        set(FORUM_NAME, value);
    }
    public void setForum_parent() {
        set(FORUM_PARENT, value);
    }
    public void setForum_post() {
        set(FORUM_POST, value);
    }
    public void setForum_type() {
        set(FORUM_TYPE, value);
    }
    public void setForum_perm() {
        set(FORUM_PERM, value);
    }
    public void setForum_owner_id() {
        set(FORUM_OWNER_ID, value);
    }

}
