package party.davidsherenowitsa.transparensbee;

import android.support.test.runner.AndroidJUnit4;
import android.util.Base64;

import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

@RunWith(AndroidJUnit4.class)
public class VerifySTHTest {
    @Test
    public void testSignatureVerification() throws Exception {
        LogServer pilot = new LogServer(
                "ct.googleapis.com/pilot",
                Base64.decode("MFkwEwYHKoZIzj0CAQYIKoZIzj0DAQcDQgAEfahLEimAoz2t01p3uMziiLOl/fHTDM0YDOhBRuiBARsV4UvxG2LdNgoIGLrtCzWE0J5APC2em4JlvR8EEEFMoA==",
                        Base64.DEFAULT),
                "Google 'Pilot' log");
        SignedTreeHead signedTreeHead = new SignedTreeHead(
                1508457694549L,  // timestamp
                160480356L,  // tree size
                Base64.decode("TSPFlYW0wx+xrrthXS4w8OHXuEmP/e+86S0FQXaZC7o=", Base64.DEFAULT),  // root hash
                Base64.decode("BAMARzBFAiAVsr7Dq8wBvj5ItmVXiyykjJn1zDs8DcukNVNW+cARgQIhAKF8Ns0VVFA26xuvZCTs7Na99uhzLCc0GfBIHo4rBQXI", Base64.DEFAULT));  // tree head signature
        assertTrue(CryptoUtils.isSTHValid(signedTreeHead, pilot));

        SignedTreeHead wrong = new SignedTreeHead(
                signedTreeHead.getTimestamp() + 1,
                signedTreeHead.getTreeSize(),
                signedTreeHead.getRootHash(),
                signedTreeHead.getTreeHeadSignature());
        assertFalse(CryptoUtils.isSTHValid(wrong, pilot));
    }
}
