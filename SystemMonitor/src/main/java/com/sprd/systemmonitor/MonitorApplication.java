package com.sprd.systemmonitor;

import android.app.Application;
import android.view.WindowManager;

/**
 * Created by SPREADTRUM\joe.yu on 6/8/15.
 */
public class MonitorApplication extends Application{
    private static final int MONITOR_COUNT = 3;
    private WindowManager.LayoutParams [] mWindowParms = new WindowManager.LayoutParams [MONITOR_COUNT];

    @Override
    public void onCreate() {
        super.onCreate();
        for(int i = 0; i <MONITOR_COUNT; i++){
            mWindowParms[i] = new WindowManager.LayoutParams();
        }
    }

    public WindowManager.LayoutParams getWindowParms(int type){
        return mWindowParms[type];
    }
}
