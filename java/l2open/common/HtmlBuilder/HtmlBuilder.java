package l2open.common.HtmlBuilder;

import l2open.common.HtmlBuilder.components.ALIGN;
import l2open.common.HtmlBuilder.components.VALIGN;
import l2open.gameserver.xml.XmlUtils;
import org.dom4j.DocumentException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Attribute;
import org.jsoup.nodes.Attributes;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.awt.*;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by a.kiperku
 * Date: 12.02.2024
 */

public class HtmlBuilder implements HtmlElement {

    private final List<HtmlElement> elements;
    private String title;
    private boolean isScrolled;

    public HtmlBuilder() {
        this.elements = new ArrayList<>();
        this.title = "";
        this.isScrolled = true;
    }

    public HtmlBuilder(String title, boolean isScrolled) {
        this.elements = new ArrayList<>();
        this.title = title;
        this.isScrolled = isScrolled;
    }

    public void addElement(HtmlElement element){
        this.elements.add(element);
    }

    public static HtmlBuilder parsFile(File file) throws IOException {

        final HtmlBuilder htmlBuilder = new HtmlBuilder();
        final Document doc = Jsoup.parse(file, "UTF-8");

        final Element first = doc.getElementsByTag("table").first().getElementsByTag("td").first().getElementsByTag("button").first();

        first.attribute("value").setValue("FUZZY");

        System.out.println(doc);




        final Elements html = doc.getElementsByTag("html");

        final Attribute noscrollbar = html.first().attribute("noscrollbar");
        if (noscrollbar != null){
            htmlBuilder.setScrolled(false);
        }
        final Element title = doc.getElementsByTag("title").first();
        if (title != null){
            htmlBuilder.setTitle(title.text());
        }
        final Element body = doc.getElementsByTag("body").first();

        final Elements bodyElements = body.getAllElements();
        for (Element element: bodyElements){
          if (element.tag().getName().equalsIgnoreCase("table")){
              final Attributes attributes = element.attributes();
              final Table table = new Table();
              for (Attribute attribute: attributes){
                  switch (attribute.getKey()){
                      case "border" : table.setBorder(Integer.parseInt(attribute.getValue()));break;
                      case "cellpadding" : table.setCellPadding(Integer.parseInt(attribute.getValue()));break;
                      case "cellspacing" : table.setCellSpacing(Integer.parseInt(attribute.getValue()));break;
                      case "width" : table.setWidth(Integer.parseInt(attribute.getValue()));break;
                      case "height" : table.setHeight(Integer.parseInt(attribute.getValue()));break;
                      case "background" : table.setBackground(attribute.getValue());break;
                      case "bgColor" : table.setBgColor(Color.decode("#" + attribute.getValue()));break;
                  }
              }
              final Elements tr_elements = element.getElementsByTag("tr");
              for (Element tr: tr_elements){
                  final Elements td_elements = tr.getElementsByTag("td");
                  final Row row = new Row(td_elements.size());
                  for (Element td: td_elements){
                      final Col col = new Col();
                      final Attributes td_attributes = td.attributes();
                      for (Attribute td_attr: td_attributes){
                          switch (td_attr.getKey()){
                              case "valign" : col.setValign(VALIGN.valueOf(td_attr.getValue()));break;
                              case "align" : col.setAlign(ALIGN.valueOf(td_attr.getValue()));break;
                              case "width" : col.setWidth(Integer.parseInt(td_attr.getValue()));break;
                              case "height" : col.setHeight(Integer.parseInt(td_attr.getValue()));break;
                              case "fixheight" : col.setFixWidth(Integer.parseInt(td_attr.getValue()));break;
                          }
                      }
                      row.addCol(col);
                  }
                  table.addRow(row);
              }

              htmlBuilder.addElement(table);
          } else if (element.tag().getName().equalsIgnoreCase("br")) {
              htmlBuilder.addElement(new BR());
          }
        }
        return htmlBuilder;
    }


    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public boolean isScrolled() {
        return isScrolled;
    }

    public void setScrolled(boolean scrolled) {
        isScrolled = scrolled;
    }

    @Override
    public String toString() {
        return build();
    }

    @Override
    public String build() {
        StringBuilder builder = new StringBuilder();

        builder.append("<html");
        if (!isScrolled){
            builder.append(" noscrollbar>\n");
        } else {
            builder.append(">\n");
        }
        if (!title.isEmpty()){
            builder.append("<title>").append(title).append("</title>\n");
        }
        builder.append("<body>");

        for (HtmlElement element: elements){
            builder.append(element);
        }

        builder.append("\n</body>");
        builder.append("\n</html>");

        return builder.toString();
    }
}
