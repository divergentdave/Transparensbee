package party.davidsherenowitsa.transparensbee;

import android.util.Pair;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class InMemoryStatistics implements Statistics {
    private HashMap<Server, Pair<Integer, Integer>> stats;
    private List<StatisticsListener> listenerList;

    private static InMemoryStatistics singleton;

    public static InMemoryStatistics getInstance() {
        if (singleton == null) {
            singleton = new InMemoryStatistics();
        }
        return singleton;
    }

    private InMemoryStatistics() {
        stats = new HashMap<>();
        listenerList = new ArrayList<>();
    }

    @Override
    public synchronized void addFailure(Server server) {
        Pair<Integer, Integer> oldCounts = stats.get(server);
        if (oldCounts == null) {
            stats.put(server, new Pair<>(0, 1));
        } else {
            stats.put(server, new Pair<>(oldCounts.first, oldCounts.second + 1));
        }
        for (StatisticsListener listener : listenerList)
        {
            listener.notify(server);
        }
    }

    @Override
    public synchronized void addSuccess(Server server) {
        Pair<Integer, Integer> oldCounts = stats.get(server);
        if (oldCounts == null) {
            stats.put(server, new Pair<>(1, 0));
        } else {
            stats.put(server, new Pair<>(oldCounts.first + 1, oldCounts.second));
        }
        for (StatisticsListener listener : listenerList)
        {
            listener.notify(server);
        }
    }

    @Override
    public Pair<Integer, Integer> getServerSuccessFailure(Server log) {
        return stats.get(log);
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
