package party.davidsherenowitsa.transparensbee;

import java.util.List;

public interface Pollen
{
    void addFromLog(LogServer log, SignedTreeHead sth);
    void addFromAuditor(AuditorServer auditor, List<PollinationSignedTreeHead> sths);
    List<PollinationSignedTreeHead> getForAuditor(AuditorClient auditor);
}