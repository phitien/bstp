package bstp.android.boschsi.com.bstp.model;

/**
 * Created by sgp0458 on 4/12/15.
 */
public class User extends Model {
    protected String tenantName;
    protected String userName;
    protected String password;

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

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }
}
