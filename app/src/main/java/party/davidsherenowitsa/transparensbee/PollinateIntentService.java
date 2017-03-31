package party.davidsherenowitsa.transparensbee;

import android.app.IntentService;
import android.content.Intent;
import android.content.Context;
import android.util.Base64;

import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

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
        int n = LogServer.CT_LOGS.length;
        ArrayList<FutureTask<SignedTreeHead>> futures = new ArrayList<>(n);
        for (int i = 0; i < n; i++)
        {
            LogServer log = LogServer.CT_LOGS[i];
            futures.add(LogClient.getSTH(log));
        }
        for (int i = 0; i < n; i++)
        {
            try {
                LogServer log = LogServer.CT_LOGS[i];
                SignedTreeHead sth = futures.get(i).get();
                System.out.printf("%s: %s\n", log.getHumanReadableName(), Base64.encodeToString(sth.getRootHash(), Base64.NO_WRAP));
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        }
        List<AuditorServer> auditors = new ArrayList<>(Arrays.asList(AuditorServer.AUDITORS));
        Collections.shuffle(auditors);
        for (AuditorServer auditor : auditors)
        {
            try {
                System.out.printf("%s %s\n",
                        auditor.getHumanReadableName(),
                        AuditorClient.pollinateSynchronous(auditor, new LinkedList<PollinationSignedTreeHead>()));
            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }
        }
    }
}
