package party.davidsherenowitsa.transparensbee;

import android.content.Intent;
import android.util.Pair;

import java.util.HashMap;

public class InMemoryStatistics implements Statistics {
    private HashMap<LogServer, Pair<Integer, Integer>> logStats;
    private HashMap<AuditorServer, Pair<Integer, Integer>> auditorStats;

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
    }

    @Override
    public synchronized void addLogFailure(LogServer log) {
        Pair<Integer, Integer> oldCounts = logStats.get(log);
        if (oldCounts == null) {
            logStats.put(log, new Pair<Integer, Integer>(0, 1));
        } else {
            logStats.put(log, new Pair<Integer, Integer>(oldCounts.first, oldCounts.second + 1));
        }
    }

    @Override
    public synchronized void addLogSuccess(LogServer log) {
        Pair<Integer, Integer> oldCounts = logStats.get(log);
        if (oldCounts == null) {
            logStats.put(log, new Pair<Integer, Integer>(1, 0));
        } else {
            logStats.put(log, new Pair<Integer, Integer>(oldCounts.first + 1, oldCounts.second));
        }
    }

    @Override
    public synchronized void addAuditorFailure(AuditorServer auditor) {
        Pair<Integer, Integer> oldCounts = auditorStats.get(auditor);
        if (oldCounts == null) {
            auditorStats.put(auditor, new Pair<Integer, Integer>(0, 1));
        } else {
            auditorStats.put(auditor, new Pair<Integer, Integer>(oldCounts.first, oldCounts.second + 1));
        }
    }

    @Override
    public synchronized void addAuditorSuccess(AuditorServer auditor) {
        Pair<Integer, Integer> oldCounts = auditorStats.get(auditor);
        if (oldCounts == null) {
            auditorStats.put(auditor, new Pair<Integer, Integer>(1, 0));
        } else {
            auditorStats.put(auditor, new Pair<Integer, Integer>(oldCounts.first + 1, oldCounts.second));
        }
    }
}
