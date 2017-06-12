package party.davidsherenowitsa.transparensbee;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class StatsArrayAdapter extends ArrayAdapter<Server> implements Statistics.StatisticsListener {
    private Context context;
    private int viewId;

    public StatsArrayAdapter(Context context, int id, Server[] array) {
        super(context, id, array);
        this.context = context;
        this.viewId = id;
    }

    @Override @NonNull
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        if (convertView == null || convertView.getId() != viewId) {
            LayoutInflater layoutInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(viewId, parent, false);
        }
        TextView textView = convertView.findViewById(R.id.listItemTextView);
        if (textView != null) {
            Server server = getItem(position);
            if (server != null) {
                textView.setText(server.getHumanReadableName());
            } else {
                textView.setText("");
            }
        }
        return convertView;
    }

    @Override
    public void notifyLog(LogServer log) {
        super.notifyDataSetChanged();
    }

    @Override
    public void notifyAuditor(AuditorServer auditor) {
        super.notifyDataSetChanged();
    }
}
