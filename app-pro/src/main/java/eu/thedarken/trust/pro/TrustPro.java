package eu.thedarken.trust.pro;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.Toast;

public class TrustPro extends ActionBarActivity {
	private static final String PACKAGE_TRUST = "eu.thedarken.trust";
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
	}

	public void hide_icon(View v) {
		PackageManager p = getPackageManager();
		p.setComponentEnabledSetting(getComponentName(), PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP);
		Toast.makeText(getApplicationContext(), "Icon hidden (may need reboot)", Toast.LENGTH_SHORT).show();
	}

	public void trust(View v) {
		PackageManager pm = getPackageManager();
		try {
			pm.getPackageInfo(PACKAGE_TRUST, PackageManager.GET_ACTIVITIES);
			Intent LaunchIntent = getPackageManager().getLaunchIntentForPackage(PACKAGE_TRUST);
			startActivity(LaunchIntent);
		} catch (PackageManager.NameNotFoundException e1) {
			try {
				Intent marketIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + PACKAGE_TRUST));
				startActivity(marketIntent);
			} catch (ActivityNotFoundException e2) {
				e2.printStackTrace();
			}
		}
	}
}