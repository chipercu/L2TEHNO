package l2open.database.schemes.resources;

import l2open.database.DataBaseTable;
import l2open.database.anotations.Column;
import l2open.database.anotations.DATA_TYPE;
import l2open.database.anotations.IS_NULLABLE;
import l2open.database.anotations.Table;

import static l2open.database.schemes.resources.CharacterQuestsResource.*;

@Table(
        name = "character_quests",
        primary_key = {CHAR_ID,NAME,VAR},
        fields = {
                @Column(is_null = IS_NULLABLE.NO , type = DATA_TYPE.INT , type_size = 0 , name = CHAR_ID , defValue = "0"),
                @Column(is_null = IS_NULLABLE.NO , type = DATA_TYPE.VARCHAR , type_size = 40 , name = NAME , defValue = ""),
                @Column(is_null = IS_NULLABLE.NO , type = DATA_TYPE.VARCHAR , type_size = 20 , name = VAR , defValue = ""),
                @Column(is_null = IS_NULLABLE.YES , type = DATA_TYPE.VARCHAR , type_size = 255 , name = VALUE , defValue = "NULL"),
        }
)
public class CharacterQuestsResource extends DataBaseTable<CharacterQuestsResource> {

    public static final String CHAR_ID = "char_id";
    public static final String NAME = "name";
    public static final String VAR = "var";
    public static final String VALUE = "value";

    public CharacterQuestsResource() {
        super(CharacterQuestsResource.class);
}

    public Integer getCharId() {
        return get(CHAR_ID);
    }
    public String getName() {
        return get(NAME);
    }
    public String getVar() {
        return get(VAR);
    }
    public String getValue() {
        return get(VALUE);
    }

    public void setCharId(Integer value) {
        set(CHAR_ID, value);
    }
    public void setName(String value) {
        set(NAME, value);
    }
    public void setVar(String value) {
        set(VAR, value);
    }
    public void setValue(String value) {
        set(VALUE, value);
    }

}
