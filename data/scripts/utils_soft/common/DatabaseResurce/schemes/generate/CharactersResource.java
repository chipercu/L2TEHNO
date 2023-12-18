package utils_soft.common.DatabaseResurce.schemes.generate;

import utils_soft.common.DatabaseResurce.anotations.DefValue;
import utils_soft.common.DatabaseResurce.anotations.Field;
import utils_soft.common.DatabaseResurce.anotations.Table;
import utils_soft.common.DatabaseResurce.DataBaseTable;
import static utils_soft.common.DatabaseResurce.schemes.generate.CharactersResource.*;
import static utils_soft.common.DatabaseResurce.anotations.DATA_TYPE.*;

@Table(
        name = "characters",
        primary_key = {OBJ__ID},
        fields = {
                @Field(name = ACCOUNT_NAME , type = VARCHAR , type_size = 45 , nullable = false , defValue = @DefValue(String = "")),
                @Field(name = OBJ__ID , type = INT , nullable = false , defValue = @DefValue(Integer = 0)),
                @Field(name = CHAR_NAME , type = VARCHAR , type_size = 35 , nullable = false , defValue = @DefValue(String = "")),
                @Field(name = FACE , type = TINYINT , nullable = true , defValue = @DefValue(Integer = NULL)),
                @Field(name = HAIR_STYLE , type = TINYINT , nullable = true , defValue = @DefValue(Integer = NULL)),
                @Field(name = HAIR_COLOR , type = TINYINT , nullable = true , defValue = @DefValue(Integer = NULL)),
                @Field(name = SEX , type = TINYINT , nullable = true , defValue = @DefValue(Integer = NULL)),
                @Field(name = HEADING , type = MEDIUMINT , nullable = true , defValue = @DefValue(Integer = NULL)),
                @Field(name = X , type = MEDIUMINT , nullable = true , defValue = @DefValue(Integer = NULL)),
                @Field(name = Y , type = MEDIUMINT , nullable = true , defValue = @DefValue(Integer = NULL)),
                @Field(name = Z , type = MEDIUMINT , nullable = true , defValue = @DefValue(Integer = NULL)),
                @Field(name = KARMA , type = INT , nullable = true , defValue = @DefValue(Integer = NULL)),
                @Field(name = PVPKILLS , type = INT , nullable = true , defValue = @DefValue(Integer = NULL)),
                @Field(name = PKKILLS , type = INT , nullable = true , defValue = @DefValue(Integer = NULL)),
                @Field(name = CLANID , type = INT , nullable = true , defValue = @DefValue(Integer = NULL)),
                @Field(name = CREATETIME , type = INT , nullable = false , defValue = @DefValue(Integer = 0)),
                @Field(name = DELETETIME , type = INT , nullable = false , defValue = @DefValue(Integer = 0)),
                @Field(name = TITLE , type = VARCHAR , type_size = 16 , nullable = true , defValue = @DefValue(String = "")),
                @Field(name = REC_HAVE , type = TINYINT , nullable = false , defValue = @DefValue(Integer = 0)),
                @Field(name = REC_LEFT , type = TINYINT , nullable = false , defValue = @DefValue(Integer = 20)),
                @Field(name = REC_TIMELEFT , type = INT , nullable = false , defValue = @DefValue(Integer = 3600)),
                @Field(name = ACCESSLEVEL , type = TINYINT , nullable = true , defValue = @DefValue(Integer = NULL)),
                @Field(name = ONLINE , type = TINYINT , nullable = true , defValue = @DefValue(Integer = NULL)),
                @Field(name = ONLINETIME , type = INT , nullable = false , defValue = @DefValue(Integer = 0)),
                @Field(name = LAST_ACCESS , type = INT , nullable = false , defValue = @DefValue(Integer = 0)),
                @Field(name = LEAVECLAN , type = INT , nullable = false , defValue = @DefValue(Integer = 0)),
                @Field(name = DELETECLAN , type = INT , nullable = false , defValue = @DefValue(Integer = 0)),
                @Field(name = NOCHANNEL , type = INT , nullable = false , defValue = @DefValue(Integer = 0)),
                @Field(name = PLEDGE_TYPE , type = SMALLINT , nullable = false , defValue = @DefValue(Integer = 0)),
                @Field(name = PLEDGE_RANK , type = TINYINT , nullable = false , defValue = @DefValue(Integer = 0)),
                @Field(name = LVL_JOINED_ACADEMY , type = TINYINT , nullable = false , defValue = @DefValue(Integer = 0)),
                @Field(name = APPRENTICE , type = INT , nullable = false , defValue = @DefValue(Integer = 0)),
                @Field(name = KEY_BINDINGS , type = VARBINARY , type_size = 8192 , nullable = true , defValue = @DefValue(String = "")),
                @Field(name = PC_BANG_POINTS , type = INT , nullable = false , defValue = @DefValue(Integer = 0)),
                @Field(name = VITALITY , type = SMALLINT , nullable = false , defValue = @DefValue(Integer = 10000)),
                @Field(name = FAME , type = INT , nullable = false , defValue = @DefValue(Integer = 0)),
                @Field(name = BOOKMARKS , type = TINYINT , nullable = false , defValue = @DefValue(Integer = 0)),
                @Field(name = HUNT_BONUS , type = SMALLINT , nullable = true , defValue = @DefValue(Integer = 0)),
                @Field(name = HUNT_TIMELEFT , type = SMALLINT , nullable = true , defValue = @DefValue(Integer = 0)),
                @Field(name = BOT , type = TINYINT , nullable = true , defValue = @DefValue(Integer = 0)),
                @Field(name = LAST_HWID , type = VARCHAR , type_size = 50 , nullable = true , defValue = @DefValue(String = "")),
                @Field(name = FRACTION , type = SMALLINT , nullable = false , defValue = @DefValue(Integer = 0)),
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


    public CharactersResource(String obj_Id){
        super(CharactersResource.class);
        getSTAT_SET().set(OBJ__ID, obj_Id);
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
