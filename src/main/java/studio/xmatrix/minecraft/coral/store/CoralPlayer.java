package studio.xmatrix.minecraft.coral.store;

import java.util.Date;

/**
 * Coral 专属存储的玩家信息
 */
public class CoralPlayer {
    private String name;
    private Date playTime;

    CoralPlayer() {
    }

    CoralPlayer(String name, Date playTime) {
        this.name = name;
        this.playTime = playTime;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getPlayTime() {
        return playTime;
    }

    public void setPlayTime(Date playTime) {
        this.playTime = playTime;
    }
}
