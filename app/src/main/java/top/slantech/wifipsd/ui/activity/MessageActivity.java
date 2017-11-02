package top.slantech.wifipsd.ui.activity;

import android.app.NotificationManager;
import android.os.Bundle;
import android.text.Html;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import top.slantech.wifipsd.BaseActivity;
import top.slantech.wifipsd.R;
import top.slantech.wifipsd.common.UIHelper;

public class MessageActivity extends BaseActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ac_message);
        ButterKnife.inject(this);
        UIHelper.initSystemBar(this, R.color.main_bg5);
        //TitleUtils.Init(this, getString(R.string.messages));
        String str = getIntent().getStringExtra("str");
        if (!TextUtils.isEmpty(str))
            tvContent.setText(Html.fromHtml(str));


        int ids = getIntent().getIntExtra("ids", 2);
        //清空通知栏
        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notificationManager.cancel(ids);

    }

    @InjectView(R.id.tv_content)
    TextView tvContent;

    @OnClick({R.id.tv_close})
    public void onClick(View v){
        switch (v.getId()){
            case R.id.tv_close:
                finish();
                break;
        }
    }
}
