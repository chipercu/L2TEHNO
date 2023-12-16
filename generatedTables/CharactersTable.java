package utils_soft.common.DatabaseResurce.schemes.generate;

import utils_soft.common.DatabaseResurce.anotations.DefValue;
import utils_soft.common.DatabaseResurce.anotations.Field;
import utils_soft.common.DatabaseResurce.anotations.Table;
import utils_soft.common.DatabaseResurce.DataBaseTable;
import static utils_soft.common.DatabaseResurce.schemes.CharactersTable.*;

@Table(
        name = "characters",
        fields = {
                @Field(name = ACCOUNT_NAME,
                @Field(name = OBJ_ID,
                @Field(name = CHAR_NAME,
                @Field(name = FACE,
                @Field(name = HAIRSTYLE,
                @Field(name = HAIRCOLOR,
                @Field(name = SEX,
                @Field(name = HEADING,
                @Field(name = X,
                @Field(name = Y,
                @Field(name = Z,
                @Field(name = KARMA,
                @Field(name = PVPKILLS,
                @Field(name = PKKILLS,
                @Field(name = CLANID,
                @Field(name = CREATETIME,
                @Field(name = DELETETIME,
                @Field(name = TITLE,
                @Field(name = REC_HAVE,
                @Field(name = REC_LEFT,
                @Field(name = REC_TIMELEFT,
                @Field(name = ACCESSLEVEL,
                @Field(name = ONLINE,
                @Field(name = ONLINETIME,
                @Field(name = LASTACCESS,
                @Field(name = LEAVECLAN,
                @Field(name = DELETECLAN,
                @Field(name = NOCHANNEL,
                @Field(name = PLEDGE_TYPE,
                @Field(name = PLEDGE_RANK,
                @Field(name = LVL_JOINED_ACADEMY,
                @Field(name = APPRENTICE,
                @Field(name = KEY_BINDINGS,
                @Field(name = PCBANGPOINTS,
                @Field(name = VITALITY,
                @Field(name = FAME,
                @Field(name = BOOKMARKS,
                @Field(name = HUNT_BONUS,
                @Field(name = HUNT_TIMELEFT,
                @Field(name = BOT,
                @Field(name = LAST_HWID,
                @Field(name = FRACTION,
        }
)
public class CharactersTable extends DataBaseTable<CharactersTable> {

    public static final String ACCOUNT_NAME = "account_name";
    public static final String OBJ_ID = "obj_Id";
    public static final String CHAR_NAME = "char_name";
    public static final String FACE = "face";
    public static final String HAIRSTYLE = "hairStyle";
    public static final String HAIRCOLOR = "hairColor";
    public static final String SEX = "sex";
    public static final String HEADING = "heading";
    public static final String X = "x";
    public static final String Y = "y";
    public static final String Z = "z";
    public static final String KARMA = "karma";
    public static final String PVPKILLS = "pvpkills";
    public static final String PKKILLS = "pkkills";
    public static final String CLANID = "clanid";
    public static final String CREATETIME = "createtime";
    public static final String DELETETIME = "deletetime";
    public static final String TITLE = "title";
    public static final String REC_HAVE = "rec_have";
    public static final String REC_LEFT = "rec_left";
    public static final String REC_TIMELEFT = "rec_timeleft";
    public static final String ACCESSLEVEL = "accesslevel";
    public static final String ONLINE = "online";
    public static final String ONLINETIME = "onlinetime";
    public static final String LASTACCESS = "lastAccess";
    public static final String LEAVECLAN = "leaveclan";
    public static final String DELETECLAN = "deleteclan";
    public static final String NOCHANNEL = "nochannel";
    public static final String PLEDGE_TYPE = "pledge_type";
    public static final String PLEDGE_RANK = "pledge_rank";
    public static final String LVL_JOINED_ACADEMY = "lvl_joined_academy";
    public static final String APPRENTICE = "apprentice";
    public static final String KEY_BINDINGS = "key_bindings";
    public static final String PCBANGPOINTS = "pcBangPoints";
    public static final String VITALITY = "vitality";
    public static final String FAME = "fame";
    public static final String BOOKMARKS = "bookmarks";
    public static final String HUNT_BONUS = "hunt_bonus";
    public static final String HUNT_TIMELEFT = "hunt_timeleft";
    public static final String BOT = "bot";
    public static final String LAST_HWID = "last_hwid";
    public static final String FRACTION = "fraction";
    public CharactersTable() {
super(CharactersTable.class);
}
    public StringgetAccount_name() {
        return get(ACCOUNT_NAME, String.class);
    }
    public IntegergetObj_Id() {
        return get(OBJ_ID, Integer.class);
    }
    public StringgetChar_name() {
        return get(CHAR_NAME, String.class);
    }
    public IntegergetFace() {
        return get(FACE, Integer.class);
    }
    public IntegergetHairStyle() {
        return get(HAIRSTYLE, Integer.class);
    }
    public IntegergetHairColor() {
        return get(HAIRCOLOR, Integer.class);
    }
    public IntegergetSex() {
        return get(SEX, Integer.class);
    }
    public IntegergetHeading() {
        return get(HEADING, Integer.class);
    }
    public IntegergetX() {
        return get(X, Integer.class);
    }
    public IntegergetY() {
        return get(Y, Integer.class);
    }
    public IntegergetZ() {
        return get(Z, Integer.class);
    }
    public IntegergetKarma() {
        return get(KARMA, Integer.class);
    }
    public IntegergetPvpkills() {
        return get(PVPKILLS, Integer.class);
    }
    public IntegergetPkkills() {
        return get(PKKILLS, Integer.class);
    }
    public IntegergetClanid() {
        return get(CLANID, Integer.class);
    }
    public IntegergetCreatetime() {
        return get(CREATETIME, Integer.class);
    }
    public IntegergetDeletetime() {
        return get(DELETETIME, Integer.class);
    }
    public StringgetTitle() {
        return get(TITLE, String.class);
    }
    public IntegergetRec_have() {
        return get(REC_HAVE, Integer.class);
    }
    public IntegergetRec_left() {
        return get(REC_LEFT, Integer.class);
    }
    public IntegergetRec_timeleft() {
        return get(REC_TIMELEFT, Integer.class);
    }
    public IntegergetAccesslevel() {
        return get(ACCESSLEVEL, Integer.class);
    }
    public IntegergetOnline() {
        return get(ONLINE, Integer.class);
    }
    public IntegergetOnlinetime() {
        return get(ONLINETIME, Integer.class);
    }
    public IntegergetLastAccess() {
        return get(LASTACCESS, Integer.class);
    }
    public IntegergetLeaveclan() {
        return get(LEAVECLAN, Integer.class);
    }
    public IntegergetDeleteclan() {
        return get(DELETECLAN, Integer.class);
    }
    public IntegergetNochannel() {
        return get(NOCHANNEL, Integer.class);
    }
    public IntegergetPledge_type() {
        return get(PLEDGE_TYPE, Integer.class);
    }
    public IntegergetPledge_rank() {
        return get(PLEDGE_RANK, Integer.class);
    }
    public IntegergetLvl_joined_academy() {
        return get(LVL_JOINED_ACADEMY, Integer.class);
    }
    public IntegergetApprentice() {
        return get(APPRENTICE, Integer.class);
    }
    public ObjectgetKey_bindings() {
        return get(KEY_BINDINGS, Object.class);
    }
    public IntegergetPcBangPoints() {
        return get(PCBANGPOINTS, Integer.class);
    }
    public IntegergetVitality() {
        return get(VITALITY, Integer.class);
    }
    public IntegergetFame() {
        return get(FAME, Integer.class);
    }
    public IntegergetBookmarks() {
        return get(BOOKMARKS, Integer.class);
    }
    public IntegergetHunt_bonus() {
        return get(HUNT_BONUS, Integer.class);
    }
    public IntegergetHunt_timeleft() {
        return get(HUNT_TIMELEFT, Integer.class);
    }
    public IntegergetBot() {
        return get(BOT, Integer.class);
    }
    public StringgetLast_hwid() {
        return get(LAST_HWID, String.class);
    }
    public IntegergetFraction() {
        return get(FRACTION, Integer.class);
    }

    public void setAccount_name() {
        set(ACCOUNT_NAME, value);
    }
    public void setObj_Id() {
        set(OBJ_ID, value);
    }
    public void setChar_name() {
        set(CHAR_NAME, value);
    }
    public void setFace() {
        set(FACE, value);
    }
    public void setHairStyle() {
        set(HAIRSTYLE, value);
    }
    public void setHairColor() {
        set(HAIRCOLOR, value);
    }
    public void setSex() {
        set(SEX, value);
    }
    public void setHeading() {
        set(HEADING, value);
    }
    public void setX() {
        set(X, value);
    }
    public void setY() {
        set(Y, value);
    }
    public void setZ() {
        set(Z, value);
    }
    public void setKarma() {
        set(KARMA, value);
    }
    public void setPvpkills() {
        set(PVPKILLS, value);
    }
    public void setPkkills() {
        set(PKKILLS, value);
    }
    public void setClanid() {
        set(CLANID, value);
    }
    public void setCreatetime() {
        set(CREATETIME, value);
    }
    public void setDeletetime() {
        set(DELETETIME, value);
    }
    public void setTitle() {
        set(TITLE, value);
    }
    public void setRec_have() {
        set(REC_HAVE, value);
    }
    public void setRec_left() {
        set(REC_LEFT, value);
    }
    public void setRec_timeleft() {
        set(REC_TIMELEFT, value);
    }
    public void setAccesslevel() {
        set(ACCESSLEVEL, value);
    }
    public void setOnline() {
        set(ONLINE, value);
    }
    public void setOnlinetime() {
        set(ONLINETIME, value);
    }
    public void setLastAccess() {
        set(LASTACCESS, value);
    }
    public void setLeaveclan() {
        set(LEAVECLAN, value);
    }
    public void setDeleteclan() {
        set(DELETECLAN, value);
    }
    public void setNochannel() {
        set(NOCHANNEL, value);
    }
    public void setPledge_type() {
        set(PLEDGE_TYPE, value);
    }
    public void setPledge_rank() {
        set(PLEDGE_RANK, value);
    }
    public void setLvl_joined_academy() {
        set(LVL_JOINED_ACADEMY, value);
    }
    public void setApprentice() {
        set(APPRENTICE, value);
    }
    public void setKey_bindings() {
        set(KEY_BINDINGS, value);
    }
    public void setPcBangPoints() {
        set(PCBANGPOINTS, value);
    }
    public void setVitality() {
        set(VITALITY, value);
    }
    public void setFame() {
        set(FAME, value);
    }
    public void setBookmarks() {
        set(BOOKMARKS, value);
    }
    public void setHunt_bonus() {
        set(HUNT_BONUS, value);
    }
    public void setHunt_timeleft() {
        set(HUNT_TIMELEFT, value);
    }
    public void setBot() {
        set(BOT, value);
    }
    public void setLast_hwid() {
        set(LAST_HWID, value);
    }
    public void setFraction() {
        set(FRACTION, value);
    }

}
