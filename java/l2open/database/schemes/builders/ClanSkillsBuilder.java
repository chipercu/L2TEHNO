package l2open.database.schemes.builders;

import l2open.database.ResourceBuilder;
import l2open.database.schemes.resources.ClanSkillsResource;

public class ClanSkillsBuilder extends ResourceBuilder<ClanSkillsResource> {

    public ClanSkillsBuilder() {
        try {
            resourceClass = ClanSkillsResource.class;
            resource = resourceClass.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public ClanSkillsBuilder withClanId(Integer value) {
        with(ClanSkillsResource.CLAN_ID, value);
        return this;
    }

    public ClanSkillsBuilder withSkillId(Integer value) {
        with(ClanSkillsResource.SKILL_ID, value);
        return this;
    }

    public ClanSkillsBuilder withSkillLevel(Integer value) {
        with(ClanSkillsResource.SKILL_LEVEL, value);
        return this;
    }

    public ClanSkillsBuilder withSkillName(String value) {
        with(ClanSkillsResource.SKILL_NAME, value);
        return this;
    }

    public ClanSkillsBuilder withSquadIndex(Integer value) {
        with(ClanSkillsResource.SQUAD_INDEX, value);
        return this;
    }

}
