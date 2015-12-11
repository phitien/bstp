package com.bosch.si.emobility.bstp;

import android.test.suitebuilder.annotation.MediumTest;

import com.bosch.si.emobility.bstp.model.User;
import com.bosch.si.emobility.bstp.service.IRestServiceAPI;

import junit.framework.TestCase;

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
