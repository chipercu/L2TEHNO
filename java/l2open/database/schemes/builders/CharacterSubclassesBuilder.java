package l2open.database.schemes.builders;

import l2open.database.ResourceBuilder;
import l2open.database.schemes.resources.CharacterSubclassesResource;

public class CharacterSubclassesBuilder extends ResourceBuilder<CharacterSubclassesResource> {

    public CharacterSubclassesBuilder() {
        try {
            resourceClass = CharacterSubclassesResource.class;
            resource = resourceClass.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public CharacterSubclassesBuilder withCharObjId(Integer value) {
        with(CharacterSubclassesResource.CHAR_OBJ_ID, value);
        return this;
    }

    public CharacterSubclassesBuilder withClassId(Integer value) {
        with(CharacterSubclassesResource.CLASS_ID, value);
        return this;
    }

    public CharacterSubclassesBuilder withLevel(Integer value) {
        with(CharacterSubclassesResource.LEVEL, value);
        return this;
    }

    public CharacterSubclassesBuilder withExp(Long value) {
        with(CharacterSubclassesResource.EXP, value);
        return this;
    }

    public CharacterSubclassesBuilder withSp(Long value) {
        with(CharacterSubclassesResource.SP, value);
        return this;
    }

    public CharacterSubclassesBuilder withCurHp(Double value) {
        with(CharacterSubclassesResource.CUR_HP, value);
        return this;
    }

    public CharacterSubclassesBuilder withCurMp(Double value) {
        with(CharacterSubclassesResource.CUR_MP, value);
        return this;
    }

    public CharacterSubclassesBuilder withCurCp(Double value) {
        with(CharacterSubclassesResource.CUR_CP, value);
        return this;
    }

    public CharacterSubclassesBuilder withMaxHp(Integer value) {
        with(CharacterSubclassesResource.MAX_HP, value);
        return this;
    }

    public CharacterSubclassesBuilder withMaxMp(Integer value) {
        with(CharacterSubclassesResource.MAX_MP, value);
        return this;
    }

    public CharacterSubclassesBuilder withMaxCp(Integer value) {
        with(CharacterSubclassesResource.MAX_CP, value);
        return this;
    }

    public CharacterSubclassesBuilder withActive(Integer value) {
        with(CharacterSubclassesResource.ACTIVE, value);
        return this;
    }

    public CharacterSubclassesBuilder withIsBase(Integer value) {
        with(CharacterSubclassesResource.IS_BASE, value);
        return this;
    }

    public CharacterSubclassesBuilder withDeathPenalty(Integer value) {
        with(CharacterSubclassesResource.DEATH_PENALTY, value);
        return this;
    }

    public CharacterSubclassesBuilder withCertification(Integer value) {
        with(CharacterSubclassesResource.CERTIFICATION, value);
        return this;
    }

}
