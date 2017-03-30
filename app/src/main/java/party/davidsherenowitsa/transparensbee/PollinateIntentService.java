package party.davidsherenowitsa.transparensbee;

import android.app.IntentService;
import android.content.Intent;
import android.content.Context;

import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

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
        for (CertificateTransparencyLog log : CertificateTransparencyLog.CT_LOGS)
        {
            System.out.println(log.getHumanReadableName());
            try {
                log.getSTHSynchronous();
            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }
        }
        List<AuditorServer> auditors = new ArrayList<>(Arrays.asList(AuditorServer.AUDITORS));
        Collections.shuffle(auditors);
        for (AuditorServer auditor : auditors)
        {
            System.out.println(auditor.getHumanReadableName());
        }
    }
}
