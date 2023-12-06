package l2open.common.Html_Constructor.tags;

import l2open.common.Html_Constructor.tags.parameters.Color;

public class Font implements HtmlBuildInterface {

    private final Color color;
    private final String text;

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

    @Override
    public String build() {
        return "<font color=" + color.getValue() +">" + text + "</font>";
    }
}
