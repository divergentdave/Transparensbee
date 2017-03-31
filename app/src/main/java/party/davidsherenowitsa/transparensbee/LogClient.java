package party.davidsherenowitsa.transparensbee;

import android.util.Base64;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
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

    public static FutureTask<SignedTreeHead> getSTH(final LogServer log)
    {
        FutureTask<SignedTreeHead> futureTask = new FutureTask<>(new Callable<SignedTreeHead>() {
            @Override
            public SignedTreeHead call() throws Exception {
                return getSTHSynchronous(log);
            }
        });
        singleton.threadPoolExecutor.execute(futureTask);
        return futureTask;
    }

    public static SignedTreeHead getSTHSynchronous(LogServer log) throws IOException, JSONException
    {
        URL url = log.getGetSTHEndpoint();
        URLConnection conn = url.openConnection();
        InputStream is = conn.getInputStream();
        SignedTreeHead sth = parseSTH(IOUtils.slurpInputStreamUTF8(is));
        is.close();
        return sth;
    }

    public static SignedTreeHead parseSTH(String input) throws JSONException
    {
        JSONTokener tokener = new JSONTokener(input);
        JSONObject jsonSTH = (JSONObject)tokener.nextValue();
        return new SignedTreeHead(
                jsonSTH.getLong("timestamp"),
                jsonSTH.getLong("tree_size"),
                Base64.decode(jsonSTH.getString("sha256_root_hash"), Base64.DEFAULT),
                Base64.decode(jsonSTH.getString("tree_head_signature"), Base64.DEFAULT));
    }
}
