package top.slantech.yzlibrary.utils;

import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.util.Log;

import java.io.DataOutputStream;
import java.io.File;

import top.slantech.yzlibrary.interfaces.OnGetDataLinstener;

/**
 * 系统设置工具类
 * 功能描述：
 * 1、Gps是否打开 isGpsEnabled(Activity);
 * 2、调用系统分享功能 分享文本 systemShareText(Activity,content,title);
 * 3、调用系统分享功能 分享图片 systemShareImg(Activity,imgPath,title);
 * 4、是否root isRootSystem();
 * 5、是否root getRootAhth();
 * 6、是否打开wifi需配置权限
 */
public class SettingUtils {

    /**
     * Gps是否打开
     * 需要<uses-permission android:name="android.permission.ACCESS_FINE_LOCATION" />权限
     *
     * @param context the context
     * @return true, if is gps enabled
     */
    public static boolean isGpsEnabled(Context context) {
        LocationManager lm = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        return lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }
    /**
     * 调用系统分享功能 分享文本
     *
     * @param context 操作对象
     * @param content 内容
     * @param title   标题
     */
    public static void systemShareText(Context context, String content, String title) {
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, content);
        sendIntent.setType("text/plain");
        context.startActivity(Intent.createChooser(sendIntent, title));
    }

    /**
     * 调用系统分享功能 分享图片
     *
     * @param context 操作对象
     * @param imgPath 图片路径
     * @param title   标题
     */
    public static void systemShareImg(Context context, String imgPath, String title) {
        File f = new File(imgPath);
        if (f.exists()) {
            Uri u = Uri.fromFile(f);
            Intent shareIntent = new Intent();
            shareIntent.setAction(Intent.ACTION_SEND);
            shareIntent.putExtra(Intent.EXTRA_STREAM, u);
            shareIntent.setType("image/png");
            context.startActivity(Intent.createChooser(shareIntent, title));
        }
    }

    private final static int kSystemRootStateUnknow = -1;
    private final static int kSystemRootStateDisable = 0;
    private final static int kSystemRootStateEnable = 1;
    private static int systemRootState = kSystemRootStateUnknow;

    /**
     * 是否root 1
     *
     * @return
     */
    public static boolean isRootSystem() {
        if (systemRootState == kSystemRootStateEnable) {
            return true;
        } else if (systemRootState == kSystemRootStateDisable) {
            return false;
        }
        File f = null;
        final String kSuSearchPaths[] = {"/system/bin/", "/system/xbin/",
                "/system/sbin/", "/sbin/", "/vendor/bin/"};
        try {
            for (int i = 0; i < kSuSearchPaths.length; i++) {
                f = new File(kSuSearchPaths[i] + "su");
                if (f != null && f.exists()) {
                    systemRootState = kSystemRootStateEnable;
                    return true;
                }
            }
        } catch (Exception e) {
        }
        systemRootState = kSystemRootStateDisable;
        return false;
    }

    /**
     * 是否root 2
     *
     * @return
     */
    public static synchronized void isRootSystem2(final OnGetDataLinstener linstener) {
        new Thread() {
            @Override
            public void run() {
                super.run();
                Process process = null;
                DataOutputStream os = null;
                try {
                    process = Runtime.getRuntime().exec("su");
                    os = new DataOutputStream(process.getOutputStream());
                    os.writeBytes("exit\n");
                    os.flush();
                    int exitValue = process.waitFor();
                    if (exitValue == 0) {
                        linstener.onBack(true);
                    } else {
                        linstener.onBack(false);
                    }
                } catch (Exception e) {
                    linstener.onBack(false);
                } finally {
                    try {
                        if (os != null) {
                            os.close();
                        }
                        process.destroy();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }.start();
    }

    /**
     * 是否root 2
     *
     * @return
     */
    public static synchronized boolean getRootAhth() {
        Process process = null;
        DataOutputStream os = null;
        try {
            process = Runtime.getRuntime().exec("su");
            os = new DataOutputStream(process.getOutputStream());
            os.writeBytes("exit\n");
            os.flush();
            int exitValue = process.waitFor();
            if (exitValue == 0) {
                return true;
            } else {
                return false;
            }
        } catch (Exception e) {
            Log.d("*** DEBUG ***", "Unexpected error - Here is what I know: "
                    + e.getMessage());
            return false;
        } finally {
            try {
                if (os != null) {
                    os.close();
                }
                process.destroy();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 检查wifi是否打开
     * @param context
     * @return
     */
    public static boolean isWifiOpened(Context context) {
        try {
            WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
            return wifiManager.isWifiEnabled();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}
