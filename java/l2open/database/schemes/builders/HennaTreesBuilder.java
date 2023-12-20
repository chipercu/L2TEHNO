package l2open.database.schemes.builders;

import l2open.database.ResourceBuilder;
import l2open.database.schemes.resources.HennaTreesResource;

public class HennaTreesBuilder extends ResourceBuilder<HennaTreesResource> {

    public HennaTreesBuilder() {
        try {
            resourceClass = HennaTreesResource.class;
            resource = resourceClass.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public HennaTreesBuilder withClassId(Integer value) {
        with(HennaTreesResource.CLASS_ID, value);
        return this;
    }

    public HennaTreesBuilder withSymbolId(Integer value) {
        with(HennaTreesResource.SYMBOL_ID, value);
        return this;
    }

}
