package communityboard.bufferService;

import java.util.*;
import java.util.stream.Collectors;

public class BuffService {

    private final BuffRepository buffRepository;
    private final SchemeRepository schemeRepository;
    private final Map<Long, Map<String, Scheme>> BUFF_CASH;

    private final Map<Long, Scheme> CASH;
    private List<Scheme> SCHEME_CASH;

    private static final String PREMIUM = "PREMIUM";
    private static final String ORDINARY = "ORDINARY";
    private static final long SYSTEM_LISTS = -1;

    public BuffService() {
        this.buffRepository = new BuffRepository();
        this.schemeRepository = new SchemeRepository();
        this.BUFF_CASH = new HashMap<>();
        this.CASH = new HashMap<>();
        this.SCHEME_CASH = schemeRepository.getAllScheme();
        initSchemes();

//        updateBuffCash();
//        initLists();

    }
    private void initSchemes(){
        for (Scheme scheme: SCHEME_CASH){
            final List<SchemeBuff> schemeBuffs = schemeRepository.getBuffsIdsBySchemeId(scheme.getId());
            schemeBuffs.sort(Comparator.comparingInt(SchemeBuff::getIndex));
            for (SchemeBuff schemeBuff : schemeBuffs){
                final Optional<Buff> buff = buffRepository.getBuff(schemeBuff.getBuff_id());
                buff.ifPresent(value -> scheme.getBuffs().add(value));
            }
        }
    }

    public void addScheme(Scheme scheme){
        schemeRepository.createScheme(scheme);
        SCHEME_CASH.add(scheme);
    }
    public void removeScheme(Scheme scheme){
        SCHEME_CASH.remove(scheme);
        schemeRepository.removeScheme(scheme);
    }

    public void addBuffInScheme(Scheme scheme, Buff buff){
        schemeRepository.addBuffInScheme(scheme, buff);
        scheme.getBuffs().add(buff);
    }

    public void removeBuffInScheme(Scheme scheme, Buff buff){
        schemeRepository.removeBuffsInScheme(scheme, buff);
    }



    public void initLists() {
        if (!BUFF_CASH.containsKey(SYSTEM_LISTS)) {
            final HashMap<String, Scheme> map = new HashMap<>();
            BUFF_CASH.put(SYSTEM_LISTS, map);
        }
        final Map<String, Scheme> systemBuffsMap = BUFF_CASH.get(SYSTEM_LISTS);

        if (!systemBuffsMap.containsKey(ORDINARY)){
            final Scheme buffListModel = new Scheme(SYSTEM_LISTS, ORDINARY, new ArrayList<>());
            systemBuffsMap.put(ORDINARY, buffListModel);
        }
        if (!systemBuffsMap.containsKey(PREMIUM)){
            final Scheme buffListModel = new Scheme(SYSTEM_LISTS, PREMIUM, new ArrayList<>());
            systemBuffsMap.put(PREMIUM, buffListModel);
        }
    }

    public Map<Long, Map<String, Scheme>> getBUFF_CASH() {
        return BUFF_CASH;
    }

    public Scheme getBuffsList(String list_type, long owner) {
        final Scheme buffListModel = BUFF_CASH.get(owner).get(list_type);
        buffListModel.getBuffList().sort(Comparator.comparingInt(Buff::getList_index));
        return buffListModel;
    }

    public List<Scheme> getReadyLists() {
        return BUFF_CASH.get(SYSTEM_LISTS).values().stream()
                .filter(buffListModel -> !buffListModel.getList_type().equals(ORDINARY))
                .filter(buffListModel -> !buffListModel.getList_type().equals(PREMIUM))
                .collect(Collectors.toList());
    }

    public List<Scheme> getReadyListsByOwner(long owner) {
        final Map<String, Scheme> buffListModelMap = BUFF_CASH.get(owner);
        if (buffListModelMap == null){
            return null;
        }
        return new ArrayList<>(BUFF_CASH.get(owner).values());
    }

    public Scheme changeListIndex(Scheme buffListModel, int oldIndex, int newIndex){
        final List<Buff> list = buffListModel.getBuffList();
        Collections.swap(list, oldIndex, newIndex);
        updateListIndex(list);
        return buffListModel;
    }



    public Scheme getPremiumList() {
        return BUFF_CASH.get(SYSTEM_LISTS).get(PREMIUM);
    }

    public Scheme getOrdinaryList() {
        return BUFF_CASH.get(SYSTEM_LISTS).get(ORDINARY);
    }


