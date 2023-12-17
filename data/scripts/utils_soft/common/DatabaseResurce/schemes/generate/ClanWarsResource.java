package utils_soft.common.DatabaseResurce.schemes.generate;

import utils_soft.common.DatabaseResurce.anotations.DefValue;
import utils_soft.common.DatabaseResurce.anotations.Field;
import utils_soft.common.DatabaseResurce.anotations.Table;
import utils_soft.common.DatabaseResurce.DataBaseTable;
import static utils_soft.common.DatabaseResurce.schemes.generate.ClanWarsResource.*;

@Table(
        name = "clan_wars",
        primary_key = {},
        fields = {
                @Field(name = CLAN1 , data_type = "int" , defValue = @DefValue(Integer = 0)),
                @Field(name = CLAN2 , data_type = "int" , defValue = @DefValue(Integer = 0)),
        }
)
public class ClanWarsResource extends DataBaseTable<ClanWarsResource> {

    public static final String CLAN1 = "clan1";
    public static final String CLAN2 = "clan2";

    public ClanWarsResource() {
super(ClanWarsResource.class);
}

    public Integer getClan1() {
        return get(CLAN1, Integer.class);
    }
    public Integer getClan2() {
        return get(CLAN2, Integer.class);
    }

    public void setClan1(Integer value) {
        set(CLAN1, value);
    }
    public void setClan2(Integer value) {
        set(CLAN2, value);
    }

}
