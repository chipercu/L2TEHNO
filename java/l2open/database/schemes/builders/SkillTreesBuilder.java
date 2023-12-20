package l2open.database.schemes.builders;

import l2open.database.ResourceBuilder;
import l2open.database.schemes.resources.SkillTreesResource;

public class SkillTreesBuilder extends ResourceBuilder<SkillTreesResource> {

    public SkillTreesBuilder() {
        try {
            resourceClass = SkillTreesResource.class;
            resource = resourceClass.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public SkillTreesBuilder withClassId(Integer value) {
        with(SkillTreesResource.CLASS_ID, value);
        return this;
    }

    public SkillTreesBuilder withSkillId(Integer value) {
        with(SkillTreesResource.SKILL_ID, value);
        return this;
    }

    public SkillTreesBuilder withLevel(Integer value) {
        with(SkillTreesResource.LEVEL, value);
        return this;
    }

    public SkillTreesBuilder withName(String value) {
        with(SkillTreesResource.NAME, value);
        return this;
    }

    public SkillTreesBuilder withSp(Integer value) {
        with(SkillTreesResource.SP, value);
        return this;
    }

    public SkillTreesBuilder withMinLevel(Integer value) {
        with(SkillTreesResource.MIN_LEVEL, value);
        return this;
    }

    public SkillTreesBuilder withRep(Integer value) {
        with(SkillTreesResource.REP, value);
        return this;
    }

}
