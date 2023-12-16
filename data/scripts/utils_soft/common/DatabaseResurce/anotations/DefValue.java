package utils_soft.common.DatabaseResurce.anotations;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Target({})
@Retention(RUNTIME)
public @interface DefValue {
    String STRING() default "";
    int INT() default 0;
    double DOUBLE() default 0.0;
    long LONG() default 0;
    boolean BOOLEAN() default false;
}
