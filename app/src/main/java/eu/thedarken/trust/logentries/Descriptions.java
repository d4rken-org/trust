package eu.thedarken.trust.logentries;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager.NameNotFoundException;

import eu.thedarken.trust.R;

public class Descriptions {
    public static String getDescription(Context c, LogEntry e) {
        return getDescription(c, e, 0);
    }

    public static String getDescription(Context c, LogEntry entry, int previoustype) {
        switch (entry.getType()) {
            case LogEntry.LOG_START:
                return c.getString(R.string.log_recording_started);
            case LogEntry.LOG_RESET:
                return c.getString(R.string.log_has_been_reset);
            case LogEntry.LOG_PAUSE:
                return c.getString(R.string.logging_paused_by_user);
            case LogEntry.LOG_RESUME:
                return c.getString(R.string.logging_resumed_by_user);
            case LogEntry.LOG_SYSTEM_RESUME:
                return c.getString(R.string.log_was_resumed_by_the_system);
            case LogEntry.LOG_SYSTEM_KILLED:
                return c.getString(R.string.logging_was_killed_by_system);
            case LogEntry.MEDIA_REMOVED:
                return c.getString(R.string.a_medium_has_been_removed);
            case LogEntry.NEW_INCOMING_CALL:
                return c.getString(R.string.incoming_call_from, entry.getExtra());
            case LogEntry.NEW_OUTGOING_CALL:
                return c.getString(R.string.new_outgoing_call_to, entry.getExtra());
            case LogEntry.CALL_OFFHOOK:
                if (previoustype == LogEntry.NEW_OUTGOING_CALL) {
                    return c.getString(R.string.call_placed_to_network);
                } else {
                    return c.getString(R.string.call_accepted_by_you);
                }
            case LogEntry.CALL_IDLE:
                return c.getString(R.string.call_done_declined);
            case LogEntry.POWER_CONNECTED:
                return c.getString(R.string.power_plugged_in);
            case LogEntry.POWER_DISCONNECTED:
                return c.getString(R.string.power_disconnected);
            case LogEntry.BOOT_COMPLETED:
                return c.getString(R.string.boot_completed);
            case LogEntry.SHUTDOWN:
                return c.getString(R.string.shutdown_initiated);
            case LogEntry.SCREEN_ON:
                return c.getString(R.string.screen_on);
            case LogEntry.SCREEN_OFF:
                return c.getString(R.string.screen_off);
            case LogEntry.USER_PRESENT:
                return c.getString(R.string.lockscreen_unlocked);
            case LogEntry.PACKAGE_ADDED:
                return c.getString(R.string.app_added, getAppNameFromPackage(c, getPackageFromExtra(entry.getExtra())));
            case LogEntry.PACKAGE_CHANGED:
                return c.getString(R.string.app_changed, getAppNameFromPackage(c, getPackageFromExtra(entry.getExtra())));
            case LogEntry.PACKAGE_DATA_CLEARED:
                return c.getString(R.string.app_data_wiped_for, getAppNameFromPackage(c, getPackageFromExtra(entry.getExtra())));
            case LogEntry.PACKAGE_INSTALL:
                return c.getString(R.string.app_installed, getAppNameFromPackage(c, getPackageFromExtra(entry.getExtra())));
            case LogEntry.PACKAGE_REMOVED:
                return c.getString(R.string.app_deleted, getPackageFromExtra(entry.getExtra()));
            case LogEntry.PACKAGE_REPLACED:
                return c.getString(R.string.app_replaced, getAppNameFromPackage(c, getPackageFromExtra(entry.getExtra())));
            case LogEntry.PACKAGE_RESTARTED:
                return c.getString(R.string.app_restarted, getAppNameFromPackage(c, getPackageFromExtra(entry.getExtra())));
            case LogEntry.LOST_CELL_SERVICE:
                return c.getString(R.string.lost_mobile_data_connection);
            case LogEntry.RESTORED_CELL_SERVICE:
                return c.getString(R.string.restored_mobile_data_connection);
            case LogEntry.LOST_WIFI_CONNECTION: {
                String ssid = entry.getExtra().replace("ssid:", "");
                return c.getString(R.string.lost_wifi_connection, "SSID:" + ssid);
            }
            case LogEntry.RESTORED_WIFI_CONNECTION: {
                String ssid = entry.getExtra().replace("ssid:", "");
                return c.getString(R.string.restored_wifi_connection, "SSID:" + ssid);
            }
            case LogEntry.APP_ACTIVITY:
                return c.getString(R.string.app_foreground_activity, getAppNameFromPackage(c, getPackageFromExtra(entry.getExtra())));
            case -2:
                return c.getString(R.string.free_version_limited);
            default:
                return "";
        }
    }

