package party.davidsherenowitsa.transparensbee;

import android.util.Base64;

import java.security.NoSuchAlgorithmException;

public class CertificateTransparencyLog {
    public static final CertificateTransparencyLog PILOT = new CertificateTransparencyLog(
            "ct.googleapis.com/pilot",
            Base64.decode("MFkwEwYHKoZIzj0CAQYIKoZIzj0DAQcDQgAEfahLEimAoz2t01p3uMziiLOl/fHTDM0YDOhBRuiBARsV4UvxG2LdNgoIGLrtCzWE0J5APC2em4JlvR8EEEFMoA==",
                    Base64.DEFAULT));
    public static final CertificateTransparencyLog[] CT_LOGS = {PILOT};

    private String serverPrefix;
    private byte[] publicKey;
    private byte[] logID;

    public CertificateTransparencyLog(String serverPrefix, byte[] publicKey)
    {
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
}
