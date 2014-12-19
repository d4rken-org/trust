package eu.thedarken.trust.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import eu.thedarken.trust.logentries.LogEntry;

import java.util.ArrayList;

public class TrustDB {
    public final static long WEEK = 604800000;
    public final static long DAY = 86400000;
    static final String EVENT_TABLE = "event_data";
    public final static String NEW_EVENT_INTENT = "eu.thedarken.trust.intent.action.NEW_EVENT";
    static final String create_event_data = "create table IF NOT EXISTS " + EVENT_TABLE + " " + "(time long, " + "ID int, " + "type int, " + "extra string "
            + "); ";

    private static Context mContext = null;

    private SQLiteDatabase mDB;
    private DatabaseHelper mDBhelper;

    private static final TrustDB instance = new TrustDB();

    public TrustDB() {
        // open the DB for read and write
        // mDB = mDBhelper.getWritableDatabase();

    }

    public static TrustDB getInstance(Context context) {
        mContext = context;
        return instance;
    }

    private static class DatabaseHelper extends SQLiteOpenHelper {
        private ArrayList<String> create_tables = new ArrayList<String>();
        private ArrayList<String> tables = new ArrayList<String>();

        DatabaseHelper(Context context) {
            super(context, "event.db", null, 1);
            tables.add(TrustDB.EVENT_TABLE);
            create_tables.add(create_event_data);
        }

        // this is called for first time db is created.
        // put all CREATE TABLE here
        @Override
        public void onCreate(SQLiteDatabase db) {
            createTables(db);
        }

        // this is called when an existing user updates to a newer version of
        // the app
        // add CREATE TABLE and ALTER TABLE here
        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            // TODO
        }

        private void createTables(SQLiteDatabase db) {
            if (db != null) {
                for (String t : create_tables) {
                    db.execSQL(t);
                }
            }
        }
    }

    private synchronized boolean openWrite() {
        if (mDB != null && mDB.isOpen())
            return true;

        try {
            if (mDBhelper == null)
                mDBhelper = new DatabaseHelper(mContext);
            mDB = mDBhelper.getWritableDatabase();
        } catch (SQLiteException e) {
            e.printStackTrace();
        }
        // Log.d(mContext.getPackageName(), "DB write open");
        if (mDB == null) {
            return false;
        } else {
            return mDB.isOpen();
        }
    }

    private synchronized boolean openRead() {
        if (mDB != null && mDB.isOpen())
            return true;

        if (mDBhelper == null)
            mDBhelper = new DatabaseHelper(mContext);
        try {
            mDB = mDBhelper.getReadableDatabase();
            // Log.d(mContext.getPackageName(), "DB read open");
        } catch (SQLiteException e) {
            e.printStackTrace();
        }
        if (mDB == null) {
            return false;
        } else {
            return mDB.isOpen();
        }
    }

    private synchronized boolean tryClose() {
        if (mDB != null && mDB.isOpen()) {
            mDB.close();
            SQLiteDatabase.releaseMemory();
        }
        // Log.d(mContext.getPackageName(), "DB close");

        return mDB == null || !mDB.isOpen();
    }

    public synchronized void clean(long time) {
        if (openWrite()) {
            try {
                Log.d(mContext.getPackageName(), "Cleaning old entries");
                mDB.execSQL("DELETE FROM " + TrustDB.EVENT_TABLE + " WHERE time<" + (System.currentTimeMillis() - time) + ";");
                Log.d(mContext.getPackageName(), "Done cleaning");
            } catch (SQLiteException e) {
                tryClose();
            }
        }
        tryClose();
    }

    public void init() {
        if (openRead())
            tryClose();
    }

    public synchronized void addEntry(LogEntry e) {
        if (openWrite()) {
            mDB.beginTransaction();
            ContentValues values = new ContentValues();
            values.put("time", e.getTime());
            values.put("ID", e.getID());
            values.put("type", e.getType());
            values.put("extra", "'" + e.getExtra() + "'");
            mDB.insert(EVENT_TABLE, null, values);
            mDB.setTransactionSuccessful();
            mDB.endTransaction();

        }
        tryClose();
    }

    public synchronized LogEntry getEntryByID(long ID) {
        LogEntry ret = null;
        if (openRead()) {
            Cursor c;
            try {
                c = mDB.rawQuery("SELECT * " + "FROM " + TrustDB.EVENT_TABLE + " " + "WHERE ID=" + ID + ";", null);
            } catch (SQLiteException e) {
                tryClose();
                e.printStackTrace();
                return null;
            }
            if (c != null && c.getCount() == 1) {
                ret = new LogEntry();
                c.moveToFirst();
                ret.setTime(c.getLong(c.getColumnIndex("time")));
                ret.setID(c.getInt(c.getColumnIndex("ID")));
                ret.setType(c.getInt(c.getColumnIndex("type")));
                String extra = c.getString(c.getColumnIndex("extra"));
                ret.setExtra(extra.substring(1, extra.length() - 1));
                c.close();
            } else {
                Log.w(mContext.getPackageName(), "getEntry cursos was either NULL or empty or more than one");
                tryClose();
                return null;
            }
        }
        tryClose();
        return ret;
    }

    public synchronized ArrayList<LogEntry> getEntries() {
        ArrayList<LogEntry> ret = new ArrayList<LogEntry>();
        if (openRead()) {
            Cursor c;
            try {
                c = mDB.rawQuery("SELECT * " + "FROM " + TrustDB.EVENT_TABLE + " " + "ORDER BY time DESC " + ";", null);
            } catch (SQLiteException e) {
                tryClose();
                e.printStackTrace();
                return null;
            }
            if (c != null && c.getCount() > 0) {
                c.moveToFirst();
                do {
                    LogEntry e = new LogEntry();
                    e.setTime(c.getLong(c.getColumnIndex("time")));
                    e.setID(c.getInt(c.getColumnIndex("ID")));
                    e.setType(c.getInt(c.getColumnIndex("type")));
                    String extra = c.getString(c.getColumnIndex("extra"));
                    e.setExtra(extra.substring(1, extra.length() - 1));
                    ret.add(e);
                } while (c.moveToNext());
                c.close();
            } else {
                Log.w(mContext.getPackageName(), "getEntry cursos was either NULL or empty");
                tryClose();
                return new ArrayList<LogEntry>();
            }
        }
        tryClose();
        return ret;
    }

    public synchronized ArrayList<LogEntry> getEntries(long from, long to) {
        ArrayList<LogEntry> ret = new ArrayList<LogEntry>();
        if (openRead()) {
            Cursor c;
            try {
                c = mDB.rawQuery("SELECT * FROM " + TrustDB.EVENT_TABLE + " WHERE ID BETWEEN " + from + " AND " + to + " ORDER BY ID DESC;", null);
            } catch (SQLiteException e) {
                tryClose();
                e.printStackTrace();
                return null;
            }
            if (c != null && c.getCount() > 0) {
                c.moveToFirst();
                do {
                    LogEntry e = new LogEntry();
                    e.setTime(c.getLong(c.getColumnIndex("time")));
                    e.setID(c.getInt(c.getColumnIndex("ID")));
                    e.setType(c.getInt(c.getColumnIndex("type")));
                    String extra = c.getString(c.getColumnIndex("extra"));
                    e.setExtra(extra.substring(1, extra.length() - 1));
                    ret.add(e);
                } while (c.moveToNext());
                c.close();
            } else {
                Log.w(mContext.getPackageName(), "getEntry cursor was either NULL or empty");
                tryClose();
                return new ArrayList<LogEntry>();
            }
        }
        tryClose();
        return ret;
    }
}