package utils_soft.common.DatabaseResurce.schemes.builders;

import utils_soft.common.DatabaseResurce.ResourceBuilder;
import utils_soft.common.DatabaseResurce.schemes.resources.CharacterSkillsResource;
import java.lang.reflect.Field;
import java.util.NoSuchElementException;

public class CharacterSkillsBuilder extends ResourceBuilder<CharacterSkillsResource> {

    public CharacterSkillsBuilder() {
        try {
            resourceClass = CharacterSkillsResource.class;
            resource = resourceClass.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public CharacterSkillsBuilder withCharObjId(Integer value) {
        with(CharacterSkillsResource.CHAR_OBJ_ID, value);
        return this;
    }

    public CharacterSkillsBuilder withSkillId(Integer value) {
        with(CharacterSkillsResource.SKILL_ID, value);
        return this;
    }

    public CharacterSkillsBuilder withSkillLevel(Integer value) {
        with(CharacterSkillsResource.SKILL_LEVEL, value);
        return this;
    }

    public CharacterSkillsBuilder withSkillName(String value) {
        with(CharacterSkillsResource.SKILL_NAME, value);
        return this;
    }

    public CharacterSkillsBuilder withClassIndex(Integer value) {
        with(CharacterSkillsResource.CLASS_INDEX, value);
        return this;
    }

}
