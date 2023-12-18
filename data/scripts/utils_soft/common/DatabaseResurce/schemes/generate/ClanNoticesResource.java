package utils_soft.common.DatabaseResurce.schemes.generate;

import utils_soft.common.DatabaseResurce.anotations.DefValue;
import utils_soft.common.DatabaseResurce.anotations.Field;
import utils_soft.common.DatabaseResurce.anotations.Table;
import utils_soft.common.DatabaseResurce.DataBaseTable;
import static utils_soft.common.DatabaseResurce.schemes.generate.ClanNoticesResource.*;
import static utils_soft.common.DatabaseResurce.anotations.DATA_TYPE.*;

@Table(
        name = "clan_notices",
        primary_key = {CLAN_ID},
        fields = {
                @Field(name = CLAN_ID , type = INT , nullable = false , defValue = @DefValue(Integer = null)),
                @Field(name = NOTICE , type = VARCHAR , type_size = 512 , nullable = false , defValue = @DefValue(String = "")),
                @Field(name = ENABLED , type = VARCHAR , type_size = 5 , nullable = false , defValue = @DefValue(String = "")),
        }
)
public class ClanNoticesResource extends DataBaseTable<ClanNoticesResource> {

    public static final String CLAN_ID = "clanID";
    public static final String NOTICE = "notice";
    public static final String ENABLED = "enabled";

    public ClanNoticesResource() {
super(ClanNoticesResource.class);
}


    public ClanNoticesResource(String clanID){
        super(ClanNoticesResource.class);
        getSTAT_SET().set(CLAN_ID, clanID);
    }

    public Integer getClanID() {
        return get(CLAN_ID, Integer.class);
    }
    public String getNotice() {
        return get(NOTICE, String.class);
    }
    public String getEnabled() {
        return get(ENABLED, String.class);
    }

    public void setClanID(Integer value) {
        set(CLAN_ID, value);
    }
    public void setNotice(String value) {
        set(NOTICE, value);
    }
    public void setEnabled(String value) {
        set(ENABLED, value);
    }

}
