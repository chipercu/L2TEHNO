package utils_soft.NpcEditor.enums;

public enum INSTANCE_TYPE {
    MONSTER("L2Monster"),
    MINION("L2Minion"),
    RAIDBOSS("L2RaidBoss"),
    NPC("L2Npc"),
    GUARD("L2Guard"),
    MERCHANT("L2Merchant");

    private final String type;

    INSTANCE_TYPE(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }
}
