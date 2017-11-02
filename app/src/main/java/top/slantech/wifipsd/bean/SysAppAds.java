package top.slantech.wifipsd.bean;

/**
 * 广告实体类
 * Created by slantech on 2017/01/02 15:40
 */
public class SysAppAds {
    private int id;
    private String adsappnum;
    private String appType;
    private int adsType;//0开屏，1信息流ListView，2信息流设置，3信息流详情，4视频，5横幅，6插屏
    private int isOpen;//-1不展示广告,0展示
    private int adsPlatform;//0讯飞，1广点通

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getAdsappnum() {
        return adsappnum;
    }

    public void setAdsappnum(String adsappnum) {
        this.adsappnum = adsappnum;
    }

    public String getAppType() {
        return appType;
    }

    public void setAppType(String appType) {
        this.appType = appType;
    }

    public int getAdsType() {
        return adsType;
    }

    public void setAdsType(int adsType) {
        this.adsType = adsType;
    }

    public int getIsOpen() {
        return isOpen;
    }

    public void setIsOpen(int isOpen) {
        this.isOpen = isOpen;
    }

    public int getAdsPlatform() {
        return adsPlatform;
    }

    public void setAdsPlatform(int adsPlatform) {
        this.adsPlatform = adsPlatform;
    }

    @Override
    public String toString() {
        return "SysAppAds{" +
                "id=" + id +
                ", adsappnum='" + adsappnum + '\'' +
                ", appType='" + appType + '\'' +
                ", adsType=" + adsType +
                ", isOpen=" + isOpen +
                ", adsPlatform=" + adsPlatform +
                '}';
    }
}
