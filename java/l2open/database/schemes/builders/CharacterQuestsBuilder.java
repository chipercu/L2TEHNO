package l2open.database.schemes.builders;

import l2open.database.ResourceBuilder;
import l2open.database.schemes.resources.CharacterQuestsResource;

public class CharacterQuestsBuilder extends ResourceBuilder<CharacterQuestsResource> {

    public CharacterQuestsBuilder() {
        try {
            resourceClass = CharacterQuestsResource.class;
            resource = resourceClass.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public CharacterQuestsBuilder withCharId(Integer value) {
        with(CharacterQuestsResource.CHAR_ID, value);
        return this;
    }

    public CharacterQuestsBuilder withName(String value) {
        with(CharacterQuestsResource.NAME, value);
        return this;
    }

    public CharacterQuestsBuilder withVar(String value) {
        with(CharacterQuestsResource.VAR, value);
        return this;
    }

    public CharacterQuestsBuilder withValue(String value) {
        with(CharacterQuestsResource.VALUE, value);
        return this;
    }

}
