package party.davidsherenowitsa.transparensbee;

import java.net.MalformedURLException;
import java.net.URL;

public class AuditorServer {
    public static final AuditorServer CERTSPOTTER = new AuditorServer("certspotter.com", "Cert Spotter");
    public static final AuditorServer GRAHAM_EDGECOMBE = new AuditorServer("ct.grahamedgecombe.com", "Graham Edgecombe Certificate Transparency Monitor");
    public static final AuditorServer[] AUDITORS = new AuditorServer[] {CERTSPOTTER, GRAHAM_EDGECOMBE};

    private final String domain;
    private final String humanReadableName;

    public AuditorServer(String domain, String humanReadableName)
    {
        this.domain = domain;
        this.humanReadableName = humanReadableName;
    }

    public String getDomain()
    {
        return domain;
    }

    public String getHumanReadableName()
    {
        return humanReadableName;
    }

    public URL getPollinationEndpoint() throws MalformedURLException {
        return new URL("https://" + domain + "/.well-known/ct/v1/sth-pollination");
    }
}
