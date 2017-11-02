package top.slantech.wifipsd.bean;

/**
 * Created by admin on 2016/7/10 0010.
 */
public class Wifis {
    private String Ssid="";
    private String Password="";
    private int Rssi;
    private String priority;
    private String key_mgmt;
    private String frequency;

    private Boolean isChecked;

    public Wifis() {
    }

    public String getSsid() {
        return Ssid;
    }

    public void setSsid(String ssid) {
        Ssid = ssid;
    }

    public String getPassword() {
        return Password;
    }

    public void setPassword(String password) {
        Password = password;
    }

    public int getRssi() {
        return Rssi;
    }

    public void setRssi(int rssi) {
        Rssi = rssi;
    }

    public Boolean getChecked() {
        return isChecked;
    }

    public void setChecked(Boolean checked) {
        isChecked = checked;
    }

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    public String getKey_mgmt() {
        return key_mgmt;
    }

    public void setKey_mgmt(String key_mgmt) {
        this.key_mgmt = key_mgmt;
    }

    public String getFrequency() {
        return frequency;
    }

    public void setFrequency(String frequency) {
        this.frequency = frequency;
    }
}
