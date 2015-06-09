package com.sprd.systemmonitor;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Created by SPREADTRUM\joe.yu on 6/4/15.
 */
public class DeviceInfoStat {



    /*
 Various pieces   of  information about  kernel activity  are  available in the
/proc/stat file.  All  of  the numbers reported  in  this file are  aggregates
since the system first booted.  For a quick look, simply cat the file:

  > cat /proc/stat
  cpu  2255 34 2290 22625563 6290 127 456 0 0
  cpu0 1132 34 1441 11311718 3675 127 438 0 0
  cpu1 1123 0 849 11313845 2614 0 18 0 0
  intr 114930548 113199788 3 0 5 263 0 4 [... lots more numbers ...]
  ctxt 1990473
  btime 1062191376
  processes 2915
  procs_running 1
  procs_blocked 0
  softirq 183433 0 21755 12 39 1137 231 21459 2263

The very first  "cpu" line aggregates the  numbers in all  of the other "cpuN"
lines.  These numbers identify the amount of time the CPU has spent performing
different kinds of work.  Time units are in USER_HZ (typically hundredths of a
second).  The meanings of the columns are as follows, from left to right:

- user: normal processes executing in user mode
- nice: niced processes executing in user mode
- system: processes executing in kernel mode
- idle: twiddling thumbs
- iowait: waiting for I/O to complete
- irq: servicing interrupts
- softirq: servicing softirqs
- steal: involuntary wait
- guest: running a normal guest
- guest_nice: running a niced guest

The "intr" line gives counts of interrupts  serviced since boot time, for each
of the  possible system interrupts.   The first  column  is the  total of  all
interrupts serviced; each  subsequent column is the  total for that particular
interrupt.

The "ctxt" line gives the total number of context switches across all CPUs.

The "btime" line gives  the time at which the  system booted, in seconds since
the Unix epoch.

The "processes" line gives the number  of processes and threads created, which
includes (but  is not limited  to) those  created by  calls to the  fork() and
clone() system calls.

The "procs_running" line gives the total number of threads that are
running or ready to run (i.e., the total number of runnable threads).

The   "procs_blocked" line gives  the  number of  processes currently blocked,
waiting for I/O to complete.

The "softirq" line gives counts of softirqs serviced since boot time, for each
of the possible system softirqs. The first column is the total of all
softirqs serviced; each subsequent column is the total for that particular
softirq.


    * */
    //values....
    static String  TAG = "DeviceStat";
    static String  PROC_PATH_PREFIX = "/proc/";
    static String  PROC_STAT = "/proc/stat";
    static String  PROC_MEMINFO = "/proc/meminfo";
    static String PROC_VMSTAT = "/proc/vmstat";
    static long    MONITOR_DELAY = 1000;//ms
    static final int CPU_INFO_NUMBS = 7;
    static final long  INITAL_VALUE = 0;
    private static final int  MSG_UPDATE_DEVICEINFO = 1;
    private byte [] mBuff = new byte[5*1048];
    private Context mContext;
    private Handler mMainHandler;
    private MonitorThread mMonitorThread ;
    private Boolean mStartMonitor = false;
    private File mStatFile ;
    private File mMemFile;
    private File mVmstatFile ;

    private MemInfo mMeminfo;
    private VmstatInfo mVmstatInfo;
    private VmstatInfo mOldVmstatInfo;
    //device info....
    //mem...
    /*
    private String mMemTotal ;
    private String mMemFree ;
    private String mMemMapped ;
    private String mMemBuff ;
    private String mMemCache ;
    private String mMemAnon ;
    private String mMemSlab;
    private String mMemSwapCache ;

    //cpu...
    private String mRunningProcesses  ;
    private String mBlockedProcesses ;
    private String mProcesses ;
    private String mContextSwitchCount ;
    private String mBootTime ;
    private String mInterrupt ;
    private String mCpuUser ;
    private String mCpuNiced ;
    private String mCpuSystem ;
    private String mCpuIdle ;
    private String mCpuWait ;
    private String mCpuInterrupt ;
    private String mCpuSoftIRQ ;
    private String mCpuInfos[] = {"0","0","0","0","0","0","0"};
    private String mPageFlt;
    */
    //cpu freq
    private String mCpuFreq;
    private DeviceInfo mDeviceInfo;






