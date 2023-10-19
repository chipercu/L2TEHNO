package communityboard.bufferService;

import l2open.database.DatabaseUtils;
import l2open.database.L2DatabaseFactory;

import java.sql.ResultSet;
import java.util.*;

public class BuffService {

    private final BuffRepository buffRepository;
    private final Map<String, List<BuffModel>> BUFF_CASH;

    public BuffService() {
        this.buffRepository = new BuffRepository();
        this.BUFF_CASH = new HashMap<>();
        updateBuffCash();
    }

    public List<BuffModel> getBuffsList(String list_type){
        return BUFF_CASH.get(list_type);
    }

    public List<BuffModel> createEmptyBuffList(String list_type){
        List<BuffModel> buffModels = BUFF_CASH.get(list_type);
        if (buffModels != null){
            return buffModels;
        }
        buffModels = new ArrayList<>();
        BUFF_CASH.put(list_type, buffModels);
        return buffModels;
    }

    private void updateBuffList(String list_type) {
        BUFF_CASH.remove(list_type);
        final ArrayList<BuffModel> buffModels = new ArrayList<>(buffRepository.getBuffsList(list_type));
        BUFF_CASH.put(list_type, buffModels);
    }

    public void clearBuffList(String list_type){
        final List<BuffModel> buffModels = BUFF_CASH.get(list_type);
        if (buffModels == null){
            return;
        }
        if (buffModels.isEmpty()){
            return;
        }
        buffRepository.removeBuffList(list_type);
        BUFF_CASH.get(list_type).clear();
    }

    public Optional<BuffModel> getBuff(int buff_id, int buff_level, String list_type) {

        final List<BuffModel> buffModels = BUFF_CASH.get(list_type);
        if (buffModels == null || buffModels.isEmpty()){
            return Optional.empty();
        }

        return buffModels.stream()
                .filter(buffModel -> buffModel.getBuff_id() == buff_id)
                .filter(buffModel -> buffModel.getBuff_level() == buff_level)
                .findFirst();
    }

    public Optional<BuffModel> createBuff(BuffModel buffModel) {
        if (buffModel == null){
            return Optional.empty();
        }
        final Optional<BuffModel> buff = buffRepository.createBuff(buffModel);
        if (buff.isPresent()){
            final BuffModel newBuffModel = buff.get();
            final List<BuffModel> buffModels = BUFF_CASH.get(newBuffModel.getList_type());
            if (buffModels == null){
                return Optional.empty();
            }
            buffModels.add(newBuffModel);
        }
        return buff;
    }

    public Optional<BuffModel> updateBuff(int buff_id, int buff_level, String list_type, BuffModel buffModel) {

        if (buffModel == null){
            return Optional.empty();
        }

        final List<BuffModel> buffModels = BUFF_CASH.get(list_type);
        if (buffModels == null || buffModels.isEmpty()){
            return Optional.empty();
        }

        final Optional<BuffModel> first = buffModels.stream()
                .filter(buff -> buff.getBuff_id() == buff_id)
                .filter(buff -> buff.getBuff_level() == buff_level)
                .findFirst();
        if (!first.isPresent()){
            return Optional.empty();
        }

        final Optional<BuffModel> updateBuff = buffRepository.updateBuff(buff_id, buff_level, list_type, buffModel);
        if (updateBuff.isPresent()){
            int index = buffModels.indexOf(first.get());
            buffModels.set(index ,updateBuff.get());
        }
        return updateBuff;
    }

    public boolean removeBuff(int buff_id, int buff_level, String list_type) {
        boolean result;
        final List<BuffModel> buffModels = BUFF_CASH.get(list_type);
        if (buffModels == null || buffModels.isEmpty()){
            return false;
        }
         result = buffRepository.removeBuff(buff_id, buff_level, list_type);
        if (result){
            final Optional<BuffModel> first = buffModels.stream()
                    .filter(buffModel -> buffModel.getBuff_id() == buff_id)
                    .filter(buffModel -> buffModel.getBuff_level() == buff_level)
                    .findFirst();
            first.ifPresent(buffModels::remove);
        }
        return result;
    }

    private void updateBuffCash() {
        BUFF_CASH.clear();
        buffRepository.getAllBuffs().forEach(buffModel -> {
            final String list_type = buffModel.getList_type();
            if (BUFF_CASH.get(list_type) == null){
                final ArrayList<BuffModel> buffModels = new ArrayList<>();
                buffModels.add(buffModel);
                BUFF_CASH.put(list_type, buffModels);
            }else {
                BUFF_CASH.get(list_type).add(buffModel);
            }
        });
    }




}
