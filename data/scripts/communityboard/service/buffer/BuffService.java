package communityboard.service.buffer;

import communityboard.cash.buffer.BuffCash;
import communityboard.models.buffer.Buff;
import communityboard.models.buffer.Scheme;
import communityboard.models.buffer.SchemeBuff;
import communityboard.repository.buffer.BuffRepository;
import communityboard.repository.buffer.SchemeRepository;

import java.util.*;
import java.util.stream.Collectors;

public class BuffService {

    private final BuffRepository buffRepository;
    private final SchemeRepository schemeRepository;
    private final BuffCash buffCash;
    private static final int SYSTEM_LISTS = -1;

    public BuffService() {
        this.buffRepository = new BuffRepository();
        this.schemeRepository = new SchemeRepository();
        this.buffCash = BuffCash.getInstance();
    }

    public void createBuff(Buff buff) {
        if (buff == null) {
            return;
        }
        final Optional<Buff> repositoryBuff = buffRepository.getBuff(buff.getSkill_id(), buff.getSkill_level(), buff.getType());
        if (repositoryBuff.isPresent()){
            return;
        }

        final Buff buff1 = buffRepository.createBuff(buff);
        buffCash.addBuff(buff1);
    }

    public Buff updateBuff(Buff buff) {
        if (buff == null) {
            return null;
        }
        return buffRepository.updateBuff(buff);
    }

    public void removeBuff(Buff buff) {
        buffRepository.removeBuff(buff.getId(), buff.getType());
        buffCash.removeBuff(buff.getId());
        final List<Scheme> allSchemes = buffCash.getAllSchemes();
        for (Scheme scheme : allSchemes){
            scheme.getBuffs().
        }
    }

    public List<Buff> getBuffs(){
        return buffCash.getBuffs();
    }

    public List<Buff> getBuffs(String type){
        return getBuffs().stream()
                .filter(buff -> buff.getType().equals(type))
                .collect(Collectors.toList());
    }

    public Buff getBuff(int id){
        return buffCash.getBuff(id).get();
    }


    public Optional<Scheme> getScheme(int owner, String schemeName) {
        return schemeRepository.getScheme(owner, schemeName);
    }
    public Scheme getScheme(int schemeId){
        return buffCash.getAllSchemes().stream()
                .filter(scheme -> scheme.getId() == schemeId)
                .findFirst()
                .orElse(null);
    }


    public List<Scheme> getSchemes(int owner) {
       return buffCash.getSchemes(owner);
    }

    public void createScheme(Scheme scheme){
        schemeRepository.createScheme(scheme)
                .ifPresent(s -> buffCash.addScheme(s.getOwner(), s));
    }

    public void removeScheme(int owner, int schemeId) {
        schemeRepository.removeScheme(schemeId);
        buffCash.removeScheme(owner, schemeId);
    }

    public void clearScheme(int owner, int schemeId) {
        final Scheme scheme = buffCash.getScheme(owner, schemeId);
        if (scheme == null) {
            return;
        }
        if (scheme.getBuffs().isEmpty()) {
            return;
        }
        schemeRepository.clearSchemeBuffs(scheme.getId());
        scheme.getBuffs().clear();
    }

    public void addBuffInScheme(int schemeId, int buffId, int index){
        final Scheme scheme = getScheme(schemeId);
        if (scheme == null){
            return;
        }
        final Buff buff = getBuff(buffId);
        if (buff == null){
            return;
        }

        SchemeBuff schemeBuff;
        if (schemeRepository.getSchemeBuff(schemeId, index).isPresent()){
            schemeBuff = schemeRepository.updateSchemeBuff(scheme, buff, index);
        }else {
            schemeBuff = schemeRepository.createSchemeBuff(scheme, buff, index);
        }

        if (schemeBuff != null){
            buffCash.getScheme(scheme.getOwner(), scheme.getId()).getBuffs().add(schemeBuff);
        }
    }

    public void removeBuffFromScheme(Scheme scheme, int buffId) {
        schemeRepository.removeSchemeBuff(scheme.getId(), buffId);

        final Optional<SchemeBuff> first = scheme.getBuffs().stream()
                .filter(schemeBuff -> schemeBuff.getBuff_id() == buffId)
                .findFirst();

        first.ifPresent(schemeBuff -> scheme.getBuffs().remove(schemeBuff));
    }


}
