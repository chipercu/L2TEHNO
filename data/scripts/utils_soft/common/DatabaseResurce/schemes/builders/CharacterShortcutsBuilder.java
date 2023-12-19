package utils_soft.common.DatabaseResurce.schemes.builders;

import utils_soft.common.DatabaseResurce.ResourceBuilder;
import utils_soft.common.DatabaseResurce.schemes.resources.CharacterShortcutsResource;
import java.lang.reflect.Field;
import java.util.NoSuchElementException;

public class CharacterShortcutsBuilder extends ResourceBuilder<CharacterShortcutsResource> {

    public CharacterShortcutsBuilder() {
        try {
            resourceClass = CharacterShortcutsResource.class;
            resource = resourceClass.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public CharacterShortcutsBuilder withCharObjId(Integer value) {
        with(CharacterShortcutsResource.CHAR_OBJ_ID, value);
        return this;
    }

    public CharacterShortcutsBuilder withSlot(Integer value) {
        with(CharacterShortcutsResource.SLOT, value);
        return this;
    }

    public CharacterShortcutsBuilder withPage(Integer value) {
        with(CharacterShortcutsResource.PAGE, value);
        return this;
    }

    public CharacterShortcutsBuilder withType(Integer value) {
        with(CharacterShortcutsResource.TYPE, value);
        return this;
    }

    public CharacterShortcutsBuilder withShortcutId(Integer value) {
        with(CharacterShortcutsResource.SHORTCUT_ID, value);
        return this;
    }

    public CharacterShortcutsBuilder withLevel(Integer value) {
        with(CharacterShortcutsResource.LEVEL, value);
        return this;
    }

    public CharacterShortcutsBuilder withClassIndex(Integer value) {
        with(CharacterShortcutsResource.CLASS_INDEX, value);
        return this;
    }

}
