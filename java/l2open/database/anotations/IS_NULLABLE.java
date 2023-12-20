package l2open.database.anotations;

/**
 * Created by a.kiperku
 * Date: 19.12.2023
 */

public enum IS_NULLABLE {

    YES(true),
    NO(false);


    final boolean isNullable;

    IS_NULLABLE(boolean isNullable) {
        this.isNullable = isNullable;
    }

    public boolean isNullable() {
        return isNullable;
    }
}
