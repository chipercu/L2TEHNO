package utils_soft.common.DatabaseResurce.schemes.builders;

import utils_soft.common.DatabaseResurce.ResourceBuilder;
import utils_soft.common.DatabaseResurce.schemes.resources.CharacterPremiumItemsResource;
import java.lang.reflect.Field;
import java.util.NoSuchElementException;

public class CharacterPremiumItemsBuilder extends ResourceBuilder<CharacterPremiumItemsResource> {

    public CharacterPremiumItemsBuilder() {
        try {
            resourceClass = CharacterPremiumItemsResource.class;
            resource = resourceClass.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public CharacterPremiumItemsBuilder withCharId(Integer value) {
        with(CharacterPremiumItemsResource.CHAR_ID, value);
        return this;
    }

    public CharacterPremiumItemsBuilder withItemNum(Integer value) {
        with(CharacterPremiumItemsResource.ITEM_NUM, value);
        return this;
    }

    public CharacterPremiumItemsBuilder withItemId(Integer value) {
        with(CharacterPremiumItemsResource.ITEM_ID, value);
        return this;
    }

    public CharacterPremiumItemsBuilder withItemCount(Long value) {
        with(CharacterPremiumItemsResource.ITEM_COUNT, value);
        return this;
    }

    public CharacterPremiumItemsBuilder withItemSender(String value) {
        with(CharacterPremiumItemsResource.ITEM_SENDER, value);
        return this;
    }

}
