package l2open.common.Html_Builder_JSOUP.Elements;

import org.jsoup.nodes.Element;

/**
 * Created by a.kiperku
 * Date: 12.02.2024
 */

public class Button extends Element {

    public Button(String action) {
        super("button");
        this.attributes().put("value", "");
        this.attributes().put("action", action);
        this.attributes().put("width", String.valueOf(32));
        this.attributes().put("height", String.valueOf(32));
        this.attributes().put("back", "L2UI_ct1.button_df");
        this.attributes().put("fore", "L2UI_ct1.button_df");
    }
    public Button(String value, String action) {
        super("button");
        this.attributes().put("value", value);
        this.attributes().put("action", action);
        this.attributes().put("width", String.valueOf(32));
        this.attributes().put("height", String.valueOf(32));
        this.attributes().put("back", "L2UI_ct1.button_df");
        this.attributes().put("fore", "L2UI_ct1.button_df");
    }
    public Button(String value, String action, int width, int height) {
        super("button");
        this.attributes().put("value", value);
        this.attributes().put("action", action);
        this.attributes().put("width", String.valueOf(width));
        this.attributes().put("height", String.valueOf(height));
        this.attributes().put("back", "L2UI_ct1.button_df");
        this.attributes().put("fore", "L2UI_ct1.button_df");
    }
    public Button(String value, String action, int width, int height, String back, String fore) {
        super("button");
        this.attributes().put("value", value);
        this.attributes().put("action", action);
        this.attributes().put("width", String.valueOf(width));
        this.attributes().put("height", String.valueOf(height));
        this.attributes().put("back", back);
        this.attributes().put("fore", fore);
    }

}
