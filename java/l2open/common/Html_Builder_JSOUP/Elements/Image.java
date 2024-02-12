package l2open.common.Html_Builder_JSOUP.Elements;

import org.jsoup.nodes.Element;

public class Image extends Element {

    public Image(String src) {
        super("img");
        this.attributes().put("src", src);
        this.attributes().put("width", String.valueOf(32));
        this.attributes().put("height", String.valueOf(32));
    }

    public Image(String src, int width, int height) {
        super("img");
        this.attributes().put("src", src);
        this.attributes().put("width", String.valueOf(width));
        this.attributes().put("height", String.valueOf(height));
    }

}
