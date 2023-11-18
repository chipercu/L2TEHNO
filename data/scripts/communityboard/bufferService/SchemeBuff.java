package communityboard.bufferService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SchemeBuff {

    private long scheme_id;
    private long buff_id;
    private int index;


    public SchemeBuff() {
    }

    public SchemeBuff(long scheme_id, long buff_id, int index) {
        this.scheme_id = scheme_id;
        this.buff_id = buff_id;
        this.index = index;
    }

    public long getScheme_id() {
        return scheme_id;
    }

    public void setScheme_id(long scheme_id) {
        this.scheme_id = scheme_id;
    }

    public long getBuff_id() {
        return buff_id;
    }

    public void setBuff_id(long buff_id) {
        this.buff_id = buff_id;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }
}
