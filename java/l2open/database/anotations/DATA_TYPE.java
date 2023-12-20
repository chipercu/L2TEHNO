package l2open.database.anotations;

import java.math.BigDecimal;
import java.sql.*;

/**
 * Created by a.kiperku
 * Date: 18.12.2023
 */
public enum DATA_TYPE {
    TINYINT("tinyint", Integer.class),
    SMALLINT("smallint", Integer.class),
    MEDIUMINT("mediumint", Integer.class),
    INT("int", Integer.class),
    INTEGER("integer", Integer.class),
    BIGINT("bigint", Long.class),
    BIT("bit", Boolean.class),
    REAL("real", Float.class),
    DOUBLE("double", Double.class),
    FLOAT("float", Double.class),
    DECIMAL("decimal", Double.class),
    NUMERIC("numeric", BigDecimal.class),
    CHAR("char", String.class),
    VARCHAR("varchar", String.class),
    TINYTEXT("tinytext", String.class),
    TEXT("text", String.class),
    MEDIUMTEXT("mediumtext", String.class),
    LONGTEXT("longtext", String.class),
    DATE("date", Date.class),
    TIME("time", Time.class),
    TIMESTAMP("timestamp", Timestamp.class),
    TINYBLOB("tinyblob", Blob.class),
    BLOB("blob", Blob.class),
    MEDIUMBLOB("mediumblob", Blob.class),
    LONGBLOB("longblob", Blob.class),
    ENUM("enum", Enum.class),
    BOOLEAN("enum", Boolean.class),
    BINARY("binary", Array.class),
    VARBINARY("varbinary", Array.class);

    final String name;
    final Class<?> type;

    DATA_TYPE(String name, Class<?> type) {
        this.name = name;
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public Class<?> getType(){
        return type;
    }


}
