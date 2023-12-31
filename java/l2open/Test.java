package l2open;

import l2open.config.ConfigValue;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Test {
    public static void main(String[] args) throws Exception {

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

        for (String bypass: matches){
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

        for (String bypass: encryptMatches){
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

    private static byte[] base64ToBytes(String str){
        return Base64.getDecoder().decode(str);
    }


    private static void saveBackup(){
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

        String command = "mysqldump -u "+ ConfigValue.Login +" -p"+ ConfigValue.Password + " l2open -r \"" + canonicalPath + formattedDate + ".sql\"";
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