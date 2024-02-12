package l2open.common.HtmlBuilder;

/**
 * Created by a.kiperku
 * Date: 15.08.2023
 */

public class MultiEdit implements HtmlBuildInterface {

    private final int width;
    private final int height;
    private final String var;

    public MultiEdit(String var, int width, int height){
        this.var = var;
        this.width = width;
        this.height = height;
    }

    public String build(){
        return "<multiedit var=\"" + var + "\"" + " width=" + width + " height=" + height + "\">";
    }

}
