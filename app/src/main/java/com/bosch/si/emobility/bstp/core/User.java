package com.bosch.si.emobility.bstp.core;

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

    private String tenantName = "DHL";
    private String username;
    private String password;
    private String contextId;
    private boolean saveCredentials;

    public String getContextId() {
        return contextId;
    }

    public void setContextId(String contextId) {
        this.contextId = contextId;
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

    public boolean isSaveCredentials() {
        return saveCredentials;
    }

    public void setSaveCredentials(boolean saveCredentials) {
        this.saveCredentials = saveCredentials;
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
        if (this.contextId == null || this.contextId.isEmpty()) {
            return false;
        }
        return true;
    }

    /**
     * <im:authentication xmlns:im='http://www.bosch.com/IAP/im1_1_0'>
     *     <im:userNamePassword>
     *         <im:tenantName>DEFAULT</im:tenantName>
     *         <im:userName>sgp0458</im:userName>
     *         <im:password>Sgp04581234</im:password>
     *         <im:contextId>contextId</im:contextId>
     *     </im:userNamePassword>
     * </im:authentication>
     *
     * @return user information in xml format
     */
    public String toXMLString() {
        return "<im:authentication xmlns:im='http://www.bosch.com/IAP/im1_1_0'><im:userNamePassword><im:tenantName>" + tenantName + "</im:tenantName><im:userName>" + username + "</im:userName><im:password>" + password + "</im:password><im:contextId>" + contextId + "</im:contextId></im:userNamePassword></im:authentication>";
    }
}
