package party.davidsherenowitsa.transparensbee;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class StatsArrayAdapter extends ArrayAdapter<Server> implements StatisticsListener {
    private Activity activity;
    private int viewId;
    private DBStatistics statistics;

    public StatsArrayAdapter(Activity activity, int id, Server[] array, DBStatistics statistics) {
        super(activity, id, array);
        this.activity = activity;
        this.viewId = id;
        this.statistics = statistics;
    }

    @Override
    @NonNull
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        if (convertView == null || convertView.getId() != viewId) {
            LayoutInflater layoutInflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(viewId, parent, false);
        }
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
}
