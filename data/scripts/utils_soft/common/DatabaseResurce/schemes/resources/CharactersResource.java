package utils_soft.common.DatabaseResurce.schemes.resources;

import utils_soft.common.DatabaseResurce.anotations.Column;
import utils_soft.common.DatabaseResurce.anotations.Table;
import utils_soft.common.DatabaseResurce.DataBaseTable;
import static utils_soft.common.DatabaseResurce.schemes.resources.CharactersResource.*;
import static utils_soft.common.DatabaseResurce.anotations.IS_NULLABLE.*;
import static utils_soft.common.DatabaseResurce.anotations.DATA_TYPE.*;

@Table(
        name = "characters",
        primary_key = {OBJ__ID},
        fields = {
                @Column(is_null = NO , type = VARCHAR , type_size = 45 , name = ACCOUNT_NAME , defValue = ""),
                @Column(is_null = NO , type = INT , type_size = 0 , name = OBJ__ID , defValue = "0"),
                @Column(is_null = NO , type = VARCHAR , type_size = 35 , name = CHAR_NAME , defValue = ""),
                @Column(is_null = YES , type = TINYINT , type_size = 0 , name = FACE , defValue = "NULL"),
                @Column(is_null = YES , type = TINYINT , type_size = 0 , name = HAIR_STYLE , defValue = "NULL"),
                @Column(is_null = YES , type = TINYINT , type_size = 0 , name = HAIR_COLOR , defValue = "NULL"),
                @Column(is_null = YES , type = TINYINT , type_size = 0 , name = SEX , defValue = "NULL"),
                @Column(is_null = YES , type = MEDIUMINT , type_size = 0 , name = HEADING , defValue = "NULL"),
                @Column(is_null = YES , type = MEDIUMINT , type_size = 0 , name = X , defValue = "NULL"),
                @Column(is_null = YES , type = MEDIUMINT , type_size = 0 , name = Y , defValue = "NULL"),
                @Column(is_null = YES , type = MEDIUMINT , type_size = 0 , name = Z , defValue = "NULL"),
                @Column(is_null = YES , type = INT , type_size = 0 , name = KARMA , defValue = "NULL"),
                @Column(is_null = YES , type = INT , type_size = 0 , name = PVPKILLS , defValue = "NULL"),
                @Column(is_null = YES , type = INT , type_size = 0 , name = PKKILLS , defValue = "NULL"),
                @Column(is_null = YES , type = INT , type_size = 0 , name = CLANID , defValue = "NULL"),
                @Column(is_null = NO , type = INT , type_size = 0 , name = CREATETIME , defValue = "0"),
                @Column(is_null = NO , type = INT , type_size = 0 , name = DELETETIME , defValue = "0"),
                @Column(is_null = YES , type = VARCHAR , type_size = 16 , name = TITLE , defValue = "NULL"),
                @Column(is_null = NO , type = TINYINT , type_size = 0 , name = REC_HAVE , defValue = "0"),
                @Column(is_null = NO , type = TINYINT , type_size = 0 , name = REC_LEFT , defValue = "20"),
                @Column(is_null = NO , type = INT , type_size = 0 , name = REC_TIMELEFT , defValue = "3600"),
                @Column(is_null = YES , type = TINYINT , type_size = 0 , name = ACCESSLEVEL , defValue = "NULL"),
                @Column(is_null = YES , type = TINYINT , type_size = 0 , name = ONLINE , defValue = "NULL"),
                @Column(is_null = NO , type = INT , type_size = 0 , name = ONLINETIME , defValue = "0"),
                @Column(is_null = NO , type = INT , type_size = 0 , name = LAST_ACCESS , defValue = "0"),
                @Column(is_null = NO , type = INT , type_size = 0 , name = LEAVECLAN , defValue = "0"),
                @Column(is_null = NO , type = INT , type_size = 0 , name = DELETECLAN , defValue = "0"),
                @Column(is_null = NO , type = INT , type_size = 0 , name = NOCHANNEL , defValue = "0"),
                @Column(is_null = NO , type = SMALLINT , type_size = 0 , name = PLEDGE_TYPE , defValue = "0"),
                @Column(is_null = NO , type = TINYINT , type_size = 0 , name = PLEDGE_RANK , defValue = "0"),
                @Column(is_null = NO , type = TINYINT , type_size = 0 , name = LVL_JOINED_ACADEMY , defValue = "0"),
                @Column(is_null = NO , type = INT , type_size = 0 , name = APPRENTICE , defValue = "0"),
                @Column(is_null = YES , type = VARBINARY , type_size = 8192 , name = KEY_BINDINGS , defValue = "NULL"),
                @Column(is_null = NO , type = INT , type_size = 0 , name = PC_BANG_POINTS , defValue = "0"),
                @Column(is_null = NO , type = SMALLINT , type_size = 0 , name = VITALITY , defValue = "10000"),
                @Column(is_null = NO , type = INT , type_size = 0 , name = FAME , defValue = "0"),
                @Column(is_null = NO , type = TINYINT , type_size = 0 , name = BOOKMARKS , defValue = "0"),
                @Column(is_null = YES , type = SMALLINT , type_size = 0 , name = HUNT_BONUS , defValue = "0"),
                @Column(is_null = YES , type = SMALLINT , type_size = 0 , name = HUNT_TIMELEFT , defValue = "0"),
                @Column(is_null = YES , type = TINYINT , type_size = 0 , name = BOT , defValue = "0"),
                @Column(is_null = YES , type = VARCHAR , type_size = 50 , name = LAST_HWID , defValue = ""),
                @Column(is_null = NO , type = SMALLINT , type_size = 0 , name = FRACTION , defValue = "0"),
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
        return get(ACCOUNT_NAME);
    }
    public Integer getObjId() {
        return get(OBJ__ID);
    }
    public String getCharName() {
        return get(CHAR_NAME);
    }
    public Integer getFace() {
        return get(FACE);
    }
    public Integer getHairStyle() {
        return get(HAIR_STYLE);
    }
    public Integer getHairColor() {
        return get(HAIR_COLOR);
    }
    public Integer getSex() {
        return get(SEX);
    }
    public Integer getHeading() {
        return get(HEADING);
    }
    public Integer getX() {
        return get(X);
    }
    public Integer getY() {
        return get(Y);
    }
    public Integer getZ() {
        return get(Z);
    }
    public Integer getKarma() {
        return get(KARMA);
    }
    public Integer getPvpkills() {
        return get(PVPKILLS);
    }
    public Integer getPkkills() {
        return get(PKKILLS);
    }
    public Integer getClanid() {
        return get(CLANID);
    }
    public Integer getCreatetime() {
        return get(CREATETIME);
    }
    public Integer getDeletetime() {
        return get(DELETETIME);
    }
    public String getTitle() {
        return get(TITLE);
    }
    public Integer getRecHave() {
        return get(REC_HAVE);
    }
    public Integer getRecLeft() {
        return get(REC_LEFT);
    }
    public Integer getRecTimeleft() {
        return get(REC_TIMELEFT);
    }
    public Integer getAccesslevel() {
        return get(ACCESSLEVEL);
    }
    public Integer getOnline() {
        return get(ONLINE);
    }
    public Integer getOnlinetime() {
        return get(ONLINETIME);
    }
    public Integer getLastAccess() {
        return get(LAST_ACCESS);
    }
    public Integer getLeaveclan() {
        return get(LEAVECLAN);
    }
    public Integer getDeleteclan() {
        return get(DELETECLAN);
    }
    public Integer getNochannel() {
        return get(NOCHANNEL);
    }
    public Integer getPledgeType() {
        return get(PLEDGE_TYPE);
    }
    public Integer getPledgeRank() {
        return get(PLEDGE_RANK);
    }
    public Integer getLvlJoinedAcademy() {
        return get(LVL_JOINED_ACADEMY);
    }
    public Integer getApprentice() {
        return get(APPRENTICE);
    }
    public String getKeyBindings() {
        return get(KEY_BINDINGS);
    }
    public Integer getPcBangPoints() {
        return get(PC_BANG_POINTS);
    }
    public Integer getVitality() {
        return get(VITALITY);
    }
    public Integer getFame() {
        return get(FAME);
    }
    public Integer getBookmarks() {
        return get(BOOKMARKS);
    }
    public Integer getHuntBonus() {
        return get(HUNT_BONUS);
    }
    public Integer getHuntTimeleft() {
        return get(HUNT_TIMELEFT);
    }
    public Integer getBot() {
        return get(BOT);
    }
    public String getLastHwid() {
        return get(LAST_HWID);
    }
    public Integer getFraction() {
        return get(FRACTION);
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
