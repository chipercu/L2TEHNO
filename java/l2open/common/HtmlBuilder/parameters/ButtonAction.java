package l2open.common.HtmlBuilder.parameters;

public interface ButtonAction {

    default String simpleAction(String action){
        return " action=\"bypass" + action + "\"";
    }

}
