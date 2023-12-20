package l2open.database.schemes.builders;

import l2open.database.ResourceBuilder;
import l2open.database.schemes.resources.NpcElementResource;

public class NpcElementBuilder extends ResourceBuilder<NpcElementResource> {

    public NpcElementBuilder() {
        try {
            resourceClass = NpcElementResource.class;
            resource = resourceClass.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public NpcElementBuilder withId(Integer value) {
        with(NpcElementResource.ID, value);
        return this;
    }

    public NpcElementBuilder withAtkElement(Integer value) {
        with(NpcElementResource.ATK_ELEMENT, value);
        return this;
    }

    public NpcElementBuilder withElemAtkPower(Integer value) {
        with(NpcElementResource.ELEM_ATK_POWER, value);
        return this;
    }

    public NpcElementBuilder withFireRes(Integer value) {
        with(NpcElementResource.FIRE_RES, value);
        return this;
    }

    public NpcElementBuilder withWaterRes(Integer value) {
        with(NpcElementResource.WATER_RES, value);
        return this;
    }

    public NpcElementBuilder withWindRes(Integer value) {
        with(NpcElementResource.WIND_RES, value);
        return this;
    }

    public NpcElementBuilder withEarthRes(Integer value) {
        with(NpcElementResource.EARTH_RES, value);
        return this;
    }

    public NpcElementBuilder withHolyRes(Integer value) {
        with(NpcElementResource.HOLY_RES, value);
        return this;
    }

    public NpcElementBuilder withDarkRes(Integer value) {
        with(NpcElementResource.DARK_RES, value);
        return this;
    }

}
