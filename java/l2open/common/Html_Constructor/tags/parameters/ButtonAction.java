package l2open.common.Html_Constructor.tags.parameters;

public interface ButtonAction {

    default String simpleAction(String action){
        return " action=\"bypass" + action + "\"";
    }

}
