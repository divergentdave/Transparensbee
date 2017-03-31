package party.davidsherenowitsa.transparensbee;

public class PollinationSignedTreeHead extends SignedTreeHead {
    private final byte[] logID;

    public PollinationSignedTreeHead(long timestamp, long treeSize, byte[] rootHash, byte[] treeHeadSignature, byte[] logID)
    {
        super(timestamp, treeSize, rootHash, treeHeadSignature);
        this.logID = logID;
    }

    public PollinationSignedTreeHead(SignedTreeHead sth, byte[] logID)
    {
        this(sth.getTimestamp(), sth.getTreeSize(), sth.getRootHash(), sth.getTreeHeadSignature(), logID);
    }

    public byte[] getLogID()
    {
        return logID;
    }
}
