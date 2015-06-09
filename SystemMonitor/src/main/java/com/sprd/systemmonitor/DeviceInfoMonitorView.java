package com.sprd.systemmonitor;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * Created by SPREADTRUM\joe.yu on 6/8/15.
 */
public class DeviceInfoMonitorView  extends MonitorView{


    DeviceInfoText mVmText ,mMemText;

    public DeviceInfoMonitorView(Context context) {
        super(context);
        this.setBackgroundColor(Color.BLUE);
    }

    public DeviceInfoMonitorView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.setBackgroundColor(Color.BLUE);
    }

    public void  setUpView(){

        mVmText = new DeviceInfoText(mContext);
        this.addView(mVmText);
        mMemText = new DeviceInfoText(mContext);
        this.addView(mMemText);
    }
    @Override
    void OnClick() {
        super.OnClick();
    }
    public void updateDeviceInfo(DeviceInfoStat.DeviceInfo info){
         String vmStat = info.genVmstat();
         String memStat = info.genMemStat();
         if(!vmStat.equals(mVmText.getText())){
             mVmText.setText(vmStat);
         }
         if(!memStat.equals(mMemText.getText())){
             mMemText.setText(memStat);
         }


    }

    class DeviceInfoText extends TextView{

        public DeviceInfoText(Context context) {
            super(context);
            setTextColor(Color.WHITE);
            setTextSize(15);
        }

        public DeviceInfoText(Context context, AttributeSet attrs) {
            super(context, attrs);
        }

        public DeviceInfoText(Context context, AttributeSet attrs, int defStyle) {
            super(context, attrs, defStyle);
        }
    }

}
