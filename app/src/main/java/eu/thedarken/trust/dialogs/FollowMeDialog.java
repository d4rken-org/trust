package eu.thedarken.trust.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.ActionBarActivity;
import android.view.ContextThemeWrapper;
import android.widget.Toast;

import eu.thedarken.trust.R;

public class FollowMeDialog extends DialogFragment {
    public static FollowMeDialog newInstance() {
        FollowMeDialog f = new FollowMeDialog();
        Bundle args = new Bundle();
        f.setArguments(args);
        return f;
    }

    public void showDialog(ActionBarActivity a) {
        show(a.getSupportFragmentManager(), FollowMeDialog.class.getSimpleName());
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
        AlertDialog dialog = bd.create();
        dialog.setTitle("Follow darken?");
        dialog.setIcon(R.drawable.ic_launcher);

        dialog.setButton(DialogInterface.BUTTON_POSITIVE, "Google+", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse("https://plus.google.com/116634499773478773276"));
                startActivity(i);
            }
        });

        dialog.setButton(DialogInterface.BUTTON_NEUTRAL, "Twitter", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.twitter.com/d4rken"));
                startActivity(i);
            }
        });

        dialog.setButton(DialogInterface.BUTTON_NEGATIVE, "No", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                Toast.makeText(getActivity(), "No? Thats okay too :-).", Toast.LENGTH_SHORT).show();
            }
        });

        dialog.setMessage("Stay up to date on recent developments,\n" +
                "preview updates or just give feedback!");
        dialog.setTitle("Follow darken?");
        return dialog;
    }
}