    public static String getDetailedDescription(Context c, LogEntry entry) {
        String pkg = getPackageFromExtra(entry.getExtra());
        String name = getAppNameFromPackage(c, pkg);
        switch (entry.getType()) {
            case LogEntry.LOG_START:
                return c.getString(R.string.log_recording_started_description);
            case LogEntry.LOG_RESET:
                return c.getString(R.string.log_has_been_reset_description);
            case LogEntry.LOG_PAUSE:
                return c.getString(R.string.logging_paused_by_user_description);
            case LogEntry.LOG_RESUME:
                return c.getString(R.string.logging_resumed_by_user_description);
            case LogEntry.LOG_SYSTEM_RESUME:
                return c.getString(R.string.log_was_resumed_by_the_system_description);
            case LogEntry.LOG_SYSTEM_KILLED:
                return c.getString(R.string.logging_was_killed_by_system_description);
            case LogEntry.MEDIA_REMOVED:
                return c.getString(R.string.a_medium_has_been_removed_description);
            case LogEntry.NEW_INCOMING_CALL:
                return c.getString(R.string.incoming_call_from_description, entry.getExtra());
            case LogEntry.NEW_OUTGOING_CALL:
                return c.getString(R.string.new_outgoing_call_to_description, entry.getExtra());
            case LogEntry.CALL_OFFHOOK:
                return c.getString(R.string.call_off_hook_description);
            case LogEntry.CALL_IDLE:
                return c.getString(R.string.call_done_declined_description);
            case LogEntry.POWER_CONNECTED:
                return c.getString(R.string.power_plugged_in_description);
            case LogEntry.POWER_DISCONNECTED:
                return c.getString(R.string.power_disconnected_description);
            case LogEntry.BOOT_COMPLETED:
                return c.getString(R.string.boot_completed_description);
            case LogEntry.SHUTDOWN:
                return c.getString(R.string.shutdown_initiated_description);
            case LogEntry.SCREEN_ON:
                return c.getString(R.string.screen_on_description);
            case LogEntry.SCREEN_OFF:
                return c.getString(R.string.screen_off_description);
            case LogEntry.USER_PRESENT:
                return c.getString(R.string.lockscreen_unlocked_description);
            case LogEntry.PACKAGE_ADDED:
                return c.getString(R.string.app_added_description, ("'" + name + "' (" + pkg + ")"));
            case LogEntry.PACKAGE_CHANGED:
                return c.getString(R.string.app_changed_description, ("'" + name + "' (" + pkg + ")"));
            case LogEntry.PACKAGE_DATA_CLEARED:
                return c.getString(R.string.app_data_wiped_for_description, ("'" + name + "' (" + pkg + ")"));
            case LogEntry.PACKAGE_INSTALL:
                return c.getString(R.string.app_installed_description, ("'" + name + "' (" + pkg + ")"));
            case LogEntry.PACKAGE_REMOVED:
                return c.getString(R.string.app_deleted_description, ("'" + name + "' (" + pkg + ")"));
            case LogEntry.PACKAGE_REPLACED:
                return c.getString(R.string.app_replaced_description, ("'" + name + "' (" + pkg + ")"));
            case LogEntry.PACKAGE_RESTARTED:
                return c.getString(R.string.app_restarted_description, ("'" + name + "' (" + pkg + ")"));
            case LogEntry.LOST_CELL_SERVICE:
                return c.getString(R.string.lost_mobile_data_connection_description);
            case LogEntry.RESTORED_CELL_SERVICE:
                return c.getString(R.string.restored_mobile_data_connection_description);
            case LogEntry.LOST_WIFI_CONNECTION: {
                String ssidL = entry.getExtra().replace("ssid:", "");
                return c.getString(R.string.lost_wifi_connection_description, "SSID:" + ssidL);
            }
            case LogEntry.RESTORED_WIFI_CONNECTION: {
                String ssidR = entry.getExtra().replace("ssid:", "");
                return c.getString(R.string.restored_wifi_connection_description, "SSID:" + ssidR);
            }
            case LogEntry.APP_ACTIVITY:
                return c.getString(R.string.app_foreground_activity_description, ("'" + name + "' (" + pkg + ")"));
            case -2:
                return c.getString(R.string.free_version_limited_desc);
            default:
                return "";
        }
    }

    private static String getPackageFromExtra(String extra) {
        if (extra == null)
            return "?";
        String pkg = extra.replace("package:", "");
        if (pkg.startsWith("'") && pkg.endsWith("'"))
            pkg = pkg.replace("'", "");
        return pkg;
    }

    private static String getAppNameFromPackage(Context c, String pkg) {
        if (pkg == null)
            return "?";
        try {
            ApplicationInfo info = c.getPackageManager().getApplicationInfo(pkg, 0);
            return info.loadLabel(c.getPackageManager()).toString();
        } catch (NameNotFoundException e) {
            return "?";
        }
    }
}
