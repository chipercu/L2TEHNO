package l2open.common.Html_Constructor.tags;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Col implements Build{
    private static final String START_COL = "       <td";
    private static final String END_COL = "</td>\n";
    private static final String CLOSE_PARAM = ">";
    private final List<String> params = new ArrayList<>();
    private String body = "";

    public Col(){
    }

    public Col setParams(String... parameters){
        params.addAll(Arrays.asList(parameters));
        return this;
    }
    public Col insert(String html){
        this.body += html;
        return this;
    }

    public String build(){
        StringBuilder sb = new StringBuilder();
        sb.append(START_COL);
        if (!params.isEmpty()){
            for (String p: params){
                sb.append(p);
            }
        }
        sb.append(CLOSE_PARAM).append(body).append(END_COL);

        return sb.toString();
    }
}
