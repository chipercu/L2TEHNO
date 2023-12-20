package l2open.database.schemes.resources;

import l2open.database.DataBaseTable;
import l2open.database.anotations.Column;
import l2open.database.anotations.DATA_TYPE;
import l2open.database.anotations.IS_NULLABLE;
import l2open.database.anotations.Table;

import static l2open.database.schemes.resources.AutoChatTextResource.*;

@Table(
        name = "auto_chat_text",
        primary_key = {GROUP_ID,CHAT_TEXT},
        fields = {
                @Column(is_null = IS_NULLABLE.NO , type = DATA_TYPE.INT , type_size = 0 , name = GROUP_ID , defValue = "0"),
                @Column(is_null = IS_NULLABLE.NO , type = DATA_TYPE.VARCHAR , type_size = 255 , name = CHAT_TEXT , defValue = ""),
        }
)
public class AutoChatTextResource extends DataBaseTable<AutoChatTextResource> {

    public static final String GROUP_ID = "groupId";
    public static final String CHAT_TEXT = "chatText";

    public AutoChatTextResource() {
        super(AutoChatTextResource.class);
}

    public Integer getGroupId() {
        return get(GROUP_ID);
    }
    public String getChatText() {
        return get(CHAT_TEXT);
    }

    public void setGroupId(Integer value) {
        set(GROUP_ID, value);
    }
    public void setChatText(String value) {
        set(CHAT_TEXT, value);
    }

}
