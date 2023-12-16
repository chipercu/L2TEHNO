package utils_soft.common.DatabaseResurce.schemes.generate;

import utils_soft.common.DatabaseResurce.anotations.DefValue;
import utils_soft.common.DatabaseResurce.anotations.Field;
import utils_soft.common.DatabaseResurce.anotations.Table;
import utils_soft.common.DatabaseResurce.DataBaseTable;
import static utils_soft.common.DatabaseResurce.schemes.Auto_chatTable.*;

@Table(
        name = "auto_chat",
        fields = {
                @Field(name = GROUPID,
                @Field(name = NPCID,
                @Field(name = CHATDELAY,
        }
)
public class Auto_chatTable extends DataBaseTable<Auto_chatTable> {

    public static final String GROUPID = "groupId";
    public static final String NPCID = "npcId";
    public static final String CHATDELAY = "chatDelay";
    public Auto_chatTable() {
super(Auto_chatTable.class);
}
    public IntegergetGroupId() {
        return get(GROUPID, Integer.class);
    }
    public IntegergetNpcId() {
        return get(NPCID, Integer.class);
    }
    public IntegergetChatDelay() {
        return get(CHATDELAY, Integer.class);
    }

    public void setGroupId() {
        set(GROUPID, value);
    }
    public void setNpcId() {
        set(NPCID, value);
    }
    public void setChatDelay() {
        set(CHATDELAY, value);
    }

}
