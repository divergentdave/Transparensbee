package party.davidsherenowitsa.transparensbee;

import java.nio.ByteBuffer;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.Signature;
import java.security.SignatureException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;

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

    public static boolean isSTHValid(SignedTreeHead signedTreeHead, LogServer logServer) throws NoSuchAlgorithmException, InvalidKeySpecException, InvalidKeyException, SignatureException {
        byte[] treeHeadData = new byte[50];
        ByteBuffer byteBuffer = ByteBuffer.wrap(treeHeadData);
        byteBuffer.put(signedTreeHead.getVersion());
        byteBuffer.put(signedTreeHead.getSignatureType());
        byteBuffer.putLong(signedTreeHead.getTimestamp());
        byteBuffer.putLong(signedTreeHead.getTreeSize());
        byteBuffer.put(signedTreeHead.getRootHash());

        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(logServer.getPublicKey());
        byte[] signatureBytes = signedTreeHead.getTreeHeadSignature();
        if (signatureBytes[0] != 4) {
            // HashAlgorithm of sha256
            return false;
        }
        Signature signature;
        KeyFactory keyFactory;
        PublicKey publicKey;
        if (signatureBytes[1] == 1) {
            // SignatureAlgorithm of rsa
            signature = Signature.getInstance("SHA256withRSA");

            keyFactory = KeyFactory.getInstance("RSA");
            publicKey = keyFactory.generatePublic(keySpec);
        } else if (signatureBytes[1] == 3) {
            // SignatureAlgorithm of ecdsa
            signature = Signature.getInstance("SHA256withECDSA");

            keyFactory = KeyFactory.getInstance("EC");
            publicKey = keyFactory.generatePublic(keySpec);
        } else {
            return false;
        }
        int signatureLength = signatureBytes[2] << 8 | signatureBytes[3];
        if (signatureLength != signatureBytes.length - 4) {
            // length mismatch
            return false;
        }

        signature.initVerify(publicKey);
        signature.update(treeHeadData);
        return signature.verify(signatureBytes, 4, signatureBytes.length - 4);
    }
}
