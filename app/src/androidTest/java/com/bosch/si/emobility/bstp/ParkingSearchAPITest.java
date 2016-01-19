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
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * Created by SSY1SGP on 13/1/16.
 */
public class ParkingSearchAPITest extends TestCase{

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


            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");

            Calendar calendar = Calendar.getInstance(); // gets a calendar using the default time zone and locale.
            calendar.add(Calendar.HOUR, 24);
            String startTime = df.format(calendar.getTime());

            calendar.add(Calendar.HOUR, 48);
            String endTime = df.format(calendar.getTime());


            Log.d("BSTP_TEST","start time"+startTime);
            Log.d("BSTP_TEST","end time"+endTime);


            //create search criteria
            SearchCriteria searchCriteria = new SearchCriteria();
            searchCriteria.setDirection("Notused");
            searchCriteria.setHighway("Notused");
            searchCriteria.setStartTime(startTime);
            searchCriteria.setEndTime(endTime);
            searchCriteria.setLatitude("48.83610");
            searchCriteria.setLongitude("9.19935");
            searchCriteria.setLocationName("stuttgart");
            searchCriteria.setRadius(15000);
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


                    /*AsyncTask<Void, Void, Void> task = new AsyncTask<Void, Void, Void>() {
                        @Override
                        protected Void doInBackground(Void... params) {
                            Gson gson = new Gson();
                            List<ParkingLocation> locations = gson.fromJson(responseString, new TypeToken<ArrayList<ParkingLocation>>() {
                            }.getType());
                            for (ParkingLocation parkingLocation : locations) {
                                parkingLocations.put(parkingLocation.getParkingId(), parkingLocation);
                            }
                            return null;
                        }

                        @Override
                        protected void onPostExecute(Void aVoid) {

                        }
                    };

                    task.executeOnExecutor(AsyncTask.SERIAL_EXECUTOR);*/
                }

                @Override
                public void failure(IService service) {

                    Log.d("BSTP_TEST", "Search service response" + service.getResponseString());

                    assertFalse("failed to search",false);
                }
            });

            searchServiceSignal.await(TestUtil.WAITING_TIME, TimeUnit.SECONDS);

            Log.d("BSTP_TEST", "Search service response" + searchService.getResponseCode());

            assertTrue("Service is ok",searchService.isOK());

        }
        catch (Exception e) {

            Log.d("BSTP_TEST","Search service execption"+e.getLocalizedMessage());
            assertFalse(false);
        }
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
    }
}
