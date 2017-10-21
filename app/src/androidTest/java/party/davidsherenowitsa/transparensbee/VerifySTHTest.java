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
    public void testECDSASignatureVerification() throws Exception {
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

    @Test
    public void testRSASignatureVerification() throws Exception {
        LogServer venafi = new LogServer(
                "ctlog.api.venafi.com/",
                Base64.decode("MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAolpIHxdSlTXLo1s6H1OCdpSj/4DyHDc8wLG9wVmLqy1lk9fz4ATVmm+/1iN2Nk8jmctUKK2MFUtlWXZBSpym97M7frGlSaQXUWyA3CqQUEuIJOmlEjKTBEiQAvpfDjCHjlV2Be4qTM6jamkJbiWtgnYPhJL6ONaGTiSPm7Byy57iaz/hbckldSOIoRhYBiMzeNoA0DiRZ9KmfSeXZ1rB8y8X5urSW+iBzf2SaOfzBvDpcoTuAaWx2DPazoOl28fP1hZ+kHUYvxbcMjttjauCFx+JII0dmuZNIwjfeG/GBb9frpSX219k1O4Wi6OEbHEr8at/XQ0y7gTikOxBn/s5wQIDAQAB", Base64.DEFAULT),
                "Venafi log");
        SignedTreeHead signedTreeHead = new SignedTreeHead(
                1508592962212L,
                99732L,
                Base64.decode("qqHt4DkKFY6rb52tPQDaiLKBINaPm/UohEDenJ9vyz8=", Base64.DEFAULT),
                Base64.decode("BAEBAHqbaueW2ZOEBeBLzfv9ZOyBezr92vxDCPwzm6hb1aGnBkgrrBlbUPummJ6uXUMCjJpWitkkWUXqDK+sueUNVruWyRxaBMmHXpXi9zeiuUKD33JKwkqWB+wAz9DhEI87kp1uizVPwCrDw3oyj/MnpwZchw1g7WMeEwQC8hXgsY6HB9sE2qtfMnyQcVxwdejO3tEuzzrBXPp3HkM3sM6UEGNH1Lt4urvzeujaRaxiAf3Glrrgmg6F0arD7ZhhdxB5GZa2+Rly6i41vsYJI/mK2gM19caczJVIsS01UQhHGjDMcpZvqRtLU10Cwqskwb11cFFXrX2YKvI6lVhaE9HWdYw=", Base64.DEFAULT));
        assertTrue(CryptoUtils.isSTHValid(signedTreeHead, venafi));

        SignedTreeHead wrong = new SignedTreeHead(
                signedTreeHead.getTimestamp() + 1,
                signedTreeHead.getTreeSize(),
                signedTreeHead.getRootHash(),
                signedTreeHead.getTreeHeadSignature());
        assertFalse(CryptoUtils.isSTHValid(wrong, venafi));
    }
}
