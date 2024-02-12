package l2open.common.Html_Builder_JSOUP;

import l2open.common.Html_Builder_JSOUP.Elements.Table;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

/**
 * Created by a.kiperku
 * Date: 12.02.2024
 */

public class HtmlBuilder {

    private final Document document;

    public HtmlBuilder() {
        this.document = Jsoup.parse("");
        this.document.getElementsByTag("head").clear();
    }

    public HtmlBuilder(Document document) {
        this.document = document;
    }

    public Document getDocument() {
        return document;
    }

    public Table addTable(){
        final Table table = new Table();
        document.body().appendChild(table);
        return table;
    }

    public HtmlBuilder setTitle(String title){
        final Element element = getDocument().body().getElementsByTag("title").first();
        if (element == null){
            getDocument().body().appendElement("title").text(title);
        }
        return this;
    }

    public<T extends Element> T put(T element){
        document.body().appendChild(element);
        return element;
    }


}
