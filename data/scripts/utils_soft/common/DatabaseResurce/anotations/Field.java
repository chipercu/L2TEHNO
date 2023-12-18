package utils_soft.common.DatabaseResurce.anotations;


import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.RetentionPolicy.RUNTIME;


@Target({})
@Retention(RUNTIME)
public @interface Field {

    /**
     * Имя колонки таблицы
     * Table column name
     */
    String name();
    DATA_TYPE type();
    int type_size() default -1;
    boolean nullable() default false;
    DefValue defValue() default @DefValue;
    /**
     *Тип данных в колонке
     * Column data type
     */



//


}
