package eu.thedarken.trust;

import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.preference.PreferenceScreen;
import android.util.Log;
import android.widget.Toast;

import eu.thedarken.trust.db.TrustDB;
import eu.thedarken.trust.lockpattern.LockPatternActivity;
import eu.thedarken.trust.logentries.IDMaker;
import eu.thedarken.trust.logentries.LogEntryMaker;

public class TrustPreferences extends PreferenceActivity {
    private SharedPreferences settings;
    private Editor prefEditor;
    private Context mContext;
    private PackageManager packageManager;
    private ComponentName autostart;
    public static final int PREFERENCE_ACTIVITY = 20;
    private boolean check_done = false;
    private final static String TAG = "eu.thedarken.trust.TrustPreferences";
    public final static int RESULT_EXIT_APP = 10;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mContext = this;
        settings = PreferenceManager.getDefaultSharedPreferences(mContext);
        prefEditor = settings.edit();
        setTitle(R.string.settings);
        try {
            addPreferencesFromResource(R.xml.preferences);
        } catch (Exception e) {
            prefEditor.clear();
            prefEditor.commit();
            Log.d(mContext.getPackageName(), "Settings were corrupt and have been reset!");
            addPreferencesFromResource(R.xml.preferences);
        }

        packageManager = this.getPackageManager();

        autostart = new ComponentName(mContext, TrustAutostart.class);

        ((CheckBoxPreference) findPreference("general.autostart"))
                .setChecked(packageManager.getComponentEnabledSetting(autostart) == PackageManager.COMPONENT_ENABLED_STATE_ENABLED
                        || packageManager.getComponentEnabledSetting(autostart) == PackageManager.COMPONENT_ENABLED_STATE_DEFAULT);
        Intent i = getIntent();
        check_done = i.getBooleanExtra("check_done", false);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (!check_done)
            TrustActivity.checkPattern(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        check_done = false;
    }

    @Override
    public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, final Preference preference) {
        if (preference.getKey().equals("general.autostart")) {
            if (preference.getSharedPreferences().getBoolean(preference.getKey(), true)) {
                mContext.getPackageManager()
                        .setComponentEnabledSetting(autostart, PackageManager.COMPONENT_ENABLED_STATE_ENABLED, PackageManager.DONT_KILL_APP);
                Log.d(TAG, "On");
            } else {
                mContext.getPackageManager().setComponentEnabledSetting(autostart, PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
                        PackageManager.DONT_KILL_APP);
                Log.d(TAG, "Off");
            }

        } else if (preference.getKey().equals("general.protection.config.create")) {
            Intent patternlock = new Intent(this, LockPatternActivity.class);
            patternlock.putExtra(LockPatternActivity.MODE, LockPatternActivity.LPMode.CreatePattern);
            patternlock.putExtra(LockPatternActivity.AutoSave, true);
            startActivityForResult(patternlock, LockPatternActivity.REQUEST_PATTERN_CREATION);

        } else if (preference.getKey().equals("general.protection.config.clear")) {
            SharedPreferences pattern_prefs = getSharedPreferences(LockPatternActivity.PREFERENCE_FILE, 0);
            SharedPreferences.Editor pattern_editor = pattern_prefs.edit();
            pattern_editor.putString(LockPatternActivity.KEY, "");
            pattern_editor.commit();
            Toast.makeText(mContext, getString(R.string.pattern_removed), Toast.LENGTH_LONG).show();
        } else if (preference.getKey().equals("database.reset")) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setPositiveButton(R.string.preferences_database_reset_title, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    TrustDB db = TrustDB.getInstance(getApplicationContext());
                    db.clean(0);
                    LogEntryMaker maker = new LogEntryMaker(getApplicationContext());
                    IDMaker.getInstance(getApplicationContext()).resetIDs();
                    maker.makeResetEntry();
                    Toast.makeText(TrustPreferences.this, R.string.success, Toast.LENGTH_SHORT).show();
                }
            });
            builder.setNegativeButton(R.string.abort, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    dialog.dismiss();
                }
            });
            builder.show();
        } else if (preference.getKey().equals("github.trust")) {
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/d4rken/trust"));
            startActivity(browserIntent);
        } else if (preference.getKey().equals("licenses.lockpattern")) {
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://code.google.com/p/android-lockpattern/"));
            startActivity(browserIntent);
        } else if (preference.getKey().equals("misc.privacy")) {
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/d4rken/trust/blob/master/privacy_policy_for_gplay.md"));
            startActivity(browserIntent);
        }
        return false;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == LockPatternActivity.REQUEST_PATTERN_CHECK) {
            if (resultCode == RESULT_OK) {
                // Pattern correct
                check_done = true;
            } else {
                // Pattern failed
                setResult(RESULT_EXIT_APP);
                finish();
            }
        }

    }

}
