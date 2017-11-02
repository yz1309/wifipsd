package top.slantech.wifipsd.utils;

import android.app.Activity;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import top.slantech.wifipsd.R;


/**
 * Created by admin on 2016/7/17 0017.
 */
public class TitleUtils {
    static LinearLayout llBack, llRight;
    static TextView tvTopTitle,tvRight;
    static ImageView ivRight;


    public static void Init(final Activity activity, String title) {
        llBack = (LinearLayout) activity.findViewById(R.id.ll_back);
        tvTopTitle = (TextView) activity.findViewById(R.id.tv_top_title);
        if (!TextUtils.isEmpty(title))
            tvTopTitle.setText(title);

        llBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.finish();
            }
        });
    }

    public static void setRightIVVisible(final Activity activity, Boolean bl) {
        ivRight = (ImageView) activity.findViewById(R.id.iv_right);
        if (bl)
            ivRight.setVisibility(View.VISIBLE);
        else
            ivRight.setVisibility(View.INVISIBLE);
    }

    public static void setRightTVVisible(final Activity activity, Boolean bl) {
        tvRight = (TextView) activity.findViewById(R.id.tv_right);
        if (bl)
            tvRight.setVisibility(View.VISIBLE);
        else
            tvRight.setVisibility(View.INVISIBLE);
    }


}
