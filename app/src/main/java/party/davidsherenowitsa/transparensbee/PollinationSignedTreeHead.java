package party.davidsherenowitsa.transparensbee;

public class PollinationSignedTreeHead extends SignedTreeHead {
    private final byte[] logID;

    public PollinationSignedTreeHead(int version, int signatureType, long timestamp, long treeSize, byte[] rootHash, byte[] logID)
    {
        super(version, signatureType, timestamp, treeSize, rootHash);
        this.logID = logID;
    }

    public PollinationSignedTreeHead(SignedTreeHead sth, byte[] logID)
    {
        this(sth.getVersion(), sth.getSignatureType(), sth.getTimestamp(), sth.getTreeSize(), sth.getRootHash(), logID);
    }

    public byte[] getLogID()
    {
        return logID;
    }
}
