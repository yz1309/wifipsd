package top.slantech.wifipsd.ui.activity;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.reflect.TypeToken;
import com.iflytek.voiceads.AdError;
import com.iflytek.voiceads.IFLYNativeAd;
import com.iflytek.voiceads.IFLYNativeListener;
import com.iflytek.voiceads.NativeADDataRef;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import top.slantech.wifipsd.BaseActivity;
import top.slantech.wifipsd.MyApplication;
import top.slantech.wifipsd.R;
import top.slantech.wifipsd.WelComeActivity3;
import top.slantech.wifipsd.adapter.BackAdapter;
import top.slantech.wifipsd.adapter.CommonNewAdapter;
import top.slantech.wifipsd.api.ApiHelper;
import top.slantech.wifipsd.bean.Wifis;
import top.slantech.wifipsd.common.DataConfig;
import top.slantech.wifipsd.common.KeyConfig;
import top.slantech.wifipsd.common.UIHelper;
import top.slantech.wifipsd.utils.TitleUtils;
import top.slantech.wifipsd.utils.WifiManage;
import top.slantech.yzlibrary.utils.CalendarTool;
import top.slantech.yzlibrary.utils.DisplayUtil;
import top.slantech.yzlibrary.utils.FileUtils;
import top.slantech.yzlibrary.utils.SPUtils;
import top.slantech.yzlibrary.utils.SettingUtils;
import top.slantech.yzlibrary.utils.StringUtils;
import top.slantech.yzlibrary.utils.ULog;

public class SetActivity extends BaseActivity implements CommonNewAdapter.Callback {

    private Dialog dialog;
    private TextView tvDelAll,tvCancel;
    private ListView listView;
    private BackAdapter mAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ac_set);
        ButterKnife.inject(this);

        TitleUtils.Init(this, getString(R.string.set));


        initDialog();

        if (UIHelper.checkIsShowAds(this, KeyConfig.Ads_Is_Show_Server_XinXiLiu_Set, -1).getIsOpen() == 0) {
            initVoicesXinXiLiuAds();
        }

//        if (UIHelper.checkIsShowAds(this, KeyConfig.Ads_Is_Show_Server_Video, 0).getIsOpen() == 0) {
//            llVideo.setVisibility(View.VISIBLE);
//            // 设置服务器回调 userId，一定要在请求广告之前设置，否则无效
//            // userId 如果有登录那么取usernum字段
//            String userId = PushManager.getInstance().getClientid(this);
//            if (StringUtils.isEmpty(userId))
//                userId = MyApplication.mAppName + "-" + CalendarTool.getCurRandomDate();
//        } else {
            llVideo.setVisibility(View.GONE);
