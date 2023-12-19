package utils_soft.common.DatabaseResurce.schemes.resources;

import utils_soft.common.DatabaseResurce.anotations.Column;
import utils_soft.common.DatabaseResurce.anotations.Table;
import utils_soft.common.DatabaseResurce.DataBaseTable;
import static utils_soft.common.DatabaseResurce.schemes.resources.AutoChatResource.*;
import static utils_soft.common.DatabaseResurce.anotations.IS_NULLABLE.*;
import static utils_soft.common.DatabaseResurce.anotations.DATA_TYPE.*;

@Table(
        name = "auto_chat",
        primary_key = {GROUP_ID,NPC_ID},
        fields = {
                @Column(is_null = NO , type = INT , type_size = 0 , name = GROUP_ID , defValue = "0"),
                @Column(is_null = NO , type = INT , type_size = 0 , name = NPC_ID , defValue = "0"),
                @Column(is_null = NO , type = INT , type_size = 0 , name = CHAT_DELAY , defValue = "-1"),
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
        return get(GROUP_ID);
    }
    public Integer getNpcId() {
        return get(NPC_ID);
    }
    public Integer getChatDelay() {
        return get(CHAT_DELAY);
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
