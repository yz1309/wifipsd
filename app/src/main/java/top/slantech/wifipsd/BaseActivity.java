package top.slantech.wifipsd;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;

import com.umeng.analytics.MobclickAgent;

import top.slantech.wifipsd.common.UIHelper;
import top.slantech.wifipsd.ui.activity.MessageActivity;
import top.slantech.wifipsd.ui.activity.ViewPwdActivity;
import top.slantech.yzlibrary.AppManager;


public abstract class BaseActivity extends Activity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /**
         * 默认设置系统状态栏颜色为绿色
         *
         android:clipToPadding="true"
         android:fitsSystemWindows="true"
         */
        if (this.getClass().getName().equals(MessageActivity.class.getName())
                ||
                this.getClass().getName().equals(MessageActivity.class.getName())
                ) {
            //
        } else
            UIHelper.initSystemBar(this, R.color.mainbg);
        //将activity加入到AppManager堆栈中
        AppManager.getAppManager().addActivity(this);
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (this.getClass().getName().equals(ViewPwdActivity.class.getName()) || this.getClass().getName().equals(ViewPwdActivity.class.getName())) {
                if (isDoubleTouch()) {
                    AppManager.getAppManager().AppExit(this);
                } else {
                    MyApplication.cusToast(this, "再点一次将退出当前应用", 0);
                }
            } else {
                finish();
            }
        }
        return false;
    }

    private long backCunt = 0;

    /***
     * 是否连续按了两次
     *
     * @return
     */
    public boolean isDoubleTouch() {
        if (backCunt == 0) {
            backCunt = System.currentTimeMillis();
            return false;
        }

        if (System.currentTimeMillis() - backCunt > 1000 * 5) {
            backCunt = 0;
            return false;
        } else {
            return true;
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        AppManager.getAppManager().removeActivity(this);

    }

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }


    /**
     * Activity 跳转
     *
     * @param tClass
     */
    public void goActivity(Class<?> tClass) {
        Intent intent = new Intent(this, tClass);
        startActivity(intent);
        // 此切换动画效果不能放到 thread 进程中 ，否则无效
        // 动画笑话 从右往左侧滑动
        overridePendingTransition(R.anim.scroll_in, R.anim.scroll_out);
    }

    /**
     * Activity 跳转
     *
     * @param intent
     */
    public void goActivity(Intent intent) {
        startActivity(intent);
        // 此切换动画效果不能放到 thread 进程中 ，否则无效
        // 动画笑话 从右往左侧滑动
        overridePendingTransition(R.anim.scroll_in, R.anim.scroll_out);
    }

    /**
     * 方便调用提示信息
     *
     * @param mes
     */
    public void showToast(String mes) {
        MyApplication.getInstance().cusToast(this, mes, 0);
    }

    /**
     * 方便调用提示信息
     *
     * @param mes
     */
    public void showToast2(String mes) {
        MyApplication.getInstance().cusToast(this, mes, 1);
    }

}
