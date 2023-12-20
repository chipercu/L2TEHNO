package l2open.database.schemes.builders;

import l2open.database.ResourceBuilder;
import l2open.database.schemes.resources.CharacterSecondaryPasswordResource;

public class CharacterSecondaryPasswordBuilder extends ResourceBuilder<CharacterSecondaryPasswordResource> {

    public CharacterSecondaryPasswordBuilder() {
        try {
            resourceClass = CharacterSecondaryPasswordResource.class;
            resource = resourceClass.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public CharacterSecondaryPasswordBuilder withAccountName(String value) {
        with(CharacterSecondaryPasswordResource.ACCOUNT_NAME, value);
        return this;
    }

    public CharacterSecondaryPasswordBuilder withVar(String value) {
        with(CharacterSecondaryPasswordResource.VAR, value);
        return this;
    }

    public CharacterSecondaryPasswordBuilder withValue(String value) {
        with(CharacterSecondaryPasswordResource.VALUE, value);
        return this;
    }

}
