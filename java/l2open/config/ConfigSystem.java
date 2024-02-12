package l2open.config;

import l2open.Server;
import l2open.common.ThreadPoolManager;
import l2open.extensions.scripts.Scripts;
import l2open.gameserver.GameStart;
import l2open.gameserver.loginservercon.AdvIP;
import l2open.gameserver.model.*;
import l2open.gameserver.model.base.PlayerAccess;
import l2open.gameserver.model.quest.Quest;
import l2open.util.*;

import org.w3c.dom.Document;
import org.w3c.dom.Node;

import gnu.trove.TIntIntHashMap;

import java.io.*;
import java.lang.reflect.Field;
import java.math.BigInteger;
import java.nio.file.Files;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;
import java.util.regex.Pattern;
import java.util.*;

import javax.xml.parsers.DocumentBuilderFactory;

/**
 * @author : Diagod
 * @date : 12.06.12
 */
public class ConfigSystem {
    private static final boolean develop = Boolean.parseBoolean(System.getenv("DEVELOP"));
    private static final Logger _log = Logger.getLogger(ConfigSystem.class.getName());
    private static final String dir = develop ? "java/config" : "./config";
    private static Map<Integer, Float> questRewardRates = new HashMap<Integer, Float>();
    private static Map<Integer, Float> questDropRates = new HashMap<Integer, Float>();
    private static ConcurrentHashMap<String, String> properties = new ConcurrentHashMap<String, String>();

    private static Map<String, GArray<String>> _cacheProperties = new HashMap<String, GArray<String>>();

    private static GArray<String> configKey = new GArray<String>();

    private static Class _configValue = ConfigValue.class;

    /**
     * Some more LS Settings
     */
    public static GArray<String> INTERNAL_IP = null;
    /**
     * Продвинутый список локальных сетей / ip-адресов
     */
    public static NetList INTERNAL_NETLIST = null;

    public static NetList PACKETLOGGER_IPS = null;

    public static Pattern[] TRADE_WORDS;

