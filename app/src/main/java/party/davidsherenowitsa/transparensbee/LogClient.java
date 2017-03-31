package party.davidsherenowitsa.transparensbee;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class LogClient {
    private static LogClient singleton;
    static {
        singleton = new LogClient();
    }

    private final ThreadPoolExecutor threadPoolExecutor;

    private LogClient()
    {
        BlockingQueue<Runnable> workQueue = new LinkedBlockingQueue<>();
        threadPoolExecutor = new ThreadPoolExecutor(0, 10, 1, TimeUnit.SECONDS, workQueue);
    }

    public static FutureTask<SignedTreeHead> getSTH(final CertificateTransparencyLog log)
    {
        FutureTask<SignedTreeHead> futureTask = new FutureTask<>(new Callable<SignedTreeHead>() {
            @Override
            public SignedTreeHead call() throws Exception {
                return log.getSTHSynchronous();
            }
        });
        singleton.threadPoolExecutor.execute(futureTask);
        return futureTask;
    }
}
