package eu.thedarken.trust.dialogs;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.view.ContextThemeWrapper;
import android.view.View;

import eu.thedarken.trust.R;

public class AboutDialogFragment extends DialogFragment {
    private int mVersionCode;
    private String mVersionName;

    public static AboutDialogFragment newInstance() {
        return new AboutDialogFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            mVersionCode = getActivity().getPackageManager().getPackageInfo(getActivity().getPackageName(), 0).versionCode;
            mVersionName = getActivity().getPackageManager().getPackageInfo(getActivity().getPackageName(), 0).versionName;
        } catch (Exception e) {
            e.printStackTrace();
            mVersionCode = 0;
            mVersionName = "";
        }
    }

    public void showDialog(FragmentManager fragman) {
        // Create the fragment and show it as a dialog.
        DialogFragment show = AboutDialogFragment.newInstance();
        show.show(fragman, "AboutDialogFragment");
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
                .setTitle(getString(R.string.app_name) + " " + mVersionName + "(" + mVersionCode + ")")
                .setCancelable(true)
                .setPositiveButton(getString(R.string.visit_market), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent browserIntent = new Intent("android.intent.action.VIEW", Uri.parse("https://play.google.com/store/apps/details?id=eu.thedarken.trust"));
                        startActivity(browserIntent);
                    }
                })
                .setNegativeButton(getString(R.string.hide), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        getDialog().dismiss();
                    }
                });
        View dialoglayout = getActivity().getLayoutInflater().inflate(R.layout.about_dialog_fragment, null);
        bd.setView(dialoglayout);

        return bd.create();
    }

}