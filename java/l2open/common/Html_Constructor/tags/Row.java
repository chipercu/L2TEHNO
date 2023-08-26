package l2open.common.Html_Constructor.tags;

import java.util.ArrayList;
import java.util.List;

import static l2open.common.Html_Constructor.tags.parameters.Parameters.*;

public class Row implements Build {
    public static final String START_ROW = "    <tr>\n";
    public static final String END_ROW = "    </tr>\n";
    private final List<Col> columns = new ArrayList<>();

    public Row(int cols){
        if (cols > 0){
            for (int i = 0; i < cols; i++) {
                columns.add(new Col());
            }
        }
    }

    public Col col(int index){
        return columns.get(index);
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