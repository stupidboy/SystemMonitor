package com.sprd.systemmonitor;

import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

/**
 * Created by SPREADTRUM\joe.yu on 6/5/15.
 */
public class MonitorView extends LinearLayout {

    private static final String TAG = "MonitorView";
     WindowManager mWindowManager;
     WindowManager.LayoutParams mParms ;
     Context mContext;
     int mStartX,mStartY;
     int mStatusBarHeight = 0;
    public MonitorView(Context context) {
        super(context);
    }

    public MonitorView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }


    public void  init( Context context,int type){
        mContext = context;
        mWindowManager = (WindowManager)context.getSystemService(Context.WINDOW_SERVICE);
        mParms = ((MonitorApplication) context.getApplicationContext()).getWindowParms(type);
    }
    void updateCurrentPosition(int x , int y){
        getStatusBarHeight();
        mParms.x = x ;
        mParms.y = y-mStatusBarHeight;
        mWindowManager.updateViewLayout(this,mParms);

    }
    int getStatusBarHeight(){
        if(mStatusBarHeight == 0){
            View rootView = this.getRootView();
            Rect r = new Rect();
            rootView.getWindowVisibleDisplayFrame(r);
            mStatusBarHeight = r.top;
        }
        return mStatusBarHeight;
    }



    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        switch(event.getAction()){

            case MotionEvent.ACTION_DOWN:{
                mStartX = (int)event.getX();
                mStartY = (int)event.getY();
                break;
            }
            case MotionEvent.ACTION_MOVE:{
                Log.e(TAG,"x = "+event.getX()+" y = "+event.getY()+"raw x = "+event.getRawX()+" raw y ="+event.getRawY());
                updateCurrentPosition((int) Math.abs(event.getRawX()-mStartX),(int) Math.abs(event.getRawY()-mStartY));
                break;
            }
            case MotionEvent.ACTION_UP:{
                if(Math.abs(event.getX()-mStartX) <= 3 && Math.abs((event.getY()-mStartY) )<= 3){
                    OnClick();
                }else{
                    //updateCurrentPosition((int)event.getX(),(int)event.getY());
                }
                mStartX = mStartY = 0;
                break;
            }
            case MotionEvent.ACTION_CANCEL:{
                mStartX = mStartY = 0;
                break;
            }
        }

        return true;
    }

    void OnClick() {

    Log.e(TAG, "this is a  click");

    }


}


