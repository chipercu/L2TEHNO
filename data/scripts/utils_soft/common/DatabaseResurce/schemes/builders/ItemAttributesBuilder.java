package utils_soft.common.DatabaseResurce.schemes.builders;

import utils_soft.common.DatabaseResurce.ResourceBuilder;
import utils_soft.common.DatabaseResurce.schemes.resources.ItemAttributesResource;
import java.lang.reflect.Field;
import java.util.NoSuchElementException;

public class ItemAttributesBuilder extends ResourceBuilder<ItemAttributesResource> {

    public ItemAttributesBuilder() {
        try {
            resourceClass = ItemAttributesResource.class;
            resource = resourceClass.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public ItemAttributesBuilder withItemId(Integer value) {
        with(ItemAttributesResource.ITEM_ID, value);
        return this;
    }

    public ItemAttributesBuilder withAugAttributes(Integer value) {
        with(ItemAttributesResource.AUG_ATTRIBUTES, value);
        return this;
    }

    public ItemAttributesBuilder withAugSkillId(Integer value) {
        with(ItemAttributesResource.AUG_SKILL_ID, value);
        return this;
    }

    public ItemAttributesBuilder withAugSkillLevel(Integer value) {
        with(ItemAttributesResource.AUG_SKILL_LEVEL, value);
        return this;
    }

    public ItemAttributesBuilder withElemType(Integer value) {
        with(ItemAttributesResource.ELEM_TYPE, value);
        return this;
    }

    public ItemAttributesBuilder withElemValue(Integer value) {
        with(ItemAttributesResource.ELEM_VALUE, value);
        return this;
    }

    public ItemAttributesBuilder withElem0(Integer value) {
        with(ItemAttributesResource.ELEM0, value);
        return this;
    }

    public ItemAttributesBuilder withElem1(Integer value) {
        with(ItemAttributesResource.ELEM1, value);
        return this;
    }

    public ItemAttributesBuilder withElem2(Integer value) {
        with(ItemAttributesResource.ELEM2, value);
        return this;
    }

    public ItemAttributesBuilder withElem3(Integer value) {
        with(ItemAttributesResource.ELEM3, value);
        return this;
    }

    public ItemAttributesBuilder withElem4(Integer value) {
        with(ItemAttributesResource.ELEM4, value);
        return this;
    }

    public ItemAttributesBuilder withElem5(Integer value) {
        with(ItemAttributesResource.ELEM5, value);
        return this;
    }

}
