package l2open.common.Html_Builder_JSOUP.Elements;

import l2open.common.Html_Builder_JSOUP.Elements.enums.HSize;
import org.jsoup.nodes.Element;

import java.awt.*;

public class Font extends Element {

    public Font(String text, Color color) {
        super("font");
        String colorHex = String.format("%02x%02x%02x", color.getRed(), color.getGreen(), color.getBlue());
        this.attributes().put("color", colorHex);
        this.text(text);
    }

    public Font(String text, Color color, HSize size) {
        super("font");
        String colorHex = String.format("%02x%02x%02x", color.getRed(), color.getGreen(), color.getBlue());
        this.attributes().put("color", colorHex);
        this.attributes().put("name", size.getName());
        this.text(text);
    }

    public Font setText(String text){
        this.text(text);
        return this;
    }

    public Font setSize(HSize size){
        this.attributes().put("name", size.getName());
        return this;
    }

    public Font setColor(Color color){
        String colorHex = String.format("%02x%02x%02x", color.getRed(), color.getGreen(), color.getBlue());
        this.attributes().put("color", colorHex);
        return this;
    }



}
