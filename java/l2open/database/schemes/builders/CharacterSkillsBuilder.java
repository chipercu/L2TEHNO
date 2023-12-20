package l2open.database.schemes.builders;

import l2open.database.ResourceBuilder;
import l2open.database.schemes.resources.CharacterSkillsResource;

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
