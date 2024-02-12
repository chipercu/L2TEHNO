package l2open.common.Html_Builder_JSOUP.Elements;

import l2open.common.HtmlBuilder.components.ALIGN;
import l2open.common.HtmlBuilder.components.VALIGN;
import org.jsoup.nodes.Element;

/**
 * Created by a.kiperku
 * Date: 12.02.2024
 */

public class TD extends Element {
    public TD() {
        super("td");
    }

    public TD setWidth(int width) {
        this.attributes().add("width", String.valueOf(width));
        return this;
    }

    public TD setHeight(int height) {
        this.attributes().add("height", String.valueOf(height));
        return this;
    }

    public TD setAlign(ALIGN align){
        this.attributes().add("align", align.name());
        return this;
    }
    public TD setValign(VALIGN valign){
        this.attributes().add("valign", valign.name());
        return this;
    }

    public<T extends Element> T put(T element){
        this.appendChild(element);
        return element;
    }

}
