package party.davidsherenowitsa.transparensbee;

import android.support.test.runner.AndroidJUnit4;
import android.util.Base64;

import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.*;

@RunWith(AndroidJUnit4.class)
public class STHParseUnitTest {
    @Test
    public void testParseSTH() throws Exception
    {
        String fixture = "{\"tree_size\":46466472,\"timestamp\":1480512258330,\"sha256_root_hash\":\"LcGcZRsm+LGYmrlyC5LXhV1T6OD8iH5dNlb0sEJl9bA=\",\"tree_head_signature\":\"BAMASDBGAiEA/M0Nvt77aNe+9eYbKsv6rRpTzFTKa5CGqb56ea4hnt8CIQCJDE7pL6xgAewMd5i3G1lrBWgFooT2kd3+zliEz5Rw8w==\"}";
        SignedTreeHead sth = LogClient.parseSTH(fixture);
        assertEquals(sth.getVersion(), 0);
        assertEquals(sth.getSignatureType(), 1);
        assertEquals(sth.getTimestamp(), 1480512258330L);
        assertEquals(sth.getTreeSize(), 46466472L);
        assertEquals(Base64.encodeToString(sth.getRootHash(), Base64.NO_WRAP), "LcGcZRsm+LGYmrlyC5LXhV1T6OD8iH5dNlb0sEJl9bA=");
        assertEquals(Base64.encodeToString(sth.getTreeHeadSignature(), Base64.NO_WRAP), "BAMASDBGAiEA/M0Nvt77aNe+9eYbKsv6rRpTzFTKa5CGqb56ea4hnt8CIQCJDE7pL6xgAewMd5i3G1lrBWgFooT2kd3+zliEz5Rw8w==");
    }
}
