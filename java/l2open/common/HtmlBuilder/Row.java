package l2open.common.HtmlBuilder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static l2open.common.HtmlBuilder.parameters.Parameters.*;

public class Row implements HtmlElement {
    public static final String START_ROW = "    <tr>\n";
    public static final String END_ROW = "    </tr>\n";
    private final List<Col> columns = new ArrayList<>();

    private final Map<Integer, Col> colMap = new HashMap<>();

    public Row(int cols){
        if (cols > 0){
            for (int i = 0; i < cols; i++) {
                columns.add(new Col());
                colMap.put(i, new Col());
            }
        }
    }

    public Row addCol(Col col){
        this.colMap.put(colMap.size(), col);
        return this;
    }

    public Map<Integer, Col> getColMap() {
        return colMap;
    }

    public Col col(int index){
        return columns.get(index);
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder("\n\t<tr>");
        for (Map.Entry<Integer, Col> entry : colMap.entrySet()){
            builder.append(entry.getValue());
        }
        builder.append("\n\t</tr>");
        return builder.toString();
    }

    public String build(){
        StringBuilder sb = new StringBuilder();
        sb.append(START_ROW);
        if (!columns.isEmpty()){
            columns.forEach(e -> sb.append(e.build()));
        }
        sb.append(END_ROW);

        return sb.toString();
    }
    public Row addCols(int count){
        for (int i = 0; i < count; i++) {
            columns.add(new Col());
        }
        return this;
    }

    public Row setHeight(int size){
        columns.get(0).setParams(HEIGHT(size));
        return this;
    }
    public Row setHeightAll(int size){
        for(Col col: columns){
            col.setParams(HEIGHT(size));
        }
        return this;
    }

    public List<Col> getColumns(){
        return columns;
    }
}