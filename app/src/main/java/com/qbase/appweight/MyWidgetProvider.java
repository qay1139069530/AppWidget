package com.qbase.appweight;

import android.app.ActivityManager;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.RemoteViews;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Create by qay on 2018/11/29
 * AppWidgetManger 类
 * <p>
 * bindAppWidgetId(int appWidgetId, ComponentName provider)
 * 通过给定的ComponentName 绑定appWidgetId
 * getAppWidgetIds(ComponentName provider)
 * 通过给定的ComponentName 获取AppWidgetId
 * getAppWidgetInfo(int appWidgetId)
 * 通过AppWidgetId 获取 AppWidget 信息
 * getInstalledProviders()
 * 返回一个List<AppWidgetProviderInfo>的信息
 * getInstance(Context context)
 * 获取 AppWidgetManger 实例使用的上下文对象
 * updateAppWidget(int[] appWidgetIds, RemoteViews views)
 * 通过appWidgetId 对传进来的 RemoteView 进行修改，并重新刷新AppWidget 组件
 * updateAppWidget(ComponentName provider, RemoteViews views)
 * 通过 ComponentName 对传进来的 RemoeteView 进行修改，并重新刷新AppWidget 组件
 * updateAppWidget(int appWidgetId, RemoteViews views)
 * 通过appWidgetId 对传进来的 RemoteView 进行修改，并重新刷新AppWidget 组件
 */
public class MyWidgetProvider extends AppWidgetProvider {


    public static final String ACTION_WIDGET = "com.qbase.appweight.MyWidgetProvider";
    public static String ACTION_SERVICE = "com.qbase.appweight.UpdateWidgetService";
    int i = 0;

    private Timer mTimer;

    private Handler mHandler = new Handler();

    public MyWidgetProvider() {
        super();
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
        i++;
        Log.e("----", "onReceive  i: " + i);
        if (intent.getAction().equals(ACTION_WIDGET)) {
            RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.app_weidget);
            remoteViews.setTextViewText(R.id.txt, "Success  " + i);
            ComponentName thisName = new ComponentName(context, MyWidgetProvider.class);
            AppWidgetManager manager = AppWidgetManager.getInstance(context);
            manager.updateAppWidget(thisName, remoteViews);
        } else if (intent.getAction().equals(ACTION_SERVICE)) {
//            Intent startUpdateIntent = new Intent(context, UpdateWidgetService.class);
//            context.startService(startUpdateIntent);
        }

        if (!isServiceWork(context, UpdateWidgetService.class.getName())) {
            Intent startUpdateIntent = new Intent(context, UpdateWidgetService.class);
            context.startService(startUpdateIntent);
        }
    }

    // onUpdate() 在更新 widget 时，被执行，
    @Override
    public void onUpdate(final Context context, final AppWidgetManager appWidgetManager, final int[] appWidgetIds) {
        super.onUpdate(context, appWidgetManager, appWidgetIds);
        Log.e("----", "onUpdate");
        i++;
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < appWidgetIds.length; i++) {
                    onWidgetUpdate(context, appWidgetManager, appWidgetIds[i]);
                }
            }
        });
//        if (mTimer == null) {
//            mTimer = new Timer();
//            mTimer.schedule(new TimerTask() {
//                @Override
//                public void run() {
//                    i++;
//                    //RemoteViews remoteViews=new RemoteViews(context.getPackageName(), R.layout.app_weidget);
//                    //Intent intent=new Intent();
//                    //intent.setAction(ACTION_WIDGET);
//                    //PendingIntent pendingIntent=PendingIntent.getBroadcast(context, 0, intent, 0);
//                    //remoteViews.setOnClickPendingIntent(R.id.btn, pendingIntent);
////                    Bundle bundle = new Bundle();
////                    bundle.putInt("key_value", i);
////                    //appWidgetManager.updateAppWidget(appWidgetIds, remoteViews);
////                    appWidgetManager.updateAppWidgetOptions(appWidgetIds[0], bundle);
//
//                    mHandler.post(new Runnable() {
//                        @Override
//                        public void run() {
//                            for (int i = 0; i < appWidgetIds.length; i++) {
//                                onWidgetUpdate(context, appWidgetManager, appWidgetIds[i]);
//                            }
//                        }
//                    });
//                }
//            }, 0, 1000);
//        }
    }

    // 当 widget 被初次添加 或者 当 widget 的大小被改变时，被调用
    @Override
    public void onAppWidgetOptionsChanged(Context context, AppWidgetManager appWidgetManager, int appWidgetId, Bundle newOptions) {
        super.onAppWidgetOptionsChanged(context, appWidgetManager, appWidgetId, newOptions);
        Log.e("----", "onAppWidgetOptionsChanged");
    }


    private void onWidgetUpdate(Context context, AppWidgetManager appWidgetManager, int appWidgetId) {
        RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.app_weidget);

        //设置点击事件
        Intent intent = new Intent(ACTION_WIDGET);
        PendingIntent pIntent = PendingIntent.getBroadcast(context, 0, intent, 0);
        remoteViews.setOnClickPendingIntent(R.id.btn, pIntent);


        //设置点击事件
        Intent intent2 = new Intent(ACTION_SERVICE);
        PendingIntent pIntent2 = PendingIntent.getService(context, 0, intent2, 0);
        remoteViews.setOnClickPendingIntent(R.id.service, pIntent2);

        //更新部件文本
        remoteViews.setTextViewText(R.id.txt, "结果 ： " + i);
        //更新部件
        appWidgetManager.updateAppWidget(appWidgetId, remoteViews);
    }


    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {
        super.onDeleted(context, appWidgetIds);
        Log.e("----", "onDeleted");
        if (mTimer != null) {
            mTimer.cancel();
        }
        mTimer = null;
    }

    //开始添加的挂件的时候跑的方法，注意在android机制中同一个挂件可以添加多次，
    // 这个只有第一次添加该挂件时才会跑的方法
    @Override
    public void onEnabled(Context context) {
        super.onEnabled(context);
        Log.e("----", "onEnabled");
        //当Widget第一次创建的时候，该方法调用，然后启动后台的服务
        Intent startUpdateIntent = new Intent(context, UpdateWidgetService.class);
        context.startService(startUpdateIntent);
    }

    //当把桌面上的Widget全部都删掉的时候，调用该方法
    @Override
    public void onDisabled(Context context) {
        super.onDisabled(context);
        Log.e("----", "onDisabled");
        Intent stopUpdateIntent = new Intent(context, UpdateWidgetService.class);
        context.stopService(stopUpdateIntent);
    }

    @Override
    public void onRestored(Context context, int[] oldWidgetIds, int[] newWidgetIds) {
        super.onRestored(context, oldWidgetIds, newWidgetIds);
        Log.e("----", "onRestored");
    }

    /**
     * 判断某个服务是否正在运行的方法
     *
     * @param mContext
     * @param serviceName 是包名+服务的类名（例如：net.loonggg.testbackstage.TestService）
     * @return true代表正在运行，false代表服务没有正在运行
     */
    public boolean isServiceWork(Context mContext, String serviceName) {
        boolean isWork = false;
        ActivityManager myAM = (ActivityManager) mContext
                .getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningServiceInfo> myList = myAM.getRunningServices(40);
        if (myList.size() <= 0) {
            return false;
        }
        for (int i = 0; i < myList.size(); i++) {
            String mName = myList.get(i).service.getClassName().toString();
            if (mName.equals(serviceName)) {
                isWork = true;
                break;
            }
        }
        return isWork;
    }

}