    public static void load() {
        File files = new File(dir);
        if (!files.exists())
            _log.warning("WARNING! " + dir + " not exists! Config not loaded!");
        else
            parseFiles(files.listFiles());

        if (Server.SERVER_MODE == Server.MODE_GAMESERVER || Server.SERVER_MODE == Server.MODE_COMBOSERVER) {
            loadSkillDurationList();
            loadMyConfig(dir + "/my.properties");
            loadMyConfig("../../../java/dev.properties");

            abuseLoad();
            loadGMAccess();
            _log.info("loading xml GMAccess");
            if (ConfigValue.AdvIPSystem)
                ipsLoad();
            loadPL();
        } else
            loadMyConfig("../../../java/dev.properties");

        if (ConfigValue.GuardType.equals("LameGuard")) {
            ConfigValue.LameGuard = true;
            ConfigValue.CCPGuardEnable = false;
            ConfigValue.SmartGuard = false;
            ConfigValue.FirstTeam = false;
            ConfigValue.ProtectEnable = true;
            ConfigValue.StrixGuardEnable = false;
            ConfigValue.ScriptsGuardEnable = false;
        } else if (ConfigValue.GuardType.equals("CCPGuard")) {
            ConfigValue.CCPGuardEnable = true;
            ConfigValue.LameGuard = false;
            ConfigValue.SmartGuard = false;
            ConfigValue.FirstTeam = false;
            ConfigValue.ProtectEnable = true;
            ConfigValue.StrixGuardEnable = false;
            ConfigValue.ScriptsGuardEnable = false;
        } else if (ConfigValue.GuardType.equals("SmartGuard")) {
            ConfigValue.CCPGuardEnable = false;
            ConfigValue.LameGuard = false;
            ConfigValue.SmartGuard = true;
            ConfigValue.FirstTeam = false;
            ConfigValue.ProtectEnable = true;
            ConfigValue.StrixGuardEnable = false;
            ConfigValue.ScriptsGuardEnable = false;
        } else if (ConfigValue.GuardType.equals("FirstTeam")) {
            ConfigValue.CCPGuardEnable = false;
            ConfigValue.LameGuard = false;
            ConfigValue.SmartGuard = false;
            ConfigValue.FirstTeam = true;
            ConfigValue.ProtectEnable = true;
            ConfigValue.StrixGuardEnable = false;
            ConfigValue.ScriptsGuardEnable = false;
        } else if (ConfigValue.GuardType.equals("StrixGuard")) {
            org.strixplatform.StrixPlatform.getInstance();
            ConfigValue.CCPGuardEnable = false;
            ConfigValue.LameGuard = false;
            ConfigValue.SmartGuard = false;
            ConfigValue.FirstTeam = false;
            ConfigValue.ProtectEnable = true;
            ConfigValue.StrixGuardEnable = true;
            ConfigValue.ScriptsGuardEnable = false;
        } else if (ConfigValue.GuardType.equals("ScriptsGuard")) {
            ConfigValue.CCPGuardEnable = false;
            ConfigValue.LameGuard = false;
            ConfigValue.SmartGuard = false;
            ConfigValue.FirstTeam = false;
            ConfigValue.ProtectEnable = true;
            ConfigValue.StrixGuardEnable = false;
            ConfigValue.ScriptsGuardEnable = true;
        } else if (ConfigValue.GuardType.equals("SmartGuard|LameGuard") || ConfigValue.GuardType.equals("LameGuard|SmartGuard")) {
            ConfigValue.CCPGuardEnable = false;
            ConfigValue.LameGuard = true;
            ConfigValue.SmartGuard = true;
            ConfigValue.FirstTeam = false;
            ConfigValue.ProtectEnable = true;
            ConfigValue.StrixGuardEnable = false;
            ConfigValue.ScriptsGuardEnable = false;
        } else {
            ConfigValue.LameGuard = false;
            ConfigValue.CCPGuardEnable = false;
            ConfigValue.SmartGuard = false;
            ConfigValue.FirstTeam = false;
            ConfigValue.ProtectEnable = false;
            ConfigValue.StrixGuardEnable = false;
            ConfigValue.ScriptsGuardEnable = false;
        }

        String[] chars =
                {
                        "68VkN3pN7FSO722VIn3ljY21Rfs033",
                        "48lTZq45e150TnVYN5ync82Kq0sN4a",
                        "DiagoD"
                };
        if (!Util.contains(chars, ConfigValue.LicenseKey)) {
            ConfigValue.RangEnable = false;
            ConfigValue.RangReloadInfoInterval = 86400;
            //ConfigValue.EnableMarcket = false;
        }
        RateService.loadDefaultValue();

        if (ConfigValue.MultiSub_Enable)
            ConfigValue.Multi_Enable = true;

        if (develop) {
            ConfigValue.DatapackRoot = "";
        }
    }

    public static void reload() {
        synchronized (questRewardRates) {
            synchronized (questDropRates) {
                properties.clear();
                questRewardRates.clear();
                questDropRates.clear();
                if (INTERNAL_IP != null)
                    INTERNAL_IP.clear();
                configKey.clear();
                _cacheProperties.clear();
                load();
            }
        }
    }

