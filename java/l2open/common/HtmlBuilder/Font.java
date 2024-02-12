package l2open.common.HtmlBuilder;

import l2open.common.HtmlBuilder.components.HSize;
import l2open.common.HtmlBuilder.parameters.Color;
import l2open.common.HtmlBuilder.components.Position;

public class Font implements HtmlElement {

    private final Color color;
    private final String text;
    private Position position;
    private HSize hSize;

    public Font(Color color, String text){
        this.color = color;
        this.text = text;
    }
    public Font(Color color, int text){
        this.color = color;
        this.text = String.valueOf(text);
    }
    public Font(Color color, long text){
        this.color = color;
        this.text = String.valueOf(text);
    }


    public Color getColor() {
        return color;
    }

    public String getText() {
        return text;
    }

    public Position getPosition() {
        return position;
    }

    public void setPosition(Position position) {
        this.position = position;
    }

    public HSize gethSize() {
        return hSize;
    }

    public void sethSize(HSize hSize) {
        this.hSize = hSize;
    }

    @Override
    public String build() {
        return "<font color=" + color.getValue() +">" + text + "</font>";
    }
}
