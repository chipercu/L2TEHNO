package l2open.common.Html_Constructor.tags.parameters;

import l2open.common.Html_Constructor.tags.Img;

public interface Parameters extends ButtonAction{


    default String HEIGHT(int h) {
        return " height=" + h;
    }

    default String WIDTH(int w) {
        return " width=" + w;
    }

    default String ALIGN(Position value) {
        return " align=\"" + value.getValue() + "\"";
    }

    default String VALIGN(Position value) {
        return " valign=\"" + value.getValue() + "\"";
    }

    default String BORDER(int value) {
        return " border=" + value;
    }

    default String align(Position value) {
        return new ParametrValue(" align=\"", value.getValue()).toString();
    }

    default String valign(Position value) {
        return new ParametrValue(" valign=\"", value.getValue()).toString();
    }

    default String background(String value) {
        return new ParametrValue(" background=\"", value).toString();
    }

    default String color(String value) {
        return new ParametrValue(" color=\"", value).toString();
    }

    default String value(String value) {
        return new ParametrValue(" value=\"", value).toString();
    }

    default String action(String value) {
        return new ParametrValue(" action=\"", value).toString();
    }
    default String actionNpc(String value) {
        return new ParametrValue(" action=\"bypass -h npc_%objectId%_", value).toString();
    }

    default String back(String value) {
        return new ParametrValue(" back=\"", value).toString();
    }

    default String fore(String value) {
        return new ParametrValue(" fore=\"", value).toString();
    }

    default String src(String value) {
        return new ParametrValue(" src=\"", value).toString();
    }

    default String cellpadding(int size) {
        return new ParametrValue(" cellpadding=", size).toString();
    }

    default String border(int size) {
        return new ParametrValue(" border=", size).toString();
    }

    default String cellspacing(int size) {
        return new ParametrValue(" cellspacing=", size).toString();
    }

    default String width(int size) {
        return new ParametrValue(" width=", size).toString();
    }

    default String fixwidth(int size) {
        return new ParametrValue(" FIXWIDTH=", size).toString();
    }

    default String height(int size) {
        return new ParametrValue(" height=", size).toString();
    }

    default String separator(int width, int... height){
        int h = 1;
        if (height.length > 0){
            h = height[0];
        }
        return new Img("l2ui.squaregray", width, h).build();
    }



}
