package eu.thedarken.trust;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.os.IBinder;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.telephony.TelephonyManager;
import android.util.Log;
import eu.thedarken.trust.logentries.LogEntryMaker;

public class TrustService extends Service {
    private static final int PENDING_INTENT_SWIPEFIX_ID = 55;
    private SharedPreferences settings;

    private final static String TAG = "TR:TrustService";
    private static final int NOTIFICATION_ID = 27;
    private TrustReceivers mTrustReceivers;
    public static boolean sIsRunning = false;
    private static boolean sPausedVoluntarilz = false;
    private static boolean sStartedVoluntarily = false;
    private Notification mNotification;
    private PendingIntent mPendingIntent;
    private AppWatcher mAppWatcher;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "service started");

        sPausedVoluntarilz = false;
        wasVoluntaryStart();

        settings = PreferenceManager.getDefaultSharedPreferences(this);
        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);

        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_SCREEN_ON);
        filter.addAction(Intent.ACTION_SCREEN_OFF);
        filter.addAction(Intent.ACTION_USER_PRESENT);
        filter.addAction(Intent.ACTION_NEW_OUTGOING_CALL);
        filter.addAction(Intent.ACTION_MEDIA_REMOVED);
        filter.addAction(Intent.ACTION_POWER_CONNECTED);
        filter.addAction(Intent.ACTION_POWER_DISCONNECTED);
        filter.addAction(Intent.ACTION_REBOOT);
        filter.addAction(Intent.ACTION_SHUTDOWN);
        filter.addAction(Intent.ACTION_DEVICE_STORAGE_LOW);
        filter.addAction(TelephonyManager.ACTION_PHONE_STATE_CHANGED);
        filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);

        mTrustReceivers = new TrustReceivers();
        registerReceiver(mTrustReceivers, filter);

        IntentFilter filterPackage = new IntentFilter();
        filterPackage.addAction(Intent.ACTION_PACKAGE_ADDED);
        filterPackage.addAction(Intent.ACTION_PACKAGE_CHANGED);
        filterPackage.addAction(Intent.ACTION_PACKAGE_DATA_CLEARED);
        filterPackage.addAction(Intent.ACTION_PACKAGE_REMOVED);
        filterPackage.addAction(Intent.ACTION_PACKAGE_REPLACED);
        filterPackage.addAction(Intent.ACTION_PACKAGE_RESTARTED);
        filterPackage.addDataScheme("package");

        registerReceiver(mTrustReceivers, filterPackage);
        sIsRunning = true;

        mNotification = new Notification(R.drawable.ic_launcher, "Trust is good", System.currentTimeMillis());
        Intent i = new Intent(this, TrustActivity.class);

        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);

        mPendingIntent = PendingIntent.getActivity(this, 0, i, 0);

        mNotification.setLatestEventInfo(this, "Control is better", "Trust running", mPendingIntent);
        mNotification.flags |= Notification.FLAG_NO_CLEAR;
        mNotification.flags |= Notification.FLAG_FOREGROUND_SERVICE;
        mNotification.flags |= Notification.FLAG_ONGOING_EVENT;

        if (settings.getBoolean("general.notification", false))
            startForeground(NOTIFICATION_ID, mNotification);


        mAppWatcher = new AppWatcher(this);
        mAppWatcher.startWatching();
    }

    @Override
    public void onDestroy() {
        mAppWatcher.stopWatching();
        this.unregisterReceiver(mTrustReceivers);

        wasVoluntaryPause();
        Log.d(TAG, "service destroyed");

        sIsRunning = false;
        sStartedVoluntarily = false;
        stopForeground(true);
        super.onDestroy();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null && intent.getExtras() != null && intent.getExtras().getBoolean(TrustAutostart.KEY_AUTOSTARTSTRING, false)) {
            LogEntryMaker maker = new LogEntryMaker(getApplicationContext());
            maker.makeBootCompleteEntry();
        }
        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO Auto-generated method stub
        return null;
    }

    public static void doStartVoluntarily() {
        sStartedVoluntarily = true;
    }

    private void wasVoluntaryStart() {
        LogEntryMaker maker = new LogEntryMaker(getApplicationContext());
        maker.createStartEventIfNecessary();
        if (!sStartedVoluntarily) {
            maker.makeSystemResumeEntry();
        } else {
            maker.makeResumeEntry();
        }
    }

    public static void doPauseVoluntarily() {
        sPausedVoluntarilz = true;
    }

    private void wasVoluntaryPause() {
        LogEntryMaker maker = new LogEntryMaker(getApplicationContext());
        if (!sPausedVoluntarilz) {
            maker.makeKilledEntry();
        } else {
            maker.makePauseEntry();
        }
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        swipeFix(getApplicationContext());
    }

    public static void swipeFix(Context c) {
        Log.d(TAG, "Got swiped, restore TrustService");
        LogEntryMaker maker = new LogEntryMaker(c.getApplicationContext());
        maker.makeKilledEntry();
        Intent svc = new Intent(c, TrustService.class);
        PendingIntent restartServicePendingIntent = PendingIntent.getService(c, PENDING_INTENT_SWIPEFIX_ID, svc, PendingIntent.FLAG_CANCEL_CURRENT);

        AlarmManager alarmService = (AlarmManager) c.getSystemService(Context.ALARM_SERVICE);
        alarmService.set(AlarmManager.ELAPSED_REALTIME, SystemClock.elapsedRealtime() + 3000, restartServicePendingIntent);
    }

}
