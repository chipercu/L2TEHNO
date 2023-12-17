package utils_soft.NpcEditor;

import utils_soft.NpcEditor.enums.AI_TYPE;
import utils_soft.NpcEditor.enums.INSTANCE_TYPE;

public class NpcBuilder {

    private String name;
    private int level;
    private INSTANCE_TYPE instance_type;
    private AI_TYPE ai_type;
    private static final int DEFAULT_DISPLAY_ID = 18223;

    public NpcBuilder(String name, int level, INSTANCE_TYPE instance_type, AI_TYPE ai_type) {
        this.name = name;
        this.level = level;
        this.instance_type = instance_type;
        this.ai_type = ai_type;
    }

}
