package eu.thedarken.trust.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.text.SimpleDateFormat;

import eu.thedarken.trust.R;
import eu.thedarken.trust.logentries.Descriptions;
import eu.thedarken.trust.logentries.LogEntry;

public class DetailsDialogFragment extends DialogFragment {

    private final SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy, HH:mm");
    private LogEntry entry;

    public static DetailsDialogFragment newInstance(LogEntry entry) {
        DetailsDialogFragment f = new DetailsDialogFragment();
        Bundle b = new Bundle();
        b.putParcelable("entry", entry);
        f.setArguments(b);
        return f;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NO_FRAME, android.R.style.Theme_Dialog);
        entry = getArguments().getParcelable("entry");
    }

    public void showDialog(FragmentManager fragman) {
        this.show(fragman, "DetailsDialogFragment");
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
        View layout = LayoutInflater.from(context).inflate(R.layout.details_dialog_fragment, null);
        String formateddate = sdf.format(entry.getTime());
        TextView date = (TextView) layout.findViewById(R.id.tv_date);
        date.setText(formateddate);

        TextView entry_id = (TextView) layout.findViewById(R.id.tv_id);
        entry_id.append(" " + entry.getID());

        TextView desc = (TextView) layout.findViewById(R.id.tv_infocontent);
        desc.setText(Descriptions.getDetailedDescription(getActivity(), entry));

        TextView gplayLink = (TextView) layout.findViewById(R.id.tv_gplay);
        LinearLayout idline = (LinearLayout) layout.findViewById(R.id.ll_idline);
        LinearLayout dateline = (LinearLayout) layout.findViewById(R.id.ll_dateline);
        if (entry.getType() >= LogEntry.PACKAGE_ADDED && entry.getType() <= LogEntry.PACKAGE_RESTARTED || entry.getType() == LogEntry.APP_ACTIVITY) {
            gplayLink.setVisibility(View.VISIBLE);
            gplayLink.setMovementMethod(LinkMovementMethod.getInstance());

            String link = "<a href=\"https://play.google.com/store/apps/details?id=" + entry.getExtra().replace("package:", "").replace("'", "") + "\">"
                    + getString(R.string.show_app_in_gplay) + "</a>";
            gplayLink.setText(Html.fromHtml(link));
        } else if (entry.getType() == -2) {
            idline.setVisibility(View.GONE);
            dateline.setVisibility(View.GONE);

            entry_id.setVisibility(View.INVISIBLE);
            gplayLink.setVisibility(View.VISIBLE);
            gplayLink.setMovementMethod(LinkMovementMethod.getInstance());

            String trustprolink = "<a href=\"https://play.google.com/store/apps/details?id=eu.thedarken.trust.pro" + "\">"
                    + getString(R.string.buy_pro) + "</a>";
            gplayLink.setText(Html.fromHtml(trustprolink));
        } else {
            idline.setVisibility(View.VISIBLE);
            dateline.setVisibility(View.VISIBLE);
        }
        bd.setView(layout);
        return bd.create();
    }
}
