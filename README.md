Trust - Event Logger
=============

A small event logging tool for Android.

Trust keeps a small log of events for you.

Whether it is app changes, people using your phone or missing calls, Trust will tell you what happened and when.

Events currently include:
- Launched apps (not available on Android 5.0)
- Installed/deleted/replaced/restarted/cleared apps
- Power connect/disconnect
- Shutdowns/Boots
- Screen on/off events
- Lockscreen events
- Loss of wifi/cell connectivity
- Removed media
- Calls

What does Trust need the **PERMISSION**s for:
* PROCESS_OUTGOING_CALLS is needed to detect incoming or outgoing calls
* RECEIVE_BOOT_COMPLETED for autostart and detecting reboots
* READ_PHONE_STATE to detect connectivity changes
* WRITE_EXTERNAL_STORAGE to export the database
* GET_TASKS for current foreground apps
* ACCESS_WIFI_STATE for wifi SSIDs
