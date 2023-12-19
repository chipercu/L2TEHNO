package utils_soft.common.DatabaseResurce.schemes.builders;

import utils_soft.common.DatabaseResurce.ResourceBuilder;
import utils_soft.common.DatabaseResurce.schemes.resources.RecitemsResource;
import java.lang.reflect.Field;
import java.util.NoSuchElementException;

public class RecitemsBuilder extends ResourceBuilder<RecitemsResource> {

    public RecitemsBuilder() {
        try {
            resourceClass = RecitemsResource.class;
            resource = resourceClass.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public RecitemsBuilder withRid(Integer value) {
        with(RecitemsResource.RID, value);
        return this;
    }

    public RecitemsBuilder withItem(Integer value) {
        with(RecitemsResource.ITEM, value);
        return this;
    }

    public RecitemsBuilder withQ(Integer value) {
        with(RecitemsResource.Q, value);
        return this;
    }

    public RecitemsBuilder withHasRecipe(Integer value) {
        with(RecitemsResource.HAS_RECIPE, value);
        return this;
    }

}
