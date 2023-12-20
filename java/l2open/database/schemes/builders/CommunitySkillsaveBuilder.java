package l2open.database.schemes.builders;

import l2open.database.ResourceBuilder;
import l2open.database.schemes.resources.CommunitySkillsaveResource;

public class CommunitySkillsaveBuilder extends ResourceBuilder<CommunitySkillsaveResource> {

    public CommunitySkillsaveBuilder() {
        try {
            resourceClass = CommunitySkillsaveResource.class;
            resource = resourceClass.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public CommunitySkillsaveBuilder withCharId(Integer value) {
        with(CommunitySkillsaveResource.CHAR_ID, value);
        return this;
    }

    public CommunitySkillsaveBuilder withSchameid(Integer value) {
        with(CommunitySkillsaveResource.SCHAMEID, value);
        return this;
    }

    public CommunitySkillsaveBuilder withName(String value) {
        with(CommunitySkillsaveResource.NAME, value);
        return this;
    }

    public CommunitySkillsaveBuilder withSkills(String value) {
        with(CommunitySkillsaveResource.SKILLS, value);
        return this;
    }

}
