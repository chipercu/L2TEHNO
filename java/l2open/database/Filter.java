package l2open.database;

import java.util.ArrayList;
import java.util.List;

public class Filter {

    private String WHERE = "";
    private final List<String> AND = new ArrayList<>();
    private String ORDER_BY = "";
    private String LIKE = "";
    private String LIMIT = "";
    private String OFFSET = "";

    public Filter() {
    }

    public Filter WHERE(String column, Object findValue){
        WHERE = " WHERE " + column + " = '" + findValue + "'";
        return this;
    }

    public Filter AND(String column, Object findValue){
        AND.add(" AND " + column + " = '" + findValue + "'");
        return this;
    }

    public Filter LIKE(String column, Object findValue){
        LIKE = " WHERE `" + column + "` LIKE '%" + findValue + "%'";
        return this;
    }

    public Filter ORDER_BY(String column){
        ORDER_BY = " ORDER BY " + column;
        return this;
    }

    public Filter LIMIT(int limit){
        LIMIT = " LIMIT " + limit;
        return this;
    }

    public Filter OFFSET(int offset){
        OFFSET = " OFFSET " + offset;
        return this;
    }

    public String build(){
        StringBuilder result = new StringBuilder();
        result.append(WHERE);
        result.append(LIKE);
        for (String and: AND){
            result.append(and);
        }
        result.append(ORDER_BY);
        result.append(LIMIT);
        result.append(OFFSET);
        return result.toString();
    }


}
