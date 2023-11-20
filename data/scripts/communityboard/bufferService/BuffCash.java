package communityboard.bufferService;

import java.util.*;

public class BuffCash {

    private static BuffCash buffCash;
    private final Map<Long, Buff> buffs;
    private final Map<Long, Map<String, Scheme>> scheme_buffs;


    public static BuffCash getInstance(){
        if (buffCash == null){
            buffCash = new BuffCash();
        }
        return buffCash;
    }

    private BuffCash() {
        this.buffs = new HashMap<>();
        this.scheme_buffs = new HashMap<>();
    }


    public Buff getBuff(long id){
       return this.buffs.get(id);
    }

    public List<Buff> getBuffs(){
        return new ArrayList<>(this.buffs.values());
    }

    public void addBuff(Buff buff){
        this.buffs.put(buff.getId(), buff);
    }

    public void removeBuff(long id){
        this.buffs.remove(id);
    }

    public Scheme getScheme(long owner, String schemeName){
        final Map<String, Scheme> schemeMap = this.scheme_buffs.get(owner);
        return schemeMap.get(schemeName);
    }

    public List<Scheme> getSchemes(long owner){
        final Collection<Scheme> values = this.scheme_buffs.get(owner).values();
        return new ArrayList<>(values);
    }

    public List<Scheme> getAllSchemes(){
        List<Scheme> schemes = new ArrayList<>();
        final Collection<Map<String, Scheme>> values = this.scheme_buffs.values();
        for (Map.Entry<Long, Map<String, Scheme>> entry: this.scheme_buffs.entrySet()){
            schemes.addAll(entry.getValue().values());
        }
        return schemes;
    }

    public void addScheme(long owner, Scheme scheme){

        Map<String, Scheme> schemeMap = new HashMap<>();
        schemeMap.put(scheme.getName(), scheme);

        final Map<String, Scheme> map = this.scheme_buffs.get(owner);
        if (map == null){
            this.scheme_buffs.put(owner, schemeMap);
        }else {
            this.scheme_buffs.get(owner).put(scheme.getName(), scheme);
        }
    }

    public void removeScheme(long owner, String schemeName){
        this.scheme_buffs.get(owner).remove(schemeName);
    }



}
