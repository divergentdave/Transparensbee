package party.davidsherenowitsa.transparensbee;

import android.provider.BaseColumns;

public final class CTDBContract {
    private CTDBContract() {
    }

    public static class STH implements BaseColumns {
        public static final String TABLE_NAME = "signed_tree_head";
        public static final String COLUMN_NAME_VERSION = "version";
        public static final String COLUMN_NAME_SIGNATURE_TYPE = "signature_type";
        public static final String COLUMN_NAME_TIMESTAMP = "timestamp";
        public static final String COLUMN_NAME_TREE_SIZE = "tree_size";
        public static final String COLUMN_NAME_ROOT_HASH = "root_hash";
        public static final String COLUMN_NAME_TREE_HEAD_SIGNATURE = "tree_head_signature";
        public static final String COLUMN_NAME_TREE_HEAD_SIGNATURE_HEX = "tree_head_signature_hex";
        public static final String COLUMN_NAME_LOG_ID = "log_id";
    }

    public static class Auditor implements BaseColumns {
        public static final String TABLE_NAME = "auditor";
        public static final String COLUMN_NAME_DOMAIN = "domain";
    }

    public static class STHSeen implements BaseColumns {
        public static final String TABLE_NAME = "sth_seen";
        public static final String COLUMN_NAME_STH_ID = "sth_id";
        public static final String COLUMN_NAME_AUDITOR_ID = "auditor_id";
    }
}
