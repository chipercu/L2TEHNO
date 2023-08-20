package l2open.common.Html_Constructor.tags;

public class Button implements Build {
   // <button value="MP" action="bypass -h _bbsbufferheal MP $Who" width=40 height=25 back="L2UI_CT1.Button_DF_Down" fore="L2UI_CT1.Button_DF">
    private String value;
    private String action;
    private int width;
    private int height;
    private String back = "L2UI_ct1.button_df";
    private String fore = "L2UI_ct1.button_df";

    private String defaultBack = "L2UI_ct1.button_df";
    private String defaultFore = "L2UI_ct1.button_df";
//    private String buttonHTML = "";

    public Button(String value, String action, int width, int height, String back, String fore) {
        this.value = value;
        this.action = action;
        this.width = width;
        this.height = height;
        this.back = back;
        this.fore = fore;
    }
    public Button(String value, String action, int width, int height) {
        this.value = value;
        this.action = action;
        this.width = width;
        this.height = height;
    }
    public Button(String action, int width, int height, String back, String fore) {
        this.action = action;
        this.width = width;
        this.height = height;
        this.back = back;
        this.fore = fore;
    }

    public String getValue() {
        return value;
    }

    public Button setValue(String value) {
        this.value = value;
        return this;
    }

    public String getAction() {
        return action;
    }

    public Button setAction(String action) {
        this.action = action;
        return this;
    }

    public int getWidth() {
        return width;
    }

    public Button setWidth(int width) {
        this.width = width;
        return this;
    }

    public int getHeight() {
        return height;
    }

    public Button setHeight(int height) {
        this.height = height;
        return this;
    }

    public String getBack() {
        return back;
    }

    public Button setBack(String back) {
        this.back = back;
        return this;
    }

    public String getFore() {
        return fore;
    }

    public Button setFore(String fore) {
        this.fore = fore;
        return this;
    }

    @Override
    public String build() {

//        action="bypass -h scripts_events.lastHero.LastHero:addPlayer";



        return "<button " + (value != null ? "value=\"" + value : "")+ "\"" + action + " width="+ width + " height=" + height + " back=\"" + back + "\" fore=\"" + fore +  "\">";
    }
}
