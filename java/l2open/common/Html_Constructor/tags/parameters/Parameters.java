package l2open.common.Html_Constructor.tags.parameters;


import l2open.common.Html_Constructor.tags.Img;

public interface Parameters extends ButtonAction{


    static String HEIGHT(int h) {
        return " height=" + h;
    }

    static String WIDTH(int w) {
        return " width=" + w;
    }

    static String ALIGN(Position value) {
        return " align=\"" + value.getValue() + "\"";
    }

    static String VALIGN(Position value) {
        return " valign=\"" + value.getValue() + "\"";
    }

    static String BORDER(int value) {
        return " border=" + value;
    }

    static String align(Position value) {
        return new ParametrValue(" align=\"", value.getValue()).toString();
    }

    static String valign(Position value) {
        return new ParametrValue(" valign=\"", value.getValue()).toString();
    }

    static String background(String value) {
        return new ParametrValue(" background=\"", value).toString();
    }

    static String color(String value) {
        return new ParametrValue(" color=\"", value).toString();
    }

    static String value(String value) {
        return new ParametrValue(" value=\"", value).toString();
    }

    static String action(String value) {
        return new ParametrValue(" action=\"", value).toString();
    }
    static String actionNpc(String value) {
        return new ParametrValue(" action=\"bypass -h npc_%objectId%_", value).toString();
    }

    static String back(String value) {
        return new ParametrValue(" back=\"", value).toString();
    }

    static String fore(String value) {
        return new ParametrValue(" fore=\"", value).toString();
    }

    static String src(String value) {
        return new ParametrValue(" src=\"", value).toString();
    }

    static String cellpadding(int size) {
        return new ParametrValue(" cellpadding=", size).toString();
    }

    static String border(int size) {
        return new ParametrValue(" border=", size).toString();
    }

    static String cellspacing(int size) {
        return new ParametrValue(" cellspacing=", size).toString();
    }

    static String width(int size) {
        return new ParametrValue(" width=", size).toString();
    }

    static String fixwidth(int size) {
        return new ParametrValue(" FIXWIDTH=", size).toString();
    }

    static String height(int size) {
        return new ParametrValue(" height=", size).toString();
    }

    static String separator(int width, int... height){
        int h = 1;
        if (height.length > 0){
            h = height[0];
        }
        return new Img("l2ui.squaregray", width, h).build();
    }



}
