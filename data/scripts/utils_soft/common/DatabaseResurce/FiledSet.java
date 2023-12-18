package utils_soft.common.DatabaseResurce;

public class FiledSet {

    private final Object value;
    private final String field;

    public FiledSet(String field, Object value) {
        this.field = field;
        this.value = value;
    }

    public Object getValue() {
        return value;
    }

    public String getField() {
        return field;
    }
}
