package top.slantech.wifipsd.services;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import com.igexin.sdk.GTServiceManager;

import top.slantech.yzlibrary.utils.ULog;


/**
 * 核心服务, 继承 android.app.Service, 必须实现以下几个接口, 并在 AndroidManifest 声明该服务并配置成
 * android:process=":pushservice",
 * PushManager.getInstance().initialize(this.getApplicationContext(), userPushService), 其中
 * userPushService 为 用户自定义服务 即 DemoPushService.
 */
public class DemoPushService extends Service {

    public static final String TAG = DemoPushService.class.getName();

    @Override
    public void onCreate() {
        // 该行日志在 release 版本去掉
        ULog.e( TAG + " call -> onCreate -------");

        super.onCreate();
        GTServiceManager.getInstance().onCreate(this);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // 该行日志在 release 版本去掉
        ULog.e( TAG + " call -> onStartCommand -------");

        super.onStartCommand(intent, flags, startId);
        return GTServiceManager.getInstance().onStartCommand(this, intent, flags, startId);
    }

    @Override
    public IBinder onBind(Intent intent) {
        // 该行日志在 release 版本去掉
        ULog.e( "onBind -------");
        return GTServiceManager.getInstance().onBind(intent);
    }

    @Override
    public void onDestroy() {
        // 该行日志在 release 版本去掉
        ULog.e( "onDestroy -------");

        super.onDestroy();
        GTServiceManager.getInstance().onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        GTServiceManager.getInstance().onLowMemory();
    }
}
