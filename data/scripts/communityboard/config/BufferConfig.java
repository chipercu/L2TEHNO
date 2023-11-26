package communityboard.config;

import communityboard.repository.buffer.BufferConfigRepository;
import l2open.config.ConfigValue;

public class BufferConfig {

    private static BufferConfig bufferConfig;

    public static final String ALL_BUFFS_FILE = "data/scripts/communityboard/config/allBuffList.txt";
    public static final String HTML_PATCH = "data/scripts/communityboard/html/buffer/";

    private int defaultSimpleBuffPrice;
    private int defaultSimpleBuffItem;
    private int defaultPremiumBuffPrice;
    private int defaultPremiumBuffItem;
    private int buffLimit;
    private int songLimit;
    private int minLevel;
    private int maxLevel;
    private int buffTime;

    private BufferConfig(){
    }

    public static BufferConfig getInstance(){
        if (bufferConfig == null){
            bufferConfig = new BufferConfig();
        }
        return bufferConfig;
    }

    public int getDefaultSimpleBuffPrice() {
        return defaultSimpleBuffPrice;
    }

    public void setDefaultSimpleBuffPrice(int defaultSimpleBuffPrice) {
        this.defaultSimpleBuffPrice = defaultSimpleBuffPrice;
    }

    public int getDefaultSimpleBuffItem() {
        return defaultSimpleBuffItem;
    }

    public void setDefaultSimpleBuffItem(int defaultSimpleBuffItem) {
        this.defaultSimpleBuffItem = defaultSimpleBuffItem;
    }

    public int getDefaultPremiumBuffPrice() {
        return defaultPremiumBuffPrice;
    }

    public void setDefaultPremiumBuffPrice(int defaultPremiumBuffPrice) {
        this.defaultPremiumBuffPrice = defaultPremiumBuffPrice;
    }

    public int getDefaultPremiumBuffItem() {
        return defaultPremiumBuffItem;
    }

    public void setDefaultPremiumBuffItem(int defaultPremiumBuffItem) {
        this.defaultPremiumBuffItem = defaultPremiumBuffItem;
    }

    public int getBuffLimit() {
        return buffLimit;
    }

    public void setBuffLimit(int buffLimit) {
        this.buffLimit = buffLimit;
    }

    public int getSongLimit() {
        return songLimit;
    }

    public void setSongLimit(int songLimit) {
        this.songLimit = songLimit;
    }

    public int getMinLevel() {
        return minLevel;
    }

    public void setMinLevel(int minLevel) {
        this.minLevel = minLevel;
    }

    public int getMaxLevel() {
        return maxLevel;
    }

    public void setMaxLevel(int maxLevel) {
        this.maxLevel = maxLevel;
    }

    public int getBuffTime() {
        return buffTime;
    }

    public void setBuffTime(int buffTime) {
        this.buffTime = buffTime;
    }
}
