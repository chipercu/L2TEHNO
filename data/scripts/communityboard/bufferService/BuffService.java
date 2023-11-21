package communityboard.bufferService;

import java.util.*;
import java.util.stream.Collectors;

public class BuffService {

    private final BuffRepository buffRepository;
    private final SchemeRepository schemeRepository;
    private final BuffCash buffCash;
    private static final long SYSTEM_LISTS = -1;

    public BuffService() {
        this.buffRepository = new BuffRepository();
        this.schemeRepository = new SchemeRepository();
        this.buffCash = BuffCash.getInstance();
        initSchemes();
        initBuffs();
    }
    private void initSchemes(){
        final List<Scheme> allScheme = schemeRepository.getAllScheme();
        for (Scheme scheme: allScheme){
            final List<SchemeBuff> schemeBuffs = schemeRepository.getSchemeBuffs(scheme.getId());
            scheme.getBuffs().addAll(schemeBuffs);
            buffCash.addScheme(scheme.getOwner(), scheme);
        }
    }
    private void initBuffs(){
        final List<Buff> allBuffs = buffRepository.getAllBuffs();
        for (Buff buff: allBuffs){
            buffCash.addBuff(buff);
        }
    }

    public List<Buff> getBuffs(){
        return buffCash.getBuffs();
    }

    public List<Buff> getBuffs(String type){
        return buffCash.getBuffs().stream()
                .filter(buff -> buff.getType().equals(type))
                .collect(Collectors.toList());
    }

    public Buff getBuff(long id){
        return buffCash.getBuff(id);
    }

    public List<Scheme> getSystemSchemes() {
        return buffCash.getSchemes(SYSTEM_LISTS);
    }

    public List<Scheme> getSchemeByOwner(long owner) {
       return buffCash.getSchemes(owner);
    }

//    public Scheme changeListIndex(Scheme buffListModel, int oldIndex, int newIndex){
//        final List<Buff> list = buffListModel.getBuffList();
//        Collections.swap(list, oldIndex, newIndex);
//        updateListIndex(list);
//        return buffListModel;
//    }

    public void clearScheme(long owner, String schemeName) {
        final Scheme scheme = buffCash.getScheme(owner, schemeName);
        if (scheme == null) {
            return;
        }
        if (scheme.getBuffs().isEmpty()) {
            return;
        }
        schemeRepository.clearSchemeBuffs(scheme.getId());
        scheme.getBuffs().clear();
    }


    public void createBuff(Buff buff) {
        if (buff == null) {
            return;
        }
        buffRepository.createBuff(buff);
        buffCash.getBuffs().add(buff);
    }

    public Buff updateBuff(Buff buff, Buff newBuff) {
        if (buff == null || newBuff == null) {
            return null;
        }
        buffRepository.updateBuff(buff, newBuff);
        buffCash.removeBuff(buff.getId());
        buffCash.addBuff(newBuff);
        return newBuff;
    }

    public void removeBuff(Buff buff) {
        buffRepository.removeBuff(buff);
        final List<Scheme> allSchemes = buffCash.getAllSchemes();
        for (Scheme scheme : allSchemes){
            final List<SchemeBuff> buffs = scheme.getBuffs().stream()
                    .filter(schemeBuff -> schemeBuff.getBuff_id() != buff.getId()).collect(Collectors.toList());
            scheme.setBuffs(buffs);
        }
    }

    public Optional<Scheme> getScheme(Long owner, String schemeName) {
        return schemeRepository.byOwnerName(owner, schemeName);
    }

    public Scheme createScheme(Scheme scheme){
        schemeRepository.createScheme(scheme);
        final Optional<Scheme> optionalScheme = schemeRepository.byOwnerName(scheme.getOwner(), scheme.getName());
        if (optionalScheme.isPresent()){
            buffCash.addScheme(scheme.getOwner(), scheme);
            return optionalScheme.get();
        }
        return null;

    }

}
