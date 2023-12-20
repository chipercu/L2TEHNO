package l2open.database.schemes.builders;

import l2open.database.ResourceBuilder;
import l2open.database.schemes.resources.CharactersResource;

import java.sql.Array;

public class CharactersBuilder extends ResourceBuilder<CharactersResource> {

    public CharactersBuilder() {
        try {
            resourceClass = CharactersResource.class;
            resource = resourceClass.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public CharactersBuilder withAccountName(String value) {
        with(CharactersResource.ACCOUNT_NAME, value);
        return this;
    }

    public CharactersBuilder withObjId(Integer value) {
        with(CharactersResource.OBJ__ID, value);
        return this;
    }

    public CharactersBuilder withCharName(String value) {
        with(CharactersResource.CHAR_NAME, value);
        return this;
    }

    public CharactersBuilder withFace(Integer value) {
        with(CharactersResource.FACE, value);
        return this;
    }

    public CharactersBuilder withHairStyle(Integer value) {
        with(CharactersResource.HAIR_STYLE, value);
        return this;
    }

    public CharactersBuilder withHairColor(Integer value) {
        with(CharactersResource.HAIR_COLOR, value);
        return this;
    }

    public CharactersBuilder withSex(Integer value) {
        with(CharactersResource.SEX, value);
        return this;
    }

    public CharactersBuilder withHeading(Integer value) {
        with(CharactersResource.HEADING, value);
        return this;
    }

    public CharactersBuilder withX(Integer value) {
        with(CharactersResource.X, value);
        return this;
    }

    public CharactersBuilder withY(Integer value) {
        with(CharactersResource.Y, value);
        return this;
    }

    public CharactersBuilder withZ(Integer value) {
        with(CharactersResource.Z, value);
        return this;
    }

    public CharactersBuilder withKarma(Integer value) {
        with(CharactersResource.KARMA, value);
        return this;
    }

    public CharactersBuilder withPvpkills(Integer value) {
        with(CharactersResource.PVPKILLS, value);
        return this;
    }

    public CharactersBuilder withPkkills(Integer value) {
        with(CharactersResource.PKKILLS, value);
        return this;
    }

    public CharactersBuilder withClanid(Integer value) {
        with(CharactersResource.CLANID, value);
        return this;
    }

    public CharactersBuilder withCreatetime(Integer value) {
        with(CharactersResource.CREATETIME, value);
        return this;
    }

    public CharactersBuilder withDeletetime(Integer value) {
        with(CharactersResource.DELETETIME, value);
        return this;
    }

    public CharactersBuilder withTitle(String value) {
        with(CharactersResource.TITLE, value);
        return this;
    }

    public CharactersBuilder withRecHave(Integer value) {
        with(CharactersResource.REC_HAVE, value);
        return this;
    }

    public CharactersBuilder withRecLeft(Integer value) {
        with(CharactersResource.REC_LEFT, value);
        return this;
    }

    public CharactersBuilder withRecTimeleft(Integer value) {
        with(CharactersResource.REC_TIMELEFT, value);
        return this;
    }

    public CharactersBuilder withAccesslevel(Integer value) {
        with(CharactersResource.ACCESSLEVEL, value);
        return this;
    }

    public CharactersBuilder withOnline(Integer value) {
        with(CharactersResource.ONLINE, value);
        return this;
    }

    public CharactersBuilder withOnlinetime(Integer value) {
        with(CharactersResource.ONLINETIME, value);
        return this;
    }

    public CharactersBuilder withLastAccess(Integer value) {
        with(CharactersResource.LAST_ACCESS, value);
        return this;
    }

    public CharactersBuilder withLeaveclan(Integer value) {
        with(CharactersResource.LEAVECLAN, value);
        return this;
    }

    public CharactersBuilder withDeleteclan(Integer value) {
        with(CharactersResource.DELETECLAN, value);
        return this;
    }

    public CharactersBuilder withNochannel(Integer value) {
        with(CharactersResource.NOCHANNEL, value);
        return this;
    }

    public CharactersBuilder withPledgeType(Integer value) {
        with(CharactersResource.PLEDGE_TYPE, value);
        return this;
    }

    public CharactersBuilder withPledgeRank(Integer value) {
        with(CharactersResource.PLEDGE_RANK, value);
        return this;
    }

    public CharactersBuilder withLvlJoinedAcademy(Integer value) {
        with(CharactersResource.LVL_JOINED_ACADEMY, value);
        return this;
    }

    public CharactersBuilder withApprentice(Integer value) {
        with(CharactersResource.APPRENTICE, value);
        return this;
    }

    public CharactersBuilder withKeyBindings(Array value) {
        with(CharactersResource.KEY_BINDINGS, value);
        return this;
    }

    public CharactersBuilder withPcBangPoints(Integer value) {
        with(CharactersResource.PC_BANG_POINTS, value);
        return this;
    }

    public CharactersBuilder withVitality(Integer value) {
        with(CharactersResource.VITALITY, value);
        return this;
    }

    public CharactersBuilder withFame(Integer value) {
        with(CharactersResource.FAME, value);
        return this;
    }

    public CharactersBuilder withBookmarks(Integer value) {
        with(CharactersResource.BOOKMARKS, value);
        return this;
    }

    public CharactersBuilder withHuntBonus(Integer value) {
        with(CharactersResource.HUNT_BONUS, value);
        return this;
    }

    public CharactersBuilder withHuntTimeleft(Integer value) {
        with(CharactersResource.HUNT_TIMELEFT, value);
        return this;
    }

    public CharactersBuilder withBot(Integer value) {
        with(CharactersResource.BOT, value);
        return this;
    }

    public CharactersBuilder withLastHwid(String value) {
        with(CharactersResource.LAST_HWID, value);
        return this;
    }

    public CharactersBuilder withFraction(Integer value) {
        with(CharactersResource.FRACTION, value);
        return this;
    }

}
