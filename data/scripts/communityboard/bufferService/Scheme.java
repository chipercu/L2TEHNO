package communityboard.bufferService;

import java.util.ArrayList;
import java.util.List;

public class Scheme {

    private long id;
    private long owner;
    private String name;
    private List<SchemeBuff> buffs;

    public Scheme(long id, long owner, String name) {
        this.id = id;
        this.owner = owner;
        this.name = name;
        this.buffs = new ArrayList<>();
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getOwner() {
        return owner;
    }

    public void setOwner(long owner) {
        this.owner = owner;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<SchemeBuff> getBuffs() {
        return buffs;
    }

    public void setBuffs(List<SchemeBuff> buffs) {
        this.buffs = buffs;
    }
}
