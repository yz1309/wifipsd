package top.slantech.wifipsd;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.awesome.android.sdk.publish.Splash;
import com.awesome.android.sdk.publish.enumbean.LayerErrorCode;
import com.awesome.android.sdk.publish.listener.IAwSplashListener;
import com.igexin.sdk.PushManager;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.util.Timer;
import java.util.TimerTask;

import butterknife.ButterKnife;
import butterknife.InjectView;
import okhttp3.Call;
import top.slantech.wifipsd.api.ApiHelper;
import top.slantech.wifipsd.bean.SysAppAds;
import top.slantech.wifipsd.common.DataConfig;
import top.slantech.wifipsd.common.KeyConfig;
import top.slantech.wifipsd.common.UIHelper;
import top.slantech.wifipsd.services.DemoIntentService;
import top.slantech.wifipsd.services.DemoPushService;
import top.slantech.wifipsd.ui.activity.ViewPwdActivity;
import top.slantech.yzlibrary.utils.CalendarTool;
import top.slantech.yzlibrary.utils.NetUtils;
import top.slantech.yzlibrary.utils.SPUtils;
import top.slantech.yzlibrary.utils.StringUtils;
import top.slantech.yzlibrary.utils.ULog;

/**
 * adbana ads
 */
public class WelComeActivity2 extends Activity {

    @InjectView(R.id.ll_one)
    LinearLayout llOne;
    @InjectView(R.id.rl_gdt_ads)
    RelativeLayout rlGDTAds;
    @InjectView(R.id.iv_gdt_logo)
    ImageView ivGDTLogo;


    private Boolean isShowAds = false;
    //设置一个变量来控制当前开屏页面是否可以跳转
    boolean canJump = false;
    // 声明开屏广告对象
    Splash splash;
    // 声明开屏广告监听对象
    IAwSplashListener splashListener;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LinearLayout view = (LinearLayout) getLayoutInflater().inflate(R.layout.ac_wel_come, null);
        setContentView(view);
        ButterKnife.inject(this);

        /**
         * 个推
         * 客户端只能获取透传的内容，不能获取推送通知的标题以及内容
         */
        PushManager.getInstance().initialize(this.getApplicationContext(), DemoPushService.class);
        PushManager.getInstance().registerPushIntentService(this.getApplicationContext(), DemoIntentService.class);


        SysAppAds sysAppAds = UIHelper.checkIsShowAds(this, KeyConfig.Ads_Is_Show_Server_Splash, 0);
        // 开屏
        if (sysAppAds.getIsOpen() == 0) {
            //ULog.e("r-" + r + ",r % 2 == 0 " + (r % 2 == 0 ? "显示" : "不显示"));
            long lastShowAdsTime = (long) SPUtils.get(this, KeyConfig.Ads_Is_Show_Server_Splash + "_show_ads_time", 0L);
            int offectMinutes = CalendarTool.getOffectMinutes(System.currentTimeMillis(), lastShowAdsTime);

            // 10分钟内不再显示
            if (offectMinutes > 10)
                isShowAds = true;
        }


        if (!NetUtils.isNetworkConnected(this) && isShowAds) {
            isShowAds = false;
        }

        if (isShowAds)
            showAds();
        else
            showNotAds();

        getServerURL();


        // 开屏请求服务端
        UIHelper.getServerAds(this, 0, KeyConfig.Ads_Is_Show_Server_Splash);

    }

    private void showNotAds() {
        timers(3000);
    }

    private void showAds() {
        llOne.setVisibility(View.GONE);
        initSplashAds();
    }

    private void initSplashAds() {
        // 初始化广告监听对象
        splashListener = new IAwSplashListener() {
            @Override
            public void onSplashPreparedFailed(LayerErrorCode errorCode) {
                ULog.e("AdBana " + "on splash prepared failed " + errorCode);
                timers(2000);
            }

            @Override
            public void onSplashPrepared() {
                ULog.e("AdBana " + "on splash prepared Prepared ");
                ivGDTLogo.setVisibility(View.INVISIBLE); // 广告展示后一定要把预设的开屏图片隐藏起来
            }

            @Override
            public void onSplashExposure() {
                ULog.e("AdBana " + "on splash prepared Exposure ");
            }

            @Override
            public void onSplashClosed() {
                ULog.e("AdBana " + "on splash prepared Closed ");

                next(1);
            }

            @Override
            public void onSplashClicked() {
                ULog.e("AdBana " + "on splash prepared Clicked ");
            }
        };
        //初始化splash对象
        splash = new Splash(this, DataConfig.AdBana_App_ID, DataConfig.AdBana_Spalsh_AdID);
        // 添加开屏容器
        splash.setSplashContainer(rlGDTAds);
        //给广告位设置监听
        splash.setSplashEventListener(splashListener);
        //开始请求广告
        splash.requestAwSplash();
    }

    private void timers(final int s) {
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                Message msg = new Message();
                msg.what = 2;
                mHandler.sendMessage(msg);
            }
        }, s);
    }

    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 2) {
                goMain();
            }
        }
    };

    private void goMain() {
        UIHelper.goActivity(this, ViewPwdActivity.class);
        WelComeActivity2.this.finish();
    }

    /**
     * 请求的服务器URL从服务端获取
     */
    private void getServerURL() {
        OkHttpUtils.get().url(ApiHelper.getInstance().Server_URL)
                .build().execute(new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {

            }

            @Override
            public void onResponse(String response, int id) {
                if (!StringUtils.isEmpty(response)) {
                    SPUtils.put(WelComeActivity2.this, "neturl", response);
                }
            }
        });
    }

    /**
     * 设置一个变量来控制当前开屏页面是否可以跳转，当开屏广告为普链类广告时，点击会打开一个广告落地页，此时开发者还不能打开自己的App主页。
     * 当从广告落地页返回以后， 才可以跳转到开发者自己的App主页；当开屏广告是App类广告时只会下载App。
     */
    private void next(int type) {
        if (type == 1)
            // 记录展示广告的时间点
            SPUtils.put(WelComeActivity2.this, KeyConfig.Ads_Is_Show_Server_Splash + "_show_ads_time", System.currentTimeMillis());
        if (canJump) {
            goMain();
        } else {
            canJump = true;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        canJump = false;
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (canJump) {
            next(2);
        }
        canJump = true;
    }

    /**
     * 为sdk添加生命周期（必须添加加 不添加会影响展示）(non-Javadoc)
     */
    @Override
    protected void onDestroy() {
        if (splash != null) {
            splash.onDestroy();
        }
        super.onDestroy();
    }

    /**
     * 开屏页最好禁止用户对返回按钮的控制
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK
                || keyCode == KeyEvent.KEYCODE_HOME) {
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

}