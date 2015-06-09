package com.sprd.systemmonitor;

import android.content.Context;
import android.database.DataSetObserver;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * Created by SPREADTRUM\joe.yu on 6/8/15.
 */
public class CpuMonitorView extends MonitorView {
    private int mCpuCount;
    private int mType;
    private CpuTextView []  mCpuFreqText ;


    public CpuMonitorView(Context context) {
        super(context);
        this.setBackgroundColor(Color.BLUE);

    }


    public CpuMonitorView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.setBackgroundColor(Color.BLUE);
    }


    void  setUpView(int count){
        mCpuCount = count;
        mCpuFreqText = new CpuTextView[count];
        for(int i = 0;i < count;i++){
            mCpuFreqText[i] = new CpuTextView(mContext);
            this.addView(mCpuFreqText[i]);
        }

    }
    @Override
    void OnClick() {
        super.OnClick();

    }

    public void UpdateCpuStatus(CpuInfoStat.CpuInfo[] info){

        for(int i = 0;i < mCpuCount;i++){
            if(!mCpuFreqText[i].getText().equals(info[i].genResult())){
                mCpuFreqText[i].setText(info[i].genResult());
            }
        }

    }
    class CpuTextView  extends TextView{


        public CpuTextView(Context context) {

            super(context);
            this.setTextColor(Color.WHITE);
            this.setTextSize(15);
        }

        public CpuTextView(Context context, AttributeSet attrs) {
            super(context, attrs);
        }

        public CpuTextView(Context context, AttributeSet attrs, int defStyle) {
            super(context, attrs, defStyle);
        }
    }
}