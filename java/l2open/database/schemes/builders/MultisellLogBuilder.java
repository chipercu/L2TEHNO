package l2open.database.schemes.builders;

import l2open.database.ResourceBuilder;
import l2open.database.schemes.resources.MultisellLogResource;

public class MultisellLogBuilder extends ResourceBuilder<MultisellLogResource> {

    public MultisellLogBuilder() {
        try {
            resourceClass = MultisellLogResource.class;
            resource = resourceClass.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public MultisellLogBuilder withId(Integer value) {
        with(MultisellLogResource.ID, value);
        return this;
    }

    public MultisellLogBuilder withDate(String value) {
        with(MultisellLogResource._DATE, value);
        return this;
    }

    public MultisellLogBuilder withItemId(String value) {
        with(MultisellLogResource.ITEM_ID, value);
        return this;
    }

    public MultisellLogBuilder withCount(String value) {
        with(MultisellLogResource.COUNT, value);
        return this;
    }

    public MultisellLogBuilder withDItemId(String value) {
        with(MultisellLogResource.D_ITEM_ID, value);
        return this;
    }

    public MultisellLogBuilder withDCount(String value) {
        with(MultisellLogResource.D_COUNT, value);
        return this;
    }

    public MultisellLogBuilder withName(String value) {
        with(MultisellLogResource.NAME, value);
        return this;
    }

}