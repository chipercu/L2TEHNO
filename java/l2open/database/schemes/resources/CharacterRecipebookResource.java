package l2open.database.schemes.resources;

import l2open.database.DataBaseTable;
import l2open.database.anotations.Column;
import l2open.database.anotations.DATA_TYPE;
import l2open.database.anotations.IS_NULLABLE;
import l2open.database.anotations.Table;

import static l2open.database.schemes.resources.CharacterRecipebookResource.*;

@Table(
        name = "character_recipebook",
        primary_key = {CHAR_ID,ID},
        fields = {
                @Column(is_null = IS_NULLABLE.NO , type = DATA_TYPE.INT , type_size = 0 , name = CHAR_ID , defValue = "0"),
                @Column(is_null = IS_NULLABLE.NO , type = DATA_TYPE.SMALLINT , type_size = 0 , name = ID , defValue = "0"),
        }
)
public class CharacterRecipebookResource extends DataBaseTable<CharacterRecipebookResource> {

    public static final String CHAR_ID = "char_id";
    public static final String ID = "id";

    public CharacterRecipebookResource() {
        super(CharacterRecipebookResource.class);
}

    public Integer getCharId() {
        return get(CHAR_ID);
    }
    public Integer getId() {
        return get(ID);
    }

    public void setCharId(Integer value) {
        set(CHAR_ID, value);
    }
    public void setId(Integer value) {
        set(ID, value);
    }

}
