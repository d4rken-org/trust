package eu.thedarken.trust.logentries;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.telephony.TelephonyManager;

/**
 * Created by darken on 22.03.14.
 */
public class IntentHelper {
    private static final String TAG = "TR:IntentHelper";

    public static String retrieve(Context context, Intent intent, int type) {
        switch (type) {
            case LogEntry.MEDIA_REMOVED:
                return intent.getDataString();
            case LogEntry.NEW_OUTGOING_CALL:
                return intent.getStringExtra(Intent.EXTRA_PHONE_NUMBER);
            case LogEntry.NEW_INCOMING_CALL:
                return intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER);
            case LogEntry.POWER_CONNECTED:
                return "";
            case LogEntry.POWER_DISCONNECTED:
                return "";
            case LogEntry.BOOT_COMPLETED:
                return "";
            case LogEntry.SCREEN_OFF:
                return "";
            case LogEntry.SCREEN_ON:
                return "";
            case LogEntry.SHUTDOWN:
                return "";
            case LogEntry.USER_PRESENT:
                return "";
            case LogEntry.PACKAGE_ADDED:
                return intent.getDataString();
            case LogEntry.PACKAGE_CHANGED:
                return intent.getDataString();
            case LogEntry.PACKAGE_DATA_CLEARED:
                return intent.getDataString();
            case LogEntry.PACKAGE_INSTALL:
                return intent.getDataString();
            case LogEntry.PACKAGE_REMOVED:
                return intent.getDataString();
            case LogEntry.PACKAGE_REPLACED:
                return intent.getDataString();
            case LogEntry.PACKAGE_RESTARTED:
                return intent.getDataString();
            case LogEntry.LOST_CELL_SERVICE:
                return "";
            case LogEntry.RESTORED_CELL_SERVICE:
                return "";
            case LogEntry.LOST_WIFI_CONNECTION: {
                WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
                WifiInfo wi = wifiManager.getConnectionInfo();
                if (wi != null && wi.getSSID() != null) {
                    String ssid = "ssid:" + wi.getSSID().replace("\"", "");
                    return ssid;
                } else {
                    return "?";
                }
            }
            case LogEntry.RESTORED_WIFI_CONNECTION: {
                WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
                WifiInfo wi = wifiManager.getConnectionInfo();
                if (wi != null && wi.getSSID() != null) {
                    String ssid = "ssid:" + wi.getSSID().replace("\"", "");
                    return ssid;
                } else {
                    return "?";
                }
            }
            default:
                return "";
        }
    }

    public static int getType(Intent intent) {
        String action = intent.getAction();
        if (action.equals(Intent.ACTION_SCREEN_ON)) {
            return LogEntry.SCREEN_ON;
        } else if (action.equals(Intent.ACTION_SCREEN_OFF)) {
            return LogEntry.SCREEN_OFF;
        } else if (action.equals(Intent.ACTION_USER_PRESENT)) {
            return LogEntry.USER_PRESENT;
        } else if (action.equals(Intent.ACTION_NEW_OUTGOING_CALL)) {
            return LogEntry.NEW_OUTGOING_CALL;
        } else if (action.equals(TelephonyManager.ACTION_PHONE_STATE_CHANGED)) {
            String newPhoneState = intent.getStringExtra(TelephonyManager.EXTRA_STATE);
            if (newPhoneState != null) {
                if (newPhoneState.equals(TelephonyManager.EXTRA_STATE_RINGING)) {
                    return LogEntry.NEW_INCOMING_CALL;
                } else if (newPhoneState.equals(TelephonyManager.EXTRA_STATE_OFFHOOK)) {
                    return LogEntry.CALL_OFFHOOK;
                } else if (newPhoneState.equals(TelephonyManager.EXTRA_STATE_IDLE)) {
                    return LogEntry.CALL_IDLE;
                }
            }
            return 0;
        } else if (action.equals(Intent.ACTION_MEDIA_REMOVED)) {
            return LogEntry.MEDIA_REMOVED;
        } else if (action.equals(Intent.ACTION_POWER_CONNECTED)) {
            return LogEntry.POWER_CONNECTED;
        } else if (action.equals(Intent.ACTION_POWER_DISCONNECTED)) {
            return LogEntry.POWER_DISCONNECTED;
        } else if (action.equals(Intent.ACTION_SHUTDOWN)) {
            return LogEntry.SHUTDOWN;
        } else if (action.equals(Intent.ACTION_PACKAGE_ADDED)) {
            return LogEntry.PACKAGE_ADDED;
        } else if (action.equals(Intent.ACTION_PACKAGE_CHANGED)) {
            return LogEntry.PACKAGE_CHANGED;
        } else if (action.equals(Intent.ACTION_PACKAGE_DATA_CLEARED)) {
            return LogEntry.PACKAGE_DATA_CLEARED;
        } else if (action.equals(Intent.ACTION_PACKAGE_INSTALL)) {
            return LogEntry.PACKAGE_INSTALL;
        } else if (action.equals(Intent.ACTION_PACKAGE_REMOVED)) {
            return LogEntry.PACKAGE_REMOVED;
        } else if (action.equals(Intent.ACTION_PACKAGE_REPLACED)) {
            return LogEntry.PACKAGE_REPLACED;
        } else if (action.equals(Intent.ACTION_PACKAGE_RESTARTED)) {
            return LogEntry.PACKAGE_RESTARTED;
        } else if (action.equals(ConnectivityManager.CONNECTIVITY_ACTION)) {
            NetworkInfo info = intent.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
            if (info != null) {
                if (info.getType() == ConnectivityManager.TYPE_WIFI) {
                    if (info.getDetailedState().equals(NetworkInfo.DetailedState.CONNECTED) && info.isConnected()) {
                        return LogEntry.RESTORED_WIFI_CONNECTION;
                    } else if (info.getDetailedState().equals(NetworkInfo.DetailedState.DISCONNECTED) && !info.isConnected()) {
                        return LogEntry.LOST_WIFI_CONNECTION;
                    }
                } else if (info.getType() == ConnectivityManager.TYPE_MOBILE) {
                    if (info.getDetailedState().equals(NetworkInfo.DetailedState.CONNECTED) && info.isConnected()) {
                        return LogEntry.RESTORED_CELL_SERVICE;
                    } else if (info.getDetailedState().equals(NetworkInfo.DetailedState.DISCONNECTED) && !info.isConnected()) {
                        return LogEntry.LOST_CELL_SERVICE;
                    }
                }
            }
            return 0;
        } else {
            return 0;
        }
    }
}
