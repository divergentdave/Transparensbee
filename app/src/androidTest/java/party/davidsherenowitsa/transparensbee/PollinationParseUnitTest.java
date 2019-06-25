package party.davidsherenowitsa.transparensbee;

import androidx.test.runner.AndroidJUnit4;
import android.util.Base64;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.nio.charset.Charset;
import java.util.List;

import static org.junit.Assert.*;

@RunWith(AndroidJUnit4.class)
public class PollinationParseUnitTest {
    public static final String FIXTURE = "{" +
            "\"sths\":[" +
            "{" +
            "\"sth_version\":0," +
            "\"tree_size\":116," +
            "\"timestamp\":1490930622752," +
            "\"sha256_root_hash\":\"ERr8mSs7l1Ohtb2xrqWVYfkbdfJUBNqUdCaGtNSOaUA=\"," +
            "\"tree_head_signature\":\"BAMARjBEAiB0Kht6TA2bcjwY7KlCBWdRboqknqZM0K9lSjegICphgAIgBUZjzhGY055hqoOtMZ55NZEoNb9k3\\/cgULSRKN3VPQY=\"," +
            "\"log_id\":\"b1N2rDHwMRnYmQCkURX\\/dxUcEdkCwQApBo2yCJo32RM=\"" +
            "}]}";

    @Test
    public void parsePollinateResponse() throws Exception
    {
        AuditorClient auditorClient = new AuditorClient(null);
        List<PollinationSignedTreeHead> list = auditorClient.parseSTHList(new ByteArrayInputStream(FIXTURE.getBytes(Charset.forName("UTF-8"))));
        assertEquals(1, list.size());
        assertEquals("b1N2rDHwMRnYmQCkURX/dxUcEdkCwQApBo2yCJo32RM=", Base64.encodeToString(list.get(0).getLogID(), Base64.NO_WRAP));
        assertEquals(0, list.get(0).getVersion());
        assertEquals(1490930622752L, list.get(0).getTimestamp());
        assertEquals(116, list.get(0).getTreeSize());
        assertEquals("ERr8mSs7l1Ohtb2xrqWVYfkbdfJUBNqUdCaGtNSOaUA=", Base64.encodeToString(list.get(0).getRootHash(), Base64.NO_WRAP));
        assertEquals("BAMARjBEAiB0Kht6TA2bcjwY7KlCBWdRboqknqZM0K9lSjegICphgAIgBUZjzhGY055hqoOtMZ55NZEoNb9k3/cgULSRKN3VPQY=", Base64.encodeToString(list.get(0).getTreeHeadSignature(), Base64.NO_WRAP));
    }

    @Test
    public void roundTripPollinate() throws Exception
    {
        AuditorClient auditorClient = new AuditorClient(null);
        List<PollinationSignedTreeHead> list = auditorClient.parseSTHList(new ByteArrayInputStream(FIXTURE.getBytes(Charset.forName("UTF-8"))));
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        auditorClient.serializeSTHList(os, list);
        assertEquals(FIXTURE, os.toString("UTF-8"));
    }
}
