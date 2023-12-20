package l2open.database.schemes.builders;

import l2open.database.ResourceBuilder;
import l2open.database.schemes.resources.SkillLearnResource;

public class SkillLearnBuilder extends ResourceBuilder<SkillLearnResource> {

    public SkillLearnBuilder() {
        try {
            resourceClass = SkillLearnResource.class;
            resource = resourceClass.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public SkillLearnBuilder withNpcId(Integer value) {
        with(SkillLearnResource.NPC_ID, value);
        return this;
    }

    public SkillLearnBuilder withClassId(Integer value) {
        with(SkillLearnResource.CLASS_ID, value);
        return this;
    }

}
