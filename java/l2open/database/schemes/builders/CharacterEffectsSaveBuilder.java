package l2open.database.schemes.builders;

import l2open.database.ResourceBuilder;
import l2open.database.schemes.resources.CharacterEffectsSaveResource;

public class CharacterEffectsSaveBuilder extends ResourceBuilder<CharacterEffectsSaveResource> {

    public CharacterEffectsSaveBuilder() {
        try {
            resourceClass = CharacterEffectsSaveResource.class;
            resource = resourceClass.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public CharacterEffectsSaveBuilder withCharObjId(Integer value) {
        with(CharacterEffectsSaveResource.CHAR_OBJ_ID, value);
        return this;
    }

    public CharacterEffectsSaveBuilder withSkillId(Integer value) {
        with(CharacterEffectsSaveResource.SKILL_ID, value);
        return this;
    }

    public CharacterEffectsSaveBuilder withSkillLevel(Integer value) {
        with(CharacterEffectsSaveResource.SKILL_LEVEL, value);
        return this;
    }

    public CharacterEffectsSaveBuilder withEffectCount(Integer value) {
        with(CharacterEffectsSaveResource.EFFECT_COUNT, value);
        return this;
    }

    public CharacterEffectsSaveBuilder withEffectCurTime(Integer value) {
        with(CharacterEffectsSaveResource.EFFECT_CUR_TIME, value);
        return this;
    }

    public CharacterEffectsSaveBuilder withDuration(Integer value) {
        with(CharacterEffectsSaveResource.DURATION, value);
        return this;
    }

    public CharacterEffectsSaveBuilder withOrder(Integer value) {
        with(CharacterEffectsSaveResource.ORDER, value);
        return this;
    }

    public CharacterEffectsSaveBuilder withClassIndex(Integer value) {
        with(CharacterEffectsSaveResource.CLASS_INDEX, value);
        return this;
    }

}