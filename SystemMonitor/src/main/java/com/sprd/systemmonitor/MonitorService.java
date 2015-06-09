package com.sprd.systemmonitor;

import android.app.ActionBar;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.RelativeLayout;

/**
 * Created by SPREADTRUM\joe.yu on 6/5/15.
 */
public class MonitorService  extends Service{



    private static final int  MSG_UPDATE_CPU = 0;
    private static final int  MSG_UPDATE_DEVICEINFO = 1;


    private static final int  MONITOR_TYPE_CPU = 0;
    private static final int  MONITOR_TYPE_DEVICEINFO = 1;
    CpuMonitorView mCpuMonitorView;
    DeviceInfoMonitorView mDeviceInfoMonitorView;
    WindowManager mWindowManager;


    Handler mHandler;
    DeviceInfoStat mInfo = null;
    CpuInfoStat mCpuInfo = null;
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
    boolean isCpuMonitorEnabled(){
        return true;
    }
    boolean isDeviceInfoMonitorEnabled(){
        return true;
    }


    @Override
    public void onCreate() {
        super.onCreate();
        mWindowManager = (WindowManager)this.getSystemService(Context.WINDOW_SERVICE);
        mHandler = new MonitorHandler() ;
        if (mInfo == null) {
            mInfo = new DeviceInfoStat(this.getApplicationContext(), mHandler);
        }
        if(mCpuInfo == null){
            mCpuInfo = new CpuInfoStat(this.getApplicationContext(),mHandler);
        }
        setUpCPUMonitorView();
        setUpDeviceInfoMonitorView();
        startMonitor();
    }


    private void  startMonitor(){
        if(isCpuMonitorEnabled()){
            mCpuInfo.startMonitor();
        }
        if(isDeviceInfoMonitorEnabled()){
            mInfo.startMonitor();
        }
    }
    private void stopMonitor(){
        mInfo.stopMonitor();
        mCpuInfo.stopMonitor();
    }





    private void setUpDeviceInfoMonitorView(){
        mDeviceInfoMonitorView = (DeviceInfoMonitorView)View.inflate(this.getApplicationContext(),R.layout.deviceinfomonitor,null);
        mDeviceInfoMonitorView.init(this.getApplicationContext(),MONITOR_TYPE_DEVICEINFO);
        mDeviceInfoMonitorView.setUpView();
        WindowManager.LayoutParams parms;
        parms = ((MonitorApplication)this.getApplicationContext()).getWindowParms(MONITOR_TYPE_DEVICEINFO);
        parms.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;
        parms.format = PixelFormat.RGBA_8888;
        parms.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        parms.gravity = Gravity.LEFT | Gravity.TOP;
        parms.x = 0;
        parms.y = 0;
        parms.width = WindowManager.LayoutParams.WRAP_CONTENT;
        parms.height = WindowManager.LayoutParams.WRAP_CONTENT;
        //mDeviceInfoMonitorView.setImageResource(R.drawable.ic_launcher);
        mWindowManager.addView(mDeviceInfoMonitorView,parms);


    }
    private void setUpCPUMonitorView(){
        //mCpuMonitorView = new CpuMonitorView(this,MONITOR_TYPE_CPU,mCpuInfo.getCpuCount());
        mCpuMonitorView = (CpuMonitorView) View.inflate(this.getApplicationContext(), R.layout.cpumonitor, null);
        mCpuMonitorView.init(this.getApplicationContext(),MONITOR_TYPE_CPU);
        mCpuMonitorView.setUpView(mCpuInfo.getCpuCount());
        WindowManager.LayoutParams parms;
        parms = ((MonitorApplication)this.getApplicationContext()).getWindowParms(MONITOR_TYPE_CPU);
        parms.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;
        parms.format = PixelFormat.RGBA_8888;
        parms.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        parms.gravity = Gravity.LEFT | Gravity.TOP;
        parms.x = 0;
        parms.y = 0;
        parms.width = WindowManager.LayoutParams.WRAP_CONTENT;
        parms.height = WindowManager.LayoutParams.WRAP_CONTENT;
        //mCpuMonitorView.setImageResource(R.drawable.ic_launcher);

        mWindowManager.addView(mCpuMonitorView,parms);
    }
    private void  removeView(View view){
        mWindowManager.removeView(view);
    }


    private void handleCpuMonitorUpdate(CpuInfoStat.CpuInfo  [] info){
        mCpuMonitorView.UpdateCpuStatus(info);
    }
    private void handleDeviceInfoMonitorUpdate(DeviceInfoStat.DeviceInfo info){
        mDeviceInfoMonitorView.updateDeviceInfo( info);
    }
    class MonitorHandler  extends  Handler{

        @Override
        public void handleMessage(Message msg) {
            switch(msg.what){
                case MSG_UPDATE_CPU:{
                    if(msg.obj !=null){
                        handleCpuMonitorUpdate((CpuInfoStat.CpuInfo[])msg.obj);
                    }
                    break;
                }
                case MSG_UPDATE_DEVICEINFO:{
                    if(msg.obj != null){
                        handleDeviceInfoMonitorUpdate((DeviceInfoStat.DeviceInfo)msg.obj);
                    }
                    break;
                }
            }

        }
    }




}
