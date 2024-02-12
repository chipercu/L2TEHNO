package l2open.common.HtmlBuilder;

import l2open.common.HtmlBuilder.components.HSize;
import l2open.common.HtmlBuilder.components.Position;

import java.awt.*;

/**
 * Created by a.kiperku
 * Date: 12.02.2024
 */

public class Text implements HtmlElement{

    private final String text;
    private Color color;
    private Position position;
    private HSize hSize;

    public Text(String text) {
        this.text = text;
    }

    public Text setColor(Color color) {
        this.color = color;
        return this;
    }

    public Text setPosition(Position position) {
        this.position = position;
        return this;
    }

    public Text sethSize(HSize hSize) {
        this.hSize = hSize;
        return this;
    }

    @Override
    public String build() {
        StringBuilder builder = new StringBuilder();
        if (color == null && position == null && hSize == null){
            return text;
        }else {
            builder.append("<font");
            if (color != null){
                String colorHex = String.format("%02x%02x%02x", color.getRed(), color.getGreen(), color.getBlue());
                builder.append(" color=\"").append(colorHex).append("\"");
            }
            if (position != null){
                builder.append(" align=").append(position.getValue());
            }
            if (hSize != null){
                builder.append(" name=").append(hSize.getName());
            }
            builder.append(">").append(text).append("</font>");
        }
        return builder.toString();
    }

    @Override
    public String toString() {
        return build();
    }
}
