package party.davidsherenowitsa.transparensbee;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.support.annotation.Nullable;
import android.util.Base64;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static party.davidsherenowitsa.transparensbee.CTDBContract.*;

public class DBPollen implements Pollen {
    private CTDBHelper dbHelper;
    private SQLiteDatabase db;

    private static final int TWO_WEEKS = 1000 * 3600 * 24 * 14;

    public DBPollen(Context context) {
        dbHelper = new CTDBHelper(context);
        db = null;
    }

    private void open() {
        if (db != null) {
            db.close();
        }
        db = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }

    private long addAuditor(AuditorServer auditor) {
        ContentValues values = new ContentValues();
        values.put(Auditor.COLUMN_NAME_DOMAIN, auditor.getDomain());
        return db.insert(Auditor.TABLE_NAME, null, values);
    }

    @Nullable
    private Long lookupAuditor(AuditorServer auditor) {
        String[] projection = {Auditor._ID};
        String predicate = Auditor.COLUMN_NAME_DOMAIN + "=?";
        String[] predicateArguments = {auditor.getDomain()};
        Cursor cursor = db.query(
                Auditor.TABLE_NAME,
                projection,
                predicate,
                predicateArguments,
                null,
                null,
                null);
        if (cursor.moveToFirst()) {
            long id = cursor.getLong(cursor.getColumnIndexOrThrow(Auditor._ID));
            cursor.close();
            return id;
        } else {
            return null;
        }
    }

    private long lookupOrAddAuditor(AuditorServer auditor) {
        db.beginTransaction();
        try {
            Long id = lookupAuditor(auditor);
            if (id != null) {
                db.setTransactionSuccessful();
                return id;
            } else {
                id = addAuditor(auditor);
                db.setTransactionSuccessful();
                return id;
            }
        } finally {
            db.endTransaction();
        }
    }

    @Nullable
    private Long lookupSth(SignedTreeHead sth) {
        String hex = Base64.encodeToString(sth.getTreeHeadSignature(), Base64.NO_WRAP);
        String[] projection = {STH._ID};
        String predicate = STH.COLUMN_NAME_TREE_HEAD_SIGNATURE_HEX + "=?";
        String[] predicateArguments = {hex};
        Cursor cursor = db.query(
                STH.TABLE_NAME,
                projection,
                predicate,
                predicateArguments,
                null,
                null,
                null);
        if (cursor.moveToFirst()) {
            long id = cursor.getLong(cursor.getColumnIndexOrThrow(STH._ID));
            cursor.close();
            return id;
        } else {
            cursor.close();
            return null;
        }
    }

    @Override
    public void addFromLog(LogServer log, SignedTreeHead sth) {
        if (db == null) {
            open();
        }
        if (lookupSth(sth) != null) {
            return;
        }
        if (sth.getTimestamp() < System.currentTimeMillis() - TWO_WEEKS) {
            return;
        }
        ContentValues values = new ContentValues();
        values.put(STH.COLUMN_NAME_VERSION, sth.getVersion());
        values.put(STH.COLUMN_NAME_SIGNATURE_TYPE, sth.getSignatureType());
        values.put(STH.COLUMN_NAME_TIMESTAMP, sth.getTimestamp());
        values.put(STH.COLUMN_NAME_TREE_SIZE, sth.getTreeSize());
        values.put(STH.COLUMN_NAME_ROOT_HASH, sth.getRootHash());
        values.put(STH.COLUMN_NAME_TREE_HEAD_SIGNATURE, sth.getTreeHeadSignature());
        String hex = Base64.encodeToString(sth.getTreeHeadSignature(), Base64.NO_WRAP);
        values.put(STH.COLUMN_NAME_TREE_HEAD_SIGNATURE_HEX, hex);
        values.put(STH.COLUMN_NAME_LOG_ID, log.getLogID());
        db.insert(STH.TABLE_NAME, null, values);
    }

