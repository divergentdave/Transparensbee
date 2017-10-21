package party.davidsherenowitsa.transparensbee;

import java.util.Arrays;

public class SignedTreeHead {
    private final byte version;
    private final byte signatureType;
    private final long timestamp;
    private final long treeSize;
    private final byte[] rootHash;
    private final byte[] treeHeadSignature;

    public SignedTreeHead(long timestamp, long treeSize, byte[] rootHash, byte[] treeHeadSignature)
    {
        this.version = 0;
        this.signatureType = 1;
        this.timestamp = timestamp;
        this.treeSize = treeSize;
        this.rootHash = rootHash;
        this.treeHeadSignature = treeHeadSignature;
    }

    public byte getVersion()
    {
        return version;
    }

    public byte getSignatureType()
    {
        return signatureType;
    }

    public long getTimestamp()
    {
        return timestamp;
    }

    public long getTreeSize()
    {
        return treeSize;
    }

    public byte[] getRootHash()
    {
        return rootHash;
    }

    public byte[] getTreeHeadSignature()
    {
        return treeHeadSignature;
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(rootHash);
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof SignedTreeHead))
        {
            return false;
        }
        SignedTreeHead other = (SignedTreeHead)obj;
        return version == other.version && signatureType == other.signatureType &&
                timestamp == other.timestamp && treeSize == other.treeSize &&
                Arrays.equals(rootHash, other.rootHash) &&
                Arrays.equals(treeHeadSignature, other.treeHeadSignature);
    }
}
