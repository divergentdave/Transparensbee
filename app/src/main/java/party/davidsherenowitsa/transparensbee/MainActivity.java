package party.davidsherenowitsa.transparensbee;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ListView;

import party.davidsherenowitsa.transparensbee.genutils.LogList;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private DBStatistics statistics;
    private StatsArrayAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FloatingActionButton runButton = (FloatingActionButton)findViewById(R.id.runButton);
        if (runButton != null) runButton.setOnClickListener(this);

        statistics = new DBStatistics(this);

        adapter = StatsArrayAdapter.factory(this,
                R.layout.header_list_item,
                R.layout.server_list_item,
                LogList.CT_LOGS,
                AuditorServer.AUDITORS,
                statistics);
        ListView listView = (ListView)findViewById(R.id.listView);
        if (listView != null) listView.setAdapter(adapter);
        statistics.registerListener(adapter);

        setAlarm(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        statistics.unregisterListener(adapter);
        adapter.notifyDataSetInvalidated();
        statistics.close();
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
