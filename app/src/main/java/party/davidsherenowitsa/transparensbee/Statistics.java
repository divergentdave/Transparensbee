package party.davidsherenowitsa.transparensbee;

public interface Statistics {
    public void addLogFailure(LogServer log);
    public void addLogSuccess(LogServer log);
    public void addAuditorFailure(AuditorServer log);
    public void addAuditorSuccess(AuditorServer log);
}
