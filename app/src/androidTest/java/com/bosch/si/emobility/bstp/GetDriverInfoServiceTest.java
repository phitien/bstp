package com.bosch.si.emobility.bstp;

import android.test.suitebuilder.annotation.LargeTest;
import android.util.Log;

import com.bosch.si.emobility.bstp.service.GetDriverInfoService;
import com.bosch.si.rest.IService;
import com.bosch.si.rest.callback.ServiceCallback;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * Created by SSY1SGP on 20/1/16.
 */
public class GetDriverInfoServiceTest extends BaseAPITest {

    @Override
    protected void setUp() throws Exception {
        super.setUp();
    }

    @LargeTest
    public void testGetDriverInfoService(){

        try {

            final CountDownLatch getDriverInfoServiceSignal = new CountDownLatch(1);

            //assert authentication
            assertNotNull(user);
            assertNotNull(user.getContextId());

            Log.d("BSTP_TEST", "user context id" + user.getContextId());

            GetDriverInfoService getDriverInfoService = new GetDriverInfoService();
            getDriverInfoService.executeAsync(new ServiceCallback() {
                @Override
                public void success(IService service) {
                    Log.d("BSTP_TEST", "Get driver info service success response" + service.getResponseString());
                }

                @Override
                public void failure(IService service) {
                    Log.d("BSTP_TEST", "Get driver info service failed");
                }
            });

            getDriverInfoServiceSignal.await(TestUtil.WAITING_TIME, TimeUnit.SECONDS);
            Log.d("BSTP_TEST", "Get driver info service response" + getDriverInfoService.getResponseCode());
            assertTrue("Service is ok", getDriverInfoService.isOK());

        } catch (Exception e) {
            Log.d("BSTP_TEST", "Get driver info service failed");
        }
    }
}
