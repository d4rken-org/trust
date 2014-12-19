package eu.thedarken.trust.dialogs;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.view.ContextThemeWrapper;
import android.widget.Toast;
import eu.thedarken.trust.R;
import eu.thedarken.trust.db.TrustDB;
import eu.thedarken.trust.logentries.Descriptions;
import eu.thedarken.trust.logentries.LogEntry;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class ExportDialogFragment extends DialogFragment {

    private final static String TAG = "TR:ExportDialogFragment";
    private final static String UTF8 = "utf8";
    private List<String> content;
    private long starttime;
    private long endtime;

    public static ExportDialogFragment newInstance() {
        return new ExportDialogFragment();
    }

    public void showDialog(FragmentManager fragman) {
        // Create the fragment and show it as a dialog.
        DialogFragment show = ExportDialogFragment.newInstance();
        show.show(fragman, "ExportDialogFragment");
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        ContextThemeWrapper context;
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
            context = new ContextThemeWrapper(getActivity(), android.R.style.Theme_Dialog);
        } else {
            context = new ContextThemeWrapper(getActivity(), R.style.TrustTheme);
        }
        Builder bd = new AlertDialog.Builder(context).setTitle(R.string.export_database_dialog_title)
                .setMessage(getString(R.string.export_database_dialog_message)).setCancelable(true)
                .setPositiveButton(R.string.export, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        new run_export().execute();
                    }
                }).setNegativeButton(R.string.abort, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        getDialog().dismiss();
                    }
                });
        return bd.create();
    }

    private class run_export extends AsyncTask<String, Void, Boolean> {
        private ProgressDialog pd;
        private Context mContext;

        protected void onPreExecute() {
            mContext = getActivity();
            pd = new ProgressDialog(getActivity());
            pd.setIndeterminate(true);
            pd.setCancelable(false);
            pd.setMessage(getString(R.string.working) + "...");
            pd.show();
        }

        @Override
        protected void onPostExecute(final Boolean ok) {
            Context activity = getActivity();
            if (activity != null) {
                if (ok) {
                    Toast.makeText(getActivity(), R.string.success, Toast.LENGTH_SHORT);
                } else {
                    Toast.makeText(getActivity(), R.string.error, Toast.LENGTH_SHORT);
                }
            }
            pd.dismiss();
        }

        @Override
        protected Boolean doInBackground(String... params) {
            TrustDB db = TrustDB.getInstance(mContext.getApplicationContext());
            ArrayList<LogEntry> entries = db.getEntries();
            if (!entries.isEmpty()) {
                ArrayList<String> generated_files = new ArrayList<String>();

                generated_files.add("exceldatetime;date;time;timestamp;ID;type;extra;description");
                SimpleDateFormat day = new SimpleDateFormat("dd.MMM.yyyy");
                SimpleDateFormat hour = new SimpleDateFormat("HH:mm:ss");
                for (LogEntry e : entries) {
                    StringBuilder b = new StringBuilder();
                    b.append(((double) e.getTime() / (double) 86400000) + 25569);
                    b.append(";");
                    b.append("\"").append(day.format(e.getTime())).append("\"");
                    b.append(";");
                    b.append("\"").append(hour.format(e.getTime())).append("\"");
                    b.append(";");
                    b.append(e.getTime());
                    b.append(";");
                    b.append(e.getID());
                    b.append(";");
                    b.append(e.getType());
                    b.append(";");
                    b.append("\"").append(e.getExtra()).append("\"");
                    b.append(";");
                    b.append("\"").append(Descriptions.getDescription(mContext, e)).append("\"");
                    generated_files.add(b.toString());
                }
                endtime = entries.get(0).getTime();
                starttime = entries.get(entries.size() - 1).getTime();

                content = generated_files;
            }

            if (!content.isEmpty()) {
                SimpleDateFormat sdf = new SimpleDateFormat("dd.MMM.yyyy HH_mm");
                String ends_at = "(" + sdf.format(endtime) + ")";
                String begins_at = "(" + sdf.format(starttime) + ")";
                File filelocation = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/Download");
                if (!filelocation.exists())
                    filelocation.mkdir();

                File file = new File(filelocation, "Trust Eventdata " + begins_at + "-" + ends_at + ".csv");
                if (file.exists())
                    file.delete();
                try {
                    BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), UTF8));
                    for (String s : content) {
                        bw.write(s + "\n");
                    }
                    bw.flush();
                    bw.close();
                } catch (Exception e) {
                    e.printStackTrace();
                    return false;
                }
            }
            return true;
        }
    }
}