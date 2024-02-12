package l2open.common.HtmlBuilder;

import java.awt.*;
import java.util.*;
import java.util.List;

public class Table implements HtmlElement {

    private int col;
    private Color bgColor;
    private int cellpadding = 0;
    private int cellspacing = 0;
    private int border = 0;
    private int width = 0;
    private int height = 0;
    private String background = "";


    private static final String START_TABLE = "\n<table";
    private static final String END_TABLE = "</table>\n";
    private static final String CLOSE_PARAM = ">\n";
    private final List<Row> rows = new ArrayList<>();
    private final List<String> params = new ArrayList<>();

    private final Map<Integer, Row> rowMap = new HashMap<>();


    public Table() {
    }

    public Table(int row, int col) {
        this.col = col;
        addRows(row);
    }

    public Table(int col) {
        this.col = col;
    }

    public Table addRow(Row row){
        rowMap.put(rowMap.size(), row);
        return this;
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

    public Col cell(int rowIndex, int colIndex){
        return rowMap.get(rowIndex).getColMap().get(colIndex);
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

    public Table put(int rowIndex, int colIndex, HtmlElement element){
        final Row row = rowMap.get(rowIndex);
        if (row == null){
            rowMap.put(rowIndex, new Row(this.col));
        }
        final Col col = rowMap.get(rowIndex).getColMap().get(colIndex);
        if (col == null){
            rowMap.get(rowIndex).getColMap().put(colIndex, new Col());
        }
        rowMap.get(rowIndex).getColMap().get(colIndex).putElement(element);
        return this;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder("\n<table");

        if (bgColor != null){
            String colorHex = String.format("%02x%02x%02x", bgColor.getRed(), bgColor.getGreen(), bgColor.getBlue());
            builder.append(" bgcolor=\"").append(colorHex).append("\"");
        }
        if (cellpadding != 0){
            builder.append(" cellpadding=").append(cellpadding);
        }
        if (cellspacing != 0){
            builder.append(" cellspacing=").append(cellspacing);
        }
        if (border != 0){
            builder.append(" border=").append(cellspacing);
        }
        if (width != 0){
            builder.append(" width=").append(width);
        }
        if (width != 0){
            builder.append(" height=").append(height);
        }
        if (!background.isEmpty()){
            builder.append(" background=\"").append(background).append("\"");
        }
        builder.append(">");
        if (!rowMap.isEmpty()) {
            int maxKey = Collections.max(rowMap.keySet());
            for (int i = 1; i <= maxKey; i++) {
                final Row row = rowMap.get(i);
                if (row == null){
                    builder.append(new Row(1));
                }else {
                    builder.append(row);
                }
            }
        }
        builder.append("\n</table>");
        return builder.toString();
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

    public Table setBgColor(Color bgColor) {
        this.bgColor = bgColor;
        return this;
    }

    public Table setCellPadding(int padding) {
        this.cellpadding = padding;
        return this;
    }

    public Table setCellSpacing(int spacing) {
        this.cellspacing = spacing;
        return this;
    }

    public Table setBorder(int border) {
        this.border = border;
        return this;
    }

    public Table setWidth(int width) {
        this.width = width;
        return this;
    }

    public Table setHeight(int height) {
        this.height = height;
        return this;
    }

    public void setBackground(String background) {
        this.background = background;
    }
}
