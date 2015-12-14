package com.bosch.si.emobility.bstp;

/**
 * Created by sgp0458 on 8/12/15.
 */
public class UserSessionManager {

    private class Credential {
        private String username;
        private String password;
        private String tenant;

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }

        public String getTenant() {
            return tenant;
        }

        public void setTenant(String tenant) {
            this.tenant = tenant;
        }

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public boolean isValid() {
            if (this.username == null || this.username.length() <= 0) {
                return false;
            }
            if (this.password == null || this.password.length() <= 0) {
                return false;
            }
            if (this.tenant == null || this.tenant.length() <= 0) {
                return false;
            }
            return true;
        }
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

    public boolean isLogged() {
        return false;
//        if (credential != null) {
//            return this.credential.isValid();
//        }
//        return false;
    }
}
