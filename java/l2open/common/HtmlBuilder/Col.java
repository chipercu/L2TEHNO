package l2open.common.HtmlBuilder;

import l2open.common.Html_Builder_JSOUP.Elements.enums.ALIGN;
import l2open.common.Html_Builder_JSOUP.Elements.enums.VALIGN;

import java.util.*;

import static l2open.common.HtmlBuilder.parameters.Parameters.*;
import static l2open.common.HtmlBuilder.components.Position.*;

public class Col implements HtmlElement {
    private int width = 0;
    private int fixWidth = 0;
    private int height = 0;
    private ALIGN align;
    private VALIGN valign;
    private List<HtmlElement> elements = new ArrayList<>();

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

    public Col insert(HtmlElement build){
        this.body += build.build();
        return this;
    }

    public Col setCenter() {
        this.center = true;
        return this;
    }

    public Col putElement(HtmlElement element){
        elements.add(element);
        return this;
    }

    public Col setWidth(int width) {
        this.width = width;
        return this;
    }

    public Col setFixWidth(int fixWidth) {
        this.fixWidth = fixWidth;
        return this;
    }

    public Col setHeight(int height) {
        this.height = height;
        return this;
    }

    public Col setAlign(ALIGN align) {
        this.align = align;
        return this;
    }

    public Col setValign(VALIGN valign) {
        this.valign = valign;
        return this;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder("\n\t\t<td");

        if (width != 0){
            builder.append(" width=").append(width);
        }
        if (fixWidth != 0){
            builder.append(" fixwidth=").append(fixWidth);
        }
        if (height != 0){
            builder.append(" height=").append(height);
        }
        if (align != null){
            builder.append(" align=\"").append(align.name()).append("\"");
        }
        if (valign != null){
            builder.append(" valign=\"").append(valign.name()).append("\"");
        }
        builder.append(">");

        for (HtmlElement element : elements){
            builder.append(element);
        }

        builder.append("</td>");
        return builder.toString();
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
