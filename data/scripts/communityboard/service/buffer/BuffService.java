package communityboard.service.buffer;

import communityboard.cash.buffer.BuffCash;
import communityboard.models.buffer.Buff;
import communityboard.models.buffer.Scheme;
import communityboard.models.buffer.SchemeBuff;
import communityboard.repository.buffer.BuffRepository;
import communityboard.repository.buffer.SchemeRepository;
import l2open.config.ConfigValue;
import l2open.extensions.multilang.CustomMessage;
import l2open.gameserver.model.*;
import l2open.gameserver.skills.Env;
import l2open.gameserver.skills.effects.EffectTemplate;
import l2open.gameserver.tables.ReflectionTable;
import l2open.gameserver.tables.SkillTable;

import java.util.*;
import java.util.stream.Collectors;

public class BuffService {

    private final BuffRepository buffRepository;
    private final SchemeRepository schemeRepository;
    private final BuffCash buffCash;

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
            scheme.getBuffs().values().stream()
                    .filter(schemeBuff -> schemeBuff.getBuff_id() == buff.getId())
                    .filter(schemeBuff -> schemeBuff.getScheme_id() == scheme.getId())
                    .forEach(schemeBuff -> scheme.getBuffs().remove(schemeBuff.getIndex()));
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

        final List<Buff> buffList = scheme.getBuffs().values().stream()
                .map(schemeBuff -> getBuff(schemeBuff.getBuff_id())).collect(Collectors.toList());




        if (buff.isSong()){
            final long songCount = buffList.stream().filter(Buff::isSong).count();
            if (songCount >= ConfigValue.SongLimit){
                return;
            }
        }else {
            final long buffCount = buffList.stream().filter(b -> !b.isSong()).count();
            if (buffCount >= ConfigValue.BuffLimit){
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
        String target = args[1];
        final Buff buff = getBuff(buffId);
        if (buff == null) {
            return;
        }


        if (checkPlayerLevel(player)) {
            player.sendMessage("Баффер доступен для игроков с уровней не ниже " + ConfigValue.BufferMinLevel + " и не выше " + ConfigValue.BufferMaxLevel + ".");
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
        applyBuff(player, buff.getSkill_id(), buff.getSkill_level(), target);
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
                    if (ConfigValue.BufferTime > 0)
                        effect.setPeriod(ConfigValue.BufferTime * 60000L);
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

    private static boolean checkPlayerLevel(L2Playable playable) {
        return playable.isPlayer() && (playable.getLevel() < ConfigValue.BufferMinLevel || playable.getLevel() > ConfigValue.BufferMaxLevel);
    }





}
