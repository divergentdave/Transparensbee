package party.davidsherenowitsa.transparensbee;

import java.util.HashSet;
import java.util.Collection;

// TODO: Replace with a SQLite-backed durable store
public class InMemoryPollen implements Pollen {
    private HashSet<PollinationSignedTreeHead> set;

    public InMemoryPollen()
    {
        set = new HashSet<>();
    }

    @Override
    public void addFromLog(LogServer log, SignedTreeHead sth) {
        set.add(new PollinationSignedTreeHead(sth, log.getLogID()));
    }

    @Override
    public void addFromAuditor(AuditorServer auditor, Collection<PollinationSignedTreeHead> sths) {
        set.addAll(sths);
    }

    @Override
    public Collection<PollinationSignedTreeHead> getForAuditor(AuditorServer auditor) {
        return set;
    }
}
