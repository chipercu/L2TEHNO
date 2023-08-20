package l2open.common.Html_Constructor.tags.parameters;

public enum Position {

    LEFT("left"),
    CENTER("center"),
    RIGHT("right"),
    TOP("top"),
    BOTTOM("bottom");

    final String value;
    Position(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }


}
