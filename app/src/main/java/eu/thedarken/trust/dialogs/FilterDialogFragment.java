package eu.thedarken.trust.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import eu.thedarken.trust.R;
import eu.thedarken.trust.TrustListFragment;

public class FilterDialogFragment extends DialogFragment {
    private SharedPreferences mSettings;

    public static FilterDialogFragment newInstance() {
        return new FilterDialogFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NO_FRAME, android.R.style.Theme_Dialog);
        mSettings = PreferenceManager.getDefaultSharedPreferences(getActivity());

    }

    public void showDialog(FragmentManager fragman) {
        // Create the fragment and show it as a dialog.
        DialogFragment show = FilterDialogFragment.newInstance();
        show.show(fragman, "FilterDialogFragment");

    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        ContextThemeWrapper context;
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
            context = new ContextThemeWrapper(getActivity(), android.R.style.Theme_Dialog);
        } else {
            context = new ContextThemeWrapper(getActivity(), R.style.TrustTheme);
        }
        AlertDialog.Builder bd = new AlertDialog.Builder(context);
        View v = LayoutInflater.from(context).inflate(R.layout.filter_dialog_fragment, null);

        CheckBox show_trust_events = (CheckBox) v.findViewById(R.id.show_trust_events);
        CheckBox show_system_events = (CheckBox) v.findViewById(R.id.show_system_events);
        CheckBox show_screen_events = (CheckBox) v.findViewById(R.id.show_screen_events);
        CheckBox show_app_events = (CheckBox) v.findViewById(R.id.show_app_events);
        CheckBox show_call_events = (CheckBox) v.findViewById(R.id.show_call_events);
        CheckBox show_cell_events = (CheckBox) v.findViewById(R.id.show_cell_events);
        CheckBox show_wifi_events = (CheckBox) v.findViewById(R.id.show_wifi_events);

        show_trust_events.setChecked(mSettings.getBoolean("show_trust_events", true));
        show_system_events.setChecked(mSettings.getBoolean("show_system_events", true));
        show_screen_events.setChecked(mSettings.getBoolean("show_screen_events", true));
        show_app_events.setChecked(mSettings.getBoolean("show_app_events", true));
        show_call_events.setChecked(mSettings.getBoolean("show_call_events", true));
        show_cell_events.setChecked(mSettings.getBoolean("show_cell_events", false));
        show_wifi_events.setChecked(mSettings.getBoolean("show_wifi_events", false));

        show_trust_events.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mSettings.edit().putBoolean("show_trust_events", isChecked).commit();
                sendRefresh();
            }
        });
        show_system_events.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mSettings.edit().putBoolean("show_system_events", isChecked).commit();
                sendRefresh();
            }
        });
        show_screen_events.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mSettings.edit().putBoolean("show_screen_events", isChecked).commit();
                sendRefresh();
            }
        });
        show_app_events.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mSettings.edit().putBoolean("show_app_events", isChecked).commit();
                sendRefresh();
            }
        });
        show_call_events.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mSettings.edit().putBoolean("show_call_events", isChecked).commit();
                sendRefresh();
            }
        });
        show_cell_events.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mSettings.edit().putBoolean("show_cell_events", isChecked).commit();
                sendRefresh();
            }
        });
        show_wifi_events.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mSettings.edit().putBoolean("show_wifi_events", isChecked).commit();
                sendRefresh();
            }
        });
        bd.setView(v);
        return bd.create();
    }

    private void sendRefresh() {
        Intent i = new Intent();
        i.setAction(TrustListFragment.NEW_REFRESH_INTENT);
        this.getActivity().sendBroadcast(i);
    }
}
