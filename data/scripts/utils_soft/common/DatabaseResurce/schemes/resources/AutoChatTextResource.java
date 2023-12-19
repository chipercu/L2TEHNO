package utils_soft.common.DatabaseResurce.schemes.resources;

import utils_soft.common.DatabaseResurce.anotations.Column;
import utils_soft.common.DatabaseResurce.anotations.Table;
import utils_soft.common.DatabaseResurce.DataBaseTable;
import static utils_soft.common.DatabaseResurce.schemes.resources.AutoChatTextResource.*;
import static utils_soft.common.DatabaseResurce.anotations.IS_NULLABLE.*;
import static utils_soft.common.DatabaseResurce.anotations.DATA_TYPE.*;

@Table(
        name = "auto_chat_text",
        primary_key = {GROUP_ID,CHAT_TEXT},
        fields = {
                @Column(is_null = NO , type = INT , type_size = 0 , name = GROUP_ID , defValue = "0"),
                @Column(is_null = NO , type = VARCHAR , type_size = 255 , name = CHAT_TEXT , defValue = ""),
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
