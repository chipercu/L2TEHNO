package l2open.common.Html_Builder_JSOUP.Elements;

import l2open.common.Html_Builder_JSOUP.Elements.enums.EditType;
import org.jsoup.nodes.Attribute;
import org.jsoup.nodes.Element;

public class Edit extends Element {

    public Edit(String var, int width, int height, EditType type, int length) {
        super("edit");
        this.attributes().put("var", var);
        this.attributes().put("type", type.name());
        this.attributes().put("width", String.valueOf(width));
        this.attributes().put("height", String.valueOf(height));
        this.attributes().put("length", String.valueOf(length));
    }

    public Edit(String var, int width, int height, EditType type) {
        super("edit");
        this.attributes().put("var", var);
        this.attributes().put("type", type.name());
        this.attributes().put("width", String.valueOf(width));
        this.attributes().put("height", String.valueOf(height));
        this.attributes().put("length", String.valueOf(12));
    }
    public Edit(String var, int width, int height) {
        super("edit");
        this.attributes().put("var", var);
        this.attributes().put("type", EditType.text.name());
        this.attributes().put("width", String.valueOf(width));
        this.attributes().put("height", String.valueOf(height));
        this.attributes().put("length", String.valueOf(12));
    }

    public String getVar(){
        return "$" + this.attribute("var").getValue();
    }

    public Edit setVar(String var){
        this.attributes().put("var", var);
        return this;
    }

    public Edit setType(EditType type){
        this.attributes().put("type", type.name());
        return this;
    }

    public Edit setWidth(int width){
        this.attributes().put("width", String.valueOf(width));
        return this;
    }

    public Edit setHeight(int height){
        this.attributes().put("height", String.valueOf(height));
        return this;
    }

    public Edit setLength(int length){
        this.attributes().put("length", String.valueOf(length));
        return this;
    }



}
