package party.davidsherenowitsa.transparensbee;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class AlarmReceiver extends BroadcastReceiver {
    public static final int PI_REQUEST_CODE = 0;

    @Override
    public void onReceive(Context context, Intent intent) {
        PollinateJobIntentService.startActionPollinate(context);
    }
}
