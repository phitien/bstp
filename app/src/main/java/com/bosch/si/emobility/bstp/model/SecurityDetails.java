package com.bosch.si.emobility.bstp.model;

import com.bosch.si.emobility.bstp.core.Model;

import java.io.Serializable;

/**
 * Created by SSY1SGP on 22/1/16.
 */
public class SecurityDetails extends Model implements Serializable {

    private String securityDetailName;
    private Boolean isAvailable;

    public String getSecurityDetailName() {
        return securityDetailName;
    }

    public void setSecurityDetailName(String securityDetailName) {
        this.securityDetailName = securityDetailName;
    }

    public Boolean getIsAvailable() {
        return isAvailable;
    }

    public void setIsAvailable(Boolean isAvailable) {
        this.isAvailable = isAvailable;
    }

    public SecurityDetails(String securityDetailName) {
        this.securityDetailName = securityDetailName;
        this.isAvailable = false;
    }
}
