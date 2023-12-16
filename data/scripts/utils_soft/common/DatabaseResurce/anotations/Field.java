package utils_soft.common.DatabaseResurce.anotations;


import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.RetentionPolicy.RUNTIME;


@Target({})
@Retention(RUNTIME)
public @interface Field {
    String name();
    DefValue defValue() default @DefValue;
}
