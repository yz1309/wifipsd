package top.slantech.wifipsd.ui.activity;

import android.os.Bundle;
import android.text.Editable;
import android.text.Html;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.igexin.sdk.PushManager;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import okhttp3.Call;
import top.slantech.wifipsd.BaseActivity;
import top.slantech.wifipsd.MyApplication;
import top.slantech.wifipsd.R;
import top.slantech.wifipsd.api.ApiHelper;
import top.slantech.wifipsd.common.UIHelper;
import top.slantech.wifipsd.utils.TitleUtils;
import top.slantech.yzlibrary.utils.EquipInfoUtils;
import top.slantech.yzlibrary.utils.NetUtils;
import top.slantech.yzlibrary.utils.StringUtils;

public class FeedBackActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ac_feed_back);

        ButterKnife.inject(this);
        TitleUtils.Init(this, getString(R.string.feedback));
        TitleUtils.setRightTVVisible(this, true);

        tvRight.setText(getString(R.string.sub));
        initData();
    }

    private void initData() {
        tvQq.setText(Html.fromHtml(getString(R.string.qqqun2) + "：" + "<font color=\"#56abe4\">" + ApiHelper.Contact_QQ_Qun + "</font>"));
        String[] versions = new String[0];
        try {
            versions = EquipInfoUtils.getVersion();
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (versions.length == 5) {
            tvDevice.setText("品牌：" + versions[4] + "  型号：" + versions[2] + "  系统：" + versions[1]);
        }
        etContent.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                String ext = etContent.getText().toString().trim();
                tvError.setText("还能输入" + (300 - ext.length()) + "个");
            }
        });
    }

    @OnClick({R.id.tv_right, R.id.ll_qq})
    public void onClick(View view) {
        if (view.getId() == R.id.ll_qq) {
            boolean b = UIHelper.joinQQGroup(FeedBackActivity.this, ApiHelper.QQ_Qun_Key);
            if (!b) {
                StringUtils.copyTxt(FeedBackActivity.this, ApiHelper.Contact_QQ_Qun);
                MyApplication.getInstance().cusToast(FeedBackActivity.this, getString(R.string.txt_copy), 0);
            }
        } else if (view.getId() == R.id.tv_right) {
            if (NetUtils.isNetworkConnected(FeedBackActivity.this)) {
                String content = etContent.getText().toString().trim();
                if (!TextUtils.isEmpty(content)) {
                    OkHttpUtils.post().url(ApiHelper.getInstance().getFeedBack(this))
                            .addParams("getui_clientid", PushManager.getInstance().getClientid(this))
                            .addParams("contacts", etContact.getText().toString().trim())
                            .addParams("contents", content)
                            .addParams("device", "版本：" + MyApplication.getInstance().getVersionCode(FeedBackActivity.this) + "(" +
                                    MyApplication.getInstance().getVersionName(FeedBackActivity.this) + ") " + tvDevice.getText().toString())
                            .addParams("types", getString(R.string.app_name))
                            .build().execute(new StringCallback() {
                        @Override
                        public void onError(Call call, Exception e, int id) {
                            //dealErrorMes(call, e, id);
                        }

                        @Override
                        public void onResponse(String response, int id) {
                        }

                    });

                    showToast(getString(R.string.thanks));
                    FeedBackActivity.this.finish();
                } else
                    showToast(getString(R.string.input_comment_require));
            } else
                showToast(getString(R.string.no_net));
        }
    }

    @InjectView(R.id.et_contact)
    EditText etContact;
    @InjectView(R.id.et_content)
    EditText etContent;
    @InjectView(R.id.tvError)
    TextView tvError;
    @InjectView(R.id.tv_device)
    TextView tvDevice;
    @InjectView(R.id.tv_qq)
    TextView tvQq;
    @InjectView(R.id.tv_right)
    TextView tvRight;
}
