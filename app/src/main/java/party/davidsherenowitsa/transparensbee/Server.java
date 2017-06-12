package party.davidsherenowitsa.transparensbee;

public abstract class Server {
    private final String humanReadableName;

    public Server(String humanReadableName) {
        this.humanReadableName = humanReadableName;
    }

    public String getHumanReadableName() {
        return humanReadableName;
    }
}
