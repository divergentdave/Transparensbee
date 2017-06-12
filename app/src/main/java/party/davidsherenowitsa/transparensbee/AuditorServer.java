package party.davidsherenowitsa.transparensbee;

import java.net.MalformedURLException;
import java.net.URL;

public class AuditorServer extends Server {
    public static final AuditorServer CERTSPOTTER = new AuditorServer("certspotter.com", "Cert Spotter");
    public static final AuditorServer GRAHAM_EDGECOMBE = new AuditorServer("ct.grahamedgecombe.com", "Graham Edgecombe Certificate Transparency Monitor");
    public static final AuditorServer[] AUDITORS = new AuditorServer[] {CERTSPOTTER, GRAHAM_EDGECOMBE};

    private final String domain;

    public AuditorServer(String domain, String humanReadableName)
    {
        super(humanReadableName);
        this.domain = domain;
    }

    public String getDomain()
    {
        return domain;
    }

    public URL getPollinationEndpoint() throws MalformedURLException {
        return new URL("https://" + domain + "/.well-known/ct/v1/sth-pollination");
    }
}
