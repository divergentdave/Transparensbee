package party.davidsherenowitsa.transparensbee;

import android.app.IntentService;
import android.content.Intent;
import android.content.Context;

public class PollinateIntentService extends IntentService {
    private static final String ACTION_POLLINATE = "party.davidsherenowitsa.transparensbee.action.POLLINATE";

    public PollinateIntentService() {
        super("PollinateIntentService");
    }

    /**
     * Starts this service to perform action Pollinate. If the service is
     * already performing a task, this action will be queued.
     *
     * @see IntentService
     */
    public static void startActionFoo(Context context) {
        Intent intent = new Intent(context, PollinateIntentService.class);
        intent.setAction(ACTION_POLLINATE);
        context.startService(intent);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_POLLINATE.equals(action)) {
                handleActionPollinate();
            }
        }
    }

    /**
     * Handle action Pollinate in the provided background thread.
     */
    private void handleActionPollinate() {
        // TODO: Handle action Pollinate
        System.out.println("Hello world!");
    }
}
