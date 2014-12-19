package eu.thedarken.trust.dialogs;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.view.ContextThemeWrapper;
import android.view.View;
import android.widget.Toast;
import eu.thedarken.trust.R;
import eu.thedarken.trust.TrustActivity;

public class NewsDialogFragment extends DialogFragment {
    public static NewsDialogFragment newInstance() {
        return new NewsDialogFragment();
    }

    public void showDialog(FragmentManager fragman) {
        // Create the fragment and show it as a dialog.
        DialogFragment show = NewsDialogFragment.newInstance();
        show.show(fragman, "NewsDialogFragment");
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        ContextThemeWrapper context;
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
            context = new ContextThemeWrapper(getActivity(), android.R.style.Theme_Dialog);
        } else {
            context = new ContextThemeWrapper(getActivity(), R.style.TrustTheme);
        }
        Builder bd = new AlertDialog.Builder(context)
                .setTitle(getString(R.string.app_name) + " " + TrustActivity.sVersionName)
                .setCancelable(true)
                .setPositiveButton(getString(R.string.buy_pro), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        try {
                            Intent marketIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=eu.thedarken.trust.pro"));
                            startActivity(marketIntent);
                        } catch (Exception e) {
                            Toast.makeText(getActivity(), R.string.no_market_application_found, Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .setNegativeButton(getString(R.string.hide), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getActivity());
                        SharedPreferences.Editor prefEditor = settings.edit();
                        prefEditor.putInt("news_version", TrustActivity.sVersionCode);
                        prefEditor.commit();
                        getDialog().dismiss();
                    }
                });
        View dialoglayout = getActivity().getLayoutInflater().inflate(R.layout.news_dialog_fragment, null);
        bd.setView(dialoglayout);

        return bd.create();
    }


}