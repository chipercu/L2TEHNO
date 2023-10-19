package l2open;

import l2open.config.ConfigValue;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Test {
    public static void main(String[] in) throws Exception {

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