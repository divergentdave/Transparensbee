package party.davidsherenowitsa.transparensbee;

import androidx.test.runner.AndroidJUnit4;
import android.util.Base64;

import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.*;

@RunWith(AndroidJUnit4.class)
public class LogMetadataUnitTest {
    @Test
    public void pilotLogIDCorrect() throws Exception {
        LogServer pilot = new LogServer(
                "ct.googleapis.com/pilot",
                Base64.decode("MFkwEwYHKoZIzj0CAQYIKoZIzj0DAQcDQgAEfahLEimAoz2t01p3uMziiLOl/fHTDM0YDOhBRuiBARsV4UvxG2LdNgoIGLrtCzWE0J5APC2em4JlvR8EEEFMoA==",
                        Base64.DEFAULT),
                "Google 'Pilot' log");
        assertEquals("pLkJkLQYWBSHuxOizGdwCjw1mAT5G9+443fNDsgN3BA=",
                Base64.encodeToString(pilot.getLogID(), Base64.NO_WRAP));
    }
}
