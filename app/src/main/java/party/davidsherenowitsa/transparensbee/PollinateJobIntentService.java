package party.davidsherenowitsa.transparensbee;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.app.JobIntentService;
import android.util.Pair;

import org.json.JSONException;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import party.davidsherenowitsa.transparensbee.genutils.LogList;

public class PollinateJobIntentService extends JobIntentService {
    private static final String ACTION_POLLINATE = "party.davidsherenowitsa.transparensbee.action.POLLINATE";
    private static final int JOB_ID_POLLINATE = 0;
    private static final String USER_AGENT = "Transparensbee (https://github.com/divergentdave/Transparensbee)";

    private static boolean stop = false;

    public static void startActionPollinate(Context context) {
        Intent intent = new Intent(context, PollinateJobIntentService.class);
        intent.setAction(ACTION_POLLINATE);
        enqueueWork(context, PollinateJobIntentService.class, JOB_ID_POLLINATE, intent);
    }

    @Override
    protected void onHandleWork(@NonNull Intent intent) {
        final String action = intent.getAction();
        if (ACTION_POLLINATE.equals(action)) {
            handleActionPollinate();
        }
    }

    /**
     * Handle action Pollinate in the provided background thread.
     */
    private void handleActionPollinate() {
        stop = false;
        DBPollen pollen = new DBPollen(this);
        final DBStatistics statistics = new DBStatistics(this);
        statistics.open();
        try {
            BlockingQueue<Runnable> workQueue = new LinkedBlockingQueue<>();
            ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(0, 10, 1, TimeUnit.SECONDS, workQueue);
            CompletionService<Pair<LogServer, SignedTreeHead>> executorCompletionService = new ExecutorCompletionService<>(threadPoolExecutor);
            final LogClient logClient = new LogClient(USER_AGENT);

            for (final LogServer log : LogList.CT_LOGS) {
                executorCompletionService.submit(new Callable<Pair<LogServer, SignedTreeHead>>() {
                    @Override
                    public Pair<LogServer, SignedTreeHead> call() throws Exception {
                        try {
                            return new Pair<>(log, logClient.getSTHSynchronous(log));
                        } catch (SocketTimeoutException e) {
                            statistics.addFailure(log);
                            return null;
                        } catch (UnknownHostException e) {
                            statistics.addFailure(log);
                            return null;
                        } catch (Exception e) {
                            e.printStackTrace();
                            statistics.addFailure(log);
                            return null;
                        }
                    }
                });
            }
            threadPoolExecutor.shutdown();

            //noinspection ForLoopReplaceableByForEach
            for (int i = 0; i < LogList.CT_LOGS.length; i++) {
                try {
                    Pair<LogServer, SignedTreeHead> results = executorCompletionService.take().get();
                    if (results != null) {
                        LogServer log = results.first;
                        SignedTreeHead sth = results.second;
                        pollen.addFromLog(log, sth);
                        statistics.addSuccess(log);
                    }
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }
                if (stop) {
                    threadPoolExecutor.shutdownNow();
                    break;
                }
            }

            AuditorClient auditorClient = new AuditorClient(USER_AGENT);
            List<AuditorServer> auditors = new ArrayList<>(Arrays.asList(AuditorServer.AUDITORS));
            Collections.shuffle(auditors);
            for (AuditorServer auditor : auditors) {
                try {
                    Collection<PollinationSignedTreeHead> sthsIn, sthsOut;
                    sthsOut = pollen.getForAuditor(auditor);
                    sthsIn = auditorClient.pollinateSynchronous(auditor, sthsOut);
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
                if (stop) {
                    break;
                }
            }
            pollen.cleanup();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } finally {
            pollen.close();
            statistics.close();
        }
    }

    @Override
    public boolean onStopCurrentWork() {
        stop = true;
        return true;
    }
}
