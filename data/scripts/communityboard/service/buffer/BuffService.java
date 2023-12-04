package communityboard.service.buffer;

import communityboard.cash.buffer.BuffCash;
import communityboard.config.BufferConfig;
import communityboard.models.buffer.Buff;
import communityboard.models.buffer.Scheme;
import communityboard.models.buffer.SchemeBuff;
import communityboard.repository.buffer.BuffRepository;
import communityboard.repository.buffer.BufferConfigRepository;
import communityboard.repository.buffer.SchemeRepository;
import l2open.config.ConfigValue;
import l2open.gameserver.common.DifferentMethods;
import l2open.gameserver.model.L2Effect;
import l2open.gameserver.model.L2Playable;
import l2open.gameserver.model.L2Player;
import l2open.gameserver.model.L2Skill;
import l2open.gameserver.skills.Env;
import l2open.gameserver.skills.effects.EffectTemplate;
import l2open.gameserver.tables.SkillTable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class BuffService {

    private final BufferConfigRepository bufferConfigRepository;
    private final BuffRepository buffRepository;
    private final SchemeRepository schemeRepository;
    private final BufferConfig bufferConfig;
    private final BuffCash buffCash;

    public BuffService() {
        this.bufferConfigRepository = new BufferConfigRepository();
        this.bufferConfig = this.bufferConfigRepository.getConfig();
        this.buffRepository = new BuffRepository();
        this.schemeRepository = new SchemeRepository();
        this.buffCash = BuffCash.getInstance();
        loadBufferConfig();
    }

    public void loadBufferConfig(){
        ConfigValue.BufferPriceOne = BufferConfig.getInstance().getDefaultSimpleBuffPrice();
        ConfigValue.BufferItem = BufferConfig.getInstance().getDefaultSimpleBuffItem();
        ConfigValue.BuffLimit = BufferConfig.getInstance().getBuffLimit();
        ConfigValue.SongLimit = BufferConfig.getInstance().getSongLimit();
        ConfigValue.BufferMinLevel = BufferConfig.getInstance().getMinLevel();
        ConfigValue.BufferMaxLevel = BufferConfig.getInstance().getMaxLevel();
        ConfigValue.BufferTime = (int) (BufferConfig.getInstance().getBuffTime() * 60000L);
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

            final SchemeBuff schemeBuff = scheme.getBuffs().values().stream()
                    .filter(s -> s.getBuff_id() == buff.getId())
                    .findFirst().orElse(null);

            if (schemeBuff != null){
                scheme.getBuffs().remove(schemeBuff.getIndex());
                schemeRepository.removeSchemeBuff(scheme.getId(), schemeBuff.getIndex());
            }
        }


    }

    public List<Buff> getBuffs(){
        return buffCash.getBuffs();
    }

    public List<Buff> getBuffs(String type){
        final List<Buff> buffs = getBuffs();

        List<Buff> buffListByType = new ArrayList<>();
        for (Buff buff: buffs){
            if (buff.getType().equals(type)){
                buffListByType.add(buff);
            }
        }
        return buffListByType;
    }

    public Buff getBuff(int id){
        return buffCash.getBuff(id);
    }

    public Scheme getScheme(int owner, String schemeName) {
        final List<Scheme> schemes = buffCash.getSchemes(owner);
        for (Scheme scheme: schemes){
            final String name = scheme.getName().trim();
            if (name.equals(schemeName.trim())){
                return scheme;
            }
        }
        return null;
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

    public Scheme createScheme(Scheme scheme){
        final Optional<Scheme> optionalScheme = schemeRepository.createScheme(scheme);

        if (optionalScheme.isPresent()){
            final Scheme scheme1 = optionalScheme.get();
            buffCash.addScheme(scheme1.getOwner(), scheme1);
            return scheme1;
        }
        return null;
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

        final List<Buff> buffList = scheme.getBuffs().values().stream()
                .map(schemeBuff -> getBuff(schemeBuff.getBuff_id())).collect(Collectors.toList());
        if (buff.isSong()){
            final long songCount = buffList.stream().filter(Buff::isSong).count();
            if (songCount >= bufferConfig.getSongLimit()){
                return;
            }
        }else {
            final long buffCount = buffList.stream().filter(b -> !b.isSong()).count();
            if (buffCount >= bufferConfig.getBuffLimit()){
                return;
            }
        }

        SchemeBuff schemeBuff;
        if (schemeRepository.getSchemeBuff(schemeId, index).isPresent()){
            schemeBuff = schemeRepository.updateSchemeBuff(scheme, buff, index);
        }else {
            schemeBuff = schemeRepository.createSchemeBuff(scheme, buff, index);
        }

        if (schemeBuff != null){
            buffCash.getScheme(scheme.getOwner(), scheme.getId()).getBuffs().put(schemeBuff.getIndex(), schemeBuff);
        }
    }

    public void removeBuffFromScheme(Scheme scheme, int index) {
        schemeRepository.removeSchemeBuff(scheme.getId(), index);
        buffCash.getScheme(scheme.getOwner(), scheme.getId()).getBuffs().remove(index);
    }

    public void castBuff(L2Player player, String[] args) {
        int buffId = Integer.parseInt(args[0]);
        String page = args[1];
        String target = args[2];
        final Buff buff = getBuff(buffId);
        if (buff == null) {
            return;
        }

        if (checkPlayerLevel(player)) {
            player.sendMessage("Баффер доступен для игроков с уровней не ниже " + bufferConfig.getMinLevel() + " и не выше " + bufferConfig.getMaxLevel() + ".");
            return;
        }
        L2Playable playable = null;
        if ("Player".equals(target)) {
            playable = player;
        } else if ("Pet".equals(target)) {
            playable = player.getPet();
        }

        if (playable == null) {
            return;
        }

        if (player.getBonus().RATE_XP <= 1 && buff.getType().equals("PREMIUM")) {
            player.sendMessage("У вас нет активирован премиум-статус!");
            return;
        }
        final boolean pay = DifferentMethods.getPay(player,
                buff.getType().equals("PREMIUM") ? BufferConfig.getInstance().getDefaultPremiumBuffItem() : BufferConfig.getInstance().getDefaultSimpleBuffItem(),
                buff.getType().equals("PREMIUM") ? BufferConfig.getInstance().getDefaultPremiumBuffPrice() : BufferConfig.getInstance().getDefaultSimpleBuffPrice(),
                true);

        if (pay){
            applyBuff(player, buff.getSkill_id(), buff.getSkill_level(), target);
        }
    }

    public void applyBuff(L2Player player, long id, long level, String target) {
        L2Skill skill = SkillTable.getInstance().getInfo((int) id, (int) level);
        L2Playable playable = null;
        if ("Player".equals(target)) {
            playable = player;
        } else if ("Pet".equals(target)) {
            playable = player.getPet();
        }
        if (playable == null) {
            return;
        }
        final double hp = playable.getCurrentHp();
        final double mp = playable.getCurrentMp();
        final double cp = playable.getCurrentCp();

        if (!skill.checkSkillAbnormal(playable) && !skill.isBlockedByChar(playable, skill)) {
            for (EffectTemplate et : skill.getEffectTemplates()) {
                int result;
                Env env = new Env(playable, playable, skill);
                L2Effect effect = et.getEffect(env);
                if (effect != null && effect.getCount() == 1 && effect.getTemplate()._instantly && !effect.getSkill().isToggle()) {
                    effect.onStart();
                    effect.onActionTime();
                    effect.onExit();
                } else if (effect != null && !effect.getEffected().p_block_buff.get()) {
                    if (bufferConfig.getBuffTime() > 0)
                        effect.setPeriod(bufferConfig.getBuffTime() * 60000L);
                    if ((result = playable.getEffectList().addEffect(effect)) > 0) {
                        if ((result & 2) == 2)
                            playable.setCurrentHp(hp, false);
                        if ((result & 4) == 4)
                            playable.setCurrentMp(mp);
                        if ((result & 8) == 8)
                            playable.setCurrentCp(cp);
                    }
                }
            }
        }
        playable.updateEffectIcons();
    }

    private boolean checkPlayerLevel(L2Playable playable) {
        return playable.isPlayer() && (playable.getLevel() < bufferConfig.getMinLevel() || playable.getLevel() > bufferConfig.getMaxLevel());
    }

    public BufferConfig setConfig(String configName, int value) {
        return bufferConfigRepository.updateConfig(configName, value);
    }
}