     class VmstatInfo{
        private long mRunningProcesses  ;
        private long mBlockedProcesses ;
        private long mProcesses ;
        private long mContextSwitchCount ;
        private long mBootTime ;
        private long mInterrupt ;
        private long mCpuUser ;
        private long mCpuNiced ;
        private long mCpuSystem ;
        private long mCpuIdle ;
        private long mCpuWait ;
        private long mCpuInterrupt ;
        private long mCpuSoftIRQ ;
        private long mPageFlt;

        public VmstatInfo(){
            reset();
        }

        public void copyTo( VmstatInfo info){
            mRunningProcesses = info.mRunningProcesses;
            mBlockedProcesses = info.mBlockedProcesses;
            mProcesses = info.mProcesses;
            mContextSwitchCount = info.mContextSwitchCount;
            mBootTime = info.mBootTime;
            mInterrupt = info.mInterrupt;
            mCpuUser = info.mCpuUser;
            mCpuNiced = info.mCpuNiced;
            mCpuSystem = info.mCpuSystem;
            mCpuIdle = info.mCpuIdle;
            mCpuWait = info.mCpuWait;
            mCpuInterrupt = info.mCpuInterrupt;
            mCpuSoftIRQ = info.mCpuSoftIRQ;
            mPageFlt = info.mPageFlt;

        }

        private  void reset(){
            mRunningProcesses = INITAL_VALUE;
            mBlockedProcesses = INITAL_VALUE;
            mProcesses = INITAL_VALUE;
            mContextSwitchCount = INITAL_VALUE;
            mBootTime = INITAL_VALUE;
            mInterrupt = INITAL_VALUE;
            mCpuUser = INITAL_VALUE;
            mCpuNiced = INITAL_VALUE;
            mCpuSystem = INITAL_VALUE;
            mCpuIdle = INITAL_VALUE;
            mCpuWait = INITAL_VALUE;
            mCpuInterrupt = INITAL_VALUE;
            mCpuSoftIRQ = INITAL_VALUE;
            mPageFlt = INITAL_VALUE;
        }
        private long fixValue(long value){
            value = Math.abs(value);
            return  value >99 ? 99:value;

        }
        public String genResult(VmstatInfo info){

            return "R:"+fixValue(mRunningProcesses-info.mRunningProcesses)+"B:"+fixValue(mBlockedProcesses - info.mBlockedProcesses)+"Usr:"
                    +fixValue(mCpuUser - info.mCpuUser)+"Sys:"+fixValue(mCpuSystem -info.mCpuSystem)+"Idle:"+fixValue(mCpuIdle - info.mCpuIdle)+"Wait:"+fixValue(mCpuWait - info.mCpuWait)
                    +"Flt:"+(mPageFlt - info.mPageFlt);
        }
    };

     class MemInfo{
        private String mMemTotal ;
        private String mMemFree ;
        private String mMemMapped ;
        private String mMemBuff ;
        private String mMemCache ;
        private String mMemAnon ;
        private String mMemSlab;
        private String mMemSwapCache ;

        @Override
        public String toString() {
            return "";
        }
        public MemInfo(){
            reset();
        }
        private void  reset(){
            mMemSwapCache = "";
            mMemFree = "";
            mMemMapped = "";
            mMemBuff = "";
            mMemCache = "";
            mMemAnon = "";
            mMemSlab = "";
            mMemSwapCache = "";
        }
         public String genMemStat(){
             return "Total:"+mMemTotal+" Free:"+mMemFree+" Cache:"+mMemCache+" Buff:"+mMemBuff;
         }
    };
    public class DeviceInfo{
        private MemInfo mMeminfo;
        private VmstatInfo mVmInfo;
        private VmstatInfo mOldVminfo;

