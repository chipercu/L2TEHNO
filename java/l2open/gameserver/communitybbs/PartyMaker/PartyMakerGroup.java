package l2open.gameserver.communitybbs.PartyMaker;

import l2open.gameserver.model.L2Player;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by a.kiperku
 * Date: 21.08.2023
 */

public class PartyMakerGroup {

    private int groupId;
    private int minLevel;
    private int maxLevel;
    private int groupLeaderId;
    private L2Player leader;
    private List<Integer> acceptedPlayers;
    private List<Integer> candidates;

    private String description;
    private String instance;

    public PartyMakerGroup(int minLevel, int maxLevel, L2Player leader, String description, String instance) {
        this.acceptedPlayers = new ArrayList<>();
        this.candidates = new ArrayList<>();
        this.minLevel = Math.min(minLevel, maxLevel);
        this.maxLevel = Math.max(minLevel, maxLevel);
        this.leader = leader;
        this.groupLeaderId = leader.getObjectId();
        this.description = description;
        this.instance = instance;
    }

    public int getPartyCount(){
        return getAcceptedPlayers().size() + 1;
    }

    public void setLeader(L2Player leader) {
        this.leader = leader;
        this.groupLeaderId = leader.getObjectId();
    }

    public L2Player getLeader() {
        return leader;
    }

    public List<Integer> getAcceptedPlayers() {
        return acceptedPlayers;
    }

    public void setAcceptedPlayers(List<Integer> acceptedPlayers) {
        this.acceptedPlayers = acceptedPlayers;
    }

    public List<Integer> getCandidates() {
        return candidates;
    }

    public void setCandidates(List<Integer> candidates) {
        this.candidates = candidates;
    }

    public String getInstance() {
        return instance;
    }

    public void setInstance(String instance) {
        this.instance = instance;
    }

    public void setMinLevel(int minLevel) {
        this.minLevel = minLevel;
    }

    public void setMaxLevel(int maxLevel) {
        this.maxLevel = maxLevel;
    }

    public void setGroupLeaderId(int groupLeaderId) {
        this.groupLeaderId = groupLeaderId;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getMinLevel() {
        return minLevel;
    }

    public int getMaxLevel() {
        return maxLevel;
    }

    public int getGroupLeaderId() {
        return groupLeaderId;
    }

    public String getDescription() {
        return description;
    }
}