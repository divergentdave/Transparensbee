package party.davidsherenowitsa.transparensbee;

import android.util.Base64;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

public class LogClient {
    public static final int CONNECT_TIMEOUT = 15000, READ_TIMEOUT = 60000;
    private final String userAgent;

    public LogClient(String userAgent) {
        this.userAgent = userAgent;
    }

    public SignedTreeHead getSTHSynchronous(LogServer log) throws IOException, JSONException {
        URL url = log.getGetSTHEndpoint();
        URLConnection conn = url.openConnection();
        conn.setRequestProperty("User-Agent", userAgent);
        conn.setConnectTimeout(CONNECT_TIMEOUT);
        conn.setReadTimeout(READ_TIMEOUT);
        InputStream is = conn.getInputStream();
        SignedTreeHead sth = parseSTH(IOUtils.slurpInputStreamUTF8(is));
        is.close();
        return sth;
    }

    public SignedTreeHead parseSTH(String input) throws JSONException {
        JSONTokener tokener = new JSONTokener(input);
        JSONObject jsonSTH = (JSONObject) tokener.nextValue();
        return new SignedTreeHead(
                jsonSTH.getLong("timestamp"),
                jsonSTH.getLong("tree_size"),
                Base64.decode(jsonSTH.getString("sha256_root_hash"), Base64.DEFAULT),
                Base64.decode(jsonSTH.getString("tree_head_signature"), Base64.DEFAULT));
    }
}
