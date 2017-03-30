package party.davidsherenowitsa.transparensbee;

import android.util.Base64;

import org.json.JSONException;
import org.json.JSONTokener;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

public class CertificateTransparencyLog {
    public static final CertificateTransparencyLog PILOT = new CertificateTransparencyLog(
            "ct.googleapis.com/pilot",
            Base64.decode("MFkwEwYHKoZIzj0CAQYIKoZIzj0DAQcDQgAEfahLEimAoz2t01p3uMziiLOl/fHTDM0YDOhBRuiBARsV4UvxG2LdNgoIGLrtCzWE0J5APC2em4JlvR8EEEFMoA==",
                    Base64.DEFAULT),
            "Google 'Pilot' log");
    public static final CertificateTransparencyLog AVIATOR = new CertificateTransparencyLog(
            "ct.googleapis.com/aviator",
            Base64.decode("MFkwEwYHKoZIzj0CAQYIKoZIzj0DAQcDQgAE1/TMabLkDpCjiupacAlP7xNi0I1JYP8bQFAHDG1xhtolSY1l4QgNRzRrvSe8liE+NPWHdjGxfx3JhTsN9x8/6Q==",
                    Base64.DEFAULT),
            "Google 'Aviator' log");
    public static final CertificateTransparencyLog[] CT_LOGS = {PILOT, AVIATOR};

    private String serverPrefix;
    private byte[] publicKey;
    private byte[] logID;
    private String humanReadableName;

    public CertificateTransparencyLog(String serverPrefix, byte[] publicKey, String humanReadableName)
    {
        this.serverPrefix = serverPrefix;
        this.publicKey = publicKey;
        this.humanReadableName = humanReadableName;
        logID = CryptoUtils.SHA256NoThrow(publicKey);
    }

    public String getServerPrefix()
    {
        return serverPrefix;
    }

    public byte[] getPublicKey()
    {
        return publicKey;
    }

    public byte[] getLogID()
    {
        return logID;
    }

    public String getHumanReadableName()
    {
        return humanReadableName;
    }

    public URL getGetSTHEndpoint() throws MalformedURLException
    {
        return new URL("https://" + serverPrefix + "/ct/v1/get-sth");
    }

    public SignedTreeHead getSTHSynchronous() throws IOException, JSONException {
        URL url = getGetSTHEndpoint();
        URLConnection conn = url.openConnection();
        InputStream is = conn.getInputStream();
        Reader reader = new InputStreamReader(is, "UTF-8");
        StringBuilder result = new StringBuilder();
        char[] buf = new char[1024];
        int read;
        while ((read = reader.read(buf)) != -1)
        {
            result.append(buf, 0, read);
        }
        JSONTokener tokener = new JSONTokener(result.toString());
        System.out.println(tokener.nextValue());
        return null;
    }
}
