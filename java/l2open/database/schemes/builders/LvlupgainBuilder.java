package l2open.database.schemes.builders;

import l2open.database.ResourceBuilder;
import l2open.database.schemes.resources.LvlupgainResource;

public class LvlupgainBuilder extends ResourceBuilder<LvlupgainResource> {

    public LvlupgainBuilder() {
        try {
            resourceClass = LvlupgainResource.class;
            resource = resourceClass.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public LvlupgainBuilder withClassid(Integer value) {
        with(LvlupgainResource.CLASSID, value);
        return this;
    }

    public LvlupgainBuilder withDefaulthpbase(Double value) {
        with(LvlupgainResource.DEFAULTHPBASE, value);
        return this;
    }

    public LvlupgainBuilder withDefaulthpadd(Double value) {
        with(LvlupgainResource.DEFAULTHPADD, value);
        return this;
    }

    public LvlupgainBuilder withDefaulthpmod(Double value) {
        with(LvlupgainResource.DEFAULTHPMOD, value);
        return this;
    }

    public LvlupgainBuilder withDefaultcpbase(Double value) {
        with(LvlupgainResource.DEFAULTCPBASE, value);
        return this;
    }

    public LvlupgainBuilder withDefaultcpadd(Double value) {
        with(LvlupgainResource.DEFAULTCPADD, value);
        return this;
    }

    public LvlupgainBuilder withDefaultcpmod(Double value) {
        with(LvlupgainResource.DEFAULTCPMOD, value);
        return this;
    }

    public LvlupgainBuilder withDefaultmpbase(Double value) {
        with(LvlupgainResource.DEFAULTMPBASE, value);
        return this;
    }

    public LvlupgainBuilder withDefaultmpadd(Double value) {
        with(LvlupgainResource.DEFAULTMPADD, value);
        return this;
    }

    public LvlupgainBuilder withDefaultmpmod(Double value) {
        with(LvlupgainResource.DEFAULTMPMOD, value);
        return this;
    }

    public LvlupgainBuilder withClassLvl(Integer value) {
        with(LvlupgainResource.CLASS_LVL, value);
        return this;
    }

}
