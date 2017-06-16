package party.davidsherenowitsa.transparensbee;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.Nullable;
import android.util.Base64;

import java.util.ArrayList;
import java.util.Collection;

import static party.davidsherenowitsa.transparensbee.CTDBContract.*;

public class DBPollen implements Pollen {
    private CTDBHelper dbHelper;
    private SQLiteDatabase db;

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
        String[] projection = {CTDBContract.Auditor._ID};
        String predicate = Auditor.COLUMN_NAME_DOMAIN + " = ?";
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
        Long id = lookupAuditor(auditor);
        if (id != null) {
            return id;
        } else {
            return addAuditor(auditor);
        }
    }

    @Nullable
    private Long lookupSth(SignedTreeHead sth) {
        String hex = Base64.encodeToString(sth.getTreeHeadSignature(), Base64.NO_WRAP);
        String[] projection = {STH._ID};
        String predicate = STH.COLUMN_NAME_TREE_HEAD_SIGNATURE_HEX + " = ?";
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

        long auditorId = lookupOrAddAuditor(auditor);
        ContentValues sthValues = new ContentValues();
        ContentValues seenValues = new ContentValues();
        seenValues.put(STHSeen.COLUMN_NAME_AUDITOR_ID, auditorId);
        for (PollinationSignedTreeHead sth : sths) {
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

    @Override
    public Collection<PollinationSignedTreeHead> getForAuditor(AuditorServer auditor) {
        if (db == null) {
            open();
        }
        ArrayList<PollinationSignedTreeHead> list = new ArrayList<>();
        Long auditorId = lookupOrAddAuditor(auditor);

        final int TARGET_COUNT = 100;

        Cursor cursor = db.rawQuery("SELECT " +
                        STH._ID + "," +
                        STH.COLUMN_NAME_VERSION + "," +
                        STH.COLUMN_NAME_SIGNATURE_TYPE + "," +
                        STH.COLUMN_NAME_TIMESTAMP + "," +
                        STH.COLUMN_NAME_TREE_SIZE + "," +
                        STH.COLUMN_NAME_ROOT_HASH + "," +
                        STH.COLUMN_NAME_TREE_HEAD_SIGNATURE + "," +
                        STH.COLUMN_NAME_LOG_ID +
                        " FROM " + STH.TABLE_NAME +
                        " WHERE " + STH._ID +
                        " IN (SELECT " + STH._ID + " FROM (SELECT " + STH._ID + "," +
                        " (SELECT MIN(1,COUNT(*)) FROM " + STHSeen.TABLE_NAME +
                        " WHERE " + STHSeen.COLUMN_NAME_AUDITOR_ID +
                        " = ?) AS seen " +
                        " FROM " + STH.TABLE_NAME +
                        " ORDER BY seen ASC, RANDOM() LIMIT ?))"
                , new String[]{Long.toString(auditorId), Integer.toString(TARGET_COUNT)});
        int timestampIndex = cursor.getColumnIndexOrThrow(STH.COLUMN_NAME_TIMESTAMP);
        int treeSizeIndex = cursor.getColumnIndexOrThrow(STH.COLUMN_NAME_TREE_SIZE);
        int rootHashIndex = cursor.getColumnIndexOrThrow(STH.COLUMN_NAME_ROOT_HASH);
        int signatureIndex = cursor.getColumnIndexOrThrow(STH.COLUMN_NAME_TREE_HEAD_SIGNATURE);
        int logIdIndex = cursor.getColumnIndexOrThrow(STH.COLUMN_NAME_LOG_ID);
        int idIndex = cursor.getColumnIndexOrThrow(STH._ID);
        while (cursor.moveToNext()) {
            list.add(new PollinationSignedTreeHeadWithId(
                    cursor.getLong(timestampIndex),
                    cursor.getLong(treeSizeIndex),
                    cursor.getBlob(rootHashIndex),
                    cursor.getBlob(signatureIndex),
                    cursor.getBlob(logIdIndex),
                    cursor.getLong(idIndex)
            ));
        }
        cursor.close();

        return list;
    }

    public void cleanup() {
        // TODO
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
