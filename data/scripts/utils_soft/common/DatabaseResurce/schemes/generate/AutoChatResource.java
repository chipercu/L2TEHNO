package utils_soft.common.DatabaseResurce.schemes.generate;

import utils_soft.common.DatabaseResurce.anotations.DefValue;
import utils_soft.common.DatabaseResurce.anotations.Field;
import utils_soft.common.DatabaseResurce.anotations.Table;
import utils_soft.common.DatabaseResurce.DataBaseTable;
import static utils_soft.common.DatabaseResurce.schemes.generate.AutoChatResource.*;

@Table(
        name = "auto_chat",
        primary_key = {GROUP_ID,NPC_ID},
        fields = {
                @Field(name = GROUP_ID , data_type = "int" , defValue = @DefValue(Integer = 0)),
                @Field(name = NPC_ID , data_type = "int" , defValue = @DefValue(Integer = 0)),
                @Field(name = CHAT_DELAY , data_type = "int" , defValue = @DefValue(Integer = -1)),
        }
)
public class AutoChatResource extends DataBaseTable<AutoChatResource> {

    public static final String GROUP_ID = "groupId";
    public static final String NPC_ID = "npcId";
    public static final String CHAT_DELAY = "chatDelay";

    public AutoChatResource() {
super(AutoChatResource.class);
}

    public Integer getGroupId() {
        return get(GROUP_ID, Integer.class);
    }
    public Integer getNpcId() {
        return get(NPC_ID, Integer.class);
    }
    public Integer getChatDelay() {
        return get(CHAT_DELAY, Integer.class);
    }

    public void setGroupId(Integer value) {
        set(GROUP_ID, value);
    }
    public void setNpcId(Integer value) {
        set(NPC_ID, value);
    }
    public void setChatDelay(Integer value) {
        set(CHAT_DELAY, value);
    }

}
