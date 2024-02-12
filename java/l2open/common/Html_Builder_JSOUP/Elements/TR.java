package l2open.common.Html_Builder_JSOUP.Elements;

import org.jsoup.nodes.Element;

/**
 * Created by a.kiperku
 * Date: 12.02.2024
 */

public class TR extends Element {

    public TR() {
        super("tr");
    }

    public TD addTD(){
        final TD td = new TD();
        this.appendChild(td);
        return td;
    }
}
