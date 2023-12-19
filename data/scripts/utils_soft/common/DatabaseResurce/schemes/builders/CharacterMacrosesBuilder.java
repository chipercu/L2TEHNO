package utils_soft.common.DatabaseResurce.schemes.builders;

import utils_soft.common.DatabaseResurce.ResourceBuilder;
import utils_soft.common.DatabaseResurce.schemes.resources.CharacterMacrosesResource;
import java.lang.reflect.Field;
import java.util.NoSuchElementException;

public class CharacterMacrosesBuilder extends ResourceBuilder<CharacterMacrosesResource> {

    public CharacterMacrosesBuilder() {
        try {
            resourceClass = CharacterMacrosesResource.class;
            resource = resourceClass.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public CharacterMacrosesBuilder withCharObjId(Integer value) {
        with(CharacterMacrosesResource.CHAR_OBJ_ID, value);
        return this;
    }

    public CharacterMacrosesBuilder withId(Integer value) {
        with(CharacterMacrosesResource.ID, value);
        return this;
    }

    public CharacterMacrosesBuilder withIcon(Integer value) {
        with(CharacterMacrosesResource.ICON, value);
        return this;
    }

    public CharacterMacrosesBuilder withName(String value) {
        with(CharacterMacrosesResource.NAME, value);
        return this;
    }

    public CharacterMacrosesBuilder withDescr(String value) {
        with(CharacterMacrosesResource.DESCR, value);
        return this;
    }

    public CharacterMacrosesBuilder withAcronym(String value) {
        with(CharacterMacrosesResource.ACRONYM, value);
        return this;
    }

    public CharacterMacrosesBuilder withCommands(String value) {
        with(CharacterMacrosesResource.COMMANDS, value);
        return this;
    }

}
