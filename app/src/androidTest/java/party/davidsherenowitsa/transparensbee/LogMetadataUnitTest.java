package party.davidsherenowitsa.transparensbee;

import android.support.test.runner.AndroidJUnit4;
import android.util.Base64;

import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.*;

@RunWith(AndroidJUnit4.class)
public class LogMetadataUnitTest {
    @Test
    public void pilotLogIDCorrect() throws Exception {
        assertEquals(Base64.encodeToString(LogServer.PILOT.getLogID(), Base64.NO_WRAP),
                "pLkJkLQYWBSHuxOizGdwCjw1mAT5G9+443fNDsgN3BA=");
    }
}
