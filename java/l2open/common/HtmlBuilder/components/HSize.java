package l2open.common.HtmlBuilder.components;

/**
 * Created by a.kiperku
 * Date: 12.02.2024
 */
public enum HSize {

    HS9("hs9"),
    HS12("hs12"),
    HS15("hs15");

    private final String name;

    HSize(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
