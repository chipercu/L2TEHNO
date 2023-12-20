package l2open.database;

import l2open.database.anotations.DATA_TYPE;
import l2open.database.anotations.IS_NULLABLE;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class SchemesGenerator {

    private static final String SCHEME_NAMES = "SELECT * FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_SCHEMA = '%s'";
    private static final String SCHEME_COLUMNS = "SELECT * FROM information_schema.`COLUMNS` WHERE TABLE_SCHEMA = '%s' AND TABLE_NAME = '%s'";


    public static void generate(String dbName){
        final List<String> schemesNames = getSchemesNames(dbName);
        for (String scheme: schemesNames){
            String className = convertToCamelCase(scheme) + "Resource";
            final List<Column> columns = getColumns(dbName, scheme);
            StringBuilder classBuilder = new StringBuilder("package utils_soft.common.DatabaseResurce.schemes.resources;\n\n");
            classBuilder.append("import utils_soft.common.DatabaseResurce.anotations.Column;\n");
            classBuilder.append("import utils_soft.common.DatabaseResurce.anotations.Table;\n");
            classBuilder.append("import utils_soft.common.DatabaseResurce.DataBaseTable;\n");
            classBuilder.append("import static utils_soft.common.DatabaseResurce.schemes.resources.").append(className).append(".*;\n");
            classBuilder.append("import static utils_soft.common.DatabaseResurce.anotations.IS_NULLABLE.*;\n");
            classBuilder.append("import static utils_soft.common.DatabaseResurce.anotations.DATA_TYPE.*;\n\n");
            classBuilder.append("@Table(\n");
            classBuilder.append("        name = \"").append(scheme).append("\",\n");

            final List<String> collect = columns.stream().filter(column -> column.isPrimaryKey).map(column -> convertToSnakeCase(column.name)).collect(Collectors.toList());
            final String join = String.join(",", collect);
            classBuilder.append("        primary_key = {").append(join).append("},\n");
            classBuilder.append("        fields = {\n");

            for (Column column: columns){
                String name = convertToSnakeCase(column.name);
                String type = getType(column.columnType);
                classBuilder.append("                @Column(is_null = ").append(column.is_nullable);
                classBuilder.append(" , type = ").append(column.type);
                classBuilder.append(" , type_size = ").append(column.type_size);
                classBuilder.append(" , name = ").append(name);
                classBuilder.append(" , defValue = \"").append(column.defaultValue).append("\"),\n");
            }
            classBuilder.append("        }\n");
            classBuilder.append(")\n");
            classBuilder.append("public class ").append(className).append(" extends DataBaseTable<").append(className).append("> {\n\n");

            for (Column column: columns){
                String name = convertToSnakeCase(column.name);
                classBuilder.append("    public static final String ").append(name).append(" = \"").append(column.name).append("\";\n");
            }

            classBuilder.append("\n    public ").append(className).append("() {\n").append("        super(").append(className).append(".class);\n}\n\n");

            for (Column column: columns){
                String name = convertToSnakeCase(column.name);
                String type = getType(column.columnType);
                String methodName = "get" + convertToCamelCase(column.name);
                if (methodName.equals("getClass")){
                    methodName = "getClass_";
                }

                classBuilder.append("    public ").append(type).append(" ").append(methodName).append("() {\n");
                classBuilder.append("        return get(").append(name).append(");\n");
                classBuilder.append("    }\n");
            }
            classBuilder.append("\n");

            for (Column column: columns){
                String name = convertToSnakeCase(column.name);
                String type = getType(column.columnType);
                String methodName = "set" + convertToCamelCase(column.name);
                classBuilder.append("    public void ").append(methodName).append("(").append(type).append(" value) {\n");
                classBuilder.append("        set(").append(name).append(", value);\n");
                classBuilder.append("    }\n");
            }
            classBuilder.append("\n}\n");

            final File file = new File("data/scripts/utils_soft/common/DatabaseResurce/schemes/resources/" + className + ".java");
            try (FileWriter writer = new FileWriter(file)) {
                writer.write(classBuilder.toString());
            } catch (IOException e) {
                System.out.println("Ошибка при записи в файл: " + e.getMessage());
            }

            String builderName = className.replace("Resource", "Builder");

            StringBuilder builder = new StringBuilder("package utils_soft.common.DatabaseResurce.schemes.builders;\n\n");
            builder.append("import utils_soft.common.DatabaseResurce.ResourceBuilder;\n");
            builder.append("import utils_soft.common.DatabaseResurce.schemes.resources.").append(className).append(";\n");
            builder.append("import java.lang.reflect.Field;\n");
            builder.append("import java.util.NoSuchElementException;\n\n");

            builder.append("public class ").append(builderName).append(" extends ResourceBuilder<").append(className).append("> {\n\n");

            builder.append("    public ").append(builderName).append("() {\n");
            builder.append("        try {\n");
            builder.append("            resourceClass = ").append(className).append(".class;\n");
            builder.append("            resource = resourceClass.newInstance();\n");
            builder.append("        } catch (InstantiationException | IllegalAccessException e) {\n");
            builder.append("            throw new RuntimeException(e);\n");
            builder.append("        }\n");
            builder.append("    }\n\n");


            for (Column column: columns){
                final Class<?> type = column.type.getType();
                builder.append("    public ").append(builderName).append(" with").append(convertToCamelCase(column.name)).append("(").append(type.getSimpleName()).append(" value) {\n");
//                builder.append("        try {\n");
//                builder.append("            final Field field = resourceClass.getDeclaredField(\"").append(convertToSnakeCase(column.name)).append("\");\n");
//                builder.append("            field.setAccessible(true);\n");
//                builder.append("            statsSet.set((String) field.get(resource), value);\n");
//                builder.append("        } catch (Exception ignored) {\n");
//                builder.append("        }\n");
                builder.append("        with(").append(className).append(".").append(convertToSnakeCase(column.name)).append(", value);\n");
                builder.append("        return this;\n");
                builder.append("    }\n\n");
            }
            builder.append("}\n");

            final File builderFile = new File("data/scripts/utils_soft/common/DatabaseResurce/schemes/builders/" + builderName + ".java");
            try (FileWriter writer = new FileWriter(builderFile)) {
                writer.write(builder.toString());
            } catch (IOException e) {
                System.out.println("Ошибка при записи в файл: " + e.getMessage());
            }

        }
    }

    private static boolean isEmpty(Object defValue){
        if (defValue == null){
            return true;
        }
        return String.valueOf(defValue).isEmpty();
    }


    private static String getType(String name){
        String type = "Object";
        final String lowerCase = name.toLowerCase();

        if (lowerCase.startsWith("int") || lowerCase.startsWith("smallint") || lowerCase.startsWith("mediumint") || lowerCase.startsWith("tinyint")) {
            type = "Integer";
        } else if (lowerCase.startsWith("text") || lowerCase.startsWith("varchar") || lowerCase.startsWith("date")) {
            type = "String";
        } else if (lowerCase.startsWith("double") || lowerCase.startsWith("decimal") || lowerCase.startsWith("float")) {
            type = "Double";
        } else if (lowerCase.startsWith("bigint")) {
            type = "Long";
        } else if (lowerCase.startsWith("enum") && lowerCase.contains("true") || lowerCase.contains("false")) {
            type = "Boolean";
        }else {
            type = "String";
        }
        return type;
    }

    private static List<Column> getColumns(String dbName, String schemeName){
        ThreadConnection con = null;
        FiltredPreparedStatement statement = null;
        ResultSet rs = null;
        List<Column> names = new ArrayList<>();
        try {
            con = L2DatabaseFactory.getInstance().getConnection();
            statement = con.prepareStatement(String.format(SCHEME_COLUMNS, dbName, schemeName));
            rs = statement.executeQuery();
            while (rs.next()){
                final String name = rs.getString("COLUMN_NAME");
                final String dataType = rs.getString("DATA_TYPE").toUpperCase();
                final DATA_TYPE type = DATA_TYPE.valueOf(dataType);
                final int type_size = rs.getInt("CHARACTER_MAXIMUM_LENGTH");
                final String column_type = rs.getString("COLUMN_TYPE");
                final Object aDefault = rs.getString("COLUMN_DEFAULT");
                final boolean isPrimaryKey = rs.getString("COLUMN_KEY") != null && rs.getString("COLUMN_KEY").equalsIgnoreCase("PRI");
                final IS_NULLABLE is_nullable = IS_NULLABLE.valueOf(rs.getString("IS_NULLABLE"));
                names.add(new Column(name, type,type_size, aDefault, column_type, isPrimaryKey, is_nullable));
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DatabaseUtils.closeDatabaseCSR(con, statement, rs);
        }
        return names;
    }

    private static List<String> getSchemesNames(String dbName){
        ThreadConnection con = null;
        FiltredPreparedStatement statement = null;
        ResultSet rs = null;
        List<String> names = new ArrayList<>();
        try {
            con = L2DatabaseFactory.getInstance().getConnection();
            statement = con.prepareStatement(String.format(SCHEME_NAMES, dbName));
            rs = statement.executeQuery();
            while (rs.next()){
                final String table_name = rs.getString("TABLE_NAME");
                System.out.println(table_name);
                names.add(table_name);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DatabaseUtils.closeDatabaseCSR(con, statement, rs);
        }
        return names;
    }

    public static String capitalizeFirstCharacter(String input) {
        if (input == null || input.isEmpty()) {
            return input; // обработка пустых строк или null
        }
        char firstChar = input.charAt(0);
        String remainingChars = input.substring(1);
        String capitalizedFirstChar = String.valueOf(Character.toUpperCase(firstChar));
        return capitalizedFirstChar + remainingChars;
    }

    public static String convertToSnakeCase(String input) {
        if (input == null || input.isEmpty()) {
            return input; // обработка пустых строк или null
        }
        String converted = input.replaceAll("(.)(\\p{Upper})", "$1_$2");
        String upperCase = converted.toUpperCase();

        try {
            DATA_TYPE enumValue = DATA_TYPE.valueOf(upperCase);
            upperCase = "_" + upperCase;
        } catch (IllegalArgumentException ignored) {}

        return upperCase;
    }

    public static String convertToCamelCase(String input) {
        if (input == null || input.isEmpty()) {
            return input; // обработка пустых строк или null
        }

        StringBuilder builder = new StringBuilder();

        String[] parts = input.split("_");

        for (String part : parts) {
            if (!part.isEmpty()) {
                String firstChar = part.substring(0, 1).toUpperCase();
                String remainingChars = part.substring(1);
                builder.append(firstChar).append(remainingChars);
            }
        }

        return builder.toString();
    }


    public static class Column{
        String name;
        DATA_TYPE type;
        int type_size;
        String defaultValue;
        String columnType;
        boolean isPrimaryKey;
        IS_NULLABLE is_nullable;

        public Column(String name, DATA_TYPE type, int type_size, Object defaultValue, String columnType, boolean isPrimaryKey, IS_NULLABLE is_nullable) {
            this.name = name;
            this.columnType = columnType;
            this.type = type;
            this.type_size = type_size;
            this.defaultValue = getDefaultValue(defaultValue);
            this.isPrimaryKey = isPrimaryKey;
            this.is_nullable = is_nullable;
        }

        private String getDefaultValue(Object defValue){
            return String.valueOf(defValue).replaceAll("'", "");
        }

    }


}
