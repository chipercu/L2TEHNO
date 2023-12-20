package l2open.database.schemes.builders;

import l2open.database.ResourceBuilder;
import l2open.database.schemes.resources.CharacterVariablesResource;

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
