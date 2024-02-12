package l2open;

import l2open.common.Html_Builder_JSOUP.Elements.enums.ALIGN;
import l2open.common.Html_Builder_JSOUP.Elements.enums.EditType;
import l2open.common.Html_Builder_JSOUP.utils.Colors;
import l2open.common.Html_Builder_JSOUP.Elements.*;
import l2open.common.Html_Builder_JSOUP.Elements.Button;
import l2open.common.Html_Builder_JSOUP.Elements.Font;
import l2open.common.Html_Builder_JSOUP.HtmlBuilder;
import l2open.config.ConfigValue;
import l2open.database.DatabaseUtils;
import l2open.database.FiltredPreparedStatement;
import l2open.database.L2DatabaseFactory;
import l2open.database.ThreadConnection;


import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.awt.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Base64;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static l2open.extensions.common.Component.partitionList;

//import utils_soft.common.DatabaseResurce.schemes.builders.ServerVariablesBuilder;


public class Test {


    public static void main(String[] args) throws Exception {
        final File file = new File("data/scripts/events/EventBuffer/pet/songs.htm");
//        final Builder builder = HtmlParser.parse(file);
        final HtmlBuilder builder = new HtmlBuilder().setTitle("FuzzY123");

        final Table table = builder.addTable().setBgColor(Colors.Blue).setCellPadding(0).setCellSpacing(0).setWidth(100).setHeight(200);
        final TR tr = table.addTR();
        final TD td = tr.addTD().setAlign(ALIGN.center).setWidth(100);
        final Button put = td.put(new Button("button1", "bypass -h admin", 32, 32, "asdf", "asdf"));

        final Table table1 = new Table().setBgColor(Color.PINK);
        table1.addTR().addTD().put(new Button("button1", "bypass -h admin", 32, 32, "asdf", "asdf"));
        table1.addTR().addTD().put(new Font("text", Color.RED));


        tr.addTD().setAlign(ALIGN.center).setWidth(100).put(new Button("button2", "bypass -h admin", 32, 32, "asdf", "asdf"));
        tr.addTD().put(table1);

        builder.put(new BR());
        builder.put(new BR());

        Edit edit = builder.put(new Edit("edit", 50, 20));

        edit.setType(EditType.num).setLength(4);




        System.out.println(builder.getDocument());
        System.out.println(edit.getVar());

//        ConfigSystem.load();
//
//        SkillTable.getInstance();
//        testDatabaseResource();
//        DATABASE();
//        SchemesGenerator.generate("l2tehno");
    }

    public static List<Color> generateColorPalette() throws IllegalAccessException {
        List<Color> colorPalette = new ArrayList<>();
        final Field[] declaredFields = Colors.class.getDeclaredFields();
        for (Field field : declaredFields){
            final Color o = (Color) field.get(null);
            colorPalette.add(o);
        }
        return colorPalette;
    }

