package eu.thedarken.trust;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import eu.thedarken.trust.dialogs.AboutDialogFragment;
import eu.thedarken.trust.dialogs.ExportDialogFragment;
import eu.thedarken.trust.dialogs.FilterDialogFragment;
import eu.thedarken.trust.dialogs.FollowMeDialog;
import eu.thedarken.trust.dialogs.NewsDialogFragment;
import eu.thedarken.trust.lockpattern.LockPatternActivity;

public class TrustActivity extends ActionBarActivity {
    private Intent mServiceIntent;

    private TrustListFragment mListFragment;
    private SharedPreferences.Editor prefEditor;
    private boolean check_done = false;
    public static String sVersionName = "";
    public static int sVersionCode = 0;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this);
        prefEditor = settings.edit();

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setHomeButtonEnabled(true);

        mServiceIntent = new Intent(this, TrustService.class);


        if (savedInstanceState == null) {
            mListFragment = new TrustListFragment();
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.add(R.id.fragment_container, mListFragment);
            fragmentTransaction.commit();
        } else {
            mListFragment = (TrustListFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_container);
        }

        if (settings.getBoolean("general.run", true) && !isMyServiceRunning()) {
            TrustService.doStartVoluntarily();
            startService(mServiceIntent);
        }

        try {
            sVersionCode = getPackageManager().getPackageInfo(getPackageName(), 0).versionCode;
            sVersionName = getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
        } catch (Exception e) {
            e.printStackTrace();
            sVersionCode = 0;
            sVersionName = "";
        }

        if (settings.getInt("news_version", 0) < sVersionCode) {
            showNews();
        }

        int followMe = settings.getInt("followMeDialog", 0);
        if (followMe == 10) {
            FollowMeDialog followDialog = FollowMeDialog.newInstance();
            followDialog.showDialog(this);
            prefEditor.putInt("followMeDialog", ++followMe).commit();
        } else if (followMe < 10) {
            prefEditor.putInt("followMeDialog", ++followMe).commit();
        }
    }

    public static void checkPattern(Activity a) {
        SharedPreferences pattern_prefs = a.getSharedPreferences(LockPatternActivity.PREFERENCE_FILE, 0);
        if (pattern_prefs.getString(LockPatternActivity.KEY, "").length() != 0) {
            Intent patternlock = new Intent(a, LockPatternActivity.class);
            patternlock.putExtra(LockPatternActivity.MODE, LockPatternActivity.LPMode.ComparePattern);
            patternlock.putExtra(LockPatternActivity.AutoSave, true);
            a.startActivityForResult(patternlock, LockPatternActivity.REQUEST_PATTERN_CHECK);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == LockPatternActivity.REQUEST_PATTERN_CHECK) {
            if (resultCode == RESULT_OK) {
                // Pattern correct
                check_done = true;
            } else {
                // Pattern failed
                finish();
            }
        } else if (requestCode == TrustPreferences.PREFERENCE_ACTIVITY) {
            if (resultCode == TrustPreferences.RESULT_EXIT_APP) {
                finish();
            } else {
                check_done = true;
            }
        }

    }

    @Override
    public void onResume() {
        super.onResume();
        if (!check_done)
            checkPattern(this);

        ActionBar actionBar = getSupportActionBar();
        if (checkPro(this)) {
            actionBar.setIcon(R.drawable.pro_icon);
            actionBar.setTitle(R.string.app_name_pro);
        } else {
            actionBar.setIcon(R.drawable.ic_launcher);
            actionBar.setTitle(R.string.app_name);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        check_done = false;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        MenuItem toggle = menu.findItem(R.id.starttracking);
        if (toggle != null) {
            if (!isMyServiceRunning()) {
                toggle.setTitle(R.string.run);
                toggle.setIcon(R.drawable.ic_action_playback_play);
            } else {
                toggle.setTitle(R.string.stop);
                toggle.setIcon(R.drawable.ic_action_playback_pause);
            }
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.starttracking:
                if (isMyServiceRunning()) {
                    TrustService.doPauseVoluntarily();
                    prefEditor.putBoolean("general.run", false);
                    stopService(mServiceIntent);
                } else {
                    TrustService.doStartVoluntarily();
                    prefEditor.putBoolean("general.run", true);
                    startService(mServiceIntent);
                }
                prefEditor.commit();
                supportInvalidateOptionsMenu();
                break;
            case R.id.filter:
                FilterDialogFragment filter = FilterDialogFragment.newInstance();
                filter.showDialog(getSupportFragmentManager());
                break;
            case R.id.settings:
                Intent startPreferencesActivity = new Intent(this, TrustPreferences.class);
                startPreferencesActivity.putExtra("check_done", true);
                startActivityForResult(startPreferencesActivity, TrustPreferences.PREFERENCE_ACTIVITY);
                break;
            case R.id.export:
                ExportDialogFragment ex = ExportDialogFragment.newInstance();
                ex.showDialog(getSupportFragmentManager());
                break;
            case R.id.follow:
                FollowMeDialog followDialog = FollowMeDialog.newInstance();
                followDialog.showDialog(this);
                break;
            case R.id.about:
                AboutDialogFragment about = AboutDialogFragment.newInstance();
                about.showDialog(getSupportFragmentManager());
                break;
            case android.R.id.home:
                showNews();
                break;
        }
        return true;
    }

    private void showNews() {
        NewsDialogFragment news = NewsDialogFragment.newInstance();
        news.showDialog(getSupportFragmentManager());
    }

    private boolean isMyServiceRunning() {
        ActivityManager manager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
        for (RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if ("eu.thedarken.trust.TrustService".equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    public static boolean checkPro(Context currentContext) {
        if (currentContext == null)
            return false;
        Context newProContext;
        try {
            newProContext = currentContext.createPackageContext("eu.thedarken.trust.pro", 0);
        } catch (NameNotFoundException e) {
            return false;
        }
        if (newProContext != null) {
            if (currentContext.getPackageManager().checkSignatures(currentContext.getPackageName(), newProContext.getPackageName()) == PackageManager.SIGNATURE_MATCH) {
                return true;
            }
        }
        return false;
    }
}