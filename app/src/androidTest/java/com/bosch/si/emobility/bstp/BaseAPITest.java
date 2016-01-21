package com.bosch.si.emobility.bstp;

import android.util.Log;

import com.bosch.si.emobility.bstp.core.User;

import junit.framework.TestCase;

/**
 * Created by SSY1SGP on 20/1/16.
 */
public class BaseAPITest extends TestCase {

    private AuthenticationManager authenicationManager = new AuthenticationManager();

    public User user;

    private void doLogin() throws Exception {

        Log.d("BSTP_TEST", "BaseAPITest doLogin");

        try {
            authenicationManager.doLogin();
            user = authenicationManager.user;
            Log.d("BSTP_TEST", "BaseAPITest doLogin response" + user.getContextId());

        } catch (Exception e) {
            e.printStackTrace();
            assertFalse(false);
        }
    }

    @Override
    protected void setUp() throws Exception {

        Log.d("BSTP_TEST", "BaseAPITest setUp");

        super.setUp();
        doLogin();
    }
}
