package com.bosch.si.emobility.bstp;

import android.test.suitebuilder.annotation.LargeTest;
import android.util.Log;

import com.bosch.si.emobility.bstp.core.User;
import com.bosch.si.emobility.bstp.model.ParkingLocation;
import com.bosch.si.emobility.bstp.model.SearchCriteria;
import com.bosch.si.emobility.bstp.service.SearchService;
import com.bosch.si.rest.IService;
import com.bosch.si.rest.callback.ServiceCallback;

import junit.framework.TestCase;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * Created by SSY1SGP on 13/1/16.
 */
public class ParkingSearchServiceTest extends TestCase {

    private AuthenticationManager authenicationManager = new AuthenticationManager();

    private User user;
    private Map<String, ParkingLocation> parkingLocations = new HashMap<>();

    private void doLogin() throws Exception {

        try {
            authenicationManager.doLogin();
            user = authenicationManager.user;

        } catch (Exception e) {
            e.printStackTrace();
            assertFalse(false);
        }
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        doLogin();
    }

    @LargeTest
    public void testSearchParkingService() throws InterruptedException {

        try {

            final CountDownLatch searchServiceSignal = new CountDownLatch(1);


            //assert authentication
            assertNotNull(user);
            assertNotNull(user.getContextId());

            Log.d("BSTP_TEST", "user context id" + user.getContextId());

            //create search criteria
            SearchCriteria searchCriteria = new SearchCriteria();
            searchCriteria.setDirection("Notused");
            searchCriteria.setHighway("Notused");
            searchCriteria.setStartTime(new Date());
            searchCriteria.setEndTime(new Date());
            searchCriteria.setLatitude(0.0);
            searchCriteria.setLongitude(0.0);
            searchCriteria.setLocationName("telaviv");
            searchCriteria.setRadius(25000);
            searchCriteria.setSearchString("Notused");

            //create search service
            SearchService searchService = searchCriteria.createSearchService();

            //execute service
            searchService.executeAsync(new ServiceCallback() {
                @Override
                public void success(IService service) {

                    parkingLocations = new HashMap<>();

                    final String responseString = service.getResponseString();

                    Log.d("BSTP_TEST", "Search service response" + responseString);

                }

                @Override
                public void failure(IService service) {

                    Log.d("BSTP_TEST", "Search service response" + service.getResponseString());

                    assertFalse("failed to search", false);
                }
            });

            searchServiceSignal.await(TestUtil.WAITING_TIME, TimeUnit.SECONDS);

            Log.d("BSTP_TEST", "Search service response" + searchService.getResponseCode());

            assertTrue("Service is ok", searchService.isOK());

        } catch (Exception e) {

            Log.d("BSTP_TEST", "Search service execption" + e.getLocalizedMessage());
            assertFalse(false);
        }
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
    }
}
