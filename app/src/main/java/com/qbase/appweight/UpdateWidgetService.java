package com.qbase.appweight;

import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

/**
 * Create by qay on 2018/11/30
 */
public class UpdateWidgetService extends Service {


    public UpdateWidgetService() {
    }

    private static final String TAG = "---";

    // 更新 widget 的广播对应的action
    private final String ACTION_UPDATE_ALL = "com.skywang.widget.UPDATE_ALL";
    // 周期性更新 widget 的周期
    private static final int UPDATE_TIME = 10000;
    // 周期性更新 widget 的线程
    private UpdateThread mUpdateThread;
    private Context mContext;
    // 更新周期的计数
    private int count = 0;

    @Override
    public void onCreate() {
        Log.d(TAG, "UpdateWidgetService onCreate");
        // 创建并开启线程UpdateThread
        mUpdateThread = new UpdateThread();
        mUpdateThread.start();

        mContext = this.getApplicationContext();
        super.onCreate();
    }

    @Override
    public void onDestroy() {
        // 中断线程，即结束线程。
        if (mUpdateThread != null) {
            mUpdateThread.interrupt();
        }
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    /*
     * 服务开始时，即调用startService()时，onStartCommand()被执行。
     */
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "UpdateWidgetService onStartCommand");
        super.onStartCommand(intent, flags, startId);

        return START_STICKY;
    }

    private class UpdateThread extends Thread {

        @Override
        public void run() {
            super.run();
            try {
                count = 0;
                while (true) {
                    Log.d(TAG, "UpdateWidgetService run ... count:" + count);
                    count++;
                    Bundle bundle = new Bundle();
                    ComponentName thisName = new ComponentName(mContext, MyWidgetProvider.class);
                    AppWidgetManager manager = AppWidgetManager.getInstance(mContext);
                    int[] ids = manager.getAppWidgetIds(thisName);
                    bundle.putIntArray(AppWidgetManager.EXTRA_APPWIDGET_IDS, ids);
                    Intent updateIntent = new Intent(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
                    updateIntent.putExtras(bundle);
                    mContext.sendBroadcast(updateIntent);
                    Thread.sleep(UPDATE_TIME);
                }
            } catch (InterruptedException e) {
                // 将 InterruptedException 定义在while循环之外，意味着抛出 InterruptedException 异常时，终止线程。
                e.printStackTrace();
            }
        }
    }
}