    @Override
    public void addFromAuditor(AuditorServer auditor, Collection<PollinationSignedTreeHead> sths) {
        if (db == null) {
            open();
        }

        long timestampCutoff = System.currentTimeMillis() - TWO_WEEKS;
        long auditorId = lookupOrAddAuditor(auditor);
        ContentValues sthValues = new ContentValues();
        ContentValues seenValues = new ContentValues();
        seenValues.put(STHSeen.COLUMN_NAME_AUDITOR_ID, auditorId);
        for (PollinationSignedTreeHead sth : sths) {
            if (sth.getTimestamp() > timestampCutoff) {
                long sthId;
                if (sth instanceof PollinationSignedTreeHeadWithId) {
                    sthId = ((PollinationSignedTreeHeadWithId) sth).getDatabaseId();
                } else {
                    Long lookupResult = lookupSth(sth);
                    if (lookupResult != null) {
                        sthId = lookupResult;
                    } else {
                        sthValues.put(STH.COLUMN_NAME_VERSION, sth.getVersion());
                        sthValues.put(STH.COLUMN_NAME_SIGNATURE_TYPE, sth.getSignatureType());
                        sthValues.put(STH.COLUMN_NAME_TIMESTAMP, sth.getTimestamp());
                        sthValues.put(STH.COLUMN_NAME_TREE_SIZE, sth.getTreeSize());
                        sthValues.put(STH.COLUMN_NAME_ROOT_HASH, sth.getRootHash());
                        sthValues.put(STH.COLUMN_NAME_TREE_HEAD_SIGNATURE, sth.getTreeHeadSignature());
                        String hex = Base64.encodeToString(sth.getTreeHeadSignature(), Base64.NO_WRAP);
                        sthValues.put(STH.COLUMN_NAME_TREE_HEAD_SIGNATURE_HEX, hex);
                        sthValues.put(STH.COLUMN_NAME_LOG_ID, sth.getLogID());
                        sthId = db.insert(STH.TABLE_NAME, null, sthValues);
                    }
                }
                seenValues.put(STHSeen.COLUMN_NAME_STH_ID, sthId);
                db.insertWithOnConflict(STHSeen.TABLE_NAME, null, seenValues, SQLiteDatabase.CONFLICT_IGNORE);
            }
        }
    }

    @Override
    public Collection<PollinationSignedTreeHead> getForAuditor(AuditorServer auditor) {
        if (db == null) {
            open();
        }
        ArrayList<PollinationSignedTreeHead> list = new ArrayList<>();
        Long auditorId = lookupOrAddAuditor(auditor);

        final int TARGET_COUNT = 100;

        Cursor cursorNew = db.rawQuery("SELECT " +
                        STH.TABLE_NAME + "." + STH._ID + "," +
                        STH.COLUMN_NAME_VERSION + "," +
                        STH.COLUMN_NAME_SIGNATURE_TYPE + "," +
                        STH.COLUMN_NAME_TIMESTAMP + "," +
                        STH.COLUMN_NAME_TREE_SIZE + "," +
                        STH.COLUMN_NAME_ROOT_HASH + "," +
                        STH.COLUMN_NAME_TREE_HEAD_SIGNATURE + "," +
                        STH.COLUMN_NAME_LOG_ID +
                        " FROM " + STH.TABLE_NAME +
                        " LEFT OUTER JOIN " + STHSeen.TABLE_NAME +
                        " ON " +
                        STH.TABLE_NAME + "." + STH._ID + "=" +
                        STHSeen.TABLE_NAME + "." + STHSeen.COLUMN_NAME_STH_ID +
                        " AND " + STHSeen.COLUMN_NAME_AUDITOR_ID + "=?" +
                        " WHERE " + STHSeen.COLUMN_NAME_AUDITOR_ID +
                        " IS NULL ORDER BY RANDOM() LIMIT ?",
                new String[]{Long.toString(auditorId), Integer.toString(TARGET_COUNT)});
        int timestampIndex = cursorNew.getColumnIndexOrThrow(STH.COLUMN_NAME_TIMESTAMP);
        int treeSizeIndex = cursorNew.getColumnIndexOrThrow(STH.COLUMN_NAME_TREE_SIZE);
        int rootHashIndex = cursorNew.getColumnIndexOrThrow(STH.COLUMN_NAME_ROOT_HASH);
        int signatureIndex = cursorNew.getColumnIndexOrThrow(STH.COLUMN_NAME_TREE_HEAD_SIGNATURE);
        int logIdIndex = cursorNew.getColumnIndexOrThrow(STH.COLUMN_NAME_LOG_ID);
        int idIndex = cursorNew.getColumnIndexOrThrow(STH._ID);
        while (cursorNew.moveToNext()) {
            list.add(new PollinationSignedTreeHeadWithId(
                    cursorNew.getLong(timestampIndex),
                    cursorNew.getLong(treeSizeIndex),
                    cursorNew.getBlob(rootHashIndex),
                    cursorNew.getBlob(signatureIndex),
                    cursorNew.getBlob(logIdIndex),
                    cursorNew.getLong(idIndex)
            ));
        }
        cursorNew.close();

        if (list.size() < TARGET_COUNT) {
            Cursor cursorFiller = db.rawQuery("SELECT " +
                            STH.TABLE_NAME + "." + STH._ID + "," +
                            STH.COLUMN_NAME_VERSION + "," +
                            STH.COLUMN_NAME_SIGNATURE_TYPE + "," +
                            STH.COLUMN_NAME_TIMESTAMP + "," +
                            STH.COLUMN_NAME_TREE_SIZE + "," +
                            STH.COLUMN_NAME_ROOT_HASH + "," +
                            STH.COLUMN_NAME_TREE_HEAD_SIGNATURE + "," +
                            STH.COLUMN_NAME_LOG_ID +
                            " FROM " + STH.TABLE_NAME +
                            " INNER JOIN " + STHSeen.TABLE_NAME +
                            " ON " +
                            STH.TABLE_NAME + "." + STH._ID + "=" +
                            STHSeen.TABLE_NAME + "." + STHSeen.COLUMN_NAME_STH_ID +
                            " WHERE " + STHSeen.TABLE_NAME + "." + STHSeen.COLUMN_NAME_AUDITOR_ID +
                            "=? ORDER BY RANDOM() LIMIT ?",
                    new String[]{Long.toString(auditorId), Integer.toString(TARGET_COUNT - list.size())});
            timestampIndex = cursorFiller.getColumnIndexOrThrow(STH.COLUMN_NAME_TIMESTAMP);
            treeSizeIndex = cursorFiller.getColumnIndexOrThrow(STH.COLUMN_NAME_TREE_SIZE);
            rootHashIndex = cursorFiller.getColumnIndexOrThrow(STH.COLUMN_NAME_ROOT_HASH);
            signatureIndex = cursorFiller.getColumnIndexOrThrow(STH.COLUMN_NAME_TREE_HEAD_SIGNATURE);
            logIdIndex = cursorFiller.getColumnIndexOrThrow(STH.COLUMN_NAME_LOG_ID);
            idIndex = cursorFiller.getColumnIndexOrThrow(STH._ID);
            while (cursorFiller.moveToNext()) {
                list.add(new PollinationSignedTreeHeadWithId(
                        cursorFiller.getLong(timestampIndex),
                        cursorFiller.getLong(treeSizeIndex),
                        cursorFiller.getBlob(rootHashIndex),
                        cursorFiller.getBlob(signatureIndex),
                        cursorFiller.getBlob(logIdIndex),
                        cursorFiller.getLong(idIndex)
                ));
            }
            cursorFiller.close();
        }

        return list;
    }