    private void updateBuffList(String list_type, long owner) {
        BUFF_CASH.get(owner).remove(list_type);
        final Scheme buffList = buffRepository.getBuffList(list_type, owner);
        Map<String, Scheme> buffListModelMap = new HashMap<>();
        buffListModelMap.put(list_type, buffList);
        BUFF_CASH.put(owner, buffListModelMap);
    }

    public void clearBuffList(String list_type, long owner) {
        final Scheme buffListModel = BUFF_CASH.get(owner).get(list_type);
        if (buffListModel == null) {
            return;
        }
        if (buffListModel.getBuffList().isEmpty()) {
            return;
        }
        buffRepository.removeBuffList(list_type, owner);
        BUFF_CASH.get(owner).get(list_type).getBuffList().clear();
    }

    private boolean checkIfExistInList(Buff buffModel, Scheme buffListModel){
        return buffListModel.getBuffList().stream()
                .filter(buff -> buff.getSkill_id() == buffModel.getSkill_id())
                .filter(buff -> buff.getSkill_level() == buffModel.getSkill_level())
                .anyMatch(buff -> buff.getOwner() == buffModel.getOwner());
    }


    public Optional<Buff> createBuff(Buff buffModel, long owner) {
        if (buffModel == null) {
            return Optional.empty();
        }

        BUFF_CASH.computeIfAbsent(owner, k -> new HashMap<>());

        BUFF_CASH.get(owner).computeIfAbsent(buffModel.getType(), k -> new Scheme(owner, buffModel.getType(), new ArrayList<>()));

        final Scheme buffListModel = BUFF_CASH.get(owner).get(buffModel.getType());
        final Optional<Buff> buff = buffRepository.createBuff(buffModel);
        if (buff.isPresent()) {
            final Buff newBuffModel = buff.get();
            buffListModel.getBuffList().add(newBuffModel);
        }
        return buff;
    }

    public Optional<Buff> updateBuff(Buff buffModel, Buff newBuffModel, long owner) {
        if (buffModel == null) {
            return Optional.empty();
        }
        final Scheme buffListModel = BUFF_CASH.get(owner).get(buffModel.getType());
        if (buffListModel == null || buffListModel.getBuffList().isEmpty()) {
            return Optional.empty();
        }
        buffRepository.updateBuff(buffModel, newBuffModel);
        return Optional.of(buffModel);
    }

    public void removeBuff(Buff buffModel) {
        final Scheme buffListModel = BUFF_CASH.get(buffModel.getOwner()).get(buffModel.getType());
        if (buffListModel == null || buffListModel.getBuffList().isEmpty()) {
            return;
        }
        buffRepository.removeBuff(buffModel);

        final List<Buff> buffModelList = buffListModel.getBuffList();
        buffModelList.remove(buffModel);
        updateListIndex(buffModelList);
    }

    private void updateListIndex(List<Buff> buffModelList){
        for (int i = 0; i < buffModelList.size(); i++) {
            final Buff model = buffModelList.get(i);
            final Buff clone = model.clone();
            model.setList_index(i);
            buffRepository.updateBuff(clone, model);
        }
    }




    private void updateBuffCash() {
        BUFF_CASH.clear();
        SCHEME_CASH.clear();

        SCHEME_CASH = schemeRepository.getAllScheme();



        final List<Buff> allBuffs = buffRepository.getAllBuffs();

        for (Buff buffModel : allBuffs){
            final String list_type = buffModel.getType();
            final long owner = buffModel.getOwner();

            if (BUFF_CASH.get(owner) == null) {
                final Map<String, Scheme> buffListModelMap = new HashMap<>();
                final Scheme buffListModel = new Scheme(owner, list_type, new ArrayList<>());
                buffListModel.getBuffList().add(buffModel);
                buffListModelMap.put(buffModel.getType(), buffListModel);
                BUFF_CASH.put(owner, buffListModelMap);
            } else {
                if (BUFF_CASH.get(owner).get(list_type) == null){
                    BUFF_CASH.get(owner).put(list_type, new Scheme(owner, list_type, new ArrayList<>()));
                }
                BUFF_CASH.get(owner).get(buffModel.getType()).getBuffList().add(buffModel);
            }
        }
        BUFF_CASH.values()
                .forEach(e -> e.values()
                        .forEach(s -> s.getBuffList()
                                .sort(Comparator.comparingInt(Buff::getList_index))));
    }


}
