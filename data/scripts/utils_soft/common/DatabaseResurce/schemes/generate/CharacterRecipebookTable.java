package utils_soft.common.DatabaseResurce.schemes.generate;

import utils_soft.common.DatabaseResurce.anotations.DefValue;
import utils_soft.common.DatabaseResurce.anotations.Field;
import utils_soft.common.DatabaseResurce.anotations.Table;
import utils_soft.common.DatabaseResurce.DataBaseTable;
import static utils_soft.common.DatabaseResurce.schemes.generate.CharacterRecipebookTable.*;

@Table(
        name = "character_recipebook",
        fields = {
                @Field(name = CHAR_ID),
                @Field(name = ID),
        }
)
public class CharacterRecipebookTable extends DataBaseTable<CharacterRecipebookTable> {

    public static final String CHAR_ID = "char_id";
    public static final String ID = "id";

    public CharacterRecipebookTable() {
super(CharacterRecipebookTable.class);
}

    public Integer getCharId() {
        return get(CHAR_ID, Integer.class);
    }
    public Integer getId() {
        return get(ID, Integer.class);
    }

    public void setCharId(Integer value) {
        set(CHAR_ID, value);
    }
    public void setId(Integer value) {
        set(ID, value);
    }

}
