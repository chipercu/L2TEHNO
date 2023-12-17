package utils_soft.common.DatabaseResurce.schemes.generate;

import utils_soft.common.DatabaseResurce.anotations.DefValue;
import utils_soft.common.DatabaseResurce.anotations.Field;
import utils_soft.common.DatabaseResurce.anotations.Table;
import utils_soft.common.DatabaseResurce.DataBaseTable;
import static utils_soft.common.DatabaseResurce.schemes.generate.CharactersResource.*;

@Table(
        name = "characters",
        primary_key = {OBJ__ID},
        fields = {
                @Field(name = ACCOUNT_NAME , data_type = "varchar" , defValue = @DefValue(String = "")),
                @Field(name = OBJ__ID , data_type = "int" , defValue = @DefValue(Integer = 0)),
                @Field(name = CHAR_NAME , data_type = "varchar" , defValue = @DefValue(String = "")),
                @Field(name = FACE , data_type = "tinyint"),
                @Field(name = HAIR_STYLE , data_type = "tinyint"),
                @Field(name = HAIR_COLOR , data_type = "tinyint"),
                @Field(name = SEX , data_type = "tinyint"),
                @Field(name = HEADING , data_type = "mediumint"),
                @Field(name = X , data_type = "mediumint"),
                @Field(name = Y , data_type = "mediumint"),
                @Field(name = Z , data_type = "mediumint"),
                @Field(name = KARMA , data_type = "int"),
                @Field(name = PVPKILLS , data_type = "int"),
                @Field(name = PKKILLS , data_type = "int"),
                @Field(name = CLANID , data_type = "int"),
                @Field(name = CREATETIME , data_type = "int" , defValue = @DefValue(Integer = 0)),
                @Field(name = DELETETIME , data_type = "int" , defValue = @DefValue(Integer = 0)),
                @Field(name = TITLE , data_type = "varchar"),
                @Field(name = REC_HAVE , data_type = "tinyint" , defValue = @DefValue(Integer = 0)),
                @Field(name = REC_LEFT , data_type = "tinyint" , defValue = @DefValue(Integer = 20)),
                @Field(name = REC_TIMELEFT , data_type = "int" , defValue = @DefValue(Integer = 3600)),
                @Field(name = ACCESSLEVEL , data_type = "tinyint"),
                @Field(name = ONLINE , data_type = "tinyint"),
                @Field(name = ONLINETIME , data_type = "int" , defValue = @DefValue(Integer = 0)),
                @Field(name = LAST_ACCESS , data_type = "int" , defValue = @DefValue(Integer = 0)),
                @Field(name = LEAVECLAN , data_type = "int" , defValue = @DefValue(Integer = 0)),
                @Field(name = DELETECLAN , data_type = "int" , defValue = @DefValue(Integer = 0)),
                @Field(name = NOCHANNEL , data_type = "int" , defValue = @DefValue(Integer = 0)),
                @Field(name = PLEDGE_TYPE , data_type = "smallint" , defValue = @DefValue(Integer = 0)),
                @Field(name = PLEDGE_RANK , data_type = "tinyint" , defValue = @DefValue(Integer = 0)),
                @Field(name = LVL_JOINED_ACADEMY , data_type = "tinyint" , defValue = @DefValue(Integer = 0)),
                @Field(name = APPRENTICE , data_type = "int" , defValue = @DefValue(Integer = 0)),
                @Field(name = KEY_BINDINGS , data_type = "varbinary"),
                @Field(name = PC_BANG_POINTS , data_type = "int" , defValue = @DefValue(Integer = 0)),
                @Field(name = VITALITY , data_type = "smallint" , defValue = @DefValue(Integer = 10000)),
                @Field(name = FAME , data_type = "int" , defValue = @DefValue(Integer = 0)),
                @Field(name = BOOKMARKS , data_type = "tinyint" , defValue = @DefValue(Integer = 0)),
                @Field(name = HUNT_BONUS , data_type = "smallint" , defValue = @DefValue(Integer = 0)),
                @Field(name = HUNT_TIMELEFT , data_type = "smallint" , defValue = @DefValue(Integer = 0)),
                @Field(name = BOT , data_type = "tinyint" , defValue = @DefValue(Integer = 0)),
                @Field(name = LAST_HWID , data_type = "varchar" , defValue = @DefValue(String = "")),
                @Field(name = FRACTION , data_type = "smallint" , defValue = @DefValue(Integer = 0)),
        }
)
public class CharactersResource extends DataBaseTable<CharactersResource> {