        public DeviceInfo(MemInfo meminfo,VmstatInfo vminfo ,VmstatInfo oldInfo){
            mMeminfo = meminfo;
            mVmInfo = vminfo;
            mOldVminfo = oldInfo;
        }
        public String genVmstat(){
            return  mVmInfo.genResult(mOldVminfo);
        }
        public String genMemStat(){
            return mMeminfo.genMemStat();
        }
    }
    public DeviceInfoStat(Context context,Handler handler){
        mContext = context;
        mMainHandler = handler;
        mMonitorThread = new MonitorThread();
        mStatFile = new File(PROC_STAT);
        mMemFile = new File(PROC_MEMINFO);
        mVmstatFile = new File(PROC_VMSTAT);
        mVmstatInfo = new VmstatInfo();
        mOldVmstatInfo = new VmstatInfo();
        mMeminfo = new MemInfo();
        mDeviceInfo = new DeviceInfo(mMeminfo,mVmstatInfo,mOldVmstatInfo);
        checkReadPerm();
    }
    boolean checkReadPerm(){
        if(!mStatFile.canRead()){
            Toast.makeText(mContext,"can't read stat file !",Toast.LENGTH_SHORT).show();
            return false;
        }
        if(!mMemFile.canRead()){
            Toast.makeText(mContext,"can't read meminfo !",Toast.LENGTH_SHORT).show();
        }
        return true;
    }
    void startMonitor(){
        if(!mStartMonitor){
            mStartMonitor = true;
            mMonitorThread.start();
        }
    }
    void stopMonitor(){
        if(mStartMonitor){
            mStartMonitor = false;
        }
    }
    void updateCurrentStat(){
    //1. get meminfo
        updateMemInfo();
        updateStatInfo();
        dump();

    }

