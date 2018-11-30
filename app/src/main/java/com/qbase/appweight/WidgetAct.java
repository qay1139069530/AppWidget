package com.qbase.appweight;

import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.RemoteViews;

public class WidgetAct extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_widget);
    }


    private int mAppWidgetId;

    public void onTestBack(View view){
        RemoteViews remoteViews=new RemoteViews(getPackageName(), R.layout.app_weidget);
        remoteViews.setTextViewText(R.id.txt, "Activity 点击");
        ComponentName thisName=new ComponentName(WidgetAct.this, MyWidgetProvider.class);
        AppWidgetManager manager=AppWidgetManager.getInstance(WidgetAct.this);
        manager.updateAppWidget(thisName, remoteViews);

    }

    public void onTestBack2(View view){
        Bundle bundle = new Bundle();
        ComponentName thisName=new ComponentName(WidgetAct.this, MyWidgetProvider.class);
        AppWidgetManager manager=AppWidgetManager.getInstance(WidgetAct.this);
        int[] ids =  manager.getAppWidgetIds(thisName);
        bundle.putIntArray(AppWidgetManager.EXTRA_APPWIDGET_IDS,ids);
        Intent updateIntent=new Intent(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
        updateIntent.putExtras(bundle);
        sendBroadcast(updateIntent);
    }
}
