package l2open.database.schemes.builders;

import l2open.database.ResourceBuilder;
import l2open.database.schemes.resources.CharacterHennasResource;

public class CharacterHennasBuilder extends ResourceBuilder<CharacterHennasResource> {

    public CharacterHennasBuilder() {
        try {
            resourceClass = CharacterHennasResource.class;
            resource = resourceClass.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public CharacterHennasBuilder withCharObjId(Integer value) {
        with(CharacterHennasResource.CHAR_OBJ_ID, value);
        return this;
    }

    public CharacterHennasBuilder withSymbolId(Integer value) {
        with(CharacterHennasResource.SYMBOL_ID, value);
        return this;
    }

    public CharacterHennasBuilder withSlot(Integer value) {
        with(CharacterHennasResource.SLOT, value);
        return this;
    }

    public CharacterHennasBuilder withClassIndex(Integer value) {
        with(CharacterHennasResource.CLASS_INDEX, value);
        return this;
    }

}
