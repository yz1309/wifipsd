package top.slantech.wifipsd;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.os.Environment;
import android.support.multidex.MultiDex;

import com.awesome.android.sdk.publish.AwDebug;
import com.google.gson.Gson;
import com.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiscCache;
import com.nostra13.universalimageloader.cache.disc.naming.HashCodeFileNameGenerator;
import com.nostra13.universalimageloader.cache.memory.impl.LruMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.core.decode.BaseImageDecoder;
import com.nostra13.universalimageloader.core.download.BaseImageDownloader;
import com.zhy.http.okhttp.OkHttpUtils;

import java.io.File;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import top.slantech.wifipsd.utils.CrashHandlerUtils;
import top.slantech.yzlibrary.activity.BaseApplication;
import top.slantech.yzlibrary.utils.ULog;

/**
 * Created by admin on 2016/6/29 0029.
 */
public class MyApplication extends BaseApplication {
    private static MyApplication appContext;
    private static final String root = "slantech";
    public static final String mAppName = "wifipsd";//根据项目修改
    private static String mCrashPath = root + "/" + mAppName + "/crash/";
    private static String mLoaderPath = root + "/" + mAppName + "/imageloader/cache/";
    private static String mImagPath = root + "/" + mAppName + "/img/";
    private static String mFileDownPath = root + "/" + mAppName + "/down_file/";
    private static String mBackPath = root + "/" + mAppName + "/back_up/";
    private static String mApkPath = root + "/" + mAppName + "/apk/";

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    @Override
    public void onCreate() {
        super.onCreate();

        appContext = this;

        ULog.tag = "mcsson";
        ULog.isOpen = false;

        //AwDebug.runInDebugMode();//打开调试模式
        AwDebug.CloseDebugMode();//关闭调试模式

        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .connectTimeout(10000L, TimeUnit.MILLISECONDS)
                .readTimeout(10000L, TimeUnit.MILLISECONDS)
                //其他配置
                .build();

        OkHttpUtils.initClient(okHttpClient);
        /*OkHttpUtils
                .get()
                .url("http://www.slantech.top/api/urlapp/getapps2")
                .addParams("pageSize", "6")
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {

                    }

                    @Override
                    public void onResponse(String response, int id) {
                        Gson gson = new Gson();
                        List<urlapps> list = gson.fromJson(response, new TypeToken<List<urlapps>>() {
                        }.getType());
                        if (list != null)
                            Log.e("yz", list.toString());
                    }
                });*/

        initImageLoader();

        //捕捉未处理的 异常  start
        CrashHandlerUtils.getInstance().init(getApplicationContext(),getCrashFilePath());
        //捕捉未处理的 异常  end
    }


    public static MyApplication getInstance() {
        return appContext;
    }

    private void initImageLoader() {
        File cacheFile = new File(getLoaderFilePath());
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(
                this)
                .memoryCacheExtraOptions(400, 400)
                // default = device screen dimensions
                .diskCacheExtraOptions(400, 400, null)
                .threadPoolSize(5)
                // default Thread.NORM_PRIORITY - 1
                .threadPriority(Thread.NORM_PRIORITY)
                // default FIFO
                .tasksProcessingOrder(QueueProcessingType.LIFO)
                // default
                .denyCacheImageMultipleSizesInMemory()
                .memoryCache(new LruMemoryCache(2 * 1024 * 1024))
                .memoryCacheSize(2 * 1024 * 1024)
                .memoryCacheSizePercentage(13)
                // default
                .diskCache(
                        new UnlimitedDiscCache(cacheFile))

                // default
                .diskCacheSize(50 * 1024 * 1024).diskCacheFileCount(100)
                .diskCacheFileNameGenerator(new HashCodeFileNameGenerator())
                // default
                .imageDownloader(new BaseImageDownloader(this))
                // default
                .imageDecoder(new BaseImageDecoder(false))
                // default
                .defaultDisplayImageOptions(DisplayImageOptions.createSimple())
                // default
                .defaultDisplayImageOptions(getLogoOptions()).build();

        ImageLoader.getInstance().init(config);
    }

    public static DisplayImageOptions getLogoOptions() {
        DisplayImageOptions imageOptions = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.mipmap.ic_launcher)
                .showImageOnFail(R.mipmap.ic_launcher)
                .cacheInMemory(true).cacheOnDisk(true)
                .resetViewBeforeLoading(true).considerExifParams(false)
                .bitmapConfig(Bitmap.Config.RGB_565).build();

