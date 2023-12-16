package utils_soft.common.DatabaseResurce.schemes.generate;

import utils_soft.common.DatabaseResurce.anotations.DefValue;
import utils_soft.common.DatabaseResurce.anotations.Field;
import utils_soft.common.DatabaseResurce.anotations.Table;
import utils_soft.common.DatabaseResurce.DataBaseTable;
import static utils_soft.common.DatabaseResurce.schemes.generate.AutoChatTable.*;

@Table(
        name = "auto_chat",
        fields = {
                @Field(name = GROUP_ID),
                @Field(name = NPC_ID),
                @Field(name = CHAT_DELAY),
        }
)
public class AutoChatTable extends DataBaseTable<AutoChatTable> {

    public static final String GROUP_ID = "groupId";
    public static final String NPC_ID = "npcId";
    public static final String CHAT_DELAY = "chatDelay";

    public AutoChatTable() {
super(AutoChatTable.class);
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
