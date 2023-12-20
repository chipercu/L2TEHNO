package l2open.database.schemes.resources;

import l2open.database.DataBaseTable;
import l2open.database.anotations.Column;
import l2open.database.anotations.DATA_TYPE;
import l2open.database.anotations.IS_NULLABLE;
import l2open.database.anotations.Table;

import static l2open.database.schemes.resources.ClanSubpledgesResource.*;

@Table(
        name = "clan_subpledges",
        primary_key = {CLAN_ID,TYPE},
        fields = {
                @Column(is_null = IS_NULLABLE.NO , type = DATA_TYPE.INT , type_size = 0 , name = CLAN_ID , defValue = "0"),
                @Column(is_null = IS_NULLABLE.NO , type = DATA_TYPE.SMALLINT , type_size = 0 , name = TYPE , defValue = "0"),
                @Column(is_null = IS_NULLABLE.NO , type = DATA_TYPE.VARCHAR , type_size = 45 , name = NAME , defValue = ""),
                @Column(is_null = IS_NULLABLE.NO , type = DATA_TYPE.INT , type_size = 0 , name = LEADER_ID , defValue = "0"),
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

    public Integer getClanId() {
        return get(CLAN_ID);
    }
    public Integer getType() {
        return get(TYPE);
    }
    public String getName() {
        return get(NAME);
    }
    public Integer getLeaderId() {
        return get(LEADER_ID);
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
