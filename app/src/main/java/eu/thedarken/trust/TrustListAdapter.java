package eu.thedarken.trust;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import eu.thedarken.trust.logentries.Descriptions;
import eu.thedarken.trust.logentries.LogEntry;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class TrustListAdapter extends BaseAdapter {
    private final List<LogEntry> mData = new ArrayList<LogEntry>();
    private boolean mAlternateTimeDisplay = false;
    private Typeface itemDefault;

    public TrustListAdapter() {
        itemDefault = Typeface.create("sans-serif", Typeface.NORMAL);
    }

    public void setData(List<LogEntry> data) {
        mData.clear();
        if (data != null)
            mData.addAll(data);
    }

    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public LogEntry getItem(int arg0) {
        return mData.get(arg0);
    }

    @Override
    public long getItemId(int arg0) {
        // TODO Auto-generated method stub
        return 0;
    }

    public void setAlternateTimeDisplay(boolean alternateTimeDisplay) {
        mAlternateTimeDisplay = alternateTimeDisplay;
    }

    public void prepend(LogEntry entry) {
        mData.add(0, entry);
    }

    static class ViewHolder {
        TextView time;
        TextView colortag;
        TextView info;
    }

    private final SimpleDateFormat sdf = new SimpleDateFormat("dd/MMM HH:mm");

    @Override
    public View getView(final int position, View convertView, final ViewGroup parent) {
        ViewHolder holder;
        int previous_type = 0;
        if (mData.size() > (position + 1)) {
            previous_type = mData.get(position + 1).getType();
        }

        if (convertView == null) {
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.trustlist_line, null);

            holder = new ViewHolder();
            holder.time = (TextView) convertView.findViewById(R.id.time);
            holder.colortag = (TextView) convertView.findViewById(R.id.colortag);
            holder.info = (TextView) convertView.findViewById(R.id.type);
            convertView.setTag(holder);
        } else {

            holder = (ViewHolder) convertView.getTag();
        }
        Context context = convertView.getContext();

        holder.time.setTypeface(itemDefault);
        holder.info.setTypeface(itemDefault);

        LogEntry entry = mData.get(position);
        if (mAlternateTimeDisplay) {
            holder.time.setText(sdf.format(entry.getTime()));
        } else {
            holder.time.setText(TrustListAdapter.calcTime(context, entry.getTime()));
        }

        int type = entry.getType();
        if (type == LogEntry.LOG_START || type == LogEntry.LOG_RESET || type == LogEntry.LOG_PAUSE
                || type == LogEntry.LOG_RESUME || type == LogEntry.LOG_SYSTEM_KILLED
                || type == LogEntry.LOG_SYSTEM_RESUME) {
            // Trust stuff
            holder.colortag.setBackgroundResource(R.color.trustevents);
            holder.info.setText(Descriptions.getDescription(context, entry));
        } else if (type == LogEntry.POWER_CONNECTED || type == LogEntry.POWER_DISCONNECTED
                || type == LogEntry.BOOT_COMPLETED || type == LogEntry.SHUTDOWN ||
                type == LogEntry.MEDIA_REMOVED) {
            // System stuff
            holder.colortag.setBackgroundResource(R.color.system_events);
            holder.info.setText(Descriptions.getDescription(context, entry));
        } else if (type == LogEntry.SCREEN_ON || type == LogEntry.SCREEN_OFF || type == LogEntry.USER_PRESENT) {
            // Screen stuff
            holder.colortag.setBackgroundResource(R.color.screen_events);
            holder.info.setText(Descriptions.getDescription(context, entry));
        } else if (type == LogEntry.PACKAGE_ADDED || type == LogEntry.PACKAGE_CHANGED
                || type == LogEntry.PACKAGE_DATA_CLEARED || type == LogEntry.PACKAGE_INSTALL
                || type == LogEntry.PACKAGE_REMOVED || type == LogEntry.PACKAGE_REPLACED
                || type == LogEntry.PACKAGE_RESTARTED || type == LogEntry.APP_ACTIVITY) {
            // App stuff
            holder.colortag.setBackgroundResource(R.color.app_events);
            holder.info.setText(Descriptions.getDescription(context, entry));
        } else if (type == LogEntry.LOST_CELL_SERVICE || type == LogEntry.RESTORED_CELL_SERVICE) {
            // Cell stuff
            holder.colortag.setBackgroundResource(R.color.cell_events);
            holder.info.setText(Descriptions.getDescription(context, entry));
        } else if (type == LogEntry.LOST_WIFI_CONNECTION || type == LogEntry.RESTORED_WIFI_CONNECTION) {
            // Wifi stuff
            holder.colortag.setBackgroundResource(R.color.wifi_events);
            holder.info.setText(Descriptions.getDescription(context, entry));
        } else if (type == LogEntry.NEW_INCOMING_CALL || type == LogEntry.NEW_OUTGOING_CALL
                || type == LogEntry.CALL_OFFHOOK || type == LogEntry.CALL_IDLE) {
            // Call
            holder.colortag.setBackgroundResource(R.color.call_events);
            if ((position + 1) <= mData.size()) {
                holder.info.setText(Descriptions.getDescription(context, entry, previous_type));
            } else {
                holder.info.setText(Descriptions.getDescription(context, entry));
            }
        } else {
            // else
            holder.colortag.setText("");
            holder.colortag.setBackgroundColor(Color.BLACK);
            holder.info.setText(Descriptions.getDescription(context, entry, 0));
        }

        return convertView;
    }

    private static String calcTime(Context c, long time) {
        String ret = null;
        long diff = System.currentTimeMillis() - time;
        if (diff < 60000) {
            // Display seconds
            int seconds = (int) (diff / 1000);
            ret = seconds + c.getString(R.string.second_abbreviation);
        } else if (diff < 3600000) {
            // Display minutes
            int minutes = (int) ((diff / 1000) / 60);
            ret = minutes + c.getString(R.string.minute_abbreviation);
        } else if (diff < 172800000) {
            // Display hours
            int hours = (int) (((diff / 1000) / 60) / 60);
            ret = hours + c.getString(R.string.hour_abbreviation);
        } else {
            // Display days
            int days = (int) ((((diff / 1000) / 60) / 60) / 24);
            ret = days + c.getString(R.string.day_abbreviation);
        }
        return ret;
    }

}