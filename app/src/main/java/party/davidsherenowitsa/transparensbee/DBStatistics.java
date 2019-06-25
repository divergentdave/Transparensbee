package party.davidsherenowitsa.transparensbee;

import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import android.util.Pair;

import java.util.ArrayList;
import java.util.List;

import static party.davidsherenowitsa.transparensbee.CTDBContract.*;

public class DBStatistics extends BroadcastReceiver {
    private List<StatisticsListener> listenerList;
    private CTDBHelper dbHelper;
    private SQLiteDatabase db;
    private LocalBroadcastManager lbm;

    public static final String STATS_UPDATE_ACTION = "stats";

    public DBStatistics(Context context) {
        listenerList = new ArrayList<>();
        dbHelper = new CTDBHelper(context);
        db = null;
        lbm = LocalBroadcastManager.getInstance(context);
    }

    public void open() {
        db = dbHelper.getWritableDatabase();
        IntentFilter intentFilter = new IntentFilter(STATS_UPDATE_ACTION);
        lbm.registerReceiver(this, intentFilter);
    }

    public void close() {
        dbHelper.close();
        db = null;
        lbm.unregisterReceiver(this);
    }

    private String urlForServer(Server server) {
        if (server instanceof AuditorServer) {
            return String.format("https://%s/", ((AuditorServer) server).getDomain());
        } else if (server instanceof LogServer) {
            return ((LogServer) server).getServerPrefix();
        }
        throw new IllegalArgumentException();
    }

    private int countByUrl(SQLiteDatabase db, String url) {
        Cursor cursorCount = db.query(
                ServerStatus.TABLE_NAME,
                new String[]{},
                ServerStatus.COLUMN_NAME_SERVER_URL + "=?",
                new String[]{url},
                null,
                null,
                null);
        int count = cursorCount.getCount();
        cursorCount.close();
        return count;
    }

    private void notifyChangeAll() {
        Intent intent = new Intent(STATS_UPDATE_ACTION);
        lbm.sendBroadcast(intent);
    }

    public void addFailure(Server server) {
        String url = urlForServer(server);
        db.beginTransaction();
        try {
            if (countByUrl(db, url) > 0) {
                Cursor cursorUpdate = db.rawQuery("UPDATE " + ServerStatus.TABLE_NAME +
                                " SET " + ServerStatus.COLUMN_NAME_FAILURE_COUNT + "=" +
                                ServerStatus.COLUMN_NAME_FAILURE_COUNT + "+1 WHERE " +
                                ServerStatus.COLUMN_NAME_SERVER_URL + "=?",
                        new String[]{url});
                cursorUpdate.moveToFirst();
                cursorUpdate.close();
                db.setTransactionSuccessful();
            } else {
                ContentValues values = new ContentValues();
                values.put(ServerStatus.COLUMN_NAME_SERVER_URL, url);
                values.put(ServerStatus.COLUMN_NAME_SUCCESS_COUNT, 0);
                values.put(ServerStatus.COLUMN_NAME_FAILURE_COUNT, 1);
                values.put(ServerStatus.COLUMN_NAME_LAST_ERROR, "");
                db.insert(ServerStatus.TABLE_NAME, null, values);
                db.setTransactionSuccessful();
            }
        } finally {
            db.endTransaction();
        }
        notifyChangeAll();
    }

    public void addSuccess(Server server) {
        String url = urlForServer(server);
        db.beginTransaction();
        try {
            if (countByUrl(db, url) > 0) {
                Cursor cursorUpdate = db.rawQuery("UPDATE " + ServerStatus.TABLE_NAME +
                        " SET " + ServerStatus.COLUMN_NAME_SUCCESS_COUNT + "=" +
                        ServerStatus.COLUMN_NAME_SUCCESS_COUNT + "+1 WHERE " +
                        ServerStatus.COLUMN_NAME_SERVER_URL + "=?", new String[]{url});
                cursorUpdate.moveToFirst();
                cursorUpdate.close();
                db.setTransactionSuccessful();
            } else {
                ContentValues values = new ContentValues();
                values.put(ServerStatus.COLUMN_NAME_SERVER_URL, url);
                values.put(ServerStatus.COLUMN_NAME_SUCCESS_COUNT, 1);
                values.put(ServerStatus.COLUMN_NAME_FAILURE_COUNT, 0);
                values.put(ServerStatus.COLUMN_NAME_LAST_ERROR, "");
                db.insert(ServerStatus.TABLE_NAME, null, values);
                db.setTransactionSuccessful();
            }
        } finally {
            db.endTransaction();
        }
        notifyChangeAll();
    }

    public Pair<Integer, Integer> getServerSuccessFailure(Server server) {
        Cursor cursor = db.query(
                ServerStatus.TABLE_NAME,
                new String[]{
                        ServerStatus.COLUMN_NAME_SUCCESS_COUNT,
                        ServerStatus.COLUMN_NAME_FAILURE_COUNT
                },
                ServerStatus.COLUMN_NAME_SERVER_URL + "=?",
                new String[]{urlForServer(server)},
                null,
                null,
                null);
        if (cursor.moveToFirst()) {
            int success = cursor.getInt(cursor.getColumnIndexOrThrow(ServerStatus.COLUMN_NAME_SUCCESS_COUNT));
            int failure = cursor.getInt(cursor.getColumnIndexOrThrow(ServerStatus.COLUMN_NAME_FAILURE_COUNT));
            cursor.close();
            return new Pair<>(success, failure);
        } else {
            cursor.close();
            return new Pair<>(0, 0);
        }
    }

    public void registerListener(StatisticsListener listener) {
        listenerList.add(listener);
    }

    public void unregisterListener(StatisticsListener listener) {
        listenerList.remove(listener);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        for (StatisticsListener listener : listenerList) {
            listener.notifyChange();
        }
    }
}