    public void cleanup() {
        List<Long> ids = new ArrayList<>();
        long timestampCutoff = System.currentTimeMillis() - TWO_WEEKS;
        String[] projection = {STH._ID};
        Cursor cursor = db.query(
                STH.TABLE_NAME,
                projection,
                CTDBContract.STH.COLUMN_NAME_TIMESTAMP + " < ?",
                new String[]{Long.toString(timestampCutoff)},
                null,
                null,
                null);
        int idIndex = cursor.getColumnIndexOrThrow(STH._ID);
        while (cursor.moveToNext()) {
            long id = cursor.getLong(idIndex);
            ids.add(id);
        }
        cursor.close();

        SQLiteStatement deleteSeenStmt = db.compileStatement("DELETE FROM " + STHSeen.TABLE_NAME + " WHERE " + STHSeen.COLUMN_NAME_STH_ID + "=?");
        SQLiteStatement deleteSthStmt = db.compileStatement("DELETE FROM " + STH.TABLE_NAME + " WHERE " + STH._ID + "=?");
        for (long id : ids) {
            deleteSeenStmt.bindLong(1, id);
            deleteSeenStmt.executeUpdateDelete();
            deleteSthStmt.bindLong(1, id);
            deleteSthStmt.executeUpdateDelete();
        }
        deleteSeenStmt.close();
        deleteSthStmt.close();
    }

    private class PollinationSignedTreeHeadWithId extends PollinationSignedTreeHead {
        private long databaseId;

        PollinationSignedTreeHeadWithId(long timestamp, long treeSize, byte[] rootHash, byte[] treeHeadSignature, byte[] logID, long databaseId) {
            super(timestamp, treeSize, rootHash, treeHeadSignature, logID);
            this.databaseId = databaseId;
        }

        public long getDatabaseId() {
            return databaseId;
        }
    }
}
