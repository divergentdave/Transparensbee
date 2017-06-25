package party.davidsherenowitsa.transparensbee;

import java.net.MalformedURLException;
import java.net.URL;

public class LogServer extends Server {
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
        return new URL("https://" + serverPrefix + "ct/v1/get-sth");
    }
}
