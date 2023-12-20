package l2open.database.schemes.builders;

import l2open.database.ResourceBuilder;
import l2open.database.schemes.resources.SkillsResource;

public class SkillsBuilder extends ResourceBuilder<SkillsResource> {

    public SkillsBuilder() {
        try {
            resourceClass = SkillsResource.class;
            resource = resourceClass.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public SkillsBuilder withId(Integer value) {
        with(SkillsResource.ID, value);
        return this;
    }

    public SkillsBuilder withLevel(Integer value) {
        with(SkillsResource.LEVEL, value);
        return this;
    }

    public SkillsBuilder withLearn(Integer value) {
        with(SkillsResource.LEARN, value);
        return this;
    }

    public SkillsBuilder withOperateType(String value) {
        with(SkillsResource.OPERATE_TYPE, value);
        return this;
    }

    public SkillsBuilder withName(String value) {
        with(SkillsResource.NAME, value);
        return this;
    }

    public SkillsBuilder withIsMagic(Integer value) {
        with(SkillsResource.IS_MAGIC, value);
        return this;
    }

    public SkillsBuilder withMagicLevel(Integer value) {
        with(SkillsResource.MAGIC_LEVEL, value);
        return this;
    }

    public SkillsBuilder withActivateRate(Integer value) {
        with(SkillsResource.ACTIVATE_RATE, value);
        return this;
    }

    public SkillsBuilder withEffectPoint(Integer value) {
        with(SkillsResource.EFFECT_POINT, value);
        return this;
    }

    public SkillsBuilder withMpConsume1(Integer value) {
        with(SkillsResource.MP_CONSUME1, value);
        return this;
    }

    public SkillsBuilder withMpConsume2(Integer value) {
        with(SkillsResource.MP_CONSUME2, value);
        return this;
    }

    public SkillsBuilder withHpConsume(Integer value) {
        with(SkillsResource.HP_CONSUME, value);
        return this;
    }

    public SkillsBuilder withCastRange(Integer value) {
        with(SkillsResource.CAST_RANGE, value);
        return this;
    }

    public SkillsBuilder withSkillHitTime(Double value) {
        with(SkillsResource.SKILL_HIT_TIME, value);
        return this;
    }

    public SkillsBuilder withCoolTime(Double value) {
        with(SkillsResource.COOL_TIME, value);
        return this;
    }

    public SkillsBuilder withHitCancelTime(Double value) {
        with(SkillsResource.HIT_CANCEL_TIME, value);
        return this;
    }

    public SkillsBuilder withPower(Integer value) {
        with(SkillsResource.POWER, value);
        return this;
    }

    public SkillsBuilder withEnchant(String value) {
        with(SkillsResource.ENCHANT, value);
        return this;
    }

    public SkillsBuilder withReuse(Integer value) {
        with(SkillsResource.REUSE, value);
        return this;
    }

    public SkillsBuilder withOlympiadUse(Integer value) {
        with(SkillsResource.OLYMPIAD_USE, value);
        return this;
    }

    public SkillsBuilder withStaticHittime(Integer value) {
        with(SkillsResource.STATIC_HITTIME, value);
        return this;
    }

    public SkillsBuilder withStaticReuse(Integer value) {
        with(SkillsResource.STATIC_REUSE, value);
        return this;
    }

    public SkillsBuilder withAbnormalTime(Integer value) {
        with(SkillsResource.ABNORMAL_TIME, value);
        return this;
    }

    public SkillsBuilder withAbnormalType(String value) {
        with(SkillsResource.ABNORMAL_TYPE, value);
        return this;
    }

    public SkillsBuilder withAbnormalLv(Integer value) {
        with(SkillsResource.ABNORMAL_LV, value);
        return this;
    }

    public SkillsBuilder withAbnormalInstant(Integer value) {
        with(SkillsResource.ABNORMAL_INSTANT, value);
        return this;
    }

    public SkillsBuilder withBuffProtectLevel(Integer value) {
        with(SkillsResource.BUFF_PROTECT_LEVEL, value);
        return this;
    }

    public SkillsBuilder withTrait(String value) {
        with(SkillsResource.TRAIT, value);
        return this;
    }

    public SkillsBuilder withLvBonusRate(Integer value) {
        with(SkillsResource.LV_BONUS_RATE, value);
        return this;
    }

    public SkillsBuilder withBasicProperty(String value) {
        with(SkillsResource.BASIC_PROPERTY, value);
        return this;
    }

    public SkillsBuilder withNextAction(String value) {
        with(SkillsResource.NEXT_ACTION, value);
        return this;
    }

    public SkillsBuilder withTargetType(Enum value) {
        with(SkillsResource.TARGET_TYPE, value);
        return this;
    }

    public SkillsBuilder withAffectScope(Enum value) {
        with(SkillsResource.AFFECT_SCOPE, value);
        return this;
    }

    public SkillsBuilder withAffectObject(Enum value) {
        with(SkillsResource.AFFECT_OBJECT, value);
        return this;
    }

    public SkillsBuilder withAffectRange(Integer value) {
        with(SkillsResource.AFFECT_RANGE, value);
        return this;
    }

    public SkillsBuilder withAffectLimit(String value) {
        with(SkillsResource.AFFECT_LIMIT, value);
        return this;
    }

    public SkillsBuilder withFanRange(String value) {
        with(SkillsResource.FAN_RANGE, value);
        return this;
    }

    public SkillsBuilder withEffectiveRange(Integer value) {
        with(SkillsResource.EFFECTIVE_RANGE, value);
        return this;
    }

    public SkillsBuilder withEffectPoint2(Integer value) {
        with(SkillsResource.EFFECT_POINT2, value);
        return this;
    }

    public SkillsBuilder withIrreplaceableBuff(Integer value) {
        with(SkillsResource.IRREPLACEABLE_BUFF, value);
        return this;
    }

    public SkillsBuilder withAbnormalVisualEffect(String value) {
        with(SkillsResource.ABNORMAL_VISUAL_EFFECT, value);
        return this;
    }

    public SkillsBuilder withIcon(String value) {
        with(SkillsResource.ICON, value);
        return this;
    }

}
