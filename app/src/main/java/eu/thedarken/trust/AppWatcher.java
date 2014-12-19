package eu.thedarken.trust;

import android.app.ActivityManager;
import android.content.Context;
import android.os.Handler;

import java.util.List;

import eu.thedarken.trust.db.TrustDB;
import eu.thedarken.trust.logentries.LogEntryMaker;

/**
 * Created by darken on 22.03.14.
 */
public class AppWatcher {
    private static final String TAG = "TR:AppWatcher";
    private Context mContext;
    private TrustDB mDB;
    private String lastActiveApp = "";
    private Handler handler = new Handler();
    ;
    private Checker checker = new Checker();
    private long checkInterval = 1000;
    private LogEntryMaker mLogEntryMaker;

    public AppWatcher(Context context) {
        mContext = context;
        mDB = TrustDB.getInstance(context.getApplicationContext());
        mLogEntryMaker = new LogEntryMaker(mContext);
    }

    public void startWatching() {
        handler.postDelayed(checker, checkInterval);
    }

    public void stopWatching() {
        handler.removeCallbacks(checker);
    }

    private class Checker implements Runnable {
        @Override
        public void run() {
            ActivityManager am = (ActivityManager) mContext.getSystemService(Context.ACTIVITY_SERVICE);
            List<ActivityManager.RunningTaskInfo> taskInfo = am.getRunningTasks(1);
            if (taskInfo != null && !taskInfo.isEmpty()) {
                String currentForegroundApp = taskInfo.get(0).topActivity.getPackageName();
                if (!lastActiveApp.equals(currentForegroundApp)) {
                    lastActiveApp = currentForegroundApp;
                    mLogEntryMaker.makeAppEntry(currentForegroundApp);
                }

            }
            handler.postDelayed(checker, checkInterval);
        }

    }
}