    void updatePageFlt(){
        if(mVmstatFile.canRead()){
            try {
                FileInputStream fis =new FileInputStream(mVmstatFile);
                BufferedReader reader = new BufferedReader(new InputStreamReader(fis),512);

                for(String value = reader.readLine();value != null;value = reader.readLine()){
                    if(value.startsWith("pgmajfault")){

                        mVmstatInfo.mPageFlt = Long.parseLong(value.replace("pgmajfault","").trim());
                    }
                }
                reader.close();
                fis.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }



    }
    void updateStatInfo(){
        if(mStatFile.canRead()){
            try {
                FileInputStream fis = new FileInputStream(mStatFile);
                BufferedReader read = new BufferedReader(new InputStreamReader(fis),512);
                mOldVmstatInfo.copyTo(mVmstatInfo);
                for(String value = read.readLine(); value!=null ;value = read.readLine()){

                    if(value.startsWith("cpu ")){
                        String [] cpuinfo = value.split(" ");
                        /*
                        mCpuUser = ""+(-Long.parseLong(mCpuInfos[0]) + Long.parseLong(cpuinfo[2]));
                        mCpuNiced = ""+(-Long.parseLong(mCpuInfos[1]) + Long.parseLong(cpuinfo[3]));
                        mCpuSystem = ""+(-Long.parseLong(mCpuInfos[2]) + Long.parseLong(cpuinfo[4]));
                        mCpuIdle = ""+(-Long.parseLong(mCpuInfos[3]) + Long.parseLong(cpuinfo[5]));
                        mCpuWait = ""+(-Long.parseLong(mCpuInfos[4]) + Long.parseLong(cpuinfo[6]));
                        mCpuInterrupt = ""+(-Long.parseLong(mCpuInfos[5]) + Long.parseLong(cpuinfo[7]));
                        mCpuSoftIRQ = ""+(-Long.parseLong(mCpuInfos[6]) + Long.parseLong(cpuinfo[8]));
                        System.arraycopy(cpuinfo,2,mCpuInfos,0,CPU_INFO_NUMBS);
                        */

                        mVmstatInfo.mCpuUser = Long.parseLong(cpuinfo[2]);
                        mVmstatInfo.mCpuNiced =  Long.parseLong(cpuinfo[3]);
                        mVmstatInfo.mCpuSystem = Long.parseLong(cpuinfo[4]);
                        mVmstatInfo.mCpuIdle = Long.parseLong(cpuinfo[5]);
                        mVmstatInfo.mCpuWait = Long.parseLong(cpuinfo[6]);
                        mVmstatInfo.mCpuInterrupt = Long.parseLong(cpuinfo[7]);
                        mVmstatInfo.mCpuSoftIRQ = Long.parseLong(cpuinfo[8]);

                    }else if(value.startsWith("procs_running")){
                        mVmstatInfo.mRunningProcesses = Long.parseLong( value.replace("procs_running","").trim());
                    }else if(value.startsWith("procs_blocked")){
                        mVmstatInfo.mBlockedProcesses = Long.parseLong( value.replace("procs_blocked","").trim());
                    }
                }
                read.close();
                fis.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }finally {

            }

        }
        updatePageFlt();
    }
    void updateMemInfo(){
    if(mMemFile.canRead()){
        try {
            FileInputStream fis = new FileInputStream(mMemFile);
            BufferedReader read = new BufferedReader(new InputStreamReader(fis),512);
            for(String value = read.readLine();value != null;value = read.readLine()){
                value = value.trim();
                if(value.startsWith("MemTotal:")){
                    mMeminfo.mMemTotal = value.replace("MemTotal:","").trim();
                }
                else if(value.startsWith("MemFree:")){
                    mMeminfo.mMemFree = value.replace("MemFree:","").trim();
                }
                else if(value.startsWith("Buffers:")){
                    mMeminfo.mMemBuff = value.replace("Buffers:","").trim();
                }
                else if(value.startsWith("Cached:")){
                    mMeminfo.mMemCache = value.replace("Cached:","").trim();
                }
                else if(value.startsWith("SwapCached:")){
                    mMeminfo.mMemSwapCache = value.replace("SwapCached:","").trim();
                }
                else if(value.startsWith("Mapped:")){
                    mMeminfo.mMemMapped = value.replace("Mapped:","").trim();
                }
                else if(value.startsWith("Slab:")){
                    mMeminfo.mMemSlab = value.replace("Slab:","").trim();
                }
                else if(value.startsWith("AnonPages:")){
                    mMeminfo.mMemAnon = value.replace("AnonPages:","").trim();
                }
            }
            read.close();
            fis.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    }
    public void dump(){
        /*
        Log.e(TAG,"mMemTotal -->"+mMemTotal);
        Log.e(TAG,"mMemFree -->"+mMemFree);
        Log.e(TAG,"mMemBuff -->"+mMemBuff);
        Log.e(TAG,"mMemCache -->"+mMemCache);
        Log.e(TAG,"mMemSwapCache -->"+mMemSwapCache);
        Log.e(TAG,"mMemSlab -->"+mMemSlab);
        Log.e(TAG,"mMemAnon -->"+mMemAnon);
        */
       // Log.e(TAG,"mCpuUser -->"+mCpuUser+"mCpuNiced -->"+mCpuNiced+"mCpuSystem -->"+mCpuSystem+"mCpuIdle -->"+mCpuIdle+"mCpuWait -->"+mCpuWait+"mCpuInterrupt -->"+mCpuInterrupt+"mCpuSoftIRQ -->"+mCpuSoftIRQ);
        Log.e(TAG,mVmstatInfo.genResult(mOldVmstatInfo));

    }
    private void sendUpdateMessage(){
        Message msg =new Message();
        msg.what = MSG_UPDATE_DEVICEINFO;
        msg.obj = mDeviceInfo;
        mMainHandler.sendMessage(msg);
    }
    class MonitorThread  extends Thread{

        public MonitorThread() {
            super("vmstat");
        }

        @Override
        public void run() {
            while (mStartMonitor){
                //do update
                Log.d(TAG, "update........");
                updateCurrentStat();
                sendUpdateMessage();
                try{
                    sleep(MONITOR_DELAY);
                }catch (Exception e){}

            }
        }
    }
}
