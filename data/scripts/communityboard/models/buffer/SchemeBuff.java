package communityboard.models.buffer;

public class SchemeBuff {

    private int scheme_id;
    private int buff_id;
    private int index;


    public SchemeBuff() {
    }

    public SchemeBuff(int scheme_id, int buff_id, int index) {
        this.scheme_id = scheme_id;
        this.buff_id = buff_id;
        this.index = index;
    }

    public int getScheme_id() {
        return scheme_id;
    }

    public void setScheme_id(int scheme_id) {
        this.scheme_id = scheme_id;
    }

    public int getBuff_id() {
        return buff_id;
    }

    public void setBuff_id(int buff_id) {
        this.buff_id = buff_id;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }
}
