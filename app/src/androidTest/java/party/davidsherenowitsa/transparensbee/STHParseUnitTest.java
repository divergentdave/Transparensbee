package party.davidsherenowitsa.transparensbee;

import androidx.test.runner.AndroidJUnit4;
import android.util.Base64;

import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.*;

@RunWith(AndroidJUnit4.class)
public class STHParseUnitTest {
    @Test
    public void testParseSTH() throws Exception
    {
        LogClient logClient = new LogClient(null);
        String fixture = "{\"tree_size\":46466472,\"timestamp\":1480512258330,\"sha256_root_hash\":\"LcGcZRsm+LGYmrlyC5LXhV1T6OD8iH5dNlb0sEJl9bA=\",\"tree_head_signature\":\"BAMASDBGAiEA/M0Nvt77aNe+9eYbKsv6rRpTzFTKa5CGqb56ea4hnt8CIQCJDE7pL6xgAewMd5i3G1lrBWgFooT2kd3+zliEz5Rw8w==\"}";
        SignedTreeHead sth = logClient.parseSTH(fixture);
        assertEquals(0, sth.getVersion());
        assertEquals(1, sth.getSignatureType());
        assertEquals(1480512258330L, sth.getTimestamp());
        assertEquals(46466472L, sth.getTreeSize());
        assertEquals("LcGcZRsm+LGYmrlyC5LXhV1T6OD8iH5dNlb0sEJl9bA=", Base64.encodeToString(sth.getRootHash(), Base64.NO_WRAP));
        assertEquals("BAMASDBGAiEA/M0Nvt77aNe+9eYbKsv6rRpTzFTKa5CGqb56ea4hnt8CIQCJDE7pL6xgAewMd5i3G1lrBWgFooT2kd3+zliEz5Rw8w==", Base64.encodeToString(sth.getTreeHeadSignature(), Base64.NO_WRAP));
    }
}
