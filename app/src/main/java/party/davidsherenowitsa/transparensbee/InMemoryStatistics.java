package party.davidsherenowitsa.transparensbee;

import android.util.Pair;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class InMemoryStatistics implements Statistics {
    private HashMap<LogServer, Pair<Integer, Integer>> logStats;
    private HashMap<AuditorServer, Pair<Integer, Integer>> auditorStats;
    private List<StatisticsListener> listenerList;

    private static InMemoryStatistics singleton;

    public static InMemoryStatistics getInstance() {
        if (singleton == null) {
            singleton = new InMemoryStatistics();
        }
        return singleton;
    }

    private InMemoryStatistics() {
        logStats = new HashMap<>();
        auditorStats = new HashMap<>();
        listenerList = new ArrayList<>();
    }

    @Override
    public synchronized void addLogFailure(LogServer log) {
        Pair<Integer, Integer> oldCounts = logStats.get(log);
        if (oldCounts == null) {
            logStats.put(log, new Pair<>(0, 1));
        } else {
            logStats.put(log, new Pair<>(oldCounts.first, oldCounts.second + 1));
        }
        for (StatisticsListener listener : listenerList)
        {
            listener.notifyLog(log);
        }
    }

    @Override
    public synchronized void addLogSuccess(LogServer log) {
        Pair<Integer, Integer> oldCounts = logStats.get(log);
        if (oldCounts == null) {
            logStats.put(log, new Pair<>(1, 0));
        } else {
            logStats.put(log, new Pair<>(oldCounts.first + 1, oldCounts.second));
        }
        for (StatisticsListener listener : listenerList)
        {
            listener.notifyLog(log);
        }
    }

    @Override
    public synchronized void addAuditorFailure(AuditorServer auditor) {
        Pair<Integer, Integer> oldCounts = auditorStats.get(auditor);
        if (oldCounts == null) {
            auditorStats.put(auditor, new Pair<>(0, 1));
        } else {
            auditorStats.put(auditor, new Pair<>(oldCounts.first, oldCounts.second + 1));
        }
        for (StatisticsListener listener : listenerList)
        {
            listener.notifyAuditor(auditor);
        }
    }

    @Override
    public synchronized void addAuditorSuccess(AuditorServer auditor) {
        Pair<Integer, Integer> oldCounts = auditorStats.get(auditor);
        if (oldCounts == null) {
            auditorStats.put(auditor, new Pair<>(1, 0));
        } else {
            auditorStats.put(auditor, new Pair<>(oldCounts.first + 1, oldCounts.second));
        }
        for (StatisticsListener listener : listenerList)
        {
            listener.notifyAuditor(auditor);
        }
    }

    @Override
    public Pair<Integer, Integer> getLogSuccessFailure(LogServer log) {
        return logStats.get(log);
    }

    @Override
    public Pair<Integer, Integer> getAuditorSuccessFailure(AuditorServer auditor) {
        return auditorStats.get(auditor);
    }

    @Override
    public void registerListener(StatisticsListener listener) {
        listenerList.add(listener);
    }

    @Override
    public void unregisterListener(StatisticsListener listener) {
        listenerList.remove(listener);
    }
}
