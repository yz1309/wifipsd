package top.slantech.wifipsd.api;

import android.content.Context;

import top.slantech.yzlibrary.utils.SPUtils;
import top.slantech.yzlibrary.utils.StringUtils;

/**
 * Created by admin on 2016/6/29 0029.
 */
public class ApiHelper {
    public final static int Page_Size_3 = 3;
    public final static int Page_Size_4 = 4;
    public final static int Page_Size_6 = 6;
    public final static int Page_Size_8 = 8;
    public final static int Page_Size_10 = 10;
    public final static int Page_Size_15 = 15;
    public final static int Page_Size_20 = 20;
    public final static int Page_Size_40 = 40;
    public final static String QQ_Package = "com.tencent.mobileqq";
    public final static String Status_No = "0";
    public final static String Status_Yes = "1";
    public final static String Status_Repeat = "2";
    public static final String App_Show_Page = "http://fir.im/wifipsd";
    public static final String QQ_Qun_Key = "OCXYv02sdKWACiNWvC3EjxFhQPmIwx2h";
    public static final String Contact_QQ_Qun="234661863";
    public final static String Weibo_Url = "http://weibo.com/slantech";
    public final static String Contact_Weibo = "@石兰科技";
    public final static String Contact_Email = "slantech@163.com";
    public final static String Contact_Home= "http://www.slan.tech/";

    public static String baseURL = "http://www.slantech.top/";
    public static ApiHelper apiHelper;
    /**
     * 获取请求服务器URL
     * get
     * return url string
     */
    public final static String Server_URL = baseURL + "api/getserurl";

    public static ApiHelper getInstance() {
        if (apiHelper == null)
            apiHelper = new ApiHelper();
        return apiHelper;
    }

    /**
     * 获取基础URL
     *
     * @param context
     * @return
     */
    public String getBaseURL(Context context) {
        String neturl = (String) SPUtils.get(context, "neturl", baseURL);
        if (!StringUtils.isEmpty(neturl))
            return neturl.replace("\"", "");
        else
            return baseURL;
    }
    /**
     * 检查更新
     * get
     * versionCode=xx
     * appType=xx
     * return appversion 实体
     */
    public String getCheck(Context context) {
        return getBaseURL(context) + "/api/check";
    }

    /**
     * 反馈
     * post
     * contacts=xx
     * contents=xx
     * device=xx
     * types=xx
     */
    public String getFeedBack(Context context) {
        return getBaseURL(context) + "/api/feedback";
    }

    /**
     * 获取app列表
     * get
     * pageSize=xx
     * pageIndexxx
     */
    public String getOtherAppList(Context context) {
        return getBaseURL(context) + "/api/sys/getapps";
    }

    /**
     * 判断app对应的广告形式是否需要展示
     * get
     * appType=xx
     * adsType=xx
     * return 0打开 -1关闭
     * @param context
     * @return
     */
    public String getAppAdsIsShow(Context context){
        return getBaseURL(context)+"api/sys/getappadsisopen";
    }

    /**
     *获取app对应的广告实体
     * get
     * appType=xx
     * adsType=xx
     * return SysAppAds
     * @param context
     * @return
     */
    public String getAppAds(Context context){
        return getBaseURL(context)+"api/sys/getappads";
    }

    /**
     * Root 工具包名 king root
     */
    public final static String Root_King_Pck_Name = "com.kingroot.kinguser";
    /**
     * Root 工具包名 百度一键root
     */
    public final static String Root_BaiDu_Pck_Name = "com.baidu.easyroot";
    /**
     * Root 工具包名 root精灵
     */
    public final static String Root_JingLing_Pck_Name = "com.shuame.rootgenius";
    /**
     * Root 工具包名 root大师
     */
    public final static String Root_DaShi_Pck_Name = "com.mgyun.shua.su";
    /**
     * Root 工具包名 一键ROOT工具
     */
    public final static String Root_YiJian_Pck_Name = "com.shuame.oneclickroottool";
    /**
     * Root 工具包名 360超级ROOT
     */
    public final static String Root_360_Pck_Name = "com.qihoo.permmgr";
    /**
     * WiFi万能钥匙
     */
    public final static String WiFi_Pwd_Pck_Name = "com.snda.wifilocating";

}
