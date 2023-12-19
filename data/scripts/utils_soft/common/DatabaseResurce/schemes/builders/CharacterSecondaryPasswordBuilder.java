package utils_soft.common.DatabaseResurce.schemes.builders;

import utils_soft.common.DatabaseResurce.ResourceBuilder;
import utils_soft.common.DatabaseResurce.schemes.resources.CharacterSecondaryPasswordResource;
import java.lang.reflect.Field;
import java.util.NoSuchElementException;

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
