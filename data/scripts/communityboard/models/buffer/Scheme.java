package communityboard.models.buffer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Scheme {

    private int id;
    private int owner;
    private String name;
    private Map<Integer, SchemeBuff> buffs;

    public Scheme(int id, int owner, String name) {
        this.id = id;
        this.owner = owner;
        this.name = name;
        this.buffs = new HashMap<>();
    }

    public Scheme(int owner, String name) {
        this.owner = owner;
        this.name = name;
        this.buffs = new HashMap<>();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getOwner() {
        return owner;
    }

    public void setOwner(int owner) {
        this.owner = owner;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Map<Integer, SchemeBuff>  getBuffs() {
        return buffs;
    }

    public void setBuffs(Map<Integer, SchemeBuff>  buffs) {
        this.buffs = buffs;
    }
}
