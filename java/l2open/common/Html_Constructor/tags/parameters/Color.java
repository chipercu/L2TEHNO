package l2open.common.Html_Constructor.tags.parameters;

public enum Color {

    GOLD("F2C202"),
    RED("FF0000"),
    GREN("32cd32"),
    BLUE("0099FF"),
    GRAY("AAAAAA"),
    BROWN("FF6600");

    final String value;
    Color(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }




}