        return imageOptions;
    }

    public static DisplayImageOptions getSquareLoadingOptions() {
        DisplayImageOptions imageOptions = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.drawable.icon_loading_square)
                .showImageOnFail(R.drawable.icon_failed_square)
                .cacheInMemory(true).cacheOnDisk(true)
                .resetViewBeforeLoading(true).considerExifParams(false)
                .bitmapConfig(Bitmap.Config.RGB_565).build();

        return imageOptions;
    }

    public static DisplayImageOptions getRectLoadingOptions() {
        DisplayImageOptions imageOptions = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.drawable.icon_loading_rect)
                .showImageOnFail(R.drawable.icon_failed_rect)
                .cacheInMemory(true).cacheOnDisk(true)
                .resetViewBeforeLoading(true).considerExifParams(false)
                .bitmapConfig(Bitmap.Config.RGB_565).build();

        return imageOptions;
    }

    /**
     * 获取图片Load的路径
     *
     * @return 末尾不带/
     */
    public static String getLoaderFilePath() {
        String path = Environment.getExternalStorageDirectory() + "/" + mLoaderPath;
        File file = new File(path);
        if (!file.exists()) {
            file.mkdirs();
        }
        return file.getAbsolutePath();
    }

    /**
     * 获取apk下载的路径
     *
     * @return
     */
    public static String getAPKPath() {
        String path = Environment.getExternalStorageDirectory() + "/" + mApkPath;
        File file = new File(path);
        if (!file.exists()) {
            file.mkdirs();
        }
        return file.getAbsolutePath();
    }

    /**
     * 获取图片保存的路径
     *
     * @return 末尾不带/
     */
    public static String getImgFilePath() {
        String path = Environment.getExternalStorageDirectory() + "/" + mImagPath;
        File file = new File(path);
        if (!file.exists()) {
            file.mkdirs();
        }
        return file.getAbsolutePath();
    }

    /**
     * 获取Crash文件的路径
     *
     * @return 末尾不带/
     */
    public static String getCrashFilePath() {
        String path = Environment.getExternalStorageDirectory() + "/" + mCrashPath;
        File file = new File(path);
        if (!file.exists()) {
            file.mkdirs();
        }
        return file.getAbsolutePath();
    }

    /**
     * 获取文件下载的路径
     *
     * @return  末尾不带/
     */
    public static String getDownFilePath() {
        String path = Environment.getExternalStorageDirectory() + "/" + mFileDownPath;
        File file = new File(path);
        if (!file.exists()) {
            file.mkdirs();
        }
        return file.getAbsolutePath();
    }

    /**
     * 获取文件备份or恢复的路径
     *
     * @return 末尾不带/
     */
    public static String getBackUpFilePath() {
        String path = Environment.getExternalStorageDirectory() + "/" + mBackPath;
        File file = new File(path);
        if (!file.exists()) {
            file.mkdirs();
        }
        return file.getAbsolutePath();
    }

    public static String getVersionName(Context context) {
        PackageInfo packInfo = getPackageInfo(context);
        if (packInfo != null && packInfo.versionName != null) {
            return packInfo.versionName;
        }
        return "";
    }

    public static int getVersionCode(Context context) {
        PackageInfo packInfo = getPackageInfo(context);
        if (packInfo != null && packInfo.versionCode > 0) {
            return packInfo.versionCode;
        }
        return 0;
    }


    public static boolean checkBrowser(Context context, String packageName) {
        if (packageName == null || "".equals(packageName))
            return false;
        try {
            ApplicationInfo info = context.getPackageManager().getApplicationInfo(packageName, PackageManager.GET_UNINSTALLED_PACKAGES);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 启动包名 程序
     *
     * @param context
     * @param packagename
     */
    public static void doStartApplicationWithPackageName(Context context, String packagename) {
        // 通过包名获取此APP详细信息，包括Activities、services、versioncode、name等等
        PackageInfo packageinfo = null;
        try {
            packageinfo = context.getPackageManager().getPackageInfo(packagename, 0);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (packageinfo == null) {
            return;
        }

        // 创建一个类别为CATEGORY_LAUNCHER的该包名的Intent
        Intent resolveIntent = new Intent(Intent.ACTION_MAIN, null);
        resolveIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        resolveIntent.setPackage(packageinfo.packageName);

        // 通过getPackageManager()的queryIntentActivities方法遍历
        List<ResolveInfo> resolveinfoList = context.getPackageManager()
                .queryIntentActivities(resolveIntent, 0);

        ResolveInfo resolveinfo = resolveinfoList.iterator().next();
        if (resolveinfo != null) {
            // packagename = 参数packname
            String packageName = resolveinfo.activityInfo.packageName;
            // 这个就是我们要找的该APP的LAUNCHER的Activity[组织形式：packagename.mainActivityname]
            String className = resolveinfo.activityInfo.name;
            // LAUNCHER Intent
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_LAUNCHER);
            // 被启动程序退出后返回之前的程序？？

            // 设置ComponentName参数1:packagename参数2:MainActivity路径
            ComponentName cn = new ComponentName(packageName, className);

            intent.setComponent(cn);
            context.startActivity(intent);
        }
    }

    /**
     * 获取Gson实例
     *
     * @return
     */
    public static Gson createGson() {
        com.google.gson.GsonBuilder gsonBuilder = new com.google.gson.GsonBuilder();
        //gsonBuilder.setExclusionStrategies(new SpecificClassExclusionStrategy(null, Model.class));
        gsonBuilder.setDateFormat("yyyy-MM-dd HH:mm:ss");
        return gsonBuilder.create();
    }
}
