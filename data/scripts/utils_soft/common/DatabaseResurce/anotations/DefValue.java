package utils_soft.common.DatabaseResurce.anotations;

import l2open.gameserver.templates.L2Weapon;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Target({})
@Retention(RUNTIME)
public @interface DefValue {
    String String() default "";
    int Integer() default 0;
    double Double() default 0.0;
    long Long() default 0;
    boolean Boolean() default false;

}
