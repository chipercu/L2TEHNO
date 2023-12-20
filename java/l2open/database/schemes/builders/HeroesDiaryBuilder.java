package l2open.database.schemes.builders;

import l2open.database.ResourceBuilder;
import l2open.database.schemes.resources.HeroesDiaryResource;

public class HeroesDiaryBuilder extends ResourceBuilder<HeroesDiaryResource> {

    public HeroesDiaryBuilder() {
        try {
            resourceClass = HeroesDiaryResource.class;
            resource = resourceClass.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public HeroesDiaryBuilder withCharId(Integer value) {
        with(HeroesDiaryResource.CHAR_ID, value);
        return this;
    }

    public HeroesDiaryBuilder withTime(Long value) {
        with(HeroesDiaryResource._TIME, value);
        return this;
    }

    public HeroesDiaryBuilder withAction(Integer value) {
        with(HeroesDiaryResource.ACTION, value);
        return this;
    }

    public HeroesDiaryBuilder withParam(Integer value) {
        with(HeroesDiaryResource.PARAM, value);
        return this;
    }

}
