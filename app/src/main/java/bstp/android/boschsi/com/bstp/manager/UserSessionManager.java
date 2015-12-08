package bstp.android.boschsi.com.bstp.manager;

/**
 * Created by sgp0458 on 8/12/15.
 */
public class UserSessionManager {

    private class Credential {
        private String username;
        private String tenant;
    }

    private static UserSessionManager ourInstance = new UserSessionManager();

    public static UserSessionManager getInstance() {
        return ourInstance;
    }

    private UserSessionManager() {
    }

    protected Credential credential;

    public Credential getCredential() {
        return credential;
    }
}
