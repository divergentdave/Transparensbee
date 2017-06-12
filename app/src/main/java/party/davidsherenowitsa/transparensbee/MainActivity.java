package party.davidsherenowitsa.transparensbee;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private InMemoryStatistics statistics;
    private StatsArrayAdapter logAdapter, auditorAdapter;

    public MainActivity() {
        super();
        statistics = InMemoryStatistics.getInstance();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button runButton = (Button)findViewById(R.id.runButton);
        if (runButton != null) runButton.setOnClickListener(this);

        logAdapter = new StatsArrayAdapter(this, R.layout.log_list_item, LogServer.CT_LOGS);
        ListView logListView = (ListView)findViewById(R.id.logListView);
        if (logListView != null) logListView.setAdapter(logAdapter);
        statistics.registerListener(logAdapter);

        auditorAdapter = new StatsArrayAdapter(this, R.layout.auditor_list_item, AuditorServer.AUDITORS);
        ListView auditorListView = (ListView)findViewById(R.id.auditorListView);
        if (auditorListView != null) auditorListView.setAdapter(auditorAdapter);
        statistics.registerListener(auditorAdapter);

        setAlarm(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        statistics.unregisterListener(logAdapter);
        logAdapter.notifyDataSetInvalidated();
        statistics.unregisterListener(auditorAdapter);
        auditorAdapter.notifyDataSetInvalidated();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.runButton:
                PollinateIntentService.startActionFoo(this);
                break;
        }
    }

    public static void setAlarm(Context context) {
        AlarmManager alarmManager = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, AlarmReceiver.class);
        PendingIntent alarmIntent = PendingIntent.getBroadcast(context, AlarmReceiver.PI_REQUEST_CODE, intent, 0);
        alarmManager.setInexactRepeating(AlarmManager.ELAPSED_REALTIME,
                SystemClock.elapsedRealtime() + AlarmManager.INTERVAL_HOUR,
                AlarmManager.INTERVAL_HOUR,
                alarmIntent);
    }
}