    public static final String ACCOUNT_NAME = "account_name";
    public static final String OBJ__ID = "obj_Id";
    public static final String CHAR_NAME = "char_name";
    public static final String FACE = "face";
    public static final String HAIR_STYLE = "hairStyle";
    public static final String HAIR_COLOR = "hairColor";
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
    public static final String LAST_ACCESS = "lastAccess";
    public static final String LEAVECLAN = "leaveclan";
    public static final String DELETECLAN = "deleteclan";
    public static final String NOCHANNEL = "nochannel";
    public static final String PLEDGE_TYPE = "pledge_type";
    public static final String PLEDGE_RANK = "pledge_rank";
    public static final String LVL_JOINED_ACADEMY = "lvl_joined_academy";
    public static final String APPRENTICE = "apprentice";
    public static final String KEY_BINDINGS = "key_bindings";
    public static final String PC_BANG_POINTS = "pcBangPoints";
    public static final String VITALITY = "vitality";
    public static final String FAME = "fame";
    public static final String BOOKMARKS = "bookmarks";
    public static final String HUNT_BONUS = "hunt_bonus";
    public static final String HUNT_TIMELEFT = "hunt_timeleft";
    public static final String BOT = "bot";
    public static final String LAST_HWID = "last_hwid";
    public static final String FRACTION = "fraction";

    public CharactersResource() {
super(CharactersResource.class);
}

    public String getAccountName() {
        return get(ACCOUNT_NAME, String.class);
    }
    public Integer getObjId() {
        return get(OBJ__ID, Integer.class);
    }
    public String getCharName() {
        return get(CHAR_NAME, String.class);
    }
    public Integer getFace() {
        return get(FACE, Integer.class);
    }
    public Integer getHairStyle() {
        return get(HAIR_STYLE, Integer.class);
    }
    public Integer getHairColor() {
        return get(HAIR_COLOR, Integer.class);
    }
    public Integer getSex() {
        return get(SEX, Integer.class);
    }
    public Integer getHeading() {
        return get(HEADING, Integer.class);
    }
    public Integer getX() {
        return get(X, Integer.class);
    }
    public Integer getY() {
        return get(Y, Integer.class);
    }
    public Integer getZ() {
        return get(Z, Integer.class);
    }
    public Integer getKarma() {
        return get(KARMA, Integer.class);
    }
    public Integer getPvpkills() {
        return get(PVPKILLS, Integer.class);
    }
    public Integer getPkkills() {
        return get(PKKILLS, Integer.class);
    }
    public Integer getClanid() {
        return get(CLANID, Integer.class);
    }
    public Integer getCreatetime() {
        return get(CREATETIME, Integer.class);
    }
    public Integer getDeletetime() {
        return get(DELETETIME, Integer.class);
    }
    public String getTitle() {
        return get(TITLE, String.class);
    }
    public Integer getRecHave() {
        return get(REC_HAVE, Integer.class);
    }
    public Integer getRecLeft() {
        return get(REC_LEFT, Integer.class);
    }
    public Integer getRecTimeleft() {
        return get(REC_TIMELEFT, Integer.class);
    }
    public Integer getAccesslevel() {
        return get(ACCESSLEVEL, Integer.class);
    }
    public Integer getOnline() {
        return get(ONLINE, Integer.class);
    }
    public Integer getOnlinetime() {
        return get(ONLINETIME, Integer.class);
    }
    public Integer getLastAccess() {
        return get(LAST_ACCESS, Integer.class);
    }
    public Integer getLeaveclan() {
        return get(LEAVECLAN, Integer.class);
    }
    public Integer getDeleteclan() {
        return get(DELETECLAN, Integer.class);
    }
    public Integer getNochannel() {
        return get(NOCHANNEL, Integer.class);
    }
    public Integer getPledgeType() {
        return get(PLEDGE_TYPE, Integer.class);
    }
    public Integer getPledgeRank() {
        return get(PLEDGE_RANK, Integer.class);
    }
    public Integer getLvlJoinedAcademy() {
        return get(LVL_JOINED_ACADEMY, Integer.class);
    }
    public Integer getApprentice() {
        return get(APPRENTICE, Integer.class);
    }
    public String getKeyBindings() {
        return get(KEY_BINDINGS, String.class);
    }
    public Integer getPcBangPoints() {
        return get(PC_BANG_POINTS, Integer.class);
    }
    public Integer getVitality() {
        return get(VITALITY, Integer.class);
    }
    public Integer getFame() {
        return get(FAME, Integer.class);
    }
    public Integer getBookmarks() {
        return get(BOOKMARKS, Integer.class);
    }
    public Integer getHuntBonus() {
        return get(HUNT_BONUS, Integer.class);
    }
    public Integer getHuntTimeleft() {
        return get(HUNT_TIMELEFT, Integer.class);
    }
    public Integer getBot() {
        return get(BOT, Integer.class);
    }
    public String getLastHwid() {
        return get(LAST_HWID, String.class);
    }
    public Integer getFraction() {
        return get(FRACTION, Integer.class);
    }

