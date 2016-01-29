package com.bosch.si.emobility.bstp;

import android.util.Log;

import com.bosch.si.emobility.bstp.core.User;
import com.bosch.si.emobility.bstp.core.UserSessionManager;
import com.bosch.si.emobility.bstp.service.LoginService;
import com.bosch.si.rest.IService;
import com.bosch.si.rest.callback.ServiceCallback;

import org.json.JSONObject;
import org.json.XML;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * Created by SSY1SGP on 13/1/16.
 */
public class AuthenticationManager {

    public User user;

    public void doLogin() {

        try {

            final CountDownLatch loginServiceSignal = new CountDownLatch(1);

            user = new User();
            user.setUsername("dhlforwarder");
            user.setPassword("Dhlforwarder123$");

            LoginService loginService = new LoginService();
            loginService.user = user;
            loginService.executeAsync(new ServiceCallback() {
                @Override
                public void success(IService service) {
                    //Save Authorization Cookie
                    try {
                        Log.d("BSTP_TEST", "Login service response" + service.getResponseString());
                        JSONObject jsonObj = XML.toJSONObject(service.getResponseString());
                        user.setContextId(jsonObj.getJSONObject("ns2:identityContext").getString("ns2:contextId"));
                        UserSessionManager.getInstance().setUserSession(user);
                    } catch (Exception e) {
                    }
                }

                @Override
                public void failure(IService service) {
                }
            });

            loginServiceSignal.await(TestUtil.WAITING_TIME, TimeUnit.SECONDS);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}
