package top.slantech.wifipsd.common;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ProviderInfo;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.support.v7.app.AlertDialog;
import android.view.Window;
import android.view.WindowManager;

import com.readystatesoftware.systembartint.SystemBarTintManager;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.util.List;

import okhttp3.Call;
import top.slantech.wifipsd.MyApplication;
import top.slantech.wifipsd.R;
import top.slantech.wifipsd.api.ApiHelper;
import top.slantech.wifipsd.bean.AppVersion;
import top.slantech.wifipsd.bean.SysAppAds;
import top.slantech.wifipsd.services.DownService;
import top.slantech.wifipsd.ui.activity.ViewPwdActivity;
import top.slantech.yzlibrary.utils.CalendarTool;
import top.slantech.yzlibrary.utils.SPUtils;
import top.slantech.yzlibrary.utils.StringUtils;
import top.slantech.yzlibrary.utils.ULog;


/**
 * 应用程序UI工具包：封装UI相关的一些操作
 */
public class UIHelper {

    public static void initSystemBar(Activity activity, int colorId) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            setTranslucentStatus(activity, true);
        }
        SystemBarTintManager tintManager = new SystemBarTintManager(activity);
        tintManager.setStatusBarTintEnabled(true);
        // 使用颜色资源
        tintManager.setStatusBarTintResource(colorId);
    }

    @TargetApi(19)
    private static void setTranslucentStatus(Activity activity, boolean on) {
        Window win = activity.getWindow();
        WindowManager.LayoutParams winParams = win.getAttributes();
        final int bits = WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
        if (on) {
            winParams.flags |= bits;
        } else {
            winParams.flags &= ~bits;
        }
        win.setAttributes(winParams);
    }

    /**
     * 打开市场
     *
     * @param context
     */
    public static void openAppInMarket(Context context) {
        if (context != null) {
            String pckName = context.getPackageName();
            try {
                gotoMarket(context, pckName);
            } catch (Exception ex) {
                try {
                    String otherMarketUri = "http://market.android.com/details?id="
                            + pckName;
                    Intent intent = new Intent(Intent.ACTION_VIEW,
                            Uri.parse(otherMarketUri));
                    context.startActivity(intent);
                } catch (Exception e) {

                }
            }
        }
    }

    public static void gotoMarket(Context context, String pck) {
        if (!isHaveMarket(context)) {
            MyApplication.getInstance().cusToast((Activity) context, context.getString(R.string.not_set_up_market), 0);
            return;
        }
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        intent.setData(Uri.parse("market://details?id=" + pck));
        if (intent.resolveActivity(context.getPackageManager()) != null) {
            context.startActivity(intent);
        }
    }

    public static boolean isHaveMarket(Context context) {
        Intent intent = new Intent();
        intent.setAction("android.intent.action.MAIN");
        intent.addCategory("android.intent.category.APP_MARKET");
        PackageManager pm = context.getPackageManager();
        List<ResolveInfo> infos = pm.queryIntentActivities(intent, 0);
        return infos.size() > 0;
    }


    /**
     * 发起添加群流程。群号：石兰科技(234661863) 的 key 为： OCXYv02sdKWACiNWvC3EjxFhQPmIwx2h
     * 调用 joinQQGroup(OCXYv02sdKWACiNWvC3EjxFhQPmIwx2h) 即可发起手Q客户端申请加群 石兰科技(234661863)
     *
     * @param key 由官网生成的key
     * @return 返回true表示呼起手Q成功，返回fals表示呼起失败
     */
    public static boolean joinQQGroup(Context context, String key) {
        Intent intent = new Intent();
        intent.setData(Uri.parse("mqqopensdkapi://bizAgent/qm/qr?url=http%3A%2F%2Fqm.qq.com%2Fcgi-bin%2Fqm%2Fqr%3Ffrom%3Dapp%26p%3Dandroid%26k%3D" + key));
        // 此Flag可根据具体产品需要自定义，如设置，则在加群界面按返回，返回手Q主界面，不设置，按返回会返回到呼起产品界面    //intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        try {
            context.startActivity(intent);
            return true;
        } catch (Exception e) {
            // 未安装手Q或安装的版本不支持
            return false;
        }
    }


    /**
     * 在浏览器中打开指定的URL
     *
     * @param context
     * @param url
     */
    public static void goUrl(Activity context, String url) {
        try {
            Intent intent = new Intent();
            intent.setAction("android.intent.action.VIEW");
            // path 包含 http:
            Uri content_uri_browsers = Uri.parse(url);
            intent.setData(content_uri_browsers);
            context.startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Activity 跳转
     *
     * @param context
     * @param tClass
     */
    public static void goActivity(Activity context, Class<?> tClass) {
        Intent intent = new Intent(context, tClass);
        context.startActivity(intent);
    }

    /**
     * Activity 跳转
     *
     * @param context
     * @param intent
     */
    public static void goActivity(Activity context, Intent intent) {
        context.startActivity(intent);
    }


    /**
     * Activity 跳转
     *
     * @param context
     * @param tClass
     */
    public static void goActivityResult(Activity context, Class<?> tClass, int code) {
        Intent intent = new Intent(context, tClass);
        context.startActivityForResult(intent, code);
    }

    /**
     * 参数值附
     *
     * @param sc  分数 小数点只保留2位数
     * @param val 比对值
     * @return
     */
    public static String getTxtScores(double sc, int val) {
        if (sc > val) {
            String rs = StringUtils.getDoubleXS(sc + "", 2) + "";
            // 23.0|23.00|23.10
            if ((rs + "").endsWith(".0")) {
                rs = rs.replace(".0", "");
            } else if ((rs + "").endsWith(".00")) {
                rs = rs.replace(".00", "");
            } else if ((rs + "").endsWith("0")) {
                rs = rs.replace("0", "");
            }
            return rs;
        } else {
            return "..";
        }
    }


    /**
     * 参数值附
     *
     * @param sc  分数 小数点只保留2位数
     * @param val 比对值
     * @return
     */
    public static String getTxtScores2(double sc, int val) {
        if (sc > val) {
            String rs = StringUtils.getDoubleXS(sc + "", 2) + "";
            // 23.0|23.00|23.10
            if ((rs + "").endsWith(".0")) {
                rs = rs.replace(".0", "");
            } else if ((rs + "").endsWith(".00")) {
                rs = rs.replace(".00", "");
            } else if ((rs + "").endsWith("0")) {
                rs = rs.replace("0", "");
            }
            return rs;
        } else {
            return "";
        }
    }

    /**
     * 参数值附
     *
     * @param sc 分数 小数点只保留2位数
     * @return
     */
    public static String getTxtScores(double sc) {
        String rs = StringUtils.getDoubleXS(sc + "", 2) + "";
        // 23.0|23.00|23.10
        if ((rs + "").endsWith(".0")) {
            rs = rs.replace(".0", "");
        } else if ((rs + "").endsWith(".00")) {
            rs = rs.replace(".00", "");
        } else if ((rs + "").endsWith("0")) {
            rs = rs.replace("0", "");
        }
        return rs;
    }

    /**
     * 参数值附
     *
     * @param sc  分数
     * @param val 比对值
     * @return
     */
    public static String getIntTxtScores(int sc, int val) {
        if (sc > val) {
            return sc + "";
        } else {
            return "..";
        }
    }



    /**
     * 添加快捷方式[注意权限添加]
     *
     * @param context
     */
    public static void addShortcut(Activity context, String name, Class<?> cl) {
        try {

            Intent addShortcutIntent = new Intent(DataConfig.ACTION_ADD_SHORTCUT);

            // 不允许重复创建
            addShortcutIntent.putExtra("duplicate", false);// 经测试不是根据快捷方式的名字判断重复的
            // 应该是根据快链的Intent来判断是否重复的,即Intent.EXTRA_SHORTCUT_INTENT字段的value
            // 但是名称不同时，虽然有的手机系统会显示Toast提示重复，仍然会建立快链
            // 屏幕上没有空间时会提示
            // 注意：重复创建的行为MIUI和三星手机上不太一样，小米上似乎不能重复创建快捷方式

            // 名字
            addShortcutIntent.putExtra(Intent.EXTRA_SHORTCUT_NAME, name);

            // 图标
            addShortcutIntent.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE,
                    Intent.ShortcutIconResource.fromContext(context,
                            R.mipmap.ic_launcher));

            // 设置关联程序
            Intent launcherIntent = new Intent(Intent.ACTION_MAIN);
            launcherIntent.setClass(context, cl);
            launcherIntent.addCategory(Intent.CATEGORY_LAUNCHER);

            addShortcutIntent
                    .putExtra(Intent.EXTRA_SHORTCUT_INTENT, launcherIntent);

            // 发送广播
            context.sendBroadcast(addShortcutIntent);
            MyApplication.cusToast(context, context.getString(R.string.add_shortcut_suc), 0);
            SPUtils.put(context, "shortcut", 1);
        } catch (Exception ex) {
            ULog.e("添加快捷方式异常:" + ex.toString());
        }
    }

    /**
     * 检查快捷方式是否存在[注意权限添加]暂时取消此方法由于部分机型不行
     *
     * @param activity
     * @param shortcutName
     * @return
     */
    public static boolean hasShortcut(Activity activity, String shortcutName) {
        try {
            String url = "";
            // 6.0.1 getAuthorityFromPermission return null
            url = "content://" + getAuthorityFromPermission(activity, "com.android.launcher.permission.READ_SETTINGS") + "/favorites?notify=true";
            ULog.e("url-" + url);
            ContentResolver resolver = activity.getContentResolver();
            Cursor cursor = resolver.query(Uri.parse(url), new String[]{"title", "iconResource"}, "title=?", new String[]{shortcutName}, null);
            if (cursor != null && cursor.moveToFirst()) {
                cursor.close();
                return true;
            }
            return false;
        } catch (Exception e) {
            ULog.e("检查快捷方式异常：" + e.toString());
        }
        return true;
    }

    public static String getAuthorityFromPermission(Context context, String permission) {
        try {
            if (permission == null)
                return null;
            List<PackageInfo> packs = context.getPackageManager().getInstalledPackages(PackageManager.GET_PROVIDERS);
            if (packs != null) {
                for (PackageInfo pack : packs) {
                    ProviderInfo[] providers = pack.providers;
                    if (providers != null) {
                        for (ProviderInfo provider : providers) {
                            if (permission.equals(provider.readPermission))
                                return provider.authority;
                            if (permission.equals(provider.writePermission))
                                return provider.authority;
                        }
                    }
                }
            }
        } catch (Exception e) {
        }
        return null;
    }

    /**
     * 移除快捷方式[注意权限添加]
     *
     * @param context
     * @param name
     */
    public static void removeShortcut(Activity context, String name) {
        try {
            // remove shortcut的方法在小米系统上不管用，在三星上可以移除
            Intent intent = new Intent(DataConfig.ACTION_REMOVE_SHORTCUT);

            // 名字
            intent.putExtra(Intent.EXTRA_SHORTCUT_NAME, name);

            // 设置关联程序
            Intent launcherIntent = new Intent(context,
                    ViewPwdActivity.class).setAction(Intent.ACTION_MAIN);

            intent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, launcherIntent);

            // 发送广播
            context.sendBroadcast(intent);
        } catch (Exception ex) {
            ULog.e("移除快捷方式异常:" + ex.toString());
        }
    }


    /**
     * 检查版本更新
     *
     * @param context
     */
    public static void checkVersion(final Context context, final int type) {
        OkHttpUtils.get().url(ApiHelper.getInstance().getCheck(context))
                //.addParams("versionCode", 1 + "")
                .addParams("versionCode", MyApplication.getVersionCode(context) + "")
                .addParams("appType", MyApplication.mAppName)
                .build().execute(new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {

            }

            @Override
            public void onResponse(String response, int id) {
                try {
                    final AppVersion model = MyApplication.createGson().fromJson(response, AppVersion.class);
                    if (model != null) {
                        String content = "版本：" + model.getVersionName() + " 时间：" +
                                CalendarTool.formatDateStr2Desc(model.getAddtimes().replace("T", " "), CalendarTool.dateFormatYMDHM)
                                + "\r\n" + model.getRemark();
                        AlertDialog dialog = new AlertDialog.Builder(context)
                                .setTitle(context.getString(R.string.new_version))
                                .setMessage(content)
                                .setPositiveButton(context.getString(R.string.btn_ok), new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        //UIHelper.goUrl((Activity) context, model.getApkUrl());

                                        Intent intent = new Intent(context, DownService.class);
                                        intent.putExtra("filepath", MyApplication.getAPKPath());
                                        intent.putExtra("apkname", "slantech." + MyApplication.mAppName + "." + model.getVersionName());
                                        intent.putExtra("downurl", model.getApkUrl());
                                        context.startService(intent);
                                    }
                                })
                                .setNegativeButton(context.getString(R.string.btn_cancel), new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {

                                    }
                                }).show();

                    } else if (type == 2) {
                        MyApplication.cusToast((Activity) context, context.getString(R.string.cur_version_is_best), 1);
                    }
                } catch (Exception ex) {

                }
            }
        });
    }



    /**
     * 检查是否显示广告，显示哪个广告
     *
     * @param context
     * @param key
     * @param def
     * @return
     */
    public static SysAppAds checkIsShowAds(Context context, String key, int def) {
        SysAppAds model = new SysAppAds();
        model.setIsOpen(-1);
        model.setAdsPlatform(0);
        String firstKey = key + "_" + MyApplication.getVersionCode(context);
        // 检查是否是第一次安装应用 -1 不显示 0 显示
        int first = (int) SPUtils.get(context, firstKey, -1);
        if (first != -1) {
            // 检查服务端返回的控制 -1 不显示 0 显示
            int server = (int) SPUtils.get(context, key, def);
            if (server != -1) {
                model.setIsOpen(0);
            }
            int adsplatform = (int) SPUtils.get(context, key + "_platform", 0);
            model.setAdsPlatform(adsplatform);
        } else {
            // 如果 是第一次调用，那么将其值设置为已存在
            SPUtils.put(context, firstKey, 0);
        }

        return model;

    }

    /**
     * 向服务端请求是否打开or关闭广告
     *
     * @param context
     * @param adsType 0开屏，1信息流ListView，2信息流设置，3信息流详情，4视频
     * @param key
     */
    public static void getServerAds(final Context context, int adsType, final String key) {
        OkHttpUtils.get().url(ApiHelper.getInstance().getAppAds(context))
                .addParams("appType", MyApplication.mAppName)
                .addParams("adsType", adsType + "")
                .build().execute(new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {

            }

            @Override
            public void onResponse(String response, int id) {
                try {
                    SysAppAds model = MyApplication.createGson().fromJson(response, SysAppAds.class);
                    if (model != null && !StringUtils.isEmpty(model.getAdsappnum())) {
                        SPUtils.put(context, key, model.getIsOpen());
                        SPUtils.put(context, key + "_platform", model.getAdsPlatform());
                    }
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
