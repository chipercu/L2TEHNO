package utils_soft.common.DatabaseResurce.schemes.builders;

import utils_soft.common.DatabaseResurce.ResourceBuilder;
import utils_soft.common.DatabaseResurce.schemes.resources.DoorsResource;
import java.lang.reflect.Field;
import java.util.NoSuchElementException;

public class DoorsBuilder extends ResourceBuilder<DoorsResource> {

    public DoorsBuilder() {
        try {
            resourceClass = DoorsResource.class;
            resource = resourceClass.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public DoorsBuilder withId(Integer value) {
        with(DoorsResource.ID, value);
        return this;
    }

    public DoorsBuilder withName(String value) {
        with(DoorsResource.NAME, value);
        return this;
    }

    public DoorsBuilder withPtsName(String value) {
        with(DoorsResource.PTS_NAME, value);
        return this;
    }

    public DoorsBuilder withHp(Integer value) {
        with(DoorsResource.HP, value);
        return this;
    }

    public DoorsBuilder withPdef(Integer value) {
        with(DoorsResource.PDEF, value);
        return this;
    }

    public DoorsBuilder withMdef(Integer value) {
        with(DoorsResource.MDEF, value);
        return this;
    }

    public DoorsBuilder withUnlockable(Integer value) {
        with(DoorsResource.UNLOCKABLE, value);
        return this;
    }

    public DoorsBuilder withKey(Integer value) {
        with(DoorsResource.KEY, value);
        return this;
    }

    public DoorsBuilder withLevel(Integer value) {
        with(DoorsResource.LEVEL, value);
        return this;
    }

    public DoorsBuilder withShowHp(Integer value) {
        with(DoorsResource.SHOW_HP, value);
        return this;
    }

    public DoorsBuilder withPosx(Integer value) {
        with(DoorsResource.POSX, value);
        return this;
    }

    public DoorsBuilder withPosy(Integer value) {
        with(DoorsResource.POSY, value);
        return this;
    }

    public DoorsBuilder withPosz(Integer value) {
        with(DoorsResource.POSZ, value);
        return this;
    }

    public DoorsBuilder withAx(Integer value) {
        with(DoorsResource.AX, value);
        return this;
    }

    public DoorsBuilder withAy(Integer value) {
        with(DoorsResource.AY, value);
        return this;
    }

    public DoorsBuilder withBx(Integer value) {
        with(DoorsResource.BX, value);
        return this;
    }

    public DoorsBuilder withBy(Integer value) {
        with(DoorsResource.BY, value);
        return this;
    }

    public DoorsBuilder withCx(Integer value) {
        with(DoorsResource.CX, value);
        return this;
    }

    public DoorsBuilder withCy(Integer value) {
        with(DoorsResource.CY, value);
        return this;
    }

    public DoorsBuilder withDx(Integer value) {
        with(DoorsResource.DX, value);
        return this;
    }

    public DoorsBuilder withDy(Integer value) {
        with(DoorsResource.DY, value);
        return this;
    }

    public DoorsBuilder withMinz(Integer value) {
        with(DoorsResource.MINZ, value);
        return this;
    }

    public DoorsBuilder withMaxz(Integer value) {
        with(DoorsResource.MAXZ, value);
        return this;
    }

    public DoorsBuilder withSiegeWeapon(Enum value) {
        with(DoorsResource.SIEGE_WEAPON, value);
        return this;
    }

    public DoorsBuilder withGeodata(Enum value) {
        with(DoorsResource.GEODATA, value);
        return this;
    }

}
