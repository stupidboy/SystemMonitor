package com.sprd.systemmonitor;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Created by SPREADTRUM\joe.yu on 6/5/15.
 */
public class CpuInfoStat {


    private static final String TAG = "CpuInfoStat";
    private static long MONITOR_DELAY = 500;//ms
    private static String CPU_FILE_PREFIX = "/sys/devices/system/cpu/";
    private static String CPU_FILE_COUNTS = "present";
    private static String CPU_FERQ = "/cpufreq/scaling_cur_freq";
    private static String CPU_ONLINE = "/online";
    private static long  MHZ = 1024 * 1024;
    private static long  GHZ =  MHZ * 1024;
    private static final int  MSG_UPDATE_CPU = 0;



    private Context mContext;
    private Handler mMainThreadHandler;
    private MonitorThread mThread;
    private boolean mStartMonitor = false;
    private int mCpuCounts = 4;
    private File mCpuCountFile = new File(CPU_FILE_PREFIX + CPU_FILE_COUNTS);
    private CpuInfo[] mCpuInfo;

   public  class CpuInfo {
        private int mIndex ;
        private String mPath;
        private long mCpuFreq;
        private boolean mOnLine;

        public String genResult(){
            return "CPU:"+mIndex+" freq :"+genFreq(mCpuFreq);
        }
        private String genFreq(long freq){
            String rawfreq = ""+(1.00f*freq/MHZ);
            return rawfreq.substring(0,3)+"GHZ";
        }
    }

    public CpuInfoStat(Context context, Handler handler) {
        mContext = context;
        mMainThreadHandler = handler;
        mThread = new MonitorThread();
        init();
    }

    public void startMonitor() {
        if (!mStartMonitor) {
            mStartMonitor = true;
            mThread.start();
        }
    }

    public void stopMonitor() {
        if (mStartMonitor) {
            mStartMonitor = false;
        }

    }
    public int getCpuCount(){
        return mCpuCounts;
    }
    private void updateCurrentCpuState() {
        for (int i = 0; i < mCpuCounts; i++) {
            File cpuFreq = new File(mCpuInfo[i].mPath + CPU_FERQ);
            File cpuOnline = new File(mCpuInfo[i].mPath + CPU_ONLINE);
            mCpuInfo[i].mOnLine =false;
            mCpuInfo[i].mCpuFreq = 0;
            try {
                if (cpuOnline.canRead()) {
                    FileInputStream fis = new FileInputStream(cpuOnline);
                    BufferedReader read = new BufferedReader(new InputStreamReader(fis),512);
                    String value = read.readLine();
                    mCpuInfo[i].mOnLine = "1".equals(value);

                }
                if (cpuFreq.canRead()) {
                    FileInputStream fisf = new FileInputStream(cpuFreq);
                    BufferedReader readf = new BufferedReader(new InputStreamReader(fisf),512);
                    String value = readf.readLine();
                    if(value != null){
                        mCpuInfo[i].mCpuFreq = Long.parseLong(value.trim());
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }


    void init() {
        if (mCpuCountFile.canRead()) {
            try {
                FileInputStream fis = new FileInputStream(mCpuCountFile);
                BufferedReader reader = new BufferedReader(new InputStreamReader(fis), 512);
                String value = reader.readLine().trim();
                //0-x;0
                if (value.equals("0")) {
                    mCpuCounts = 1;
                } else {
                    mCpuCounts = 1 + Integer.parseInt(value.replace("0-", ""));
                }
                mCpuInfo = new CpuInfo[mCpuCounts];
                for (int i = 0; i < mCpuCounts; i++) {
                    mCpuInfo[i] = new CpuInfo();
                    mCpuInfo[i].mPath = CPU_FILE_PREFIX + "cpu" + i;
                    mCpuInfo[i].mIndex = i;
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }

    }
    public  void dump(){
        for(int i = 0;i < mCpuCounts; i++){
            Log.e(TAG,mCpuInfo[i].genResult());
        }


    }

    void sendUpdateMessage(){
        Message msg = new Message();
        msg.what =MSG_UPDATE_CPU;
        msg.obj = mCpuInfo;
        mMainThreadHandler.sendMessage(msg);
    }
    class MonitorThread extends Thread {

        public MonitorThread() {
            super("cpu");
        }

        @Override
        public void run() {
            while (mStartMonitor) {
                //do update
                updateCurrentCpuState();
                sendUpdateMessage();
                dump();
                try {
                    sleep(MONITOR_DELAY);
                } catch (Exception e) {
                }

            }
        }
    }

}
