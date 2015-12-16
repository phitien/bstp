package com.bosch.si.emobility.bstp.model;

import com.google.gson.Gson;

/**
 * Created by sgp0458 on 4/12/15.
 */
public class User extends Model {

    public static User parseUser(String jsonString) {
        try {
            return new Gson().fromJson(jsonString, User.class);
        } catch (Exception e) {
            return null;
        }
    }

    protected String tenantName = "DEFAULT";
    protected String username;
    protected String password;
    protected String authorizationCookie;

    public String getAuthorizationCookie() {
        return authorizationCookie;
    }

    public void setAuthorizationCookie(String authorizationCookie) {
        this.authorizationCookie = authorizationCookie;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getTenantName() {
        return tenantName;
    }

    public void setTenantName(String tenantName) {
        this.tenantName = tenantName;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public boolean isValid() {
        if (this.username == null || this.username.isEmpty()) {
            return false;
        }
        if (this.password == null || this.password.isEmpty()) {
            return false;
        }
        if (this.tenantName == null || this.tenantName.isEmpty()) {
            return false;
        }
        if (this.authorizationCookie == null || this.authorizationCookie.isEmpty()) {
            return false;
        }
        return true;
    }

    public String toXMLString() {
        return "<im:authentication xmlns:im='http://www.bosch.com/IAP/im1_1_0'><im:userNamePassword><im:tenantName>" + tenantName + "</im:tenantName><im:userName>" + username + "</im:userName><im:password>" + password + "</im:password><im:authorizationCookie>" + authorizationCookie + "</im:authorizationCookie></im:userNamePassword></im:authentication>";
    }
}
