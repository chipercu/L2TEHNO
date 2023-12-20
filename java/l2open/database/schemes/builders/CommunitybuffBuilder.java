package l2open.database.schemes.builders;

import l2open.database.ResourceBuilder;
import l2open.database.schemes.resources.CommunitybuffResource;

public class CommunitybuffBuilder extends ResourceBuilder<CommunitybuffResource> {

    public CommunitybuffBuilder() {
        try {
            resourceClass = CommunitybuffResource.class;
            resource = resourceClass.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public CommunitybuffBuilder withSkillID(Integer value) {
        with(CommunitybuffResource.SKILL_ID, value);
        return this;
    }

    public CommunitybuffBuilder withSkillLvl(Integer value) {
        with(CommunitybuffResource.SKILL_LVL, value);
        return this;
    }

}
