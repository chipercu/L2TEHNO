package l2open.common.Html_Constructor.tags;

public class Button implements HtmlBuildInterface {
   // <button value="MP" action="bypass -h _bbsbufferheal MP $Who" width=40 height=25 back="L2UI_CT1.Button_DF_Down" fore="L2UI_CT1.Button_DF">
    private String value = "";
    private String action = "";
    private int width = 32;
    private int height = 32;
    private String back = "L2UI_ct1.button_df";
    private String fore = "L2UI_ct1.button_df";

    public Button(String value, String action, int width, int height, String back, String fore) {
        this.value = value;
        this.action = action;
        this.width = width;
        this.height = height;
        this.back = back;
        this.fore = fore;
    }
    public Button(String action, int width, int height, String back, String fore) {
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

    public Button(String value, String action){
        this.value = value;
        this.action = action;
    }
    public Button(String value){
        this.value = value;
    }

    public Button(){
    }

    @Override
    public String build() {
//        action="bypass -h scripts_events.lastHero.LastHero:addPlayer";
        return "<button " + (value != null ? "value=\"" + value : "")+ "\"" + action + " width="+ width + " height=" + height + " back=\"" + back + "\" fore=\"" + fore +  "\">";
    }
}
