package top.slantech.wifipsd.ui.activity;

import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.awesome.android.sdk.publish.Scloth;
import com.awesome.android.sdk.publish.enumbean.LayerErrorCode;
import com.awesome.android.sdk.publish.enumbean.ViewSize;
import com.awesome.android.sdk.publish.listener.IAwSclothListener;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import top.slantech.wifipsd.BaseActivity;
import top.slantech.wifipsd.MyApplication;
import top.slantech.wifipsd.R;
import top.slantech.wifipsd.api.ApiHelper;
import top.slantech.wifipsd.bean.SysAppAds;
import top.slantech.wifipsd.common.DataConfig;
import top.slantech.wifipsd.common.KeyConfig;
import top.slantech.wifipsd.common.UIHelper;
import top.slantech.wifipsd.utils.TitleUtils;
import top.slantech.yzlibrary.utils.StringUtils;
import top.slantech.yzlibrary.utils.ULog;

public class HelpActivity extends BaseActivity {

    Scloth scloth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ac_help);
        ButterKnife.inject(this);

        TitleUtils.Init(this, getString(R.string.help));

        tvXiaoMiRoot1.setText(Html.fromHtml(getString(R.string.xiaomi_root1) + "<font color=\"#4bc4f0\">" + getString(R.string.xiaomi_root_down_url) + "</font>"));
        String res = getString(R.string.connect) + "<font color=\"#4bc4f0\">" + ApiHelper.Contact_QQ_Qun + "</font>";
        tvLinkQQ.setText(Html.fromHtml(res));
        tvLinkQQ2.setText(Html.fromHtml(res));

        SysAppAds sysAppAds = UIHelper.checkIsShowAds(this, KeyConfig.Ads_Is_Show_Server_Banner, -1);
        if (sysAppAds.getIsOpen() == 0) {
            initBannerAds();
        }

        //横幅请求服务端
        UIHelper.getServerAds(this, 5, KeyConfig.Ads_Is_Show_Server_Banner);
    }


    @OnClick({R.id.tv_kingroot, R.id.tv_root_baidu,
            R.id.tv_root_jingling, R.id.tv_root_dashi,
            R.id.tv_yijian_root, R.id.tv_360_root,
            R.id.tv_xiaomi_root1,
            R.id.ll_what_is_root_title,
            R.id.ll_root_wenti_title,
            R.id.ll_how_root_xiaomi_title,
            R.id.ll_how_root_meizu_title,
            R.id.ll_how_root_title,
            R.id.ll_root_notuse_title, R.id.ll_root_now_notuse_title,
            R.id.ll_function_title, R.id.ll_search_wifi_title, R.id.ll_wifi_connect_title,
            R.id.tv_wifi_pwd, R.id.tv_wifi_pwd2, R.id.tv_wifi_pwd3,
            R.id.tv_link_qq, R.id.tv_link_qq2
    })
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_xiaomi_root1:
                UIHelper.goUrl(this, getString(R.string.xiaomi_root_down_url));
                break;
            case R.id.ll_what_is_root_title:
                update(llWhatIsRootDetail, ivWhatIsRoot, tvWhatIsRoot);
                break;
            case R.id.ll_root_wenti_title:
                update(llRootWentiDetail, ivRootWenti, tvRootWenti);
                break;
            case R.id.ll_how_root_xiaomi_title:
                update(llHowRootXiaomiDetail, ivHowRootXiaomi, tvHowRootXiaomi);
                break;
            case R.id.ll_how_root_meizu_title:
                update(llHowRootMeizuDetail, ivHowRootMeizu, tvHowRootMeizu);
                break;
            case R.id.ll_how_root_title:
                update(llHowRootDetail, ivHowRoot, tvHowRoot);
                break;
            case R.id.ll_root_notuse_title:
                update(llRootNotuseDetail, ivRootNotuse, tvRootNotuse);
                break;
            case R.id.ll_root_now_notuse_title:
                update(llRootNowNotuseDetail, ivRootNowNotuse, tvRootNowNotuse);
                break;
            case R.id.ll_function_title:
                update(llFunctionDetail, ivFunction, tvFunction);
                break;
            case R.id.ll_search_wifi_title:
                update(llSearchWifiDetail, ivSearchWifi, tvSearchWifi);
                break;
            case R.id.ll_wifi_connect_title:
                update(llWifiConnectDetail, ivWifiConnect, tvWifiConnect);
                break;
            case R.id.tv_wifi_pwd3:
            case R.id.tv_wifi_pwd2:
            case R.id.tv_wifi_pwd:
                UIHelper.gotoMarket(this, ApiHelper.WiFi_Pwd_Pck_Name);
                break;
            case R.id.tv_kingroot:
                UIHelper.gotoMarket(this, ApiHelper.Root_King_Pck_Name);
                break;
            case R.id.tv_root_baidu:
                UIHelper.gotoMarket(this, ApiHelper.Root_BaiDu_Pck_Name);
                break;
            case R.id.tv_root_jingling:
                UIHelper.gotoMarket(this, ApiHelper.Root_JingLing_Pck_Name);
                break;
            case R.id.tv_root_dashi:
                UIHelper.gotoMarket(this, ApiHelper.Root_DaShi_Pck_Name);
                break;
            case R.id.tv_yijian_root:
                UIHelper.gotoMarket(this, ApiHelper.Root_YiJian_Pck_Name);
                break;
            case R.id.tv_360_root:
                UIHelper.gotoMarket(this, ApiHelper.Root_360_Pck_Name);
                break;
            case R.id.tv_link_qq2:
            case R.id.tv_link_qq:
                boolean b = UIHelper.joinQQGroup(this, ApiHelper.QQ_Qun_Key);
                if (!b) {
                    StringUtils.copyTxt(this, ApiHelper.Contact_QQ_Qun);
                    MyApplication.cusToast(this, getString(R.string.txt_copy), 0);
                }
                break;
        }
    }

    private void update(LinearLayout lldetail, ImageView iv, TextView tv) {
        if (lldetail.getVisibility() == View.GONE) {
            lldetail.setVisibility(View.VISIBLE);
            iv.setImageDrawable(getResources().getDrawable(R.mipmap.icon_top));
            tv.setTextColor(getResources().getColor(R.color.mainbg));
        } else {
            lldetail.setVisibility(View.GONE);
            iv.setImageDrawable(getResources().getDrawable(R.mipmap.icon_down));
            tv.setTextColor(getResources().getColor(R.color.black));
        }
    }


    private void initBannerAds() {
        //初始化横幅监听对象
        IAwSclothListener iAwSclothListener = new IAwSclothListener() {
            @Override
            public void onSclothPreparedFailed(LayerErrorCode errorCode) {
                //广告加载失败
                ULog.e("AdBana " + "on banner prepared failed " + errorCode);
                flAds.setVisibility(View.GONE);
            }

            @Override
            public void onSclothPrepared() {
                //广告预加载成功
                ULog.e("AdBana " + "on banner prepared");
                flAds.setVisibility(View.VISIBLE);
            }

            @Override
            public void onSclothExposure() {
                //广告展示成功回调
                ULog.e("AdBana " + "on banner exposure");
            }

            @Override
            public void onSclothClosed() {
                //广告被用户关闭回调
                ULog.e("AdBana " + "on banner close ");
            }

            @Override
            public void onSclothClicked() {
                //广告被点击回调
                ULog.e("AdBana " + "on banner clicked ");
            }
        };

        scloth = new Scloth(this, DataConfig.AdBana_App_ID, DataConfig.AdBana_Banner_AdID);
        //设置横幅容器并设置大小
        scloth.setSclothContainer(flAds, ViewSize.SCLOTH_SIZE_728X90);
        //给广告位设置监听
        scloth.setSclothEventListener(iAwSclothListener);
        //开始请求广告
        scloth.requestAwScloth();
    }

    /**
     * 为sdk添加生命周期（必须添加加  不添加会影响展示）(non-Javadoc)
     */
    @Override
    protected void onResume() {
        if (scloth != null) {
            //scloth.resumeScloth();
            scloth.onResume();
        }
        super.onResume();
    }

    /**
     * 为sdk添加生命周期（必须添加加  不添加会影响展示）(non-Javadoc)
     */
    @Override
    protected void onPause() {
        if (scloth != null) {
            //scloth.dismissScloth();
            scloth.onPause();
        }
        super.onPause();
    }

    /**
     * 为sdk添加生命周期（必须添加加  不添加会影响展示）(non-Javadoc)
     */
    @Override
    protected void onDestroy() {
        if (scloth != null) {
            scloth.onDestroy();
        }
        super.onDestroy();
    }


    @InjectView(R.id.fl_ads)
    FrameLayout flAds;

    @InjectView(R.id.iv_what_is_root)
    ImageView ivWhatIsRoot;
    @InjectView(R.id.ll_what_is_root_detail)
    LinearLayout llWhatIsRootDetail;
    @InjectView(R.id.tv_what_is_root)
    TextView tvWhatIsRoot;

    @InjectView(R.id.iv_root_wenti)
    ImageView ivRootWenti;
    @InjectView(R.id.ll_root_wenti_detail)
    LinearLayout llRootWentiDetail;
    @InjectView(R.id.tv_root_wenti)
    TextView tvRootWenti;

    @InjectView(R.id.tv_xiaomi_root1)
    TextView tvXiaoMiRoot1;
    @InjectView(R.id.tv_how_root_xiaomi)
    TextView tvHowRootXiaomi;
    @InjectView(R.id.iv_how_root_xiaomi)
    ImageView ivHowRootXiaomi;
    @InjectView(R.id.ll_how_root_xiaomi_detail)
    LinearLayout llHowRootXiaomiDetail;

    @InjectView(R.id.tv_how_root_meizu)
    TextView tvHowRootMeizu;
    @InjectView(R.id.iv_how_root_meizu)
    ImageView ivHowRootMeizu;
    @InjectView(R.id.ll_how_root_meizu_detail)
    LinearLayout llHowRootMeizuDetail;

    @InjectView(R.id.tv_how_root)
    TextView tvHowRoot;
    @InjectView(R.id.iv_how_root)
    ImageView ivHowRoot;
    @InjectView(R.id.ll_how_root_detail)
    LinearLayout llHowRootDetail;

    @InjectView(R.id.tv_root_notuse)
    TextView tvRootNotuse;
    @InjectView(R.id.iv_root_notuse)
    ImageView ivRootNotuse;
    @InjectView(R.id.ll_root_notuse_detail)
    LinearLayout llRootNotuseDetail;
    @InjectView(R.id.tv_root_now_notuse)
    TextView tvRootNowNotuse;
    @InjectView(R.id.iv_root_now_notuse)
    ImageView ivRootNowNotuse;
    @InjectView(R.id.ll_root_now_notuse_detail)
    LinearLayout llRootNowNotuseDetail;
    @InjectView(R.id.tv_function)
    TextView tvFunction;
    @InjectView(R.id.iv_function)
    ImageView ivFunction;
    @InjectView(R.id.ll_function_detail)
    LinearLayout llFunctionDetail;
    @InjectView(R.id.tv_search_wifi)
    TextView tvSearchWifi;
    @InjectView(R.id.iv_search_wifi)
    ImageView ivSearchWifi;
    @InjectView(R.id.ll_search_wifi_detail)
    LinearLayout llSearchWifiDetail;
    @InjectView(R.id.tv_wifi_connect)
    TextView tvWifiConnect;
    @InjectView(R.id.iv_wifi_connect)
    ImageView ivWifiConnect;
    @InjectView(R.id.ll_wifi_connect_detail)
    LinearLayout llWifiConnectDetail;

    @InjectView(R.id.tv_link_qq)
    TextView tvLinkQQ;
    @InjectView(R.id.tv_link_qq2)
    TextView tvLinkQQ2;


}
