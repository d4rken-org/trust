package eu.thedarken.trust.logentries;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Created by darken on 22.03.14.
 */
public class IDMaker {
    private static IDMaker mInstance;
    private Context mContext;
    private SharedPreferences mSettings;
    private static final String KEY = "trust.ID";
    public IDMaker(Context c) {
        mContext = c;
        mSettings = PreferenceManager.getDefaultSharedPreferences(mContext);
    }

    public static IDMaker getInstance(Context c) {
        if (mInstance == null)
            mInstance = new IDMaker(c);
        return mInstance;
    }

    public synchronized long makeID() {
        long ret = mSettings.getLong(KEY, 0);
        ret++;
        mSettings.edit().putLong(KEY, ret).commit();
        return ret;
    }

    public synchronized void resetIDs() {
        mSettings.edit().putLong(KEY,0).commit();
    }

    public synchronized long getID() {
        return mSettings.getLong(KEY, 0);
    }
}
