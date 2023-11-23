package communityboard.cash.buffer;

import communityboard.models.buffer.Buff;
import communityboard.models.buffer.Scheme;
import communityboard.models.buffer.SchemeBuff;
import communityboard.repository.buffer.BuffRepository;
import communityboard.repository.buffer.SchemeRepository;

import java.util.*;

public class BuffCash {

    private static BuffCash buffCash;
    private final List<Buff> buffs;
    private final BuffRepository buffRepository;
    private final SchemeRepository schemeRepository;
    private final Map<Integer, Map<Integer, Scheme>> scheme_buffs;


    public static BuffCash getInstance(){
        if (buffCash == null){
            buffCash = new BuffCash();
        }
        return buffCash;
    }

    public BuffCash() {
        this.buffs = new ArrayList<>();
        this.scheme_buffs = new HashMap<>();
        this.buffRepository = new BuffRepository();
        this.schemeRepository = new SchemeRepository();
        initBuffs();
        initSchemes();
    }

    private void initSchemes(){
        final List<Scheme> allScheme = schemeRepository.getAllScheme();
        for (Scheme scheme: allScheme){
            final List<SchemeBuff> schemeBuffs = schemeRepository.getSchemeBuffs(scheme.getId());

            for (SchemeBuff schemeBuff: schemeBuffs){
                scheme.getBuffs().put(schemeBuff.getIndex(), schemeBuff);
            }

            Map<Integer, Scheme> schemeMap = new HashMap<>();
            schemeMap.put(scheme.getId(), scheme);

            final Map<Integer, Scheme> map = this.scheme_buffs.get(scheme.getOwner());
            if (map == null){
                this.scheme_buffs.put(scheme.getOwner(), schemeMap);
            }else {
                this.scheme_buffs.get(scheme.getOwner()).put(scheme.getId(), scheme);
            }

        }
    }
    private void initBuffs(){
        final List<Buff> allBuffs = buffRepository.getAllBuffs();
        this.buffs.addAll(allBuffs);
    }


    public Optional<Buff> getBuff(int id){
        return this.buffs.stream().filter(buff -> buff.getId() == id).findFirst();
    }

    public List<Buff> getBuffs() {
        return buffs;
    }

    public void addBuff(Buff buff){
        this.buffs.add(buff);
    }

    public void removeBuff(int id){
        this.buffs.stream()
                .filter(buff -> buff.getId() == id).findFirst()
                .ifPresent(this.buffs::remove);
    }

    public Scheme getScheme(int owner, int schemeId){
        final Map<Integer, Scheme> schemeMap = this.scheme_buffs.get(owner);
        return schemeMap.get(schemeId);
    }

    public List<Scheme> getSchemes(int owner){
        final Map<Integer, Scheme> schemeMap = this.scheme_buffs.get(owner);
        if (schemeMap == null){
            return new ArrayList<>();
        }
        return new ArrayList<>(schemeMap.values());
    }

    public List<Scheme> getAllSchemes(){
        List<Scheme> schemes = new ArrayList<>();
        final Collection<Map<Integer, Scheme>> values = this.scheme_buffs.values();
        for (Map.Entry<Integer, Map<Integer, Scheme>> entry: this.scheme_buffs.entrySet()){
            schemes.addAll(entry.getValue().values());
        }
        return schemes;
    }

    public void addScheme(int owner, Scheme scheme){

        Map<Integer, Scheme> schemeMap = new HashMap<>();
        schemeMap.put(scheme.getId(), scheme);

        final Map<Integer, Scheme> map = this.scheme_buffs.get(owner);
        if (map == null){
            this.scheme_buffs.put(owner, schemeMap);
        }else {
            this.scheme_buffs.get(owner).put(scheme.getId(), scheme);
        }
    }

    public void removeScheme(int owner, int schemeId){
        this.scheme_buffs.get(owner).remove(schemeId);
    }



}
