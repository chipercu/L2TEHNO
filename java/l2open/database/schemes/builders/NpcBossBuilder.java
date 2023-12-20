package l2open.database.schemes.builders;

import l2open.database.ResourceBuilder;
import l2open.database.schemes.resources.NpcBossResource;

public class NpcBossBuilder extends ResourceBuilder<NpcBossResource> {

    public NpcBossBuilder() {
        try {
            resourceClass = NpcBossResource.class;
            resource = resourceClass.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public NpcBossBuilder withNpcDbName(String value) {
        with(NpcBossResource.NPC_DB_NAME, value);
        return this;
    }

    public NpcBossBuilder withAlive(Integer value) {
        with(NpcBossResource.ALIVE, value);
        return this;
    }

    public NpcBossBuilder withHp(Integer value) {
        with(NpcBossResource.HP, value);
        return this;
    }

    public NpcBossBuilder withMp(Integer value) {
        with(NpcBossResource.MP, value);
        return this;
    }

    public NpcBossBuilder withPosX(Integer value) {
        with(NpcBossResource.POS_X, value);
        return this;
    }

    public NpcBossBuilder withPosY(Integer value) {
        with(NpcBossResource.POS_Y, value);
        return this;
    }

    public NpcBossBuilder withPosZ(Integer value) {
        with(NpcBossResource.POS_Z, value);
        return this;
    }

    public NpcBossBuilder withTimeLow(Long value) {
        with(NpcBossResource.TIME_LOW, value);
        return this;
    }

    public NpcBossBuilder withTimeHigh(Long value) {
        with(NpcBossResource.TIME_HIGH, value);
        return this;
    }

    public NpcBossBuilder withI0(Integer value) {
        with(NpcBossResource.I0, value);
        return this;
    }

}
