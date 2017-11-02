package top.slantech.yzlibrary.activity;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.v4.app.ActivityCompat;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Iterator;
import java.util.List;

import top.slantech.yzlibrary.R;
import top.slantech.yzlibrary.utils.CheckUtils;
import top.slantech.yzlibrary.utils.ULog;


/**
 * author:slantech
 * 功能介绍：
 * 1、自定义Toast显示 cusToast(Activity,"提示内容",0);
 * 2、获取当前应用包信息 getPackageInfo(Activity);
 * 3、检查服务是否正在运行，通过服务名称 isServiceRunning(Activity,"com.xxx.xx..XXXService");
 * 4、检查是否连续按了2次返回键 isDoubleTouch();
 */
public class BaseApplication extends Application {


    private static BaseApplication appContext;
    @Override
    public void onCreate() {
        super.onCreate();

        appContext = this;
        ULog.tag = "ULog";
        ULog.isOpen = true;



    }
    public static BaseApplication getInstance() {
        return appContext;
    }


    /**
     * 自定义Toast显示
     *
     * @param context Activity
     * @param mes     消息内容
     * @param times   1 Long 0 Short
     */
    public static void cusToast(Activity context, String mes, int times) {
        //获取LayoutInflater对象，该对象能把XML文件转换为与之一直的View对象
        LayoutInflater inflater = context.getLayoutInflater();
        //根据指定的布局文件创建一个具有层级关系的View对象
        //第二个参数为View对象的根节点，即LinearLayout的ID
        View layout = inflater.inflate(R.layout.custom_toast, (ViewGroup) context.findViewById(R.id.llToast));
        //查找ImageView控件
        TextView text = (TextView) layout.findViewById(R.id.tvMes);
        text.setText(mes);
        Toast toast = new Toast(context);
        //设置Toast的位置
        // Y -100 剧中的上面100
        toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
        toast.setDuration(times);
        //让Toast显示为我们自定义的样子
        toast.setView(layout);
        toast.show();
    }


    /**
     * 获取包信息.
     *
     * @param context the context
     */
    public static PackageInfo getPackageInfo(Context context) {
        PackageInfo info = null;
        try {
            String packageName = context.getPackageName();
            info = context.getPackageManager().getPackageInfo(packageName, 0);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return info;
    }


    /**
     * 用来判断服务是否运行.
     *
     * @param ctx       the ctx
     * @param className 判断的服务名字 "com.xxx.xx..XXXService"
     * @return true 在运行 false 不在运行
     */
    public static boolean isServiceRunning(Context ctx, String className) {
        boolean isRunning = false;
        ActivityManager activityManager = (ActivityManager) ctx.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningServiceInfo> servicesList = activityManager.getRunningServices(Integer.MAX_VALUE);
        Iterator<ActivityManager.RunningServiceInfo> l = servicesList.iterator();
        while (l.hasNext()) {
            ActivityManager.RunningServiceInfo si = (ActivityManager.RunningServiceInfo) l.next();
            if (className.equals(si.service.getClassName())) {
                isRunning = true;
            }
        }
        return isRunning;
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


    /**
     * 拨打电话
     *  <uses-permission android:name="android.permission.CALL_PHONE"/>
     * @param context
     * @param phone
     * @param type    =1 表示直接拨号 =2 表示手动拨号
     * @return
     */
    public static String getCall(Context context, String phone, int type) {
        if (CheckUtils.isMobileNo(phone)) {
            if (type == 1) {
                Intent intent = new Intent(Intent.ACTION_CALL);
                intent.setData(Uri.parse("tel:" + phone));
                if (ActivityCompat.checkSelfPermission(context, android.Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                    return "";
                }
                context.startActivity(intent);
            } else {
                Intent intent = new Intent(Intent.ACTION_DIAL);
                Uri data = Uri.parse("tel:" + phone);
                intent.setData(data);
                context.startActivity(intent);
            }
            return "";
        } else {
            return "号码不正确";
        }
    }

}
