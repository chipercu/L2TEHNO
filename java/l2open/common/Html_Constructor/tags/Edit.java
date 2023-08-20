package l2open.common.Html_Constructor.tags;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by a.kiperku
 * Date: 15.08.2023
 */

public class Edit implements Build {


    private static final String START_EDIT = "       <edit";
    private static final String CLOSE_PARAM = ">";
    private final List<String> params = new ArrayList<String>();
    private String var;
    private String type = "text";

    public Edit(String var){
        this.var = var;
    }

    public Edit setParams(String... parameters){
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
        sb.append(" var=\"").append(var).append("\"")
                        .append(" type=\"text\"");


        sb.append(CLOSE_PARAM);
        return sb.toString();
    }

}
