package party.davidsherenowitsa.transparensbee;

import android.util.Pair;

public interface Statistics {
    void addFailure(Server server);
    void addSuccess(Server server);
    Pair<Integer, Integer> getServerSuccessFailure(Server server);
    void registerListener(StatisticsListener listener);
    void unregisterListener(StatisticsListener listener);

    interface StatisticsListener {
        void notifyChange();
    }
}
