package l2open.database.schemes.builders;

import l2open.database.ResourceBuilder;
import l2open.database.schemes.resources.CursedWeaponsResource;

public class CursedWeaponsBuilder extends ResourceBuilder<CursedWeaponsResource> {

    public CursedWeaponsBuilder() {
        try {
            resourceClass = CursedWeaponsResource.class;
            resource = resourceClass.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public CursedWeaponsBuilder withItemId(Integer value) {
        with(CursedWeaponsResource.ITEM_ID, value);
        return this;
    }

    public CursedWeaponsBuilder withPlayerId(Integer value) {
        with(CursedWeaponsResource.PLAYER_ID, value);
        return this;
    }

    public CursedWeaponsBuilder withPlayerKarma(Integer value) {
        with(CursedWeaponsResource.PLAYER_KARMA, value);
        return this;
    }

    public CursedWeaponsBuilder withPlayerPkkills(Integer value) {
        with(CursedWeaponsResource.PLAYER_PKKILLS, value);
        return this;
    }

    public CursedWeaponsBuilder withNbKills(Integer value) {
        with(CursedWeaponsResource.NB_KILLS, value);
        return this;
    }

    public CursedWeaponsBuilder withX(Integer value) {
        with(CursedWeaponsResource.X, value);
        return this;
    }

    public CursedWeaponsBuilder withY(Integer value) {
        with(CursedWeaponsResource.Y, value);
        return this;
    }

    public CursedWeaponsBuilder withZ(Integer value) {
        with(CursedWeaponsResource.Z, value);
        return this;
    }

    public CursedWeaponsBuilder withEndTime(Integer value) {
        with(CursedWeaponsResource.END_TIME, value);
        return this;
    }

}
