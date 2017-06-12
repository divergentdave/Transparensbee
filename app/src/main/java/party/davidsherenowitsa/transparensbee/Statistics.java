package party.davidsherenowitsa.transparensbee;

import android.util.Pair;

public interface Statistics {
    void addLogFailure(LogServer log);
    void addLogSuccess(LogServer log);
    void addAuditorFailure(AuditorServer auditor);
    void addAuditorSuccess(AuditorServer auditor);
    Pair<Integer, Integer> getLogSuccessFailure(LogServer log);
    Pair<Integer, Integer> getAuditorSuccessFailure(AuditorServer auditor);
    void registerListener(StatisticsListener listener);
    void unregisterListener(StatisticsListener listener);

    interface StatisticsListener {
        void notifyLog(LogServer log);
        void notifyAuditor(AuditorServer auditor);
    }
}
