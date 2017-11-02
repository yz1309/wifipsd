package top.slantech.wifipsd.services;


import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.NotificationCompat;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import top.slantech.wifipsd.R;


/**
 * 版本更新下载 需在清单文件中注册
 * slantech
 */
public class DownService extends Service {
    //apk文件保存位置
    private String mFilePath;
    //传递的文件名称
    private String mFileName = "";
    //下载地址
    private String mDownURL = "";
    //文件存储
    private File updateDir = null;
    private File updateFile = null;

    private NotificationManager updateNotificationManager = null;
    private NotificationCompat.Builder mBuilder = null;

    //下载状态
    private final static int DOWNLOAD_COMPLETE = 0;
    private final static int DOWNLOAD_FAIL = 1;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        mFilePath = intent.getStringExtra("filepath");
        mFileName = intent.getStringExtra("apkname");
        mDownURL = intent.getStringExtra("downurl");


        //创建文件
        if (android.os.Environment.MEDIA_MOUNTED.equals(android.os.Environment.getExternalStorageState())) {
            updateDir = new File(mFilePath);
            updateFile = new File(updateDir.getPath(), mFileName + ".apk");
        }
        this.updateNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        mBuilder = new NotificationCompat.Builder(this);
        mBuilder.setContentTitle(getString(R.string.new_version)+mFileName + ".apk").
                setProgress(100, 0, false).setSmallIcon(R.mipmap.ic_launcher);

        //发出通知
        updateNotificationManager.notify(0, mBuilder.build());


        //开启一个新的线程下载，如果使用Service同步下载，会导致ANR问题，Service本身也会阻塞
        new Thread(new updateRunnable()).start();//这个是下载的重点，是下载的过程
        return super.onStartCommand(intent, flags, startId);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    // 下载
    class updateRunnable implements Runnable {
        Message message = updateHandler.obtainMessage();

        public void run() {
            message.what = DOWNLOAD_COMPLETE;
            try {
                //增加权限;
                if (!updateDir.exists()) {
                    updateDir.mkdirs();
                }
                if (!updateFile.exists()) {
                    updateFile.createNewFile();
                }
                //增加权限;
                long downloadSize = downloadUpdateFile(mDownURL, updateFile);
                if (downloadSize > 0) {
                    //下载成功
                    updateHandler.sendMessage(message);
                }
            } catch (Exception ex) {
                ex.printStackTrace();
                message.what = DOWNLOAD_FAIL;
                //下载失败
                updateHandler.sendMessage(message);
            }
        }
    }

    public long downloadUpdateFile(String downloadUrl, File saveFile) throws Exception {
        //这样的下载代码很多，我就不做过多的说明
        int downloadCount = 0;
        int currentSize = 0;
        long totalSize = 0;
        int updateTotalSize = 0;
        HttpURLConnection httpConnection = null;
        InputStream is = null;
        FileOutputStream fos = null;
        try {
            URL url = new URL(downloadUrl);
            httpConnection = (HttpURLConnection) url.openConnection();
            httpConnection.setRequestProperty("User-Agent", "PacificHttpClient");
            if (currentSize > 0) {
                httpConnection.setRequestProperty("RANGE", "bytes=" + currentSize + "-");
            }
            httpConnection.setConnectTimeout(10000);
            httpConnection.setReadTimeout(20000);
            updateTotalSize = httpConnection.getContentLength();
            if (httpConnection.getResponseCode() == 404) {
                throw new Exception("fail!");
            }
            is = httpConnection.getInputStream();
            fos = new FileOutputStream(saveFile, false);
            byte buffer[] = new byte[4096];
            int readsize = 0;
            while ((readsize = is.read(buffer)) > 0) {
                fos.write(buffer, 0, readsize);
                totalSize += readsize;

                mBuilder.setContentTitle(getString(R.string.new_version)+mFileName + ".apk").setProgress(100, (int) (totalSize * 100 / updateTotalSize), false).setSmallIcon(R.mipmap.ic_launcher);

                //发出通知
                updateNotificationManager.notify(0, mBuilder.build());

            }
        } finally {
            if (httpConnection != null) {
                httpConnection.disconnect();
            }
            if (is != null) {
                is.close();
            }
            if (fos != null) {
                fos.close();
            }
        }
        return totalSize;
    }

    //下载状态处理主线程
    private Handler updateHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case DOWNLOAD_COMPLETE:

                    Intent intent = new Intent();
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.setAction(Intent.ACTION_VIEW);
                    intent.setDataAndType(Uri.fromFile(updateFile),
                            "application/vnd.android.package-archive");
                    startActivity(intent);

                    mBuilder.setContentTitle(getString(R.string.down_suc)+mFileName + ".apk").setProgress(100, 100, false).setSmallIcon(R.mipmap.ic_launcher);
                    //发出通知
                    updateNotificationManager.notify(0, mBuilder.build());
                    //停止服务
                    stopSelf();
                    break;
                case DOWNLOAD_FAIL:
                    mBuilder.setContentTitle(getString(R.string.down_failed)+mFileName + ".apk").setProgress(100, 100, false).setSmallIcon(R.mipmap.ic_launcher);
                    //发出通知
                    updateNotificationManager.notify(0, mBuilder.build());
                    break;
                default:
                    stopSelf();
            }
        }
    };
}
