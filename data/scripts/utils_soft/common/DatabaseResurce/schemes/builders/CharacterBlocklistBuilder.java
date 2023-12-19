package utils_soft.common.DatabaseResurce.schemes.builders;

import utils_soft.common.DatabaseResurce.ResourceBuilder;
import utils_soft.common.DatabaseResurce.schemes.resources.CharacterBlocklistResource;
import java.lang.reflect.Field;
import java.util.NoSuchElementException;

public class CharacterBlocklistBuilder extends ResourceBuilder<CharacterBlocklistResource> {

    public CharacterBlocklistBuilder() {
        try {
            resourceClass = CharacterBlocklistResource.class;
            resource = resourceClass.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public CharacterBlocklistBuilder withObjId(Integer value) {
        with(CharacterBlocklistResource.OBJ__ID, value);
        return this;
    }

    public CharacterBlocklistBuilder withTargetId(Integer value) {
        with(CharacterBlocklistResource.TARGET__ID, value);
        return this;
    }

}
