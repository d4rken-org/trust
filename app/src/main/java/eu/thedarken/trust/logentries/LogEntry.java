package eu.thedarken.trust.logentries;

import android.os.Parcel;
import android.os.Parcelable;

public class LogEntry implements Parcelable {

    public final static int NO_TYPE = 0;
    public final static int LOG_START = 1;
    public final static int LOG_RESET = 2;
    public final static int LOG_PAUSE = 3;
    public final static int LOG_RESUME = 4;
    public final static int LOG_SYSTEM_KILLED = 5;
    public final static int LOG_SYSTEM_RESUME = 6;
    public final static int POWER_CONNECTED = 7;
    public final static int POWER_DISCONNECTED = 8;
    public final static int BOOT_COMPLETED = 9;
    public final static int SHUTDOWN = 10;
    public final static int SCREEN_ON = 11;
    public final static int SCREEN_OFF = 12;
    public final static int USER_PRESENT = 13;
    public final static int PACKAGE_ADDED = 14;
    public final static int PACKAGE_CHANGED = 15;
    public final static int PACKAGE_DATA_CLEARED = 16;
    public final static int PACKAGE_INSTALL = 17;
    public final static int PACKAGE_REMOVED = 18;
    public final static int PACKAGE_REPLACED = 19;
    public final static int PACKAGE_RESTARTED = 20;
    public final static int LOST_CELL_SERVICE = 21;
    public final static int RESTORED_CELL_SERVICE = 22;
    public final static int LOST_WIFI_CONNECTION = 23;
    public final static int RESTORED_WIFI_CONNECTION = 24;
    public final static int MEDIA_REMOVED = 25;
    public final static int NEW_INCOMING_CALL = 26;
    public final static int NEW_OUTGOING_CALL = 27;
    public final static int CALL_OFFHOOK = 28;
    public final static int CALL_IDLE = 29;
    public final static int APP_ACTIVITY = 100;

    private long mID = 0;
    private int mType = 0;
    private long mTime = System.currentTimeMillis();
    private String mExtra = "";

    public LogEntry() {
    }

    public String getExtra() {
        return mExtra;
    }

    public void setExtra(String extra) {
        this.mExtra = extra;
    }

    public int getType() {
        return mType;
    }

    public void setType(int type) {
        this.mType = type;
    }

    public long getTime() {
        return mTime;
    }

    public void setTime(long time) {
        this.mTime = time;
    }

    public long getID() {
        return mID;
    }

    public void setID(long iD) {
        mID = iD;
    }

    @Override
    public String toString() {
        return "LOGENTRY" + " time: " + getTime() + " ID " + getID() + " type " + getType() + " extra " + getExtra();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel out, int flags) {
        out.writeLong(getID());
        out.writeInt(getType());
        out.writeLong(getTime());
        out.writeString(getExtra());
    }

    public static final Parcelable.Creator<LogEntry> CREATOR = new Parcelable.Creator<LogEntry>() {
        public LogEntry createFromParcel(Parcel in) {
            return new LogEntry(in);
        }

        public LogEntry[] newArray(int size) {
            return new LogEntry[size];
        }
    };

    @SuppressWarnings("unchecked")
    protected LogEntry(Parcel in) {
        setID(in.readLong());
        setType(in.readInt());
        setTime(in.readLong());
        setExtra(in.readString());
    }
}
