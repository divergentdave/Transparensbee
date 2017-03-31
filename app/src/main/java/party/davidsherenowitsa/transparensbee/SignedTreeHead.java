package party.davidsherenowitsa.transparensbee;

public class SignedTreeHead {
    private final int version;
    private final int signatureType;
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

    public int getVersion()
    {
        return version;
    }

    public int getSignatureType()
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
}
