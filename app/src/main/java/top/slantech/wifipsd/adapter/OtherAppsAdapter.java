package top.slantech.wifipsd.adapter;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.res.Resources;
import android.os.Build;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import top.slantech.wifipsd.MyApplication;
import top.slantech.wifipsd.R;
import top.slantech.wifipsd.bean.Apps;
import top.slantech.wifipsd.common.UIHelper;


/**
 * Created by admin on 2016/2/25 0025.
 */
public class OtherAppsAdapter extends CommonNewAdapter<Apps> {


    public OtherAppsAdapter(Callback callback) {
        super(callback);
    }


    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Override
    protected void convert(ViewNewHolder vh, final Apps item, int position) {
        if (item != null) {
            vh.setText(R.id.tv_name, item.getNames());
            vh.setText(R.id.tv_slogen, item.getSlogen());
            vh.setImageForNet2(R.id.iv_pic, item.getPics(), MyApplication.getLogoOptions());
            ImageView ivPic = vh.getView(R.id.iv_pic);
            TextView tvOper = vh.getView(R.id.tv_oper);
            TextView tvName = vh.getView(R.id.tv_name);
            TextView tvSlogen = vh.getView(R.id.tv_slogen);
            try {
                // 检测本地有没有安装应用
                if (MyApplication.checkBrowser(mCallback.getContext(), item.getPackages())) {
                    tvOper.setText(mCallback.getContext().getString(R.string.open));
                    tvOper.setBackground(mCallback.getContext().getResources().getDrawable(R.drawable.shape_selector_r5_white_yellow));
                    tvOper.setTextColor(mCallback.getContext().getResources().getColor(R.color.yellow2));
                    tvName.setTextColor(mCallback.getContext().getResources().getColor(R.color.black));
                    tvSlogen.setTextColor(mCallback.getContext().getResources().getColor(R.color.gray2));
                    ivPic.setAlpha(255);
                } else {
                    tvOper.setText(mCallback.getContext().getString(R.string.add));
                    tvOper.setBackground(mCallback.getContext().getResources().getDrawable(R.drawable.shape_selector_r5_white_mainbg));
                    tvOper.setTextColor(mCallback.getContext().getResources().getColor(R.color.mainbg));
                    tvName.setTextColor(mCallback.getContext().getResources().getColor(R.color.text_999));
                    tvSlogen.setTextColor(mCallback.getContext().getResources().getColor(R.color.light_text_color));
                    ivPic.setAlpha(100);
                }
            } catch (Resources.NotFoundException e) {
                e.printStackTrace();
            }
            LinearLayout llRoot = vh.getView(R.id.ll_root);
            llRoot.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // 检测本地有没有安装应用
                    if (MyApplication.checkBrowser(mCallback.getContext(), item.getPackages())) {
                        MyApplication.doStartApplicationWithPackageName(mCallback.getContext(), item.getPackages());
                    } else {
                        UIHelper.goUrl((Activity) mCallback.getContext(), item.getUrls());
                    }
                }
            });
        }
    }

    @Override
    protected int getLayoutId(int position, Apps item) {
        return R.layout.list_item_other_app;
    }
}
