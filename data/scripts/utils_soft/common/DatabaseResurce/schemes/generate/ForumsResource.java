package utils_soft.common.DatabaseResurce.schemes.generate;

import utils_soft.common.DatabaseResurce.anotations.DefValue;
import utils_soft.common.DatabaseResurce.anotations.Field;
import utils_soft.common.DatabaseResurce.anotations.Table;
import utils_soft.common.DatabaseResurce.DataBaseTable;
import static utils_soft.common.DatabaseResurce.schemes.generate.ForumsResource.*;
import static utils_soft.common.DatabaseResurce.anotations.DATA_TYPE.*;

@Table(
        name = "forums",
        primary_key = {FORUM_ID},
        fields = {
                @Field(name = FORUM_ID , type = INT , nullable = false , defValue = @DefValue(Integer = 0)),
                @Field(name = FORUM_NAME , type = VARCHAR , type_size = 255 , nullable = false , defValue = @DefValue(String = "")),
                @Field(name = FORUM_PARENT , type = INT , nullable = false , defValue = @DefValue(Integer = 0)),
                @Field(name = FORUM_POST , type = INT , nullable = false , defValue = @DefValue(Integer = 0)),
                @Field(name = FORUM_TYPE , type = INT , nullable = false , defValue = @DefValue(Integer = 0)),
                @Field(name = FORUM_PERM , type = INT , nullable = false , defValue = @DefValue(Integer = 0)),
                @Field(name = FORUM_OWNER_ID , type = INT , nullable = false , defValue = @DefValue(Integer = 0)),
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


    public ForumsResource(String forum_id){
        super(ForumsResource.class);
        getSTAT_SET().set(FORUM_ID, forum_id);
    }

    public Integer getForumId() {
        return get(FORUM_ID, Integer.class);
    }
    public String getForumName() {
        return get(FORUM_NAME, String.class);
    }
    public Integer getForumParent() {
        return get(FORUM_PARENT, Integer.class);
    }
    public Integer getForumPost() {
        return get(FORUM_POST, Integer.class);
    }
    public Integer getForumType() {
        return get(FORUM_TYPE, Integer.class);
    }
    public Integer getForumPerm() {
        return get(FORUM_PERM, Integer.class);
    }
    public Integer getForumOwnerId() {
        return get(FORUM_OWNER_ID, Integer.class);
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