//        }

        //信息流设置请求服务端
        UIHelper.getServerAds(this, 2, KeyConfig.Ads_Is_Show_Server_XinXiLiu_Set);
        //视频请求服务端
        //UIHelper.getServerAds(this, 4, KeyConfig.Ads_Is_Show_Server_Video);
    }

    private void initDialog() {
        dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                return false;
            }
        });
        dialog.setContentView(R.layout.dialog_list);


        final Window dialogWindow = dialog.getWindow();
        dialogWindow.setGravity(Gravity.CENTER);
        //实现铺满全屏效果
        // dialogWindow.getDecorView().setPadding(0, 0, 0, 0);
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        lp.width = WindowManager.LayoutParams.FILL_PARENT;
        lp.height = DisplayUtil.getScreenMetrics(this).y / 2;
        dialogWindow.setAttributes(lp);

        tvDelAll = (TextView) dialog.findViewById(R.id.tv_del_all);
        listView = (ListView) dialog.findViewById(R.id.list_item);
        tvCancel = (TextView) dialog.findViewById(R.id.tv_cancel);

        mAdapter = new BackAdapter(this);
        listView.setAdapter(mAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String path = MyApplication.getBackUpFilePath() + "/" + mAdapter.getItem(position);
                byte[] bytes = FileUtils.getByteArrayFromSD(path);
                // 此处是把数据给直接覆盖了
                SPUtils.put(SetActivity.this, KeyConfig.Key_WiFi_Data, new String(bytes));

                ULog.e(new String(bytes));

                MyApplication.cusToast(SetActivity.this, getString(R.string.recover_suc), 0);
                // old 恢复仅当 root 失效后起作用
                // new 混合显示


                dialog.dismiss();
            }
        });

        tvCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        tvDelAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog dialog = new AlertDialog.Builder(SetActivity.this).setTitle(getString(R.string.mes)).setMessage(getString(R.string.confirm_del))
                        .setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                mAdapter.clear();
                                FileUtils.clearFile(MyApplication.getBackUpFilePath());

                                MyApplication.cusToast(SetActivity.this, getString(R.string.del_all_suc), 0);
                            }
                        })
                        .setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        }).show();
            }
        });
    }

    private void initVoicesXinXiLiuAds() {
        IFLYNativeListener mListener = new IFLYNativeListener() {
            @Override
            public void onAdFailed(AdError error) {
                // 广告请求失败
                ULog.e("voices信息流-广告请求失败");
            }

            @Override
            public void onADLoaded(final List<NativeADDataRef> lst) {
                if (lst != null && lst.size() > 0) {
                    llAds.setVisibility(View.VISIBLE);
                    ULog.e("开始曝光");
                    boolean b = lst.get(0).onExposured(llAds);
                    ULog.e(b ? "曝光成功" : "失败");

                    // 广告请求成功
                    ULog.e("信息流广告设置页,来源-" + lst.get(0).getAdSourceMark() +
                            ";标题-" + lst.get(0).getTitle() +
                            ";副标题-" + lst.get(0).getSubTitle() +
                            ";图片-" + lst.get(0).getImage() +
                            ";图标-" + lst.get(0).getIcon()
                    );
                    if (!StringUtils.isEmpty(lst.get(0).getTitle()) && !StringUtils.isEmpty(lst.get(0).getImage())) {
                        tvAdsTitle.setText(lst.get(0).getTitle());
                        int sw = DisplayUtil.getScreenMetrics(SetActivity.this).x;
                        int h = 150;
                        int w = (int) (h / 0.6);
                        if (sw <= 768) {
                            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(w, h);
                            ivAds.setLayoutParams(lp);
                        } else if (sw <= 1080) {
                            h = 220;
                            w = (int) (h / 0.6);
                        } else {
                            h = 250;
                            w = (int) (h / 0.6);
                        }
                        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(w, h);
                        ivAds.setLayoutParams(lp);
                        ImageLoader.getInstance().displayImage(lst.get(0).getImage(), ivAds, MyApplication.getRectLoadingOptions());
                    } else {
                        llAds.setVisibility(View.GONE);
                    }
                    if (!StringUtils.isEmpty(lst.get(0).getSubTitle()))
                        tvAdsSubTitle.setText(lst.get(0).getSubTitle());

                    if (!StringUtils.isEmpty(lst.get(0).getAdSourceMark())) {
                        tvAdsMark.setText(lst.get(0).getAdSourceMark() + "|广告");
                        tvAdsMark.setVisibility(View.VISIBLE);
                    } else
                        tvAdsMark.setVisibility(View.GONE);


                    llAds.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            lst.get(0).onClicked(llAds);
                        }
                    });
                }
            }

            @Override
            public void onCancel() {
                // 下载类广告，下载提示框取消
            }

            @Override
            public void onConfirm() {
                // 下载类广告，下载提示框确认
            }
        };
        //创建原生广告：adId：开发者在广告平台(http://www.voiceads.cn/)申请的广告位 ID
        IFLYNativeAd nativeAd = new IFLYNativeAd(this, DataConfig.Voices_XinXiLiu_Set_AdID, mListener);
        int count = 1; // 一次拉取的广告条数：范围 1-30（目前仅支持每次请求一条）
        nativeAd.loadAd(count);
    }

    @OnClick({R.id.ll_back_up, R.id.ll_recover, R.id.ll_share_wifi, R.id.ll_file_path,
            R.id.ll_about_me, R.id.ll_wel_come,
            R.id.ll_feedback, R.id.ll_invit_frd,
            R.id.ll_app, R.id.ll_go_market_score
    })
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ll_back_up:
                try {
                    try {
                        // 同一分钟内单击的直接替换掉
                        String path = MyApplication.getBackUpFilePath() + "/" + CalendarTool.getCurrentDate(CalendarTool.dateFormatYMDHM);
                        File file = new File(path);
                        if (file.exists()) {
                            file.delete();
                        }
                        WifiManage wifiManage = new WifiManage();
                        List<Wifis> read = wifiManage.Read();
                        String s = MyApplication.createGson().toJson(read);
                        FileUtils.writeByteArrayToSD(path, s.getBytes(), true);
                        MyApplication.cusToast(this, getString(R.string.back_up_suc) + "(" + MyApplication.getBackUpFilePath() + ")", 0);
                    } catch (Exception e) {
                        MyApplication.cusToast(this, getString(R.string.root_failed), 0);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case R.id.ll_recover:
                String path = MyApplication.getBackUpFilePath();
                List<File> fileList = FileUtils.getFileByPath(path);
                if (fileList != null && fileList.size() > 0) {
                    List<String> stringList = new ArrayList<>();
                    for (int i = 0; i < fileList.size(); i++) {
                        stringList.add(fileList.get(i).getName());
                    }
                    mAdapter.clear();
                    mAdapter.addItem(stringList);
                    dialog.show();
                } else {
                    MyApplication.cusToast(this, getString(R.string.recover_is_null), 0);
                }
                break;
            case R.id.ll_share_wifi:
                String res = (String) SPUtils.get(this, KeyConfig.Key_WiFi_Data, "");
                List<Wifis> news = MyApplication.createGson().fromJson(res,
                        new TypeToken<List<Wifis>>() {
                        }.getType());
                if (news != null && news.size() > 0) {
                    String str = "";
                    for (int i = 0; i < news.size(); i++) {
                        str += "热点：" + news.get(i).getSsid() + " 密码：" + news.get(i).getPassword() + "\r\n";
                    }
                    SettingUtils.systemShareText(this, str, getString(R.string.app_name));
                } else
                    MyApplication.cusToast(this, getString(R.string.not_wifi_to_share), 0);
                break;
            case R.id.ll_file_path:
                String[] strs = new String[2];
                strs[0] = getString(R.string.back_up_file) + "(" + MyApplication.getBackUpFilePath() + ")";
                strs[1] = getString(R.string.apk) + "(" + MyApplication.getAPKPath() + ")";
                final AlertDialog dialog1 = new AlertDialog.Builder(this)
                        .setTitle(getString(R.string.file_path))
                        .setItems(strs, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        })
                        .setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        })
                        .setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        })
                        .show();

                break;
            case R.id.ll_about_me:
                UIHelper.goActivity(this, AboutMeActivity.class);
                break;
            case R.id.ll_feedback:
                UIHelper.goActivity(this, FeedBackActivity.class);
                break;
            case R.id.ll_invit_frd:
                String shareContent = getString(R.string.desc1) + getString(R.string.app_name) + "," +
                        getString(R.string.desc2) + ApiHelper.App_Show_Page ;
                SettingUtils.systemShareText(this, shareContent, getString(R.string.app_name));
                break;
            case R.id.ll_app:
                UIHelper.goActivity(this, OtherAppsActivity.class);
                break;
            case R.id.ll_go_market_score:
                UIHelper.openAppInMarket(this);
                break;
            case R.id.ll_go_video:
                showToast(getString(R.string.video_is_loading));
                break;
            case R.id.ll_wel_come:
                goActivity(WelComeActivity3.class);
                break;
        }
    }
    @Override
    public Context getContext() {
        return this;
    }

    @Override
    public Date getSystemTime() {
        return new Date();
    }

    @InjectView(R.id.ll_ads)
    LinearLayout llAds;
    @InjectView(R.id.iv_ads)
    ImageView ivAds;
    @InjectView(R.id.tv_ads_title)
    TextView tvAdsTitle;
    @InjectView(R.id.tv_ads_sub_title)
    TextView tvAdsSubTitle;
    @InjectView(R.id.tv_ads_mark)
    TextView tvAdsMark;
    @InjectView(R.id.ll_go_video)
    LinearLayout llVideo;

}
