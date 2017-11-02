package top.slantech.wifipsd;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.awesome.android.sdk.publish.Splash;
import com.awesome.android.sdk.publish.enumbean.LayerErrorCode;
import com.awesome.android.sdk.publish.listener.IAwSplashListener;

import butterknife.ButterKnife;
import butterknife.InjectView;
import top.slantech.wifipsd.common.DataConfig;
import top.slantech.wifipsd.common.KeyConfig;
import top.slantech.wifipsd.common.UIHelper;
import top.slantech.yzlibrary.utils.ULog;

/**
 * voices ads
 */
public class WelComeActivity3 extends Activity {

    @InjectView(R.id.ll_one)
    LinearLayout llOne;
    @InjectView(R.id.rl_gdt_ads)
    RelativeLayout rlGDTAds;
    @InjectView(R.id.iv_gdt_logo)
    ImageView ivGDTLogo;


    //设置一个变量来控制当前开屏页面是否可以跳转
    //boolean canJump = false;
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

        showAds();

        // 开屏请求服务端
        UIHelper.getServerAds(this, 0, KeyConfig.Ads_Is_Show_Server_Splash);

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
                ULog.e("AdBana "+ "on splash prepared failed " + errorCode);
            }

            @Override
            public void onSplashPrepared() {
                ULog.e("AdBana "+ "on splash prepared Prepared ");
                ivGDTLogo.setVisibility(View.INVISIBLE); // 广告展示后一定要把预设的开屏图片隐藏起来
            }

            @Override
            public void onSplashExposure() {
                ULog.e("AdBana "+ "on splash prepared Exposure ");
            }

            @Override
            public void onSplashClosed() {
                ULog.e("AdBana "+ "on splash prepared Closed ");
            }

            @Override
            public void onSplashClicked() {
                ULog.e("AdBana "+ "on splash prepared Clicked ");
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
}