package utils_soft.NpcEditor;

import utils_soft.NpcEditor.enums.AI_TYPE;
import utils_soft.NpcEditor.enums.INSTANCE_TYPE;

public class NpcBuilder {

    private String name;
    private String title;
    private int level;
    private INSTANCE_TYPE type;
    private AI_TYPE ai;

    public NpcBuilder(String name, String title, int level, INSTANCE_TYPE type, AI_TYPE ai) {
        this.name = name;
        this.title = title;
        this.level = level;
        this.type = type;
        this.ai = ai;
    }

    private void create(){



    }

}
