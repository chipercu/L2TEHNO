package utils_soft.common.DatabaseResurce.schemes.builders;

import utils_soft.common.DatabaseResurce.ResourceBuilder;
import utils_soft.common.DatabaseResurce.schemes.resources.CharacterVariablesResource;
import java.lang.reflect.Field;
import java.util.NoSuchElementException;

public class CharacterVariablesBuilder extends ResourceBuilder<CharacterVariablesResource> {

    public CharacterVariablesBuilder() {
        try {
            resourceClass = CharacterVariablesResource.class;
            resource = resourceClass.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public CharacterVariablesBuilder withObjId(Integer value) {
        with(CharacterVariablesResource.OBJ_ID, value);
        return this;
    }

    public CharacterVariablesBuilder withType(String value) {
        with(CharacterVariablesResource.TYPE, value);
        return this;
    }

    public CharacterVariablesBuilder withName(String value) {
        with(CharacterVariablesResource.NAME, value);
        return this;
    }

    public CharacterVariablesBuilder withValue(String value) {
        with(CharacterVariablesResource.VALUE, value);
        return this;
    }

    public CharacterVariablesBuilder withExpireTime(Long value) {
        with(CharacterVariablesResource.EXPIRE_TIME, value);
        return this;
    }

}
