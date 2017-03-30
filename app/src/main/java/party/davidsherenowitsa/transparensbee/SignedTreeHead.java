package party.davidsherenowitsa.transparensbee;

public class SignedTreeHead {
    private final int version;
    private final int signatureType;
    private final long timestamp;
    private final long treeSize;
    private final byte[] rootHash;

    public SignedTreeHead(int version, int signatureType, long timestamp, long treeSize, byte[] rootHash)
    {
        this.version = version;
        this.signatureType = signatureType;
        this.timestamp = timestamp;
        this.treeSize = treeSize;
        this.rootHash = rootHash;
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
}
