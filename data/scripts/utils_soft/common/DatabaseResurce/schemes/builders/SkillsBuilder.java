package utils_soft.common.DatabaseResurce.schemes.builders;

import utils_soft.common.DatabaseResurce.ResourceBuilder;
import utils_soft.common.DatabaseResurce.schemes.resources.SkillsResource;
import java.lang.reflect.Field;

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
        try {
            final Field field = resourceClass.getDeclaredField("ID");
            field.setAccessible(true);
            statsSet.set((String) field.get(resource), value);
        } catch (Exception ignored) {
        }
        return this;
    }

    public SkillsBuilder withLevel(Integer value) {
        try {
            final Field field = resourceClass.getDeclaredField("LEVEL");
            field.setAccessible(true);
            statsSet.set((String) field.get(resource), value);
        } catch (Exception ignored) {
        }
        return this;
    }

    public SkillsBuilder withLearn(Integer value) {
        try {
            final Field field = resourceClass.getDeclaredField("LEARN");
            field.setAccessible(true);
            statsSet.set((String) field.get(resource), value);
        } catch (Exception ignored) {
        }
        return this;
    }

    public SkillsBuilder withOperateType(String value) {
        try {
            final Field field = resourceClass.getDeclaredField("OPERATE_TYPE");
            field.setAccessible(true);
            statsSet.set((String) field.get(resource), value);
        } catch (Exception ignored) {
        }
        return this;
    }

    public SkillsBuilder withName(String value) {
        try {
            final Field field = resourceClass.getDeclaredField("NAME");
            field.setAccessible(true);
            statsSet.set((String) field.get(resource), value);
        } catch (Exception ignored) {
        }
        return this;
    }

    public SkillsBuilder withIsMagic(Integer value) {
        try {
            final Field field = resourceClass.getDeclaredField("IS_MAGIC");
            field.setAccessible(true);
            statsSet.set((String) field.get(resource), value);
        } catch (Exception ignored) {
        }
        return this;
    }

    public SkillsBuilder withMagicLevel(Integer value) {
        try {
            final Field field = resourceClass.getDeclaredField("MAGIC_LEVEL");
            field.setAccessible(true);
            statsSet.set((String) field.get(resource), value);
        } catch (Exception ignored) {
        }
        return this;
    }

    public SkillsBuilder withActivateRate(Integer value) {
        try {
            final Field field = resourceClass.getDeclaredField("ACTIVATE_RATE");
            field.setAccessible(true);
            statsSet.set((String) field.get(resource), value);
        } catch (Exception ignored) {
        }
        return this;
    }

    public SkillsBuilder withEffectPoint(Integer value) {
        try {
            final Field field = resourceClass.getDeclaredField("EFFECT_POINT");
            field.setAccessible(true);
            statsSet.set((String) field.get(resource), value);
        } catch (Exception ignored) {
        }
        return this;
    }

    public SkillsBuilder withMpConsume1(Integer value) {
        try {
            final Field field = resourceClass.getDeclaredField("MP_CONSUME1");
            field.setAccessible(true);
            statsSet.set((String) field.get(resource), value);
        } catch (Exception ignored) {
        }
        return this;
    }

    public SkillsBuilder withMpConsume2(Integer value) {
        try {
            final Field field = resourceClass.getDeclaredField("MP_CONSUME2");
            field.setAccessible(true);
            statsSet.set((String) field.get(resource), value);
        } catch (Exception ignored) {
        }
        return this;
    }

    public SkillsBuilder withHpConsume(Integer value) {
        try {
            final Field field = resourceClass.getDeclaredField("HP_CONSUME");
            field.setAccessible(true);
            statsSet.set((String) field.get(resource), value);
        } catch (Exception ignored) {
        }
        return this;
    }

    public SkillsBuilder withCastRange(Integer value) {
        try {
            final Field field = resourceClass.getDeclaredField("CAST_RANGE");
            field.setAccessible(true);
            statsSet.set((String) field.get(resource), value);
        } catch (Exception ignored) {
        }
        return this;
    }

    public SkillsBuilder withSkillHitTime(Double value) {
        try {
            final Field field = resourceClass.getDeclaredField("SKILL_HIT_TIME");
            field.setAccessible(true);
            statsSet.set((String) field.get(resource), value);
        } catch (Exception ignored) {
        }
        return this;
    }

    public SkillsBuilder withCoolTime(Double value) {
        try {
            final Field field = resourceClass.getDeclaredField("COOL_TIME");
            field.setAccessible(true);
            statsSet.set((String) field.get(resource), value);
        } catch (Exception ignored) {
        }
        return this;
    }

    public SkillsBuilder withHitCancelTime(Double value) {
        try {
            final Field field = resourceClass.getDeclaredField("HIT_CANCEL_TIME");
            field.setAccessible(true);
            statsSet.set((String) field.get(resource), value);
        } catch (Exception ignored) {
        }
        return this;
    }

    public SkillsBuilder withPower(Integer value) {
        try {
            final Field field = resourceClass.getDeclaredField("POWER");
            field.setAccessible(true);
            statsSet.set((String) field.get(resource), value);
        } catch (Exception ignored) {
        }
        return this;
    }

    public SkillsBuilder withEnchant(String value) {
        try {
            final Field field = resourceClass.getDeclaredField("ENCHANT");
            field.setAccessible(true);
            statsSet.set((String) field.get(resource), value);
        } catch (Exception ignored) {
        }
        return this;
    }

    public SkillsBuilder withReuse(Integer value) {
        try {
            final Field field = resourceClass.getDeclaredField("REUSE");
            field.setAccessible(true);
            statsSet.set((String) field.get(resource), value);
        } catch (Exception ignored) {
        }
        return this;
    }

    public SkillsBuilder withOlympiadUse(Integer value) {
        try {
            final Field field = resourceClass.getDeclaredField("OLYMPIAD_USE");
            field.setAccessible(true);
            statsSet.set((String) field.get(resource), value);
        } catch (Exception ignored) {
        }
        return this;
    }

    public SkillsBuilder withStaticHittime(Integer value) {
        try {
            final Field field = resourceClass.getDeclaredField("STATIC_HITTIME");
            field.setAccessible(true);
            statsSet.set((String) field.get(resource), value);
        } catch (Exception ignored) {
        }
        return this;
    }

    public SkillsBuilder withStaticReuse(Integer value) {
        try {
            final Field field = resourceClass.getDeclaredField("STATIC_REUSE");
            field.setAccessible(true);
            statsSet.set((String) field.get(resource), value);
        } catch (Exception ignored) {
        }
        return this;
    }

    public SkillsBuilder withAbnormalTime(Integer value) {
        try {
            final Field field = resourceClass.getDeclaredField("ABNORMAL_TIME");
            field.setAccessible(true);
            statsSet.set((String) field.get(resource), value);
        } catch (Exception ignored) {
        }
        return this;
    }

    public SkillsBuilder withAbnormalType(String value) {
        try {
            final Field field = resourceClass.getDeclaredField("ABNORMAL_TYPE");
            field.setAccessible(true);
            statsSet.set((String) field.get(resource), value);
        } catch (Exception ignored) {
        }
        return this;
    }

    public SkillsBuilder withAbnormalLv(Integer value) {
        try {
            final Field field = resourceClass.getDeclaredField("ABNORMAL_LV");
            field.setAccessible(true);
            statsSet.set((String) field.get(resource), value);
        } catch (Exception ignored) {
        }
        return this;
    }

    public SkillsBuilder withAbnormalInstant(Integer value) {
        try {
            final Field field = resourceClass.getDeclaredField("ABNORMAL_INSTANT");
            field.setAccessible(true);
            statsSet.set((String) field.get(resource), value);
        } catch (Exception ignored) {
        }
        return this;
    }

    public SkillsBuilder withBuffProtectLevel(Integer value) {
        try {
            final Field field = resourceClass.getDeclaredField("BUFF_PROTECT_LEVEL");
            field.setAccessible(true);
            statsSet.set((String) field.get(resource), value);
        } catch (Exception ignored) {
        }
        return this;
    }

    public SkillsBuilder withTrait(String value) {
        try {
            final Field field = resourceClass.getDeclaredField("TRAIT");
            field.setAccessible(true);
            statsSet.set((String) field.get(resource), value);
        } catch (Exception ignored) {
        }
        return this;
    }

    public SkillsBuilder withLvBonusRate(Integer value) {
        try {
            final Field field = resourceClass.getDeclaredField("LV_BONUS_RATE");
            field.setAccessible(true);
            statsSet.set((String) field.get(resource), value);
        } catch (Exception ignored) {
        }
        return this;
    }

    public SkillsBuilder withBasicProperty(String value) {
        try {
            final Field field = resourceClass.getDeclaredField("BASIC_PROPERTY");
            field.setAccessible(true);
            statsSet.set((String) field.get(resource), value);
        } catch (Exception ignored) {
        }
        return this;
    }

    public SkillsBuilder withNextAction(String value) {
        try {
            final Field field = resourceClass.getDeclaredField("NEXT_ACTION");
            field.setAccessible(true);
            statsSet.set((String) field.get(resource), value);
        } catch (Exception ignored) {
        }
        return this;
    }

    public SkillsBuilder withTargetType(Enum value) {
        try {
            final Field field = resourceClass.getDeclaredField("TARGET_TYPE");
            field.setAccessible(true);
            statsSet.set((String) field.get(resource), value);
        } catch (Exception ignored) {
        }
        return this;
    }

    public SkillsBuilder withAffectScope(Enum value) {
        try {
            final Field field = resourceClass.getDeclaredField("AFFECT_SCOPE");
            field.setAccessible(true);
            statsSet.set((String) field.get(resource), value);
        } catch (Exception ignored) {
        }
        return this;
    }

    public SkillsBuilder withAffectObject(Enum value) {
        try {
            final Field field = resourceClass.getDeclaredField("AFFECT_OBJECT");
            field.setAccessible(true);
            statsSet.set((String) field.get(resource), value);
        } catch (Exception ignored) {
        }
        return this;
    }

    public SkillsBuilder withAffectRange(Integer value) {
        try {
            final Field field = resourceClass.getDeclaredField("AFFECT_RANGE");
            field.setAccessible(true);
            statsSet.set((String) field.get(resource), value);
        } catch (Exception ignored) {
        }
        return this;
    }

    public SkillsBuilder withAffectLimit(String value) {
        try {
            final Field field = resourceClass.getDeclaredField("AFFECT_LIMIT");
            field.setAccessible(true);
            statsSet.set((String) field.get(resource), value);
        } catch (Exception ignored) {
        }
        return this;
    }

    public SkillsBuilder withFanRange(String value) {
        try {
            final Field field = resourceClass.getDeclaredField("FAN_RANGE");
            field.setAccessible(true);
            statsSet.set((String) field.get(resource), value);
        } catch (Exception ignored) {
        }
        return this;
    }

    public SkillsBuilder withEffectiveRange(Integer value) {
        try {
            final Field field = resourceClass.getDeclaredField("EFFECTIVE_RANGE");
            field.setAccessible(true);
            statsSet.set((String) field.get(resource), value);
        } catch (Exception ignored) {
        }
        return this;
    }

    public SkillsBuilder withEffectPoint2(Integer value) {
        try {
            final Field field = resourceClass.getDeclaredField("EFFECT_POINT2");
            field.setAccessible(true);
            statsSet.set((String) field.get(resource), value);
        } catch (Exception ignored) {
        }
        return this;
    }

    public SkillsBuilder withIrreplaceableBuff(Integer value) {
        try {
            final Field field = resourceClass.getDeclaredField("IRREPLACEABLE_BUFF");
            field.setAccessible(true);
            statsSet.set((String) field.get(resource), value);
        } catch (Exception ignored) {
        }
        return this;
    }

    public SkillsBuilder withAbnormalVisualEffect(String value) {
        try {
            final Field field = resourceClass.getDeclaredField("ABNORMAL_VISUAL_EFFECT");
            field.setAccessible(true);
            statsSet.set((String) field.get(resource), value);
        } catch (Exception ignored) {
        }
        return this;
    }

    public SkillsBuilder withIcon(String value) {
        try {
            final Field field = resourceClass.getDeclaredField("ICON");
            field.setAccessible(true);
            statsSet.set((String) field.get(resource), value);
        } catch (Exception ignored) {
        }
        return this;
    }

}
