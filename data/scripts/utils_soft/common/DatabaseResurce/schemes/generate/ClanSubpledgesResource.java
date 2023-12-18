package utils_soft.common.DatabaseResurce.schemes.generate;

import utils_soft.common.DatabaseResurce.anotations.DefValue;
import utils_soft.common.DatabaseResurce.anotations.Field;
import utils_soft.common.DatabaseResurce.anotations.Table;
import utils_soft.common.DatabaseResurce.DataBaseTable;
import static utils_soft.common.DatabaseResurce.schemes.generate.ClanSubpledgesResource.*;
import static utils_soft.common.DatabaseResurce.anotations.DATA_TYPE.*;

@Table(
        name = "clan_subpledges",
        primary_key = {CLAN_ID,TYPE},
        fields = {
                @Field(name = CLAN_ID , type = INT , nullable = false , defValue = @DefValue(Integer = 0)),
                @Field(name = TYPE , type = SMALLINT , nullable = false , defValue = @DefValue(Integer = 0)),
                @Field(name = NAME , type = VARCHAR , type_size = 45 , nullable = false , defValue = @DefValue(String = "")),
                @Field(name = LEADER_ID , type = INT , nullable = false , defValue = @DefValue(Integer = 0)),
        }
)
public class ClanSubpledgesResource extends DataBaseTable<ClanSubpledgesResource> {

    public static final String CLAN_ID = "clan_id";
    public static final String TYPE = "type";
    public static final String NAME = "name";
    public static final String LEADER_ID = "leader_id";

    public ClanSubpledgesResource() {
super(ClanSubpledgesResource.class);
}


    public ClanSubpledgesResource(String clan_id,String type){
        super(ClanSubpledgesResource.class);
        getSTAT_SET().set(CLAN_ID, clan_id);
        getSTAT_SET().set(TYPE, type);
    }

    public Integer getClanId() {
        return get(CLAN_ID, Integer.class);
    }
    public Integer getType() {
        return get(TYPE, Integer.class);
    }
    public String getName() {
        return get(NAME, String.class);
    }
    public Integer getLeaderId() {
        return get(LEADER_ID, Integer.class);
    }

    public void setClanId(Integer value) {
        set(CLAN_ID, value);
    }
    public void setType(Integer value) {
        set(TYPE, value);
    }
    public void setName(String value) {
        set(NAME, value);
    }
    public void setLeaderId(Integer value) {
        set(LEADER_ID, value);
    }

}
