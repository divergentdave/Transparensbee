package party.davidsherenowitsa.transparensbee;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import androidx.annotation.NonNull;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class StatsArrayAdapter extends ArrayAdapter<Server> implements StatisticsListener {
    private Activity activity;
    private int layoutIdHeader;
    private int viewIdServer;
    private DBStatistics statistics;
    private int logCount;
    private String logHeaderText;
    private String auditorHeaderText;

    public static StatsArrayAdapter factory(Activity activity,
                                            int viewIdHeader,
                                            int viewIdServer,
                                            LogServer[] logServers,
                                            AuditorServer[] auditorServers,
                                            DBStatistics statistics) {
        Server[] array = new Server[logServers.length + auditorServers.length + 2];
        System.arraycopy(logServers, 0, array, 1, logServers.length);
        System.arraycopy(auditorServers, 0, array, logServers.length + 2, auditorServers.length);
        return new StatsArrayAdapter(activity,
                viewIdHeader,
                viewIdServer,
                array,
                statistics,
                logServers.length);
    }

    private StatsArrayAdapter(Activity activity,
                              int viewIdHeader,
                              int viewIdServer,
                              Server[] array,
                              DBStatistics statistics,
                              int logCount) {
        super(activity, 0, array);
        this.activity = activity;
        this.layoutIdHeader = viewIdHeader;
        this.viewIdServer = viewIdServer;
        this.statistics = statistics;
        this.logCount = logCount;
    }

    @Override
    @NonNull
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        int expectedId;
        if (position == 0 || position == logCount + 1) {
            expectedId = layoutIdHeader;
        } else {
            expectedId = viewIdServer;
        }

        if (convertView == null || convertView.getId() != expectedId) {
            LayoutInflater layoutInflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(expectedId, parent, false);
        }

        if (expectedId == layoutIdHeader) {
            TextView textView = convertView.findViewById(R.id.textView);
            if (textView != null) {
                if (logHeaderText == null || auditorHeaderText == null) {
                    Resources resources = activity.getResources();
                    logHeaderText = resources.getString(R.string.log_server_status);
                    auditorHeaderText = resources.getString(R.string.auditor_server_status);
                }

                if (position == 0) {
                    textView.setText(logHeaderText);
                } else {
                    textView.setText(auditorHeaderText);
                }
            }
        } else {
            TextView nameTextView = convertView.findViewById(R.id.listItemName),
                    successTextView = convertView.findViewById(R.id.listItemSuccess),
                    failureTextView = convertView.findViewById(R.id.listItemFailure);
            if (nameTextView != null && successTextView != null && failureTextView != null) {
                Server server = getItem(position);
                assert server != null;
                nameTextView.setText(server.getHumanReadableName());
                Pair<Integer, Integer> pair = statistics.getServerSuccessFailure(server);
                int success, failure;
                if (pair != null) {
                    success = pair.first;
                    failure = pair.second;
                } else {
                    success = 0;
                    failure = 0;
                }
                successTextView.setText(activity.getString(R.string.list_item_success, success));
                failureTextView.setText(activity.getString(R.string.list_item_failure, failure));
            }
        }
        return convertView;
    }

    @Override
    public void notifyChange() {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                StatsArrayAdapter.super.notifyDataSetChanged();
            }
        });
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    @Override
    public boolean isEnabled(int position) {
        return !(position == 0 || position == logCount + 1);
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0 || position == logCount + 1) {
            return 1;
        } else {
            return 0;
        }
    }
}
