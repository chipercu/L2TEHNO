package events.BattlePass;

public class BattlePassQuest {

    private String name;
    private int steps;
    private int reward;

    public BattlePassQuest() {
    }

    public BattlePassQuest(String name, int steps, int reward) {
        this.name = name;
        this.steps = steps;
        this.reward = reward;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getSteps() {
        return steps;
    }

    public void setSteps(int steps) {
        this.steps = steps;
    }

    public int getReward() {
        return reward;
    }

    public void setReward(int reward) {
        this.reward = reward;
    }
}
