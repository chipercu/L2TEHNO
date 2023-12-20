package l2open.database.schemes.builders;

import l2open.database.ResourceBuilder;
import l2open.database.schemes.resources.HeroesResource;

public class HeroesBuilder extends ResourceBuilder<HeroesResource> {

    public HeroesBuilder() {
        try {
            resourceClass = HeroesResource.class;
            resource = resourceClass.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public HeroesBuilder withCharId(Integer value) {
        with(HeroesResource.CHAR_ID, value);
        return this;
    }

    public HeroesBuilder withCount(Integer value) {
        with(HeroesResource.COUNT, value);
        return this;
    }

    public HeroesBuilder withPlayed(Integer value) {
        with(HeroesResource.PLAYED, value);
        return this;
    }

    public HeroesBuilder withActive(Integer value) {
        with(HeroesResource.ACTIVE, value);
        return this;
    }

    public HeroesBuilder withMessage(String value) {
        with(HeroesResource.MESSAGE, value);
        return this;
    }

}
