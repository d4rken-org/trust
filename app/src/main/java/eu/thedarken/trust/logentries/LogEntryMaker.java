package eu.thedarken.trust.logentries;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager.NameNotFoundException;

import eu.thedarken.trust.db.TrustDB;

public class LogEntryMaker {
    private Context mContext;
    private final static String TAG = "eu.thedarken.trust.logentries.LogEntryMaker";
    private IDMaker mIDMaker;
    private TrustDB mDB;
    private String mVersionTag;

    public LogEntryMaker(Context c) {
        mContext = c;
        mIDMaker = IDMaker.getInstance(mContext);
        mDB = TrustDB.getInstance(mContext);
        try {
            mVersionTag = mContext.getPackageManager().getPackageInfo(mContext.getPackageName(), 0).versionName + "("
                    + mContext.getPackageManager().getPackageInfo(mContext.getPackageName(), 0).versionCode + ")";
        } catch (NameNotFoundException e) {
            mVersionTag = "";
        }
    }

    public void makeEntry(Intent i) {
        int type = IntentHelper.getType(i);
        if (type == LogEntry.NO_TYPE)
            return;

        LogEntry event = new LogEntry();
        event.setType(type);
        event.setID(mIDMaker.makeID());
        event.setExtra(IntentHelper.retrieve(mContext, i, event.getType()));
        mDB.addEntry(event);
        notifyList(event);
    }


    public boolean createStartEventIfNecessary() {
        if (mIDMaker.getID() == 0) {
            LogEntry event = new LogEntry();
            event.setType(LogEntry.LOG_START);
            event.setID(mIDMaker.makeID());
            event.setExtra(mVersionTag);
            mDB.addEntry(event);
            notifyList(event);
            return true;
        }
        return false;
    }

    public void makeBootCompleteEntry() {
        LogEntry event = new LogEntry();
        event.setType(LogEntry.BOOT_COMPLETED);
        event.setID(mIDMaker.makeID());
        mDB.addEntry(event);
        notifyList(event);
    }

    public void makeSystemResumeEntry() {
        LogEntry event = new LogEntry();
        event.setType(LogEntry.LOG_SYSTEM_RESUME);
        event.setID(mIDMaker.makeID());
        event.setExtra(mVersionTag);
        mDB.addEntry(event);
        notifyList(event);
    }

    public void makeResumeEntry() {
        LogEntry event = new LogEntry();
        event.setType(LogEntry.LOG_RESUME);
        event.setID(mIDMaker.makeID());
        event.setExtra(mVersionTag);
        mDB.addEntry(event);
        notifyList(event);
    }

    public void makeKilledEntry() {
        LogEntry event = new LogEntry();
        event.setType(LogEntry.LOG_SYSTEM_KILLED);
        event.setID(mIDMaker.makeID());
        event.setExtra(mVersionTag);
        mDB.addEntry(event);
        notifyList(event);
    }

    public void makePauseEntry() {
        LogEntry event = new LogEntry();
        event.setType(LogEntry.LOG_PAUSE);
        event.setID(mIDMaker.makeID());
        event.setExtra(mVersionTag);
        mDB.addEntry(event);
        notifyList(event);
    }

    public void makeResetEntry() {
        LogEntry event = new LogEntry();
        event.setType(LogEntry.LOG_RESET);
        event.setID(mIDMaker.makeID());
        event.setExtra(mVersionTag);
        mDB.addEntry(event);
        notifyList(event);
    }

    public void makeAppEntry(String currentForegroundAppPkg) {
        LogEntry event = new LogEntry();
        event.setType(LogEntry.APP_ACTIVITY);
        event.setID(mIDMaker.makeID());
        event.setExtra("package:" + currentForegroundAppPkg);
        mDB.addEntry(event);
        notifyList(event);
    }

    private void notifyList(LogEntry event) {
        Intent i = new Intent();
        i.setAction(TrustDB.NEW_EVENT_INTENT);
        i.putExtra("id", event.getID());
        mContext.sendBroadcast(i);
    }

}