    public static void DATABASE() {
        ThreadConnection con = null;
        FiltredPreparedStatement statement = null;
        ResultSet rs = null;
        try {
            final String query = "SELECT * FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_SCHEMA = 'l2tehno'";
            con = L2DatabaseFactory.getInstance().getConnection();
            statement = con.prepareStatement(query);
            rs = statement.executeQuery();
            while (rs.next()) {
                System.out.println(rs.getString("TABLE_NAME"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DatabaseUtils.closeDatabaseCSR(con, statement, rs);
        }
    }

//    public static void testDatabaseResource() throws ResourceProvideException {
//
//        Resource<ServerVariablesResource, ServerVariablesBuilder> accountsResourceResource = new ResourceProvider<>(ServerVariablesResource.class);
//        final List<ServerVariablesResource> all = accountsResourceResource.findAll();
//        System.out.println(all.size());
//        final ServerVariablesResource serverVariablesResource = accountsResourceResource.create(new ServerVariablesBuilder().withName("testname").withValue("asd"));
//        serverVariablesResource.setValue("1234");
//        System.out.println(serverVariablesResource);
//
//
//        Resource<SpawnlistResource, SpawnlistBuilder> spawnlistResource = new ResourceProvider<>(SpawnlistResource.class);
//        final List<SpawnlistResource> list = spawnlistResource.findList(new Filter().WHERE(SpawnlistResource.NPC_TEMPLATEID, 31691));
//        final SpawnlistResource spawn = spawnlistResource.create(new SpawnlistBuilder().withLocation("qwe").withNpcTemplateid(31691), true);
//        spawn.setLocation("TestLocation");
//        spawn.delete();
//
////        list.forEach(System.out::println);
//
//
//    }

    public static void testEncrypt() throws Exception {
        File file = new File("file.txt");
        StringBuilder stringBuilder = new StringBuilder();

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                stringBuilder.append(line); // Выводим содержимое файла построчно
            }
        } catch (IOException e) {
            System.out.println("Возникла ошибка при чтении файла: " + e.getMessage());
        }

        String string = stringBuilder.toString();

        long startEncriptTime = System.currentTimeMillis();

        String regex = "action=\"([^\\\"]*)\""; // Регулярное выражение для поиска значений в скобках после "a"

        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(string);

        List<String> matches = new ArrayList<>();
        while (matcher.find()) {
            String match = matcher.group(1); // Значение в скобках
            matches.add(match);
        }

        String key = "MySecretKey12345";

        for (String bypass : matches) {
            byte[] encryptedText = encrypt(bypass, key);
            string = string.replace(bypass, bytesToBase64(encryptedText));
        }

        long finishEncriptTime = System.currentTimeMillis();

        System.out.println(string);
        System.out.println(finishEncriptTime - startEncriptTime);


        long startDecriptTime = System.currentTimeMillis();
        final Matcher matcher1 = pattern.matcher(string);

        List<String> encryptMatches = new ArrayList<>();
        while (matcher1.find()) {
            String match = matcher1.group(1); // Значение в скобках
            encryptMatches.add(match);
        }

        for (String bypass : encryptMatches) {
            String decryptedText = decrypt(base64ToBytes(bypass), key);
            string = string.replace(bypass, decryptedText);
        }
        long finishDecriptTime = System.currentTimeMillis();

        System.out.println(string);
        System.out.println(finishDecriptTime - startDecriptTime);
    }

    public static byte[] encrypt(String plaintext, String key) throws Exception {
        Cipher cipher = getCipher(key, Cipher.ENCRYPT_MODE);
        return cipher.doFinal(plaintext.getBytes(StandardCharsets.UTF_8));
    }

    public static String decrypt(byte[] ciphertext, String key) throws Exception {
        Cipher cipher = getCipher(key, Cipher.DECRYPT_MODE);
        byte[] decryptedBytes = cipher.doFinal(ciphertext);
        return new String(decryptedBytes, StandardCharsets.UTF_8);
    }

    private static Cipher getCipher(String key, int cipherMode) throws Exception {
        byte[] keyBytes = key.getBytes(StandardCharsets.UTF_8);
        SecretKeySpec secretKey = new SecretKeySpec(keyBytes, "AES");
        Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
        cipher.init(cipherMode, secretKey);
        return cipher;
    }

    private static String bytesToBase64(byte[] bytes) {
        return java.util.Base64.getEncoder().encodeToString(bytes);
    }

    private static byte[] base64ToBytes(String str) {
        return Base64.getDecoder().decode(str);
    }


    private static void format() {

        String str = "12341234 : ASdasdfasdfasdf";

        String regex = "^(.*?):.*$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(str);

        if (matcher.find()) {
            String result = matcher.group(1);
            System.out.println(result);
        }


    }


    private static void saveBackup() {
        Date currentDate = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd_HH-mm");
        String formattedDate = dateFormat.format(currentDate);
        String directoryPath = ConfigValue.DatapackRoot + "/backup_dump/";
        File directory = new File(directoryPath);
        // Проверяем, существует ли уже папка с таким именем
        if (!directory.exists()) {
            // Создаем папку
            boolean result = directory.mkdirs();

            if (result) {
                System.out.println("Папка успешно создана!");
            } else {
                System.out.println("Не удалось создать папку.");
            }
        } else {
            System.out.println("Папка уже существует.");
        }
        String canonicalPath = "";

        try {
            canonicalPath = directory.getCanonicalPath() + "\\";
            System.out.println(canonicalPath);
        } catch (IOException e) {

        }

        String command = "mysqldump -u " + ConfigValue.Login + " -p" + ConfigValue.Password + " l2open -r \"" + canonicalPath + formattedDate + ".sql\"";
        try {
            Process process = Runtime.getRuntime().exec(command);
            int exitCode = process.waitFor();

            if (exitCode == 0) {
                System.out.println("Бэкап базы данных сохранен успешно.");
            } else {
                System.out.println("Ошибка при сохранении бэкапа базы данных.");
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

}