package utils_soft.common.DatabaseResurce.schemes.builders;

import utils_soft.common.DatabaseResurce.ResourceBuilder;
import utils_soft.common.DatabaseResurce.schemes.resources.SummonEffectsSaveResource;
import java.lang.reflect.Field;
import java.util.NoSuchElementException;

public class SummonEffectsSaveBuilder extends ResourceBuilder<SummonEffectsSaveResource> {

    public SummonEffectsSaveBuilder() {
        try {
            resourceClass = SummonEffectsSaveResource.class;
            resource = resourceClass.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public SummonEffectsSaveBuilder withCharObjId(Integer value) {
        with(SummonEffectsSaveResource.CHAR_OBJ_ID, value);
        return this;
    }

    public SummonEffectsSaveBuilder withNpcId(Integer value) {
        with(SummonEffectsSaveResource.NPC_ID, value);
        return this;
    }

    public SummonEffectsSaveBuilder withSkillId(Integer value) {
        with(SummonEffectsSaveResource.SKILL_ID, value);
        return this;
    }

    public SummonEffectsSaveBuilder withSkillLevel(Integer value) {
        with(SummonEffectsSaveResource.SKILL_LEVEL, value);
        return this;
    }

    public SummonEffectsSaveBuilder withEffectCount(Integer value) {
        with(SummonEffectsSaveResource.EFFECT_COUNT, value);
        return this;
    }

    public SummonEffectsSaveBuilder withEffectCurTime(Integer value) {
        with(SummonEffectsSaveResource.EFFECT_CUR_TIME, value);
        return this;
    }

    public SummonEffectsSaveBuilder withDuration(Integer value) {
        with(SummonEffectsSaveResource.DURATION, value);
        return this;
    }

    public SummonEffectsSaveBuilder withOrder(Integer value) {
        with(SummonEffectsSaveResource.ORDER, value);
        return this;
    }

}
