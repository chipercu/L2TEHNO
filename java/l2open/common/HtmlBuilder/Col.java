package l2open.common.HtmlBuilder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static l2open.common.HtmlBuilder.parameters.Parameters.*;
import static l2open.common.HtmlBuilder.parameters.Position.*;

public class Col implements HtmlBuildInterface {
    private static final String START_COL = "       <td";
    private static final String END_COL = "</td>\n";
    private static final String CLOSE_PARAM = ">";
    private final List<String> params = new ArrayList<>();
    private String body = "";

    private boolean center = false;

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

    public Col insert(int html){
        this.body += html;
        return this;
    }

    public Col insert(HtmlBuildInterface build){
        this.body += build.build();
        return this;
    }

    public Col setCenter() {
        this.center = true;
        return this;
    }

    public String build(){
        StringBuilder sb = new StringBuilder();
        sb.append(START_COL);
        if (!params.isEmpty()){

            if (!params.contains(align(CENTER)) || !params.contains(align(LEFT)) || !params.contains(align(RIGHT)) || !params.contains(align(TOP)) || !params.contains(align(BOTTOM))){
                params.add(align(CENTER));
            }
            if (!params.contains(valign(CENTER)) || !params.contains(valign(LEFT)) || !params.contains(valign(RIGHT)) || !params.contains(valign(TOP)) || !params.contains(valign(BOTTOM))) {
                params.add(valign(TOP));
            }

            for (String p: params){
                sb.append(p);
            }
        }
        sb.append(CLOSE_PARAM).append(body).append(END_COL);

        if (center){
            return "<center>" + sb + "</center>";
        }else {
            return sb.toString();
        }
    }
}
