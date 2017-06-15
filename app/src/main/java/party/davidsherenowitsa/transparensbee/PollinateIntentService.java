package party.davidsherenowitsa.transparensbee;

import android.app.IntentService;
import android.content.Intent;
import android.content.Context;

import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

public class PollinateIntentService extends IntentService {
    private static final String ACTION_POLLINATE = "party.davidsherenowitsa.transparensbee.action.POLLINATE";

    private InMemoryStatistics statistics;

    public PollinateIntentService() {
        super("PollinateIntentService");
        statistics = InMemoryStatistics.getInstance();
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
        DBPollen pollen = new DBPollen(this);
        try {
            int n = LogServer.CT_LOGS.length;
            ArrayList<FutureTask<SignedTreeHead>> futures = new ArrayList<>(n);
            for (int i = 0; i < n; i++) {
                LogServer log = LogServer.CT_LOGS[i];
                futures.add(LogClient.getSTH(log));
            }
            for (int i = 0; i < n; i++) {
                LogServer log = LogServer.CT_LOGS[i];
                try {
                    SignedTreeHead sth = futures.get(i).get();
                    pollen.addFromLog(log, sth);
                    statistics.addSuccess(log);
                } catch (InterruptedException | ExecutionException e) {
                    e.printStackTrace();
                    statistics.addFailure(log);
                }
            }
            List<AuditorServer> auditors = new ArrayList<>(Arrays.asList(AuditorServer.AUDITORS));
            Collections.shuffle(auditors);
            for (AuditorServer auditor : auditors) {
                try {
                    Collection<PollinationSignedTreeHead> sthsIn, sthsOut;
                    sthsOut = pollen.getForAuditor(auditor);
                    sthsIn = AuditorClient.pollinateSynchronous(auditor, sthsOut);
                    System.out.printf("%s %s %s\n",
                            auditor.getHumanReadableName(),
                            sthsOut.size(),
                            sthsIn.size());
                    pollen.addFromAuditor(auditor, sthsIn); // New STHs received from auditor
                    pollen.addFromAuditor(auditor, sthsOut); // STHs we just sent, mark as seen
                    statistics.addSuccess(auditor);
                } catch (IOException | JSONException e) {
                    e.printStackTrace();
                    statistics.addFailure(auditor);
                }
            }
            pollen.cleanup();
        } finally {
            pollen.close();
        }
    }
}
