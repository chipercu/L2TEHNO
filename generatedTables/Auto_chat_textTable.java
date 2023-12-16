package utils_soft.common.DatabaseResurce.schemes.generate;

import utils_soft.common.DatabaseResurce.anotations.DefValue;
import utils_soft.common.DatabaseResurce.anotations.Field;
import utils_soft.common.DatabaseResurce.anotations.Table;
import utils_soft.common.DatabaseResurce.DataBaseTable;
import static utils_soft.common.DatabaseResurce.schemes.Auto_chat_textTable.*;

@Table(
        name = "auto_chat_text",
        fields = {
                @Field(name = GROUPID,
                @Field(name = CHATTEXT,
        }
)
public class Auto_chat_textTable extends DataBaseTable<Auto_chat_textTable> {

    public static final String GROUPID = "groupId";
    public static final String CHATTEXT = "chatText";
    public Auto_chat_textTable() {
super(Auto_chat_textTable.class);
}
    public IntegergetGroupId() {
        return get(GROUPID, Integer.class);
    }
    public StringgetChatText() {
        return get(CHATTEXT, String.class);
    }

    public void setGroupId() {
        set(GROUPID, value);
    }
    public void setChatText() {
        set(CHATTEXT, value);
    }

}