    public void setAccountName(String value) {
        set(ACCOUNT_NAME, value);
    }
    public void setObjId(Integer value) {
        set(OBJ__ID, value);
    }
    public void setCharName(String value) {
        set(CHAR_NAME, value);
    }
    public void setFace(Integer value) {
        set(FACE, value);
    }
    public void setHairStyle(Integer value) {
        set(HAIR_STYLE, value);
    }
    public void setHairColor(Integer value) {
        set(HAIR_COLOR, value);
    }
    public void setSex(Integer value) {
        set(SEX, value);
    }
    public void setHeading(Integer value) {
        set(HEADING, value);
    }
    public void setX(Integer value) {
        set(X, value);
    }
    public void setY(Integer value) {
        set(Y, value);
    }
    public void setZ(Integer value) {
        set(Z, value);
    }
    public void setKarma(Integer value) {
        set(KARMA, value);
    }
    public void setPvpkills(Integer value) {
        set(PVPKILLS, value);
    }
    public void setPkkills(Integer value) {
        set(PKKILLS, value);
    }
    public void setClanid(Integer value) {
        set(CLANID, value);
    }
    public void setCreatetime(Integer value) {
        set(CREATETIME, value);
    }
    public void setDeletetime(Integer value) {
        set(DELETETIME, value);
    }
    public void setTitle(String value) {
        set(TITLE, value);
    }
    public void setRecHave(Integer value) {
        set(REC_HAVE, value);
    }
    public void setRecLeft(Integer value) {
        set(REC_LEFT, value);
    }
    public void setRecTimeleft(Integer value) {
        set(REC_TIMELEFT, value);
    }
    public void setAccesslevel(Integer value) {
        set(ACCESSLEVEL, value);
    }
    public void setOnline(Integer value) {
        set(ONLINE, value);
    }
    public void setOnlinetime(Integer value) {
        set(ONLINETIME, value);
    }
    public void setLastAccess(Integer value) {
        set(LAST_ACCESS, value);
    }
    public void setLeaveclan(Integer value) {
        set(LEAVECLAN, value);
    }
    public void setDeleteclan(Integer value) {
        set(DELETECLAN, value);
    }
    public void setNochannel(Integer value) {
        set(NOCHANNEL, value);
    }
    public void setPledgeType(Integer value) {
        set(PLEDGE_TYPE, value);
    }
    public void setPledgeRank(Integer value) {
        set(PLEDGE_RANK, value);
    }
    public void setLvlJoinedAcademy(Integer value) {
        set(LVL_JOINED_ACADEMY, value);
    }
    public void setApprentice(Integer value) {
        set(APPRENTICE, value);
    }
    public void setKeyBindings(String value) {
        set(KEY_BINDINGS, value);
    }
    public void setPcBangPoints(Integer value) {
        set(PC_BANG_POINTS, value);
    }
    public void setVitality(Integer value) {
        set(VITALITY, value);
    }
    public void setFame(Integer value) {
        set(FAME, value);
    }
    public void setBookmarks(Integer value) {
        set(BOOKMARKS, value);
    }
    public void setHuntBonus(Integer value) {
        set(HUNT_BONUS, value);
    }
    public void setHuntTimeleft(Integer value) {
        set(HUNT_TIMELEFT, value);
    }
    public void setBot(Integer value) {
        set(BOT, value);
    }
    public void setLastHwid(String value) {
        set(LAST_HWID, value);
    }
    public void setFraction(Integer value) {
        set(FRACTION, value);
    }

}
