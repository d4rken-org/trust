package eu.thedarken.trust;

import android.app.Activity;
import android.content.*;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;
import eu.thedarken.trust.db.TrustDB;
import eu.thedarken.trust.dialogs.DetailsDialogFragment;
import eu.thedarken.trust.logentries.IDMaker;
import eu.thedarken.trust.logentries.LogEntry;

import java.util.Iterator;
import java.util.List;

public class TrustListFragment extends Fragment implements AdapterView.OnItemClickListener, AbsListView.OnScrollListener {
    final static String TAG = "TR:TrustListFragment";

    private TrustListAdapter backend;
    private EventReceiver event;
    private RefreshReceiver refresh;
    private Handler updateUI;
    private Runnable updateUITask;
    private IntentFilter live_event_filter;
    private IntentFilter refresh_filter;
    private ListView mListView;
    public final static String NEW_REFRESH_INTENT = "eu.thedarken.trust.intent.action.NEW_REFRESH";
    private SharedPreferences mSettings;
    private View mLoadingFooter;
    private boolean mIsLoading = false;
    private long mMaxItemsToLoad = 50;
    private long mCurrentMaxID = -1;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View layout = inflater.inflate(R.layout.fragment_trustlist, container, false);
        mListView = (ListView) layout.findViewById(R.id.lv_trustlist);
        mLoadingFooter = inflater.inflate(R.layout.trustlist_footer, null);
        return layout;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mSettings = PreferenceManager.getDefaultSharedPreferences(getActivity());
        backend = new TrustListAdapter();
        mListView.addFooterView(mLoadingFooter);
        mListView.setAdapter(backend);
        mListView.setOnScrollListener(this);
        mListView.setOnItemClickListener(this);
        event = new EventReceiver();
        live_event_filter = new IntentFilter();
        live_event_filter.addAction(TrustDB.NEW_EVENT_INTENT);

        refresh = new RefreshReceiver();
        refresh_filter = new IntentFilter();
        refresh_filter.addAction(TrustListFragment.NEW_REFRESH_INTENT);

        updateUI = new Handler();
        updateUITask = new Runnable() {
            public void run() {
                backend.notifyDataSetChanged();
                updateUI.postDelayed(this, 1000);
            }
        };
    }


    @Override
    public void onResume() {
        super.onResume();
        getActivity().registerReceiver(event, live_event_filter);
        getActivity().registerReceiver(refresh, refresh_filter);
        sendRefresh();
        updateUI.postDelayed(updateUITask, 1);
    }


    @Override
    public void onPause() {
        updateUI.removeCallbacks(updateUITask);
        getActivity().unregisterReceiver(event);
        getActivity().unregisterReceiver(refresh);
        super.onPause();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (view == mLoadingFooter)
            return;
        DetailsDialogFragment details = DetailsDialogFragment.newInstance(backend.getItem(position));
        details.showDialog(getActivity().getSupportFragmentManager());
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {

    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        int lastInScreen = firstVisibleItem + visibleItemCount;
        //is the bottom item visible & not loading more already ? Load more !
        if ((lastInScreen == totalItemCount) && !mIsLoading) {
            if (mCurrentMaxID != -1 && (mCurrentMaxID - mMaxItemsToLoad) > 0) {
                mMaxItemsToLoad += 50;
                sendRefresh();
            } else {
                mListView.removeFooterView(mLoadingFooter);
            }
        }
    }

    private void sendRefresh() {
        Intent i = new Intent();
        i.setAction(TrustListFragment.NEW_REFRESH_INTENT);
        this.getActivity().sendBroadcast(i);
    }

    private class EventReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d(TAG, "Forward received, calling refresh");
            LogEntry entry = getEntryByID(intent.getLongExtra("id", 0));
            if (entry != null) {
                Log.d(TAG, "type:" + entry.getType() + " id:" + entry.getID());
                backend.prepend(entry);
                backend.notifyDataSetChanged();
            } else {
                Log.d(TAG, "Couldn't retrieve entry with id:" + intent.getLongExtra("id", -55));
            }
        }
    }

    private class RefreshReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (getView() != null)
                getView().post(new Runnable() {
                    @Override
                    public void run() {
                        refresh();
                    }
                });

        }
    }

    private boolean isFiltered(int type) {
        if (type == 1 || type == 2 || type == 3 || type == 4 || type == 5 || type == 6) {
            if (mSettings.getBoolean("show_trust_events", true))
                return false;
        } else if (type == 7 || type == 8 || type == 9 || type == 10 || type == 25) {
            if (mSettings.getBoolean("show_system_events", true))
                return false;
        } else if (type == 11 || type == 12 || type == 13) {
            if (mSettings.getBoolean("show_screen_events", true))
                return false;
        } else if (type == 14 || type == 15 || type == 16 || type == 17 || type == 18 || type == 19 || type == 20 || type == 100) {
            if (mSettings.getBoolean("show_app_events", true))
                return false;
        } else if (type == 26 || type == 27 || type == 28 || type == 29) {
            if (mSettings.getBoolean("show_call_events", true))
                return false;
        } else if (type == 21 || type == 22) {
            if (mSettings.getBoolean("show_cell_events", false))
                return false;
        } else if (type == 23 || type == 24) {
            if (mSettings.getBoolean("show_wifi_events", false))
                return false;
        } else if (type == -2) {
            return false;
        }
        return true;
    }

    public LogEntry getEntryByID(long ID) {
        if (ID == 0)
            return null;
        LogEntry entry = TrustDB.getInstance(getActivity().getApplicationContext()).getEntryByID(ID);
        if (entry == null || isFiltered(entry.getType()))
            return null;
        return entry;
    }

    private void refresh() {
        Activity activity = getActivity();
        if(activity == null)
            return;
        if (mIsLoading)
            return;
        mIsLoading = true;
        backend.setAlternateTimeDisplay(mSettings.getBoolean("display.time.alternative", false));
        int keep_days;
        try {
            keep_days = Integer.parseInt(mSettings.getString("database.keep.time", "14"));
        } catch (Exception e) {
            keep_days = 0;
            SharedPreferences.Editor prefEditor = mSettings.edit();
            prefEditor.putString("database.keep.time", "14");
            prefEditor.commit();
        }
        TrustDB db = TrustDB.getInstance(activity.getApplicationContext());
        long keep_for = keep_days * TrustDB.DAY;
        if (keep_for > 0) {
            db.clean(keep_for);
        }
        boolean is_pro = TrustActivity.checkPro(activity);
        mCurrentMaxID = IDMaker.getInstance(activity.getApplicationContext()).getID();
        List<LogEntry> refreshedData = db.getEntries(mCurrentMaxID - mMaxItemsToLoad, mCurrentMaxID);
        if (!is_pro && !refreshedData.isEmpty() && ((System.currentTimeMillis() - refreshedData.get(refreshedData.size() - 1).getTime()) > TrustDB.DAY)) {
            LogEntry buy = new LogEntry();
            buy.setID(-2);
            buy.setType(-2);
            buy.setTime(System.currentTimeMillis());
            refreshedData.add(buy);
        }
        Iterator<LogEntry> i = refreshedData.iterator();
        while (i.hasNext()) {
            LogEntry e = i.next();
            if (isFiltered(e.getType()) || (e.getType() != -2 && !is_pro && System.currentTimeMillis() - e.getTime() > TrustDB.DAY))
                i.remove();
        }

        backend.setData(refreshedData);
        backend.notifyDataSetChanged();
        mIsLoading = false;
    }
}
