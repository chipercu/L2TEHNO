package l2open.common.HtmlBuilder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by a.kiperku
 * Date: 15.08.2023
 */

public class Combobox implements HtmlElement {


    private static final String START_EDIT = "       <combobox";
    private static final String CLOSE_PARAM = ">";
    private final List<String> params = new ArrayList<String>();
    private String var;
    private List<String> list;

    public Combobox(String var, List<String> list){
        this.var = var;
        this.list = list;

    }

    public Combobox setParams(String... parameters){
        params.addAll(Arrays.asList(parameters));
        return this;
    }


    public String build(){
        StringBuilder sb = new StringBuilder();
        sb.append(START_EDIT);
        if (!params.isEmpty()){
            for (String p: params){
                sb.append(p);
            }
        }
        sb.append(" var=\"").append(var).append("\"");
        sb.append(" list=\"");
        if (!list.isEmpty()){
            for (String l: list){
                sb.append(l).append(";");
            }
        }
        sb.append("\"");
        sb.append(CLOSE_PARAM);
        return sb.toString();
    }

}
