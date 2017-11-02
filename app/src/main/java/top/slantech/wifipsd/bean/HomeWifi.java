package top.slantech.wifipsd.bean;

/**
 * Created by slantech on 2016/11/09 17:34
 */
public class HomeWifi {
    private String SSID;
    private int level;
    private Boolean isNeedPwd;

    public String getSSID() {
        return SSID;
    }

    public void setSSID(String SSID) {
        this.SSID = SSID;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public Boolean getNeedPwd() {
        return isNeedPwd;
    }

    public void setNeedPwd(Boolean needPwd) {
        isNeedPwd = needPwd;
    }

    @Override
    public String toString() {
        return "HomeWifi{" +
                "SSID='" + SSID + '\'' +
                ", level=" + level +
                ", isNeedPwd=" + isNeedPwd +
                '}';
    }
}
