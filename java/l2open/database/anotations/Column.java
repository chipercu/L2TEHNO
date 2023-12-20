package l2open.database.anotations;


import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.RetentionPolicy.RUNTIME;


@Target({})
@Retention(RUNTIME)
public @interface Column {

    /**
     * Имя колонки таблицы
     * Table column name
     */
    String name();
    DATA_TYPE type();
    int type_size() default -1;
    IS_NULLABLE is_null();
    String defValue() default "";



}
