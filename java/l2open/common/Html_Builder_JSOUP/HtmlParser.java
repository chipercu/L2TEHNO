package l2open.common.Html_Builder_JSOUP;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.File;
import java.io.IOException;

/**
 * Created by a.kiperku
 * Date: 12.02.2024
 */

public class HtmlParser {



    public static HtmlBuilder parse(File file){
        try {
            final Document parse = Jsoup.parse(file, "UTF-8");
            parse.getElementsByTag("head").clear();
            return new HtmlBuilder(parse);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
