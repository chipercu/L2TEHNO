package l2open.database.schemes.builders;

import l2open.database.ResourceBuilder;
import l2open.database.schemes.resources.CharacterSkillsSaveResource;

public class CharacterSkillsSaveBuilder extends ResourceBuilder<CharacterSkillsSaveResource> {

    public CharacterSkillsSaveBuilder() {
        try {
            resourceClass = CharacterSkillsSaveResource.class;
            resource = resourceClass.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public CharacterSkillsSaveBuilder withCharObjId(Integer value) {
        with(CharacterSkillsSaveResource.CHAR_OBJ_ID, value);
        return this;
    }

    public CharacterSkillsSaveBuilder withSkillId(Long value) {
        with(CharacterSkillsSaveResource.SKILL_ID, value);
        return this;
    }

    public CharacterSkillsSaveBuilder withClassIndex(Integer value) {
        with(CharacterSkillsSaveResource.CLASS_INDEX, value);
        return this;
    }

    public CharacterSkillsSaveBuilder withEndTime(Long value) {
        with(CharacterSkillsSaveResource.END_TIME, value);
        return this;
    }

    public CharacterSkillsSaveBuilder withReuseDelayOrg(Integer value) {
        with(CharacterSkillsSaveResource.REUSE_DELAY_ORG, value);
        return this;
    }

}
