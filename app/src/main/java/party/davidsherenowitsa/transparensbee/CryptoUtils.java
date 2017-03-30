package party.davidsherenowitsa.transparensbee;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class CryptoUtils {
    public static byte[] SHA256(byte[] message) throws NoSuchAlgorithmException
    {
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        return md.digest(message);
    }

    public static byte[] SHA256NoThrow(byte[] message)
    {
        try
        {
            return SHA256(message);
        }
        catch (NoSuchAlgorithmException e)
        {
            return null;
        }
    }
}
