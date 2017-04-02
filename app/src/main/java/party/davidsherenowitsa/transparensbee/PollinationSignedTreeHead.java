package party.davidsherenowitsa.transparensbee;

import java.util.Arrays;

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

    @Override
    public int hashCode() {
        return super.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof PollinationSignedTreeHead))
        {
            return false;
        }
        PollinationSignedTreeHead other = (PollinationSignedTreeHead)obj;
        return Arrays.equals(logID, other.logID) && super.equals(obj);
    }
}
