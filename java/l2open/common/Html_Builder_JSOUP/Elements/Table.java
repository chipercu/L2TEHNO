package l2open.common.Html_Builder_JSOUP.Elements;

import org.jsoup.nodes.Attribute;
import org.jsoup.nodes.Element;

import java.awt.*;

/**
 * Created by a.kiperku
 * Date: 12.02.2024
 */

public class Table extends Element {

    public Table() {
        super("table");
    }

    public TR addTR(){
        final TR tr = new TR();
        this.appendChild(tr);
        return tr;
    }

    public Table setBgColor(Color bgColor) {
        String colorHex = String.format("%02x%02x%02x", bgColor.getRed(), bgColor.getGreen(), bgColor.getBlue());
        this.attributes().put("bgcolor", colorHex);
        return this;
    }

    public Table setCellPadding(int padding) {
        this.attributes().add("cellpadding", String.valueOf(padding));
        return this;
    }

    public Table setCellSpacing(int spacing) {
        this.attributes().add("cellspacing", String.valueOf(spacing));
        return this;
    }

    public Table setBorder(int border) {
        this.attributes().add("border", String.valueOf(border));
        return this;
    }

    public Table setWidth(int width) {
        this.attributes().add("width", String.valueOf(width));
        return this;
    }

    public Table setHeight(int height) {
        this.attributes().add("height", String.valueOf(height));
        return this;
    }



}
