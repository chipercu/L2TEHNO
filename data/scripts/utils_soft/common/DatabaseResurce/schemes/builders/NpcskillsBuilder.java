package utils_soft.common.DatabaseResurce.schemes.builders;

import utils_soft.common.DatabaseResurce.ResourceBuilder;
import utils_soft.common.DatabaseResurce.schemes.resources.NpcskillsResource;
import java.lang.reflect.Field;
import java.util.NoSuchElementException;

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
