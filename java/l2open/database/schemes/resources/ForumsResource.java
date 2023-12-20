package l2open.database.schemes.resources;

import l2open.database.DataBaseTable;
import l2open.database.anotations.Column;
import l2open.database.anotations.DATA_TYPE;
import l2open.database.anotations.IS_NULLABLE;
import l2open.database.anotations.Table;

import static l2open.database.schemes.resources.ForumsResource.*;

@Table(
        name = "forums",
        primary_key = {FORUM_ID},
        fields = {
                @Column(is_null = IS_NULLABLE.NO , type = DATA_TYPE.INT , type_size = 0 , name = FORUM_ID , defValue = "0"),
                @Column(is_null = IS_NULLABLE.NO , type = DATA_TYPE.VARCHAR , type_size = 255 , name = FORUM_NAME , defValue = ""),
                @Column(is_null = IS_NULLABLE.NO , type = DATA_TYPE.INT , type_size = 0 , name = FORUM_PARENT , defValue = "0"),
                @Column(is_null = IS_NULLABLE.NO , type = DATA_TYPE.INT , type_size = 0 , name = FORUM_POST , defValue = "0"),
                @Column(is_null = IS_NULLABLE.NO , type = DATA_TYPE.INT , type_size = 0 , name = FORUM_TYPE , defValue = "0"),
                @Column(is_null = IS_NULLABLE.NO , type = DATA_TYPE.INT , type_size = 0 , name = FORUM_PERM , defValue = "0"),
                @Column(is_null = IS_NULLABLE.NO , type = DATA_TYPE.INT , type_size = 0 , name = FORUM_OWNER_ID , defValue = "0"),
        }
)
public class ForumsResource extends DataBaseTable<ForumsResource> {

    public static final String FORUM_ID = "forum_id";
    public static final String FORUM_NAME = "forum_name";
    public static final String FORUM_PARENT = "forum_parent";
    public static final String FORUM_POST = "forum_post";
    public static final String FORUM_TYPE = "forum_type";
    public static final String FORUM_PERM = "forum_perm";
    public static final String FORUM_OWNER_ID = "forum_owner_id";

    public ForumsResource() {
        super(ForumsResource.class);
}

    public Integer getForumId() {
        return get(FORUM_ID);
    }
    public String getForumName() {
        return get(FORUM_NAME);
    }
    public Integer getForumParent() {
        return get(FORUM_PARENT);
    }
    public Integer getForumPost() {
        return get(FORUM_POST);
    }
    public Integer getForumType() {
        return get(FORUM_TYPE);
    }
    public Integer getForumPerm() {
        return get(FORUM_PERM);
    }
    public Integer getForumOwnerId() {
        return get(FORUM_OWNER_ID);
    }

    public void setForumId(Integer value) {
        set(FORUM_ID, value);
    }
    public void setForumName(String value) {
        set(FORUM_NAME, value);
    }
    public void setForumParent(Integer value) {
        set(FORUM_PARENT, value);
    }
    public void setForumPost(Integer value) {
        set(FORUM_POST, value);
    }
    public void setForumType(Integer value) {
        set(FORUM_TYPE, value);
    }
    public void setForumPerm(Integer value) {
        set(FORUM_PERM, value);
    }
    public void setForumOwnerId(Integer value) {
        set(FORUM_OWNER_ID, value);
    }

}
