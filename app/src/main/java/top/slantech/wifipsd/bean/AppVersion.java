package top.slantech.wifipsd.bean;

/**
 * app版本
 * Created by admin on 2016/6/4 0004.
 */
public class AppVersion {
    private int id ;
    private int versionNode ;
    private String versionName ;
    private String appType ;//小虾客、微众号等
    private String sysType ;//android、ios
    private String remark ;
    private String apkUrl ;
    private String addtimes ;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getVersionNode() {
        return versionNode;
    }

    public void setVersionNode(int versionNode) {
        this.versionNode = versionNode;
    }

    public String getVersionName() {
        return versionName;
    }

    public void setVersionName(String versionName) {
        this.versionName = versionName;
    }

    public String getAppType() {
        return appType;
    }

    public void setAppType(String appType) {
        this.appType = appType;
    }

    public String getSysType() {
        return sysType;
    }

    public void setSysType(String sysType) {
        this.sysType = sysType;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getApkUrl() {
        return apkUrl;
    }

    public void setApkUrl(String apkUrl) {
        this.apkUrl = apkUrl;
    }

    public String getAddtimes() {
        return addtimes;
    }

    public void setAddtimes(String addtimes) {
        this.addtimes = addtimes;
    }

    @Override
    public String toString() {
        return "mainaAppVersion{" +
                "id=" + id +
                ", versionNode=" + versionNode +
                ", versionName='" + versionName + '\'' +
                ", appType='" + appType + '\'' +
                ", sysType='" + sysType + '\'' +
                ", remark='" + remark + '\'' +
                ", apkUrl='" + apkUrl + '\'' +
                ", addtimes='" + addtimes + '\'' +
                '}';
    }
}
