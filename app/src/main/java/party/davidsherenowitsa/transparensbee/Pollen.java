package party.davidsherenowitsa.transparensbee;

import java.util.Collection;

public interface Pollen
{
    void addFromLog(LogServer log, SignedTreeHead sth);
    void addFromAuditor(AuditorServer auditor, Collection<PollinationSignedTreeHead> sths);
    Collection<PollinationSignedTreeHead> getForAuditor(AuditorServer auditor);
}