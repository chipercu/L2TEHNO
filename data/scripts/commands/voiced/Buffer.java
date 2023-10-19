package commands.voiced;

import communityboard.CommunityBoardBuffer;
import l2open.config.ConfigValue;
import l2open.extensions.multilang.CustomMessage;
import l2open.extensions.scripts.Functions;
import l2open.extensions.scripts.ScriptFile;
import l2open.gameserver.common.Buff;
import l2open.gameserver.common.BuffScheme;
import l2open.gameserver.common.DifferentMethods;
import l2open.gameserver.handler.IVoicedCommandHandler;
import l2open.gameserver.handler.VoicedCommandHandler;
import l2open.gameserver.model.*;
import l2open.gameserver.model.barahlo.CBBuffSch;
import l2open.gameserver.model.entity.olympiad.Olympiad;
import l2open.gameserver.skills.Env;
import l2open.gameserver.skills.effects.EffectTemplate;
import l2open.gameserver.tables.SkillTable;
import l2open.util.Util;

import java.util.HashMap;
import java.util.Optional;
import java.util.StringTokenizer;

public class Buffer extends Functions implements IVoicedCommandHandler, ScriptFile {
    private String[] _commandList = new String[]{"buff"};

    public void onLoad() {
        VoicedCommandHandler.getInstance().registerVoicedCommandHandler(this);
    }

    public void onReload() {
    }

    public void onShutdown() {
    }

    public boolean useVoicedCommand(String command, L2Player activeChar, String args) {

        if (args != null && !args.isEmpty()) {
            if (activeChar != null) {
                activeChar.setMassUpdating(true);
                if (activeChar._buffSchem != null) {
                    final Optional<CBBuffSch> first = activeChar._buffSchem.values().stream().filter(e -> e.SchName.equals(args)).findFirst();
                    if (first.isPresent()) {
                        long price_mod = 0;
                        if (activeChar.getActiveClass().getLevel() < ConfigValue.BufferFreeLevel || activeChar.getInventory().getCountOf(ConfigValue.BufferItem) >= ((long) ConfigValue.BufferPriceOne * first.get()._buffList.length))
                            for (long ptsId : first.get()._buffList) {
                                int skillId;
                                int level = 0;
                                if (ptsId > 65536) {
                                    skillId = (int) (ptsId / 65536);
                                    level = (int) (ptsId - skillId * 65536);
                                } else{
                                    // Для совместимости со старыми схемами.
                                    skillId = (int) ptsId;
                                }
                                int lvl = SkillTable.getInstance().getBaseLevel(skillId);
                                if (level == 0){
                                    level = lvl;
                                }
                                // не ресторим премиум баф
                                int check = CommunityBoardBuffer.allow_pr_Buff.indexOf(skillId);
                                if (check != -1 && CommunityBoardBuffer.allow_pr_Buff.get(check + 1) == level){
                                    continue;
                                }
                                check = CommunityBoardBuffer.allow_pr_Buff2.indexOf(skillId);
                                if (check != -1 && CommunityBoardBuffer.allow_pr_Buff2.get(check + 1) == level) {
                                    if (canBuff(activeChar)){
                                        buff(skillId, level, activeChar);
                                    }
                                    continue;
                                }

                                if (lvl < level) {
                                    level = lvl;
                                }

                                if (CommunityBoardBuffer.allowBuff.indexOf(skillId) != -1 && (!Util.contains(ConfigValue.BufferOnlyPaBuffs, skillId) || canBuff(activeChar))) {
                                    buff(skillId, level, activeChar);
                                    price_mod++;
                                }
                            }
                        if (activeChar.getActiveClass().getLevel() >= ConfigValue.BufferFreeLevel){
                            DifferentMethods.getPay(activeChar, ConfigValue.BufferItem, ConfigValue.BufferPriceOne * price_mod, false);
                        }
                    }
                    activeChar.setMassUpdating(false);
                    activeChar.updateEffectIcons();
                }

            }

            //DifferentMethods.communityNextPage(activeChar, "_bbsbuffer");
        }
        return true;
    }

    private boolean canBuff(L2Player player) {
        return ConfigValue.PremiumBufferEnable ?
                player.getBonus().PremiumBuffer :
                ConfigValue.BufferUsePremiumItem <= 0 && player.hasBonus() || ConfigValue.BufferUsePremiumItem > 0 && player.getInventory().getCountOf(ConfigValue.BufferUsePremiumItem) > 0;
    }

    private static void buff(int id, int level, L2Playable playable) {
        if (id < 20)
            return;

        if (playable.isPlayer() && (playable.getLevel() < ConfigValue.BufferMinLevel || playable.getLevel() > ConfigValue.BufferMaxLevel)) {
            playable.sendMessage("Баффер доступен для игроков с уровней не ниже " + ConfigValue.BufferMinLevel + " и не выше " + ConfigValue.BufferMaxLevel + ".");
            return;
        }

        final double hp = playable.getCurrentHp();
        final double mp = playable.getCurrentMp();
        final double cp = playable.getCurrentCp();
        int buff_level = level > 0 ? level : SkillTable.getInstance().getBaseLevel(id);
        L2Skill skill = SkillTable.getInstance().getInfo(id, buff_level);

        if (!skill.checkSkillAbnormal(playable) && !skill.isBlockedByChar(playable, skill)) {
            for (EffectTemplate et : skill.getEffectTemplates()) {
                int result;
                Env env = new Env(playable, playable, skill);
                L2Effect effect = et.getEffect(env);
                if (effect != null && effect.getCount() == 1 && effect.getTemplate()._instantly && !effect.getSkill().isToggle()) {
                    // Эффекты однократного действия не шедулятся, а применяются немедленно
                    // Как правило это побочные эффекты для скиллов моментального действия
                    effect.onStart();
                    effect.onActionTime();
                    effect.onExit();
                } else if (effect != null && !effect.getEffected().p_block_buff.get()) {
                    if (ConfigValue.BufferTime > 0)
                        effect.setPeriod(ConfigValue.BufferTime * 60000);
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
        //skill.getEffects(playable, playable, false, false, ConfigValue.BBS_BUFFER_ALT_TIME * 60000, 0, false);
    }


    public String[] getVoicedCommandList() {
        return _commandList;
    }
}