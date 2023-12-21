package l2open.database.schemes.builders;

import l2open.database.ResourceBuilder;
import l2open.database.schemes.resources.SkillSpellbooksResource;

public class SkillSpellbooksBuilder extends ResourceBuilder<SkillSpellbooksResource> {

    public SkillSpellbooksBuilder() {
        try {
            resourceClass = SkillSpellbooksResource.class;
            resource = resourceClass.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public SkillSpellbooksBuilder withSkillId(Integer value) {
        with(SkillSpellbooksResource.SKILL_ID, value);
        return this;
    }

    public SkillSpellbooksBuilder withLevel(Integer value) {
        with(SkillSpellbooksResource.LEVEL, value);
        return this;
    }

    public SkillSpellbooksBuilder withItemId(Integer value) {
        with(SkillSpellbooksResource.ITEM_ID, value);
        return this;
    }

    public SkillSpellbooksBuilder withItemCount(Integer value) {
        with(SkillSpellbooksResource.ITEM_COUNT, value);
        return this;
    }

    public SkillSpellbooksBuilder withMinLevel(Integer value) {
        with(SkillSpellbooksResource.MIN_LEVEL, value);
        return this;
    }

    public SkillSpellbooksBuilder withItemName(String value) {
        with(SkillSpellbooksResource.ITEM_NAME, value);
        return this;
    }

}