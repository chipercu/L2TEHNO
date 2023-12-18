package utils_soft.common.DatabaseResurce;

import l2open.database.DatabaseUtils;
import l2open.database.FiltredPreparedStatement;
import l2open.database.L2DatabaseFactory;
import l2open.database.ThreadConnection;
import utils_soft.common.DatabaseResurce.anotations.DATA_TYPE;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.JDBCType;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class SchemesGenerator {

    private static final String SCHEME_NAMES = "SELECT * FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_SCHEMA = 'l2tehno'";
    private static final String SCHEME_COLUMNS = "SELECT * FROM information_schema.`COLUMNS` WHERE TABLE_SCHEMA = 'l2tehno' AND TABLE_NAME = '%s'";


    public static void generate(){
        final List<String> schemesNames = getSchemesNames();
        for (String scheme: schemesNames){
            String className = convertToCamelCase(scheme) + "Resource";
            final List<Column> columns = getColumns(scheme);
            StringBuilder classBuilder = new StringBuilder("package utils_soft.common.DatabaseResurce.schemes.generate;\n\n");
            classBuilder.append("import utils_soft.common.DatabaseResurce.anotations.DefValue;\n");
            classBuilder.append("import utils_soft.common.DatabaseResurce.anotations.Field;\n");
            classBuilder.append("import utils_soft.common.DatabaseResurce.anotations.Table;\n");
            classBuilder.append("import utils_soft.common.DatabaseResurce.DataBaseTable;\n");
            classBuilder.append("import static utils_soft.common.DatabaseResurce.schemes.generate.").append(className).append(".*;\n");
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
                classBuilder.append("                @Field(name = ").append(name);
                classBuilder.append(" , type = ").append(column.type);

                if (column.type_size != 0){
                    classBuilder.append(" , type_size = ").append(column.type_size);
                }

                classBuilder.append(" , nullable = ").append(column.isNullable);

//                if (column.defaultValue != null){
//                    classBuilder.append(" , defValue = @DefValue(").append(type).append(" = ").append(column.defaultValue).append(")");
//                }
                classBuilder.append(" , defValue = @DefValue(").append(type).append(" = ").append(column.defaultValue).append(")");
                classBuilder.append("),\n");
            }
            classBuilder.append("        }\n");
            classBuilder.append(")\n");
            classBuilder.append("public class ").append(className).append(" extends DataBaseTable<").append(className).append("> {\n\n");

            for (Column column: columns){
                String name = convertToSnakeCase(column.name);
                classBuilder.append("    public static final String ").append(name).append(" = \"").append(column.name).append("\";\n");
            }

            classBuilder.append("\n    public ").append(className).append("() {\n").append("super(").append(className).append(".class);\n}\n\n");

            final long count = columns.stream().filter(column -> column.isPrimaryKey).count();

            if (count > 0){
                classBuilder.append("\n    public ").append(className).append("(");


                final List<Column> notNullable = columns.stream()
                        .filter(column -> column.isPrimaryKey || (column.isNullable && getType(column.columnType).equals("String") && isEmpty(column.defaultValue)))
                        .collect(Collectors.toList());
                List<String> params = new ArrayList<>();
                for (Column column: notNullable){
                    params.add(getType(" " + column.columnType) + " " + column.name);
                }
                classBuilder.append(String.join(",", params)).append("){\n");
                classBuilder.append("        super(").append(className).append(".class);\n");

                for (Column column: notNullable){
                    classBuilder.append("        getSTAT_SET().set(").append(convertToSnakeCase(column.name)).append(", ").append(column.name).append(");\n");
                }
                classBuilder.append("    }\n\n");
            }

            for (Column column: columns){
                String name = convertToSnakeCase(column.name);
                String type = getType(column.columnType);
                String methodName = "get" + convertToCamelCase(column.name);
                if (methodName.equals("getClass")){
                    methodName = "getClass_";
                }

                classBuilder.append("    public ").append(type).append(" ").append(methodName).append("() {\n");
                classBuilder.append("        return get(").append(name).append(", ").append(type).append(".class);\n");
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

            final File file = new File("data/scripts/utils_soft/common/DatabaseResurce/schemes/generate/" + className + ".java");
            try (FileWriter writer = new FileWriter(file)) {
                writer.write(classBuilder.toString());
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

    private static List<Column> getColumns(String schemeName){
        ThreadConnection con = null;
        FiltredPreparedStatement statement = null;
        ResultSet rs = null;
        List<Column> names = new ArrayList<>();
        try {
            con = L2DatabaseFactory.getInstance().getConnection();
            statement = con.prepareStatement(String.format(SCHEME_COLUMNS, schemeName));
            rs = statement.executeQuery();
            while (rs.next()){
                final String name = rs.getString("COLUMN_NAME");
                final String dataType = rs.getString("DATA_TYPE").toUpperCase();
                final DATA_TYPE type = DATA_TYPE.valueOf(dataType);
                final int type_size = rs.getInt("CHARACTER_MAXIMUM_LENGTH");
                final String column_type = rs.getString("COLUMN_TYPE");
                final Object aDefault = rs.getString("COLUMN_DEFAULT");
                final boolean isPrimaryKey = rs.getString("COLUMN_KEY") != null && rs.getString("COLUMN_KEY").equalsIgnoreCase("PRI");
                final boolean isNullable = rs.getString("IS_NULLABLE").equals("YES");

                names.add(new Column(name, type,type_size, aDefault, column_type, isPrimaryKey, isNullable));
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DatabaseUtils.closeDatabaseCSR(con, statement, rs);
        }
        return names;
    }

    private static List<String> getSchemesNames(){
        ThreadConnection con = null;
        FiltredPreparedStatement statement = null;
        ResultSet rs = null;
        List<String> names = new ArrayList<>();
        try {
            con = L2DatabaseFactory.getInstance().getConnection();
            statement = con.prepareStatement(SCHEME_NAMES);
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
        Object defaultValue;
        String columnType;
        boolean isPrimaryKey;
        boolean isNullable;

        public Column(String name, DATA_TYPE type, int type_size, Object defaultValue, String columnType, boolean isPrimaryKey, boolean isNullable) {
            this.name = name;
            this.columnType = columnType;
            this.type = type;
            this.type_size = type_size;
            this.defaultValue = getDefaultValue(defaultValue);
            this.isPrimaryKey = isPrimaryKey;
            this.isNullable = isNullable;

        }

        private Object getDefaultValue(Object defaultValue){


            final String type = getType(columnType);
            if (type.equals("String")){

                if (defaultValue == null){
                    return  "\"\"";
                }

                defaultValue = defaultValue.toString().replaceAll("'", "\"");
                if (defaultValue.toString().isEmpty() || defaultValue.toString().equalsIgnoreCase("null")){
                    defaultValue = "\"\"";
                }
            } else if (type.equals("Boolean")) {
                return Boolean.parseBoolean(defaultValue.toString());
            }

            if (defaultValue == null){
                return null;
            }

            return defaultValue;
        }

    }


}
