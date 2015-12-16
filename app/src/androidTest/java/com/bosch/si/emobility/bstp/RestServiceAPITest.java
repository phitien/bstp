package com.bosch.si.emobility.bstp;

import android.test.suitebuilder.annotation.MediumTest;

import com.bosch.si.emobility.bstp.model.User;
import com.bosch.si.emobility.bstp.service.LoginService;

import junit.framework.TestCase;


/**
 * Created by sgp0458 on 4/12/15.
 */
public class RestServiceAPITest extends TestCase {

    @Override
    protected void setUp() throws Exception {
        super.setUp();
    }

    @MediumTest
    public void testLoginService() {
        User user = new User();
        user.setUsername("sgp0458");
        user.setPassword("Sgp04581234");

        LoginService loginService = new LoginService();
        loginService.user = user;
        String response = loginService.executeSync(null);

        assertNotNull(response);
        assertTrue(loginService.isOK());
    }
}
