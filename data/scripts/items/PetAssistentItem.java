package items;

import ai.petAssistent.PetAssistance;
import l2open.config.ConfigValue;
import l2open.extensions.scripts.Functions;
import l2open.extensions.scripts.ScriptFile;
import l2open.gameserver.handler.IItemHandler;
import l2open.gameserver.handler.ItemHandler;
import l2open.gameserver.model.L2ObjectsStorage;
import l2open.gameserver.model.L2Playable;
import l2open.gameserver.model.L2Player;
import l2open.gameserver.model.entity.olympiad.Olympiad;
import l2open.gameserver.model.instances.L2NpcInstance;
import l2open.gameserver.model.items.L2ItemInstance;
import l2open.gameserver.tables.NpcTable;
import l2open.gameserver.templates.L2NpcTemplate;
import l2open.util.Location;

import java.util.List;

import static ai.petAssistent.PetAssistentConst.*;

public class PetAssistentItem extends Functions implements IItemHandler, ScriptFile {



    @Override
    public void useItem(L2Playable playable, L2ItemInstance item, Boolean ctrl) {
        L2Player player = (L2Player) playable;
        try {
            if (playable.isPlayer()) {
                long delay = player.getVarLong(ASSISTENT_DELAY);
                if (delay > System.currentTimeMillis()){
                    player.sendMessage("Повторное использование доступно через " + ((delay - System.currentTimeMillis()) / 1000) + "сек.");
                    return;
                }else {
                    player.unsetVar(ASSISTENT_DELAY);
                }

                if (player.getOlympiadObserveId() != -1 || player.getOlympiadGame() != null || Olympiad.isRegisteredInComp(player) || player.getKarma() > 0) {
                    player.sendActionFailed();
                    return;
                }

                if(player.isCastingNow() || player.isActionsDisabled()) {
                    player.sendActionFailed();
                    return;
                }

                if(player.isInTransaction() || player.isInFlyingTransform()) {
                    player.sendActionFailed();
                    return;
                }


                if (player.getBonus().RATE_XP <= 1 && ConfigValue.AgationSpoilOnlyPremiumAccount){
                    player.sendMessage("Призвать ассистента могут только пользователи с ПА");
                    return;
                }

                if (player.getVar(ASSISTENT_IS_SUMMONED) == null) {
                    player.setVar(ASSISTENT_IS_SUMMONED, "false");
                    player.setVar(ASSISTENT_DISPLAY_ID, "20326");
                    player.setVar(pet_spoil_force, "false");
                    player.setVar(pet_spoil_single, "false");
                    player.setVar(pet_pick, "false");
                    player.setVar(pet_pick_only_adena, "false");
                    player.setVar(pet_pick_herb, "false");
                    player.setVar(pet_ignore_herbs, "");
                }
                boolean isSummoned = Boolean.parseBoolean(player.getVar(ASSISTENT_IS_SUMMONED));


                int npc_id = player.getVarInt(ASSISTENT_DISPLAY_ID, 20326);

                if (isSummoned) {
                    List<L2NpcInstance> allByNpcId = L2ObjectsStorage.getAllByNpcId(npc_id, false);
                    allByNpcId.stream()
                            .filter(l2NpcInstance -> l2NpcInstance.getTitle().equals(player.getName()))
                            .findFirst().ifPresent(L2NpcInstance::deleteMe);
                    player.setVar(ASSISTENT_IS_SUMMONED, "false");
                    player.setVar(ASSISTENT_DELAY, System.currentTimeMillis() + 3000, System.currentTimeMillis() + 30000);
                } else {
                    player.setVar(ASSISTENT_IS_SUMMONED, "true");
                    player.setVar(ASSISTENT_DELAY, System.currentTimeMillis() + 3000, System.currentTimeMillis() + 30000);
                    Location location = Location.coordsRandomize(playable.getLoc(), 50, 100);
                    L2NpcTemplate template = NpcTable.getTemplate(npc_id);
                    template.setInstance("PetAssistance");
                    template.name = "Assistent";
                    template.title = playable.getName();
                    template.baseRunSpd = 300;
                    template.baseMAtkSpd = 2500;
                    template.basePAtkSpd = 2500;
                    L2NpcInstance npc = template.getNewInstance();
                    if (npc != null) {
                        npc.setTitle(playable.getName());
                        npc.setName("Assistent");
                        npc.setAI(new PetAssistance(npc, player));
                        npc.setSpawnedLoc(location.correctGeoZ());
                        npc.setReflection(player.getReflectionId());
                        npc.onSpawn();
                        npc.spawnMe(npc.getSpawnedLoc());
                    }
                }
            }


        } catch (Exception e) {
            player.setVar(ASSISTENT_DISPLAY_ID, String.valueOf(20326));
            throw new NullPointerException(e.getMessage());
        }
    }

    @Override
    public int[] getItemIds() {
        return _itemIds;
    }

    @Override
    public void onLoad() {
        ItemHandler.getInstance().registerItemHandler(this);
    }

    @Override
    public void onReload() {

    }

    @Override
    public void onShutdown() {

    }
}
