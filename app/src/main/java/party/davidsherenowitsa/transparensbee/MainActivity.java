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

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private InMemoryStatistics statistics;

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

        setAlarm(this);
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
