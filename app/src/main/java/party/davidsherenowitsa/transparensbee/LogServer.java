package party.davidsherenowitsa.transparensbee;

import android.util.Base64;

import java.net.MalformedURLException;
import java.net.URL;

public class LogServer extends Server {
    public static final LogServer PILOT = new LogServer(
            "ct.googleapis.com/pilot",
            Base64.decode("MFkwEwYHKoZIzj0CAQYIKoZIzj0DAQcDQgAEfahLEimAoz2t01p3uMziiLOl/fHTDM0YDOhBRuiBARsV4UvxG2LdNgoIGLrtCzWE0J5APC2em4JlvR8EEEFMoA==",
                    Base64.DEFAULT),
            "Google 'Pilot' log");
    public static final LogServer AVIATOR = new LogServer(
            "ct.googleapis.com/aviator",
            Base64.decode("MFkwEwYHKoZIzj0CAQYIKoZIzj0DAQcDQgAE1/TMabLkDpCjiupacAlP7xNi0I1JYP8bQFAHDG1xhtolSY1l4QgNRzRrvSe8liE+NPWHdjGxfx3JhTsN9x8/6Q==",
                    Base64.DEFAULT),
            "Google 'Aviator' log");
    public static final LogServer[] CT_LOGS = {PILOT, AVIATOR};

    private String serverPrefix;
    private byte[] publicKey;
    private byte[] logID;

    public LogServer(String serverPrefix, byte[] publicKey, String humanReadableName)
    {
        super(humanReadableName);
        this.serverPrefix = serverPrefix;
        this.publicKey = publicKey;
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

    public URL getGetSTHEndpoint() throws MalformedURLException
    {
        return new URL("https://" + serverPrefix + "/ct/v1/get-sth");
    }
}
