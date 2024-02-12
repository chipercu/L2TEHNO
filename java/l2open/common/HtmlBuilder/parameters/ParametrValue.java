package l2open.common.HtmlBuilder.parameters;

public class ParametrValue {

    private String value;
    private String _string;
    private int _int;

    public ParametrValue(String value, String val) {
        this.value = value;
        this._string = val;
    }
    public ParametrValue(String value, int val) {
        this.value = value;
        this._int = val;
    }

    @Override
    public String toString() {
        return value + (_string != null ? _string + "\"" : _int+ "\"");
    }
}
