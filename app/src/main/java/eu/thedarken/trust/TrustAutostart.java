package eu.thedarken.trust;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class TrustAutostart extends BroadcastReceiver {
    private static final String TAG = "TR:Autostart";
    public static final String KEY_AUTOSTARTSTRING = "AUTOSTART";

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, "Trust autostart called");
        Intent svc = new Intent(context, TrustService.class);
        svc.putExtra(KEY_AUTOSTARTSTRING, true);
        context.startService(svc);
    }
}