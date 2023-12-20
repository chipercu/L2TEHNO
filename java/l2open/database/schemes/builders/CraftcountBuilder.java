package l2open.database.schemes.builders;

import l2open.database.ResourceBuilder;
import l2open.database.schemes.resources.CraftcountResource;

public class CraftcountBuilder extends ResourceBuilder<CraftcountResource> {

    public CraftcountBuilder() {
        try {
            resourceClass = CraftcountResource.class;
            resource = resourceClass.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public CraftcountBuilder withCharId(Integer value) {
        with(CraftcountResource.CHAR_ID, value);
        return this;
    }

    public CraftcountBuilder withItemId(Integer value) {
        with(CraftcountResource.ITEM_ID, value);
        return this;
    }

    public CraftcountBuilder withCount(Long value) {
        with(CraftcountResource.COUNT, value);
        return this;
    }

}
