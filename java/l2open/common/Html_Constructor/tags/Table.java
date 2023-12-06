package l2open.common.Html_Constructor.tags;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Table implements HtmlBuildInterface {

    private int row;
    private int col;

    private static final String START_TABLE = "\n<table";
    private static final String END_TABLE = "</table>\n";
    private static final String CLOSE_PARAM = ">\n";
    private final List<Row> rows = new ArrayList<>();
    private final List<String> params = new ArrayList<>();


    public Table(int row, int col) {
        this.col = col;
        addRows(row);
    }
    public Row row(int index){
        return rows.get(index);
    }

    public Table setParams(String... parameters){
        params.addAll(Arrays.asList(parameters));
        return this;
    }

    public String build(){
        StringBuilder sb = new StringBuilder();
        sb.append(START_TABLE);
        if (!params.isEmpty()){
            for (String p: params){
                sb.append(p);
            }
        }
        sb.append(CLOSE_PARAM);
        if (!rows.isEmpty()){
            for (Row row: rows){
                sb.append(row.build());
            }
        }
        sb.append(END_TABLE);
        return sb.toString();
    }

    public Table addRows(int count){
        for (int i = 0; i < count; i++) {
            rows.add(new Row(this.col));
        }
        return this;
    }

    public Table addCols(int count){
        for(Row row: rows){
            row.addCols(count);
            this.col++;
        }
        return this;
    }

    public int getRowsCount() {
        return rows.size();
    }

    public int getCols() {
        return col;
    }

    public List<Row> getRows(){
        return rows;
    }
}
