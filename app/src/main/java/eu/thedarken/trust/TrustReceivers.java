package eu.thedarken.trust;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import eu.thedarken.trust.logentries.IntentHelper;
import eu.thedarken.trust.logentries.LogEntry;
import eu.thedarken.trust.logentries.LogEntryMaker;

public class TrustReceivers extends BroadcastReceiver {
    private static final String TAG = "TR:TrustReceivers";
    private int mLastType = 0;

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, "Intent fired and received");
        LogEntryMaker maker = new LogEntryMaker(context.getApplicationContext());
        maker.createStartEventIfNecessary();
        int type = IntentHelper.getType(intent);
        if (type != LogEntry.NO_TYPE) {
            if (type == LogEntry.SHUTDOWN)
                maker.makeKilledEntry();
            if (type == LogEntry.RESTORED_WIFI_CONNECTION && mLastType == 0) {
                //NOPE, register triggers intent
            } else if ((type == LogEntry.RESTORED_WIFI_CONNECTION || type == LogEntry.LOST_WIFI_CONNECTION
                    || type == LogEntry.RESTORED_CELL_SERVICE || type == LogEntry.LOST_CELL_SERVICE) && type == mLastType) {
                //NOPE no double entries
            } else {
                maker.makeEntry(intent);
            }
        } else {
            Log.w(TAG, "Unknown EVENT " + intent.getAction());
        }
        mLastType = type;
    }

}
