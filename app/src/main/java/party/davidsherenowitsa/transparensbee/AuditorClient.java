package party.davidsherenowitsa.transparensbee;

import android.util.Base64;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class AuditorClient {
    public static final int CONNECT_TIMEOUT = 15000, READ_TIMEOUT = 60000;
    private final String userAgent;

    public AuditorClient(String userAgent) {
        this.userAgent = userAgent;
    }

    public List<PollinationSignedTreeHead> pollinateSynchronous(AuditorServer auditor, Collection<PollinationSignedTreeHead> sths) throws IOException, JSONException {
        URL url = auditor.getPollinationEndpoint();
        URLConnection conn = url.openConnection();
        conn.setRequestProperty("User-Agent", userAgent);
        conn.setConnectTimeout(CONNECT_TIMEOUT);
        conn.setReadTimeout(READ_TIMEOUT);
        conn.setDoOutput(true);
        OutputStream os = conn.getOutputStream();
        serializeSTHList(os, sths);
        os.close();
        InputStream is = conn.getInputStream();
        List<PollinationSignedTreeHead> newSths = parseSTHList(is);
        is.close();
        return newSths;
    }

    public void serializeSTHList(OutputStream os, Collection<PollinationSignedTreeHead> sths) throws IOException, JSONException {
        JSONObject top = new JSONObject();
        JSONArray array = new JSONArray();
        for (PollinationSignedTreeHead sth : sths) {
            JSONObject obj = new JSONObject();
            obj.put("sth_version", sth.getVersion());
            obj.put("tree_size", sth.getTreeSize());
            obj.put("timestamp", sth.getTimestamp());
            obj.put("sha256_root_hash", Base64.encodeToString(sth.getRootHash(), Base64.NO_WRAP));
            obj.put("tree_head_signature", Base64.encodeToString(sth.getTreeHeadSignature(), Base64.NO_WRAP));
            obj.put("log_id", Base64.encodeToString(sth.getLogID(), Base64.NO_WRAP));
            array.put(obj);
        }
        top.put("sths", array);
        String string = top.toString();
        Writer writer = new OutputStreamWriter(os);
        writer.write(string);
        writer.flush();
    }

    public List<PollinationSignedTreeHead> parseSTHList(InputStream is) throws IOException, JSONException {
        String string = IOUtils.slurpInputStreamUTF8(is);
        JSONTokener tokener = new JSONTokener(string);
        JSONObject json = (JSONObject) tokener.nextValue();
        JSONArray array = json.getJSONArray("sths");
        List<PollinationSignedTreeHead> list = new ArrayList<>(array.length());
        for (int i = 0; i < array.length(); i++) {
            JSONObject sth = array.getJSONObject(i);
            if (sth.getLong("sth_version") != 0) {
                throw new RuntimeException("Unsupported STH version");  // TODO
            }
            list.add(new PollinationSignedTreeHead(
                    sth.getLong("timestamp"),
                    sth.getLong("tree_size"),
                    Base64.decode(sth.getString("sha256_root_hash"), Base64.NO_WRAP),
                    Base64.decode(sth.getString("tree_head_signature"), Base64.NO_WRAP),
                    Base64.decode(sth.getString("log_id"), Base64.NO_WRAP)
            ));
        }
        return list;
    }
}