    private static void parseFiles(File[] files) {
        for (File f : files) {
            if (f.isHidden())
                continue;
            if (f.isDirectory() && !f.getName().contains("defaults"))
                parseFiles(f.listFiles());
            if (f.getName().startsWith("quest_reward_rates")) {
                try {
                    Properties p = new Properties();
                    p.load(new InputStreamReader(new FileInputStream(f), "UTF-8"));
                    loadQuestRewardRates(p);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else if (f.getName().startsWith("quest_drop_rates")) {
                try {
                    Properties p = new Properties();
                    p.load(new InputStreamReader(new FileInputStream(f), "UTF-8"));
                    loadQuestDropRates(p);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else if (f.getName().endsWith(".properties") && !f.getName().endsWith("log.properties")) {
                try {
                    Properties p = new Properties();
                    p.load(new InputStreamReader(new FileInputStream(f), "UTF-8"));
                    loadProperties(p, f.getName());
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else if (f.getName().startsWith("hexid.txt")) {
                try {
                    Properties p = new Properties();
                    p.load(new InputStreamReader(new FileInputStream(f), "UTF-8"));
                    ConfigValue.HexID = new BigInteger(p.getProperty("HexID").trim(), 16).toByteArray();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private static void loadQuestRewardRates(Properties p) {
        for (String name : p.stringPropertyNames()) {
            int id;
            try {
                id = Integer.parseInt(name);
            } catch (NumberFormatException nfe) {
                continue;
            }
            if (questRewardRates.get(id) != null) {
                questRewardRates.put(id, Float.parseFloat(p.getProperty(name).trim())); // replace
                _log.info("Duplicate quest id \"" + name + "\"");
            } else if (p.getProperty(name) == null)
                _log.info("Null property for quest id " + name);
            else
                questRewardRates.put(id, Float.parseFloat(p.getProperty(name).trim()));
        }
        p.clear();
    }

    private static void loadQuestDropRates(Properties p) {
        for (String name : p.stringPropertyNames()) {
            int id;
            try {
                id = Integer.parseInt(name);
            } catch (NumberFormatException nfe) {
                continue;
            }
            if (questDropRates.get(id) != null) {
                questDropRates.put(id, Float.parseFloat(p.getProperty(name).trim())); // replace
                _log.info("Duplicate quest id \"" + name + "\"");
            } else if (p.getProperty(name) == null)
                _log.info("Null property for quest id " + name);
            else
                questDropRates.put(id, Float.parseFloat(p.getProperty(name).trim()));
        }
        p.clear();
    }


    private static void loadProperties(Properties p, String fileName) {
        GArray<String> _configKey = new GArray<String>();
        for (String name : p.stringPropertyNames()) {
            if (p.getProperty(name) == null)
                _log.info("Null property for key " + name);
            else {
                if (!name.equals("CoreRevision") && !name.equals("LicenseRevision")) {
                    configKey.add(name);
                    _configKey.add(name);

                    if (name.equals("ExternalHostname")) {
                        ConfigValue.ExternalHostname(p.getProperty(name).trim());
                        continue;
                    }
                    if (name.equals("InternalIpList")) {
                        String internalIpList = p.getProperty(name).trim();//getProperty(serverSettings, "InternalIpList", "127.0.0.1,192.168.0.0-192.168.255.255,10.0.0.0-10.255.255.255,172.16.0.0-172.16.31.255");
                        if (internalIpList.startsWith("NetList@")) {
                            INTERNAL_NETLIST = new NetList();
                            INTERNAL_NETLIST.LoadFromFile(internalIpList.replaceFirst("NetList@", ""));
                            _log.info("Loaded " + INTERNAL_NETLIST.NetsCount() + " Internal Nets");
                        } else {
                            INTERNAL_IP = new GArray<String>();
                            INTERNAL_IP.addAll(Arrays.asList(internalIpList.split(",")));
                        }
                        continue;
                    }

                    try {
                        properties.put(name, p.getProperty(name).trim());
                    } catch (IllegalArgumentException e) {
                        e.printStackTrace();
                    }

                    try {
                        Field f = _configValue.getField(name);
                        setToType(f, p.getProperty(name).trim());
                    } catch (IllegalArgumentException e) {
                        e.printStackTrace();
                    } catch (NoSuchFieldException e) {
                        _log.warning("ConfigSystem(383): -> NoSuchFieldException(" + fileName + "): " + name);
                    }
                }
            }
        }
        _cacheProperties.put(fileName, _configKey);
        p.clear();
    }

    private static void setToType(Field f, String value) {
        if (f.getName().equals("ExternalHostname")) {
            ConfigValue.ExternalHostname(value);
            return;
        }
        try {
            if (f.getType().getName().equals("int")) {
                try {
                    f.setInt(f, Integer.parseInt(value.trim()));
                } catch (Exception e) // Если ошибка, возможно у нас RGB код, пытаемся декодировать...
                {
                    if (!value.trim().startsWith("0x"))
                        f.setInt(f, Long.decode("0x" + value).intValue());
                    else
                        f.setInt(f, Long.decode(value).intValue());
                }
            } else if (f.getType().getName().equals("boolean"))
                f.setBoolean(f, Boolean.parseBoolean(value.replace(" ", "")));
            else if (f.getType().getName().equals("byte"))
                f.setByte(f, Byte.parseByte(value.replace(" ", "")));
            else if (f.getType().getName().equals("double"))
                f.setDouble(f, Double.parseDouble(value.replace(" ", "")));
            else if (f.getType().getName().equals("float"))
                f.setFloat(f, Float.parseFloat(value.replace(" ", "")));
            else if (f.getType().getName().equals("long"))
                f.setLong(f, Long.parseLong(value.replace(" ", "")));
            else if (f.getType().getName().equals("short"))
                f.setShort(f, Short.parseShort(value.replace(" ", "")));
            else if (f.getType().getName().equals("java.lang.String"))
                f.set(f, value);
            else if (f.getType().getName().equals("[J"))
                f.set(f, Util.parseCommaSeparatedLongArray(value.replace(" ", "")));
            else if (f.getType().getName().equals("[[J"))
                f.set(f, Util.parseCommaSeparatedLongArrays(value.replace(" ", "")));
            else if (f.getType().getName().equals("[I"))
                f.set(f, Util.parseCommaSeparatedIntegerArray(value.replace(" ", "")));
            else if (f.getType().getName().equals("[[I"))
                f.set(f, Util.parseCommaSeparatedIntegerArrays(value.replace(" ", "")));
            else if (f.getType().getName().equals("[[[I"))
                f.set(f, Util.parseCommaSeparatedIntegerArrays3(value.replace(" ", "")));
            else if (f.getType().getName().equals("[D"))
                f.set(f, Util.parseCommaSeparatedDoubleArray(value.replace(" ", "")));
            else if (f.getType().getName().equals("[[D"))
                f.set(f, Util.parseCommaSeparatedDoubleArrays(value.replace(" ", "")));
            else if (f.getType().getName().startsWith("[F"))
                f.set(f, Util.parseCommaSeparatedFloatArray(value.replace(" ", "")));
            else if (f.getType().getName().startsWith("[[F"))
                f.set(f, Util.parseCommaSeparatedFloatArrays(value.replace(" ", "")));
            else if (f.getType().getName().startsWith("[Ljava.lang.String"))
                f.set(f, Util.parseCommaSeparatedStringArray(value));
            else if (f.getType().getName().startsWith("[[Ll2open.gameserver.model.L2Skill"))
                f.set(f, Util.parseCommaSeparatedL2SkillArrays(value));
            else if (f.getType().getName().startsWith("[Ll2open.gameserver.model.L2Zone$ZoneType"))
                f.set(f, Util.parseCommaSeparatedZoneTypeArray(value));
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
		/*catch(NumberFormatException e)
		{
			_log.warning("ConfigSystem(281): -> NumberFormatException: " + f.getName() + " Type: " + f.getType().getName() + " Value: " + value);
		}*/
    }

    public static float getQuestRewardRates(Quest q) {
        return questRewardRates.containsKey(q.getQuestIntId()) ? questRewardRates.get(q.getQuestIntId()) : 0.0F;
    }

    public static float getQuestDropRates(Quest q) {
        return questDropRates.containsKey(q.getQuestIntId()) ? questDropRates.get(q.getQuestIntId()) : 0.0F;
    }

    public static float getQuestDropRates(int id) {
        return questDropRates.containsKey(id) ? questDropRates.get(id) : 0.0F;
    }

    // Нужно только для старых версий лицухи.
    public static String get(String name, String ifs) {
        return get(name, false);
    }

    // Нужно только для старых версий лицухи.
    public static String get(String name, boolean ifs) {
        if (name.equals("ExternalHostname"))
            return ConfigValue.ExternalHostname();
        String result = null;
        try {
            Field f = _configValue.getField(name);
            result = "" + f.get(f);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (NoSuchFieldException e) {
            _log.warning("ConfigSystem(318): -> NoSuchFieldException: " + name);
        }
        if (result == null)
            if (ifs) {
                _log.warning("ConfigSystem: Null value for key: " + name);
                //l2open.util.Util.checkPerMission();
            } else
                return "";
        return result;
    }

    public static String get(String name) {
        return properties.get(name);
    }

    public static int[] getIntArray(String name, int[] def) {
        String config_value = properties.get(name);
        return config_value == null ? def : Util.parseCommaSeparatedIntegerArray(config_value);
    }

    // Нужно только для старых версий лицухи.
    public static boolean getBoolean(String name, boolean def) {
        return false;
    }

    // Нужно только для старых версий лицухи.
    public static void set(String name, String param) {
        if (!name.equals("CoreRevision") && !name.equals("LicenseRevision") && !name.equals("ExternalHostname")) {
            try {
                Field f = _configValue.getField(name);
                setToType(f, param);
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            } catch (NoSuchFieldException e) {
                _log.warning("ConfigSystem(353): -> NoSuchFieldException: " + name);
            }
        }
    }

    private static boolean licenseRevision = false;
    private static boolean externalHostname = false;

    // Нужно только для старых версий лицухи.
    public static void setInt(String name, String param) {
        if (name.equals("LicenseRevision") && !licenseRevision) {
            ConfigValue.LicenseRevision(Integer.parseInt(param));
            licenseRevision = true;
        } else if (name.equals("ExternalHostname") && !externalHostname) {
            ConfigValue.ExternalHostname(param);
            externalHostname = true;
        }
    }

	/*public static int getIntHex(String name, int def)
	{
        if(!get(name, String.valueOf(def)).trim().startsWith("0x"))
            return Integer.decode("0x"+"FFFFFF");
        else
            return Integer.decode("0xFFFFFF");
    }*/

    public static TIntIntHashMap SKILL_DURATION_LIST;

    public static void loadSkillDurationList() {
        if (ConfigValue.EnableModifySkillDuration) {
            if (SKILL_DURATION_LIST != null)
                SKILL_DURATION_LIST.clear();
            String[] propertySplit = ConfigValue.SkillDurationList.split(";");
            SKILL_DURATION_LIST = new TIntIntHashMap(propertySplit.length);
            for (String skill : propertySplit) {
                String[] skillSplit = skill.split(",");
                if (skillSplit.length != 2)
                    _log.warning(concat("[SkillDurationList]: invalid config property -> SkillDurationList \"", skill, "\""));
                else {
                    try {
                        SKILL_DURATION_LIST.put(Integer.parseInt(skillSplit[0]), Integer.parseInt(skillSplit[1]));
                    } catch (NumberFormatException nfe) {
                        if (!skill.isEmpty())
                            _log.warning(concat("[SkillDurationList]: invalid config property -> SkillList \"", skillSplit[0], "\"", skillSplit[1]));
                    }
                }
            }
        }
    }

    public static void loadMyConfig(String conf) {
        File file = new File(conf);
        if (file.exists()) {
            try {
                Properties p = new Properties();
                p.load(new InputStreamReader(new FileInputStream(file), "UTF-8"));
                loadProperties(p, file.getName());
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static String concat(final String... strings) {
        final StringBuilder sbString = new StringBuilder(getLength(strings));
        for (final String string : strings)
            sbString.append(string);
        return sbString.toString();
    }

    private static int getLength(final String[] strings) {
        int length = 0;
        for (final String string : strings)
            length += string.length();
        return length;
    }

    public static GArray<String> getAllKey() {
        return configKey;
    }

    public static Map<String, GArray<String>> getPropFileName() {
        return _cacheProperties;
    }

    // ----------------------------------------------------------------------------------
    public static HashMap<Integer, PlayerAccess> gmlist = new HashMap<Integer, PlayerAccess>();
    public static GArray<AdvIP> GAMEIPS = new GArray<AdvIP>();
    public static Pattern[] MAT_LIST = {};

    public static void abuseLoad() {
        GArray<Pattern> tmp = new GArray<Pattern>();

        LineNumberReader lnr = null;
        try {
            String line;


            if (ConfigValue.develop) {
                lnr = new LineNumberReader(new InputStreamReader(new FileInputStream("java/config/mats.cfg"), "UTF-8"));

            } else {
                lnr = new LineNumberReader(new InputStreamReader(new FileInputStream(l2open.Server.PhoenixHomeDir + "./config/mats.cfg"), "UTF-8"));

            }

            while ((line = lnr.readLine()) != null) {
                StringTokenizer st = new StringTokenizer(line, "\n\r");
                if (st.hasMoreTokens())
                    tmp.add(Pattern.compile(".*" + st.nextToken() + ".*", Pattern.DOTALL | Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE));
            }

            MAT_LIST = tmp.toArray(new Pattern[tmp.size()]);
            tmp.clear();
            _log.info("Abuse: Loaded " + MAT_LIST.length + " abuse words.");
        } catch (IOException e1) {
            _log.warning("Error reading abuse: " + e1);
        } finally {
            try {
                if (lnr != null)
                    lnr.close();
            } catch (Exception e2) {
                // nothing
            }
        }
    }

    private static void ipsLoad() {
        GAMEIPS.clear();
        try {
            Properties ipsSettings = new Properties();
            InputStream is = Files.newInputStream(new File(dir + "/advipsystem.properties").toPath());
            InputStreamReader reader = new InputStreamReader(is, "UTF-8");
            ipsSettings.load(reader);
            is.close();

            String NetMask;
            String ip;
            for (int i = 0; i < ipsSettings.size() / (ipsSettings.getProperty(("NetMask" + (i + 1)).trim()) == null ? 1 : 2); i++) {
                ip = ipsSettings.getProperty(("IPAdress" + (i + 1)).trim());
                NetMask = ipsSettings.getProperty(("NetMask" + (i + 1)).trim());
                if (NetMask == null || NetMask.isEmpty()) {
                    AdvIP advip = new AdvIP();
                    advip.ipadress = ip;
                    advip.ipmask = "0.0.0.0";
                    advip.bitmask = "0.0.0.0";
                    GAMEIPS.add(advip);
                } else
                    for (String mask : NetMask.split(",")) {
                        AdvIP advip = new AdvIP();
                        advip.ipadress = ip;
                        advip.ipmask = mask.split("/")[0];
                        advip.bitmask = mask.split("/")[1];
                        GAMEIPS.add(advip);
                    }
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new Error("Failed to Load" + dir + "/advipsystem.properties File.");
        }
    }

    public static void loadGMAccess() {
        gmlist.clear();
        loadGMAccess(new File(dir + "/GMAccess.xml"));
        File file = new File(dir + "/GMAccess.d/");
        if (file.exists() && file.isDirectory())
            for (File f : file.listFiles())
                // hidden файлы НЕ игнорируем
                if (!f.isDirectory() && f.getName().endsWith(".xml"))
                    loadGMAccess(f);
    }

    public static void loadGMAccess(File file) {
        try {
            Field fld;
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setValidating(false);
            factory.setIgnoringComments(true);
            Document doc = factory.newDocumentBuilder().parse(file);

            for (Node z = doc.getFirstChild(); z != null; z = z.getNextSibling())
                for (Node n = z.getFirstChild(); n != null; n = n.getNextSibling()) {
                    if (!n.getNodeName().equalsIgnoreCase("char"))
                        continue;

                    PlayerAccess pa = new PlayerAccess();
                    for (Node d = n.getFirstChild(); d != null; d = d.getNextSibling()) {
                        Class<?> cls = pa.getClass();
                        String node = d.getNodeName();

                        if (node.equalsIgnoreCase("#text"))
                            continue;
                        if (node.equalsIgnoreCase("CanNotUseCommand")) {
                            String _node = d.getAttributes().getNamedItem("set").getNodeValue();
                            if (_node.equalsIgnoreCase("NUN"))
                                continue;
                            else {
                                String[] _value = _node.split(";");
                                for (String txt : _value) {
                                    if (txt.startsWith("admin_"))
                                        pa.setCommand(txt, true);
                                    else
                                        pa.setCommand("admin_" + txt, true);
                                    //System.out.println("CanNotUseCommand: " + txt);
                                }
                                continue;
                            }
                        } else if (node.equalsIgnoreCase("CanOnlyUseCommand")) {
                            String _node = d.getAttributes().getNamedItem("set").getNodeValue();
                            if (_node.equalsIgnoreCase("NUN"))
                                continue;
                            else {
                                String[] _value = _node.split(";");
                                for (String txt : _value) {
                                    if (txt.startsWith("admin_"))
                                        pa.setCommand(txt, false);
                                    else
                                        pa.setCommand("admin_" + txt, false);
                                    //System.out.println("CanOnlyUseCommand: " + txt);
                                }
                                continue;
                            }
                        }
                        try {
                            fld = cls.getField(node);
                        } catch (NoSuchFieldException e) {
                            _log.info("Not found desclarate ACCESS name: " + node + " in XML Player access Object");
                            continue;
                        }

                        if (fld.getType().getName().equalsIgnoreCase("boolean"))
                            fld.setBoolean(pa, Boolean.parseBoolean(d.getAttributes().getNamedItem("set").getNodeValue()));
                        else if (fld.getType().getName().equalsIgnoreCase("int"))
                            fld.setInt(pa, Integer.valueOf(d.getAttributes().getNamedItem("set").getNodeValue()));
                    }
                    gmlist.put(pa.PlayerID, pa);
                }
        } catch (Exception e) {
            e.printStackTrace();
            throw new Error("Failed to Load" + dir + "/GMAccess.xml File. [" + file.getPath() + "]");
        }
    }

    public static boolean containsMat(String s) {
        for (Pattern pattern : MAT_LIST)
            if (pattern.matcher(s).matches())
                return true;
        return false;
    }

    public static void saveHexid(String string) {
        saveHexid(string, dir + "/hexid.txt");
    }

    public static void saveHexid(String string, String fileName) {
        try {
            Properties hexSetting = new Properties();
            File file = new File(fileName);
            file.createNewFile();
            OutputStream out = Files.newOutputStream(file.toPath());
            hexSetting.setProperty("HexID", string);
            hexSetting.store(out, "the hexID to auth into login");
            out.close();
        } catch (Exception e) {
            System.out.println("Failed to save hex id to " + fileName + " File.");
            e.printStackTrace();
        }
    }

    public static void loadPL() {
        if (ConfigValue.LogPacketsFromIPs.isEmpty())
            PACKETLOGGER_IPS = null;
        else {
            PACKETLOGGER_IPS = new NetList();
            PACKETLOGGER_IPS.LoadFromString(ConfigValue.LogPacketsFromIPs, ",");
        }

        TRADE_WORDS = getContainsNoCasePatternArray();
    }

    private static Pattern[] getContainsNoCasePatternArray() {
        GArray<Pattern> tempPatterns = new GArray<Pattern>();
        for (String s : ConfigValue.TradeWords.split(","))
            if (!s.isEmpty()) {
                if (ConfigValue.TradeChatsReplaceExPattern)
                    s = Strings.joinStrings("[\\\\_ *@.\\/\\-]*", s.split(""));
                tempPatterns.add(Pattern.compile(".*" + s + ".*", Pattern.DOTALL | Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE));
            }
        return tempPatterns.toArray(new Pattern[tempPatterns.size()]);
    }

    public static double getRateAdena(L2Player activeChar) {
        return RateService.getRateDropAdena(activeChar) * (activeChar == null ? 1 : activeChar.getRateAdena()) * ConfigValue.RateDropAdenaMultMod + ConfigValue.RateDropAdenaStaticMod;
    }
}
