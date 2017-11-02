package top.slantech.wifipsd.ui.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import top.slantech.wifipsd.BaseActivity;
import top.slantech.wifipsd.MyApplication;
import top.slantech.wifipsd.R;
import top.slantech.wifipsd.api.ApiHelper;
import top.slantech.wifipsd.common.UIHelper;
import top.slantech.wifipsd.utils.TitleUtils;
import top.slantech.yzlibrary.utils.NetUtils;
import top.slantech.yzlibrary.utils.StringUtils;


public class AboutMeActivity extends BaseActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ac_about_me);

        ButterKnife.inject(this);

        TitleUtils.Init(this, getString(R.string.about_me));

        tvVersion.setText("V" + MyApplication.getInstance().getVersionName(this));
        tvQqqun.setText(ApiHelper.Contact_QQ_Qun);
        tvNet.setText(ApiHelper.Contact_Home);
        tvSina.setText(ApiHelper.Contact_Weibo);
        tvEmail.setText(ApiHelper.Contact_Email);
    }

    @OnClick({R.id.tv_version, R.id.rl_qq, R.id.rl_net, R.id.rl_sina, R.id.rl_email})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_version:
                UIHelper.checkVersion(this, 2);
                break;
            case R.id.rl_qq:
                boolean b = UIHelper.joinQQGroup(this, ApiHelper.QQ_Qun_Key);
                if (!b) {
                    StringUtils.copyTxt(this, ApiHelper.Contact_QQ_Qun);
                    showToast(ApiHelper.Contact_QQ_Qun + " " + getString(R.string.copy_suc));
                }
                break;
            case R.id.rl_net:
                StringUtils.copyTxt(this, ApiHelper.Contact_Home);
                showToast(ApiHelper.Contact_Home + " " + getString(R.string.copy_suc));
                if (NetUtils.isNetworkConnected(AboutMeActivity.this)) {
                    UIHelper.goUrl(this, ApiHelper.Contact_Home);
                }
                break;
            case R.id.rl_sina:
                StringUtils.copyTxt(this, ApiHelper.Weibo_Url);
                showToast(ApiHelper.Weibo_Url + " " + getString(R.string.copy_suc));
                if (NetUtils.isNetworkConnected(AboutMeActivity.this)) {
                    UIHelper.goUrl(this, ApiHelper.Weibo_Url);
                }
                break;
            case R.id.rl_email:
                StringUtils.copyTxt(this, ApiHelper.Contact_Email);
                showToast(ApiHelper.Contact_Email + " " + getString(R.string.copy_suc));
                break;
        }
    }

    @InjectView(R.id.tv_version)
    TextView tvVersion;
    @InjectView(R.id.tv_qqqun)
    TextView tvQqqun;
    @InjectView(R.id.tv_net)
    TextView tvNet;
    @InjectView(R.id.tv_sina)
    TextView tvSina;
    @InjectView(R.id.tv_email)
    TextView tvEmail;
}
