package party.davidsherenowitsa.transparensbee;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class CTDBHelper extends SQLiteOpenHelper {
    public static final int DATABASE_VERSION = 1;  // Update this when changing schema
    public static final String DATABASE_NAME = "STH_Storage.db";

    private static final String SQL_CREATE_STH = "CREATE TABLE " + CTDBContract.STH.TABLE_NAME + " (" +
            CTDBContract.STH._ID + " INTEGER PRIMARY KEY," +
            CTDBContract.STH.COLUMN_NAME_VERSION + " INTEGER," +
            CTDBContract.STH.COLUMN_NAME_SIGNATURE_TYPE + " INTEGER," +
            CTDBContract.STH.COLUMN_NAME_TIMESTAMP + " INTEGER," +
            CTDBContract.STH.COLUMN_NAME_TREE_SIZE + " INTEGER," +
            CTDBContract.STH.COLUMN_NAME_ROOT_HASH + " BLOB," +
            CTDBContract.STH.COLUMN_NAME_TREE_HEAD_SIGNATURE + " BLOB," +
            CTDBContract.STH.COLUMN_NAME_TREE_HEAD_SIGNATURE_HEX + " TEXT," +
            CTDBContract.STH.COLUMN_NAME_LOG_ID + " BLOB)";
    private static final String SQL_CREATE_INDEX_STH = "CREATE INDEX sth_index ON " + CTDBContract.STH.TABLE_NAME + "(" +
            CTDBContract.STH.COLUMN_NAME_TREE_HEAD_SIGNATURE_HEX + "," +
            CTDBContract.STH._ID + ")";
    private static final String SQL_CREATE_AUDITOR = "CREATE TABLE " + CTDBContract.Auditor.TABLE_NAME + " (" +
            CTDBContract.Auditor._ID + " INTEGER PRIMARY KEY," +
            CTDBContract.Auditor.COLUMN_NAME_DOMAIN + " TEXT)";
    private static final String SQL_CREATE_INDEX_AUDITOR = "CREATE INDEX auditor_index ON " + CTDBContract.Auditor.TABLE_NAME + "(" +
            CTDBContract.Auditor.COLUMN_NAME_DOMAIN + "," +
            CTDBContract.Auditor._ID + ")";
    private static final String SQL_CREATE_SEEN = "CREATE TABLE " +  CTDBContract.STHSeen.TABLE_NAME + " (" +
            CTDBContract.STHSeen._ID + " INTEGER PRIMARY KEY," +
            CTDBContract.STHSeen.COLUMN_NAME_STH_ID + " INTEGER," +
            CTDBContract.STHSeen.COLUMN_NAME_AUDITOR_ID + " INTEGER," +
            "UNIQUE(" + CTDBContract.STHSeen.COLUMN_NAME_STH_ID + "," +
            CTDBContract.STHSeen.COLUMN_NAME_AUDITOR_ID + ") ON CONFLICT IGNORE)";
    private static final String SQL_CREATE_INDEX_SEEN_1 = "CREATE INDEX seen_index_1 ON " + CTDBContract.STHSeen.TABLE_NAME + "(" +
            CTDBContract.STHSeen.COLUMN_NAME_AUDITOR_ID + "," +
            CTDBContract.STHSeen.COLUMN_NAME_STH_ID + ")";
    private static final String SQL_CREATE_INDEX_SEEN_2 = "CREATE INDEX seen_index_2 ON " + CTDBContract.STHSeen.TABLE_NAME + "(" +
            CTDBContract.STHSeen.COLUMN_NAME_STH_ID + "," +
            CTDBContract.STHSeen.COLUMN_NAME_AUDITOR_ID + ")";
    private static final String SQL_DELETE_STH = "DROP TABLE IF EXISTS " + CTDBContract.STH.TABLE_NAME;
    private static final String SQL_DELETE_AUDITOR = "DROP TABLE IF EXISTS " + CTDBContract.Auditor.TABLE_NAME;
    private static final String SQL_DELETE_SEEN = "DROP TABLE IF EXISTS " + CTDBContract.STHSeen.TABLE_NAME;

    public CTDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_STH);
        db.execSQL(SQL_CREATE_INDEX_STH);
        db.execSQL(SQL_CREATE_AUDITOR);
        db.execSQL(SQL_CREATE_INDEX_AUDITOR);
        db.execSQL(SQL_CREATE_SEEN);
        db.execSQL(SQL_CREATE_INDEX_SEEN_1);
        db.execSQL(SQL_CREATE_INDEX_SEEN_2);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // I have only written one version so far, nothing to upgrade yet
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(SQL_DELETE_STH);
        db.execSQL(SQL_DELETE_AUDITOR);
        db.execSQL(SQL_DELETE_SEEN);
        onCreate(db);
    }
}
