package l2open.gameserver.xml.parser;

import l2open.config.*;
import org.dom4j.Document;
import org.dom4j.DocumentFactory;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;

import java.io.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.LogManager;
import java.util.logging.Logger;

/**
 * @author : Ragnarok
 * @date : 02.01.11    15:52
 */
public class Parser {
    private static Logger log = Logger.getLogger(Parser.class.getName());
    public static ConcurrentHashMap<Integer, Document> map = new ConcurrentHashMap<Integer, Document>();

    protected static void initialize(String pathToDelete) {
        String LOG_FOLDER = "log";
        String LOG_NAME = "./config/log.properties";

        File logFolder = new File("./", LOG_FOLDER);
        logFolder.mkdir();

        InputStream is = null;
        try {
            is = new FileInputStream(new File(LOG_NAME));
            LogManager.getLogManager().readConfiguration(is);
            is.close();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (is != null)
                    is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        ConfigSystem.load();
        if(pathToDelete != null)
            for (File file : (new File(pathToDelete)).listFiles())
                if(file.getName().endsWith(".xml"))
                    file.delete();
        for (int i = 100000; i >= 0; i -= 100) {
            Document doc = DocumentFactory.getInstance().createDocument();
            map.put(i, doc);
        }
    }

    protected static Element addSet(Element e, String name, Object val) {
        Element set = e.addElement("set");
        set.addAttribute("name", name);
        set.addAttribute("val", val.toString());
        return set;
    }

    protected static String getName(int i) {
        String name1 = String.valueOf(i);
        String name2 = String.valueOf(i + 99);
        while (name1.length() < 5)
            name1 = "0" + name1;
        while (name2.length() < 5)
            name2 = "0" + name2;
        return name1 + "-" + name2;
    }

    protected static Element getListByItemId(int item_id) {
        for (int i = 100000; i >= 0; i -= 100) {
            if (item_id >= i) {
                if (map.get(i).getRootElement() == null) {
                    Element list = map.get(i).addElement("list");
                    map.get(i).setRootElement(list);
                }
                return map.get(i).getRootElement();
            }
        }
        return null;
    }

    protected static void saveAll(String dir, String indent, boolean newlines) throws IOException {
        for (int i : map.keySet()) {
            if (map.get(i).getRootElement() == null || map.get(i).getRootElement().elements().size() == 0)
                   continue;
            File out = new File(dir + getName(i) + ".xml");
            log.info(out.getPath());
            out.createNewFile();
            OutputFormat of = new OutputFormat(indent, newlines);
            XMLWriter writer = new XMLWriter(new FileWriter(out), of);
            writer.write(map.get(i));
            writer.flush();
            writer.close();
        }
    }
}
