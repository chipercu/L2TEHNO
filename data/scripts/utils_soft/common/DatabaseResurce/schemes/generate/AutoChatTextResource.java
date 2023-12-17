package utils_soft.common.DatabaseResurce.schemes.generate;

import utils_soft.common.DatabaseResurce.anotations.DefValue;
import utils_soft.common.DatabaseResurce.anotations.Field;
import utils_soft.common.DatabaseResurce.anotations.Table;
import utils_soft.common.DatabaseResurce.DataBaseTable;
import static utils_soft.common.DatabaseResurce.schemes.generate.AutoChatTextResource.*;

@Table(
        name = "auto_chat_text",
        primary_key = {GROUP_ID,CHAT_TEXT},
        fields = {
                @Field(name = GROUP_ID , data_type = "int" , defValue = @DefValue(Integer = 0)),
                @Field(name = CHAT_TEXT , data_type = "varchar" , defValue = @DefValue(String = "")),
        }
)
public class AutoChatTextResource extends DataBaseTable<AutoChatTextResource> {

    public static final String GROUP_ID = "groupId";
    public static final String CHAT_TEXT = "chatText";

    public AutoChatTextResource() {
super(AutoChatTextResource.class);
}

    public Integer getGroupId() {
        return get(GROUP_ID, Integer.class);
    }
    public String getChatText() {
        return get(CHAT_TEXT, String.class);
    }

    public void setGroupId(Integer value) {
        set(GROUP_ID, value);
    }
    public void setChatText(String value) {
        set(CHAT_TEXT, value);
    }

}
