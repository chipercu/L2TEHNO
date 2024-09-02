package l2open.gameserver.model.instances;

import l2open.gameserver.ai.CtrlEvent;
import l2open.gameserver.model.L2Character;
import l2open.gameserver.model.L2Player;
import l2open.gameserver.model.L2Skill;
import l2open.gameserver.model.entity.siege.clanhall.RainbowSpringSiege;
import l2open.gameserver.templates.L2NpcTemplate;
import l2open.util.Rnd;
import l2open.util.Util;

public class L2ChestInstance extends L2MonsterInstance {
    public L2ChestInstance(int objectId, L2NpcTemplate template) {
        super(objectId, template);
    }

    public void tryOpen(L2Player opener, L2Skill skill) {
        getAI().notifyEvent(CtrlEvent.EVT_AGGRESSION, opener, 100);
    }

    @Override
    public boolean canChampion() {
        return false;
    }

    @Override
    public void doDie(L2Character killer) {
        if (getTemplate().getNpcId() == 35593) {
            int[] item = {8035, 8037, 8039, 8040, 8046, 8047, 8050, 8051, 8052, 8053, 8054};

            long count = Util.rollDrop(1, 1, 150000, false, killer.getPlayer());
            if (killer.isPet() || killer.isSummon())
                killer = killer.getPlayer();
            dropItem((L2Player) killer, item[Rnd.get(10)], count);
            RainbowSpringSiege.getInstance().chestDie(killer, this);
        }
        super.doDie(killer);
    }
}