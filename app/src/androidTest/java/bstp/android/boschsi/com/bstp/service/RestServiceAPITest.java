package bstp.android.boschsi.com.bstp.service;

import android.test.suitebuilder.annotation.MediumTest;

import junit.framework.TestCase;

import bstp.android.boschsi.com.bstp.model.User;
import retrofit.Retrofit;


/**
 * Created by sgp0458 on 4/12/15.
 */
public class RestServiceAPITest extends TestCase {

    private IRestServiceAPI restServiceAPI;

    @Override
    protected void setUp() throws Exception {
        super.setUp();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://ivsrv040.bosch-si.com/")
                .build();

        restServiceAPI = retrofit.create(IRestServiceAPI.class);
    }

    @MediumTest
    public void testLoginService() {
        User user = new User();
        user.setTenantName("REUTLINGEN");
        user.setUserName("sete");
        user.setPassword("sete1Elf!!");
        restServiceAPI.login(user);
    }
}
