package eu.thedarken.trust;

import android.content.Context;
import eu.thedarken.trust.db.TrustDB;
import eu.thedarken.trust.logentries.LogEntry;

public class TestGenerator {
    private Context mContext;

    public TestGenerator(Context c) {
        mContext = c;
    }

    public void addAllEventsToDB(long time) {
        TrustDB db = TrustDB.getInstance(mContext);

        LogEntry e = new LogEntry();
        e.setTime(time);
        e.setID(1);
        e.setType(LogEntry.NO_TYPE);
        //e.extra = Descriptions.getDescription(mContext, e);
        db.addEntry(e);

        e.setTime(time);
        e.setID(2);
        e.setType(LogEntry.LOG_START);
        //e.extra = Descriptions.getDescription(mContext, e);
        db.addEntry(e);

        e.setTime(time);
        e.setID(3);
        e.setType(LogEntry.LOG_RESET);
        //e.extra = Descriptions.getDescription(mContext, e);
        db.addEntry(e);

        e.setTime(time);
        e.setID(4);
        e.setType(LogEntry.LOG_PAUSE);
        //e.extra = Descriptions.getDescription(mContext, e);
        db.addEntry(e);

        e.setTime(time);
        e.setID(5);
        e.setType(LogEntry.LOG_RESUME);
        //e.extra = Descriptions.getDescription(mContext, e);
        db.addEntry(e);

        e.setTime(time);
        e.setID(6);
        e.setType(LogEntry.LOG_SYSTEM_KILLED);
        //e.extra = Descriptions.getDescription(mContext, e);
        db.addEntry(e);

        e.setTime(time);
        e.setID(7);
        e.setType(LogEntry.LOG_SYSTEM_RESUME);
        //e.extra = Descriptions.getDescription(mContext, e);
        db.addEntry(e);

        e.setTime(time);
        e.setID(8);
        e.setType(LogEntry.POWER_CONNECTED);
        //e.extra = Descriptions.getDescription(mContext, e);
        db.addEntry(e);

        e.setTime(time);
        e.setID(9);
        e.setType(LogEntry.POWER_DISCONNECTED);
        //e.extra = Descriptions.getDescription(mContext, e);
        db.addEntry(e);

        e.setTime(time);
        e.setID(10);
        e.setType(LogEntry.BOOT_COMPLETED);
        //e.extra = Descriptions.getDescription(mContext, e);
        db.addEntry(e);

        e.setTime(time);
        e.setID(11);
        e.setType(LogEntry.SHUTDOWN);
        //e.extra = Descriptions.getDescription(mContext, e);
        db.addEntry(e);

        e.setTime(time);
        e.setID(12);
        e.setType(LogEntry.SCREEN_ON);
        //e.extra = Descriptions.getDescription(mContext, e);
        db.addEntry(e);

        e.setTime(time);
        e.setID(13);
        e.setType(LogEntry.SCREEN_OFF);
        //e.extra = Descriptions.getDescription(mContext, e);
        db.addEntry(e);

        e.setTime(time);
        e.setID(14);
        e.setType(LogEntry.USER_PRESENT);
        //e.extra = Descriptions.getDescription(mContext, e);
        db.addEntry(e);

        e.setTime(time);
        e.setID(15);
        e.setType(LogEntry.PACKAGE_ADDED);
        //e.extra = Descriptions.getDescription(mContext, e);
        db.addEntry(e);

        e.setTime(time);
        e.setID(16);
        e.setType(LogEntry.PACKAGE_CHANGED);
        //e.extra = Descriptions.getDescription(mContext, e);
        db.addEntry(e);

        e.setTime(time);
        e.setID(17);
        e.setType(LogEntry.PACKAGE_DATA_CLEARED);
        //e.extra = Descriptions.getDescription(mContext, e);
        db.addEntry(e);

        e.setTime(time);
        e.setID(18);
        e.setType(LogEntry.PACKAGE_INSTALL);
        //e.extra = Descriptions.getDescription(mContext, e);
        db.addEntry(e);

        e.setTime(time);
        e.setID(19);
        e.setType(LogEntry.PACKAGE_REMOVED);
        //e.extra = Descriptions.getDescription(mContext, e);
        db.addEntry(e);

        e.setTime(time);
        e.setID(20);
        e.setType(LogEntry.PACKAGE_REPLACED);
        //e.extra = Descriptions.getDescription(mContext, e);
        db.addEntry(e);

        e.setTime(time);
        e.setID(21);
        e.setType(LogEntry.PACKAGE_RESTARTED);
        //e.extra = Descriptions.getDescription(mContext, e);
        db.addEntry(e);

        e.setTime(time);
        e.setID(22);
        e.setType(LogEntry.LOST_CELL_SERVICE);
        //e.extra = Descriptions.getDescription(mContext, e);
        db.addEntry(e);

        e.setTime(time);
        e.setID(23);
        e.setType(LogEntry.RESTORED_CELL_SERVICE);
        //e.extra = Descriptions.getDescription(mContext, e);
        db.addEntry(e);

        e.setTime(time);
        e.setID(24);
        e.setType(LogEntry.LOST_WIFI_CONNECTION);
        //e.extra = Descriptions.getDescription(mContext, e);
        db.addEntry(e);

        e.setTime(time);
        e.setID(25);
        e.setType(LogEntry.RESTORED_WIFI_CONNECTION);
        //e.extra = Descriptions.getDescription(mContext, e);
        db.addEntry(e);

        e.setTime(time);
        e.setID(26);
        e.setType(LogEntry.MEDIA_REMOVED);
        //e.extra = Descriptions.getDescription(mContext, e);
        db.addEntry(e);

        e.setTime(time);
        e.setID(27);
        e.setType(LogEntry.NEW_INCOMING_CALL);
        //e.extra = Descriptions.getDescription(mContext, e);
        db.addEntry(e);

        e.setTime(time);
        e.setID(28);
        e.setType(LogEntry.NEW_OUTGOING_CALL);
        //e.extra = Descriptions.getDescription(mContext, e);
        db.addEntry(e);

        e.setTime(time);
        e.setID(29);
        e.setType(LogEntry.CALL_OFFHOOK);
        //e.extra = Descriptions.getDescription(mContext, e);
        db.addEntry(e);

        e.setTime(time);
        e.setID(30);
        e.setType(LogEntry.CALL_IDLE);
        //e.extra = Descriptions.getDescription(mContext, e);
        db.addEntry(e);

    }

}
