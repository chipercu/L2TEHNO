package l2open.database.schemes.builders;

import l2open.database.ResourceBuilder;
import l2open.database.schemes.resources.CharacterPremiumItemsResource;

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
