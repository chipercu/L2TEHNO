package utils_soft.NpcEditor;

public class NpcModel {
    private int id;
    private String name;
    private String title;
    private int displayId;
    private String type;

    public NpcModel(int id, String name, String title, int displayId, String type) {
        this.id = id;
        this.name = name;
        this.title = title;
        this.displayId = displayId;
        this.type = type;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getDisplayId() {
        return displayId;
    }

    public void setDisplayId(int displayId) {
        this.displayId = displayId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
