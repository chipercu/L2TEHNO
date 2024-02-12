package l2open.common.Html_Builder_JSOUP.Elements;

import org.jsoup.nodes.Attribute;
import org.jsoup.nodes.Element;

import java.util.List;

public class ComboBox extends Element {

    //<combobox width=60 height=10 var="Who" list="Player;Pet">

    public ComboBox(String var, int width, int height, String list) {
        super("combobox");
        this.attributes().put("var", var);
        this.attributes().put("width", String.valueOf(width));
        this.attributes().put("height", String.valueOf(height));
        this.attributes().put("list", list);
    }
    public ComboBox(String var, int width, int height, List<String> list) {
        super("combobox");
        this.attributes().put("var", var);
        this.attributes().put("width", String.valueOf(width));
        this.attributes().put("height", String.valueOf(height));
        this.attributes().put("list", String.join(";", list));
    }
    public ComboBox(String var, int width, int height, String[] list) {
        super("combobox");
        this.attributes().put("var", var);
        this.attributes().put("width", String.valueOf(width));
        this.attributes().put("height", String.valueOf(height));
        this.attributes().put("list", String.join(";", list));
    }

    public String getVar(){
        return "$" + this.attribute("var").getValue();
    }

    public ComboBox setVar(String var){
        this.attributes().put("var", var);
        return this;
    }

    public ComboBox setWidth(int width){
        this.attributes().put("width", String.valueOf(width));
        return this;
    }

    public ComboBox setHeight(int height){
        this.attributes().put("height", String.valueOf(height));
        return this;
    }

    public ComboBox setList(String list){
        this.attributes().put("list", list);
        return this;
    }

    public ComboBox setList(List<String> list){
        this.attributes().put("list", String.join(";", list));
        return this;
    }

    public ComboBox setList(String[] list){
        this.attributes().put("list", String.join(";", list));
        return this;
    }

    public ComboBox addToList(String arg){
        Attribute list = this.attribute("list");
        if (list == null){
            this.attributes().put("list", arg);
        }else {
            this.attributes().put("list", list.getValue() + ";" + arg);
        }
        return this;
    }




}
