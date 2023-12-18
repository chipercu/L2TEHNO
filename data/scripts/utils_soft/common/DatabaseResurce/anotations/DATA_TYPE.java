package utils_soft.common.DatabaseResurce.anotations;

/**
 * Created by a.kiperku
 * Date: 18.12.2023
 */
public enum DATA_TYPE {
    TINYINT("tinyint"),
    SMALLINT("smallint"),
    MEDIUMINT("mediumint"),
    INT("int"),
    INTEGER("integer"),
    BIGINT("bigint"),
    BIT("bit"),
    REAL("real"),
    DOUBLE("double"),
    FLOAT("float"),
    DECIMAL("decimal"),
    NUMERIC("numeric"),
    CHAR("char"),
    VARCHAR("varchar"),
    DATE("date"),
    TIME("time"),
    YEAR("year"),
    TIMESTAMP("timestamp"),
    DATETIME("datetime"),
    TINYBLOB("tinyblob"),
    BLOB("blob"),
    MEDIUMBLOB("mediumblob"),
    LONGBLOB("longblob"),
    TINYTEXT("tinytext"),
    TEXT("text"),
    MEDIUMTEXT("mediumtext"),
    LONGTEXT("longtext"),
    ENUM("enum"),
    SET("set"),
    BINARY("binary"),
    VARBINARY("varbinary"),
    POINT("point"),
    MULTIPOINT("multipoint"),
    LINESTRING("linestring"),
    MULTILINESTRING("multilinestring"),
    POLYGON("polygon"),
    MULTIPOLYGON("multipolygon"),
    GEOMETRY("geometry"),
    GEOMETRYCOLLECTION("geometrycollection");

    final String name;

    DATA_TYPE(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public DATA_TYPE getType(String value){
        return valueOf(value);
    }


}
