package l2open.database.schemes.builders;

import l2open.database.ResourceBuilder;
import l2open.database.schemes.resources.NpcskillsResource;

public class NpcskillsBuilder extends ResourceBuilder<NpcskillsResource> {

    public NpcskillsBuilder() {
        try {
            resourceClass = NpcskillsResource.class;
            resource = resourceClass.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public NpcskillsBuilder withNpcid(Integer value) {
        with(NpcskillsResource.NPCID, value);
        return this;
    }

    public NpcskillsBuilder withSkillid(Integer value) {
        with(NpcskillsResource.SKILLID, value);
        return this;
    }

    public NpcskillsBuilder withLevel(Integer value) {
        with(NpcskillsResource.LEVEL, value);
        return this;
    }

}
