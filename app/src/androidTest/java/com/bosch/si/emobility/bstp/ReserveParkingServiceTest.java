package com.bosch.si.emobility.bstp;

import android.test.suitebuilder.annotation.LargeTest;
import android.util.Log;

import com.bosch.si.emobility.bstp.service.ReserveParkingService;
import com.bosch.si.rest.IService;
import com.bosch.si.rest.callback.ServiceCallback;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * Created by SSY1SGP on 20/1/16.
 */
public class ReserveParkingServiceTest extends BaseAPITest {

    private String parkingId = "10000";
    private String additionalInfo = "Testing Service";
    private String startTime ="";
    private String endTime = "";
    private String identificationType = "Testing Service";
    private String paymentMode = "CreditCard";
    private String vehicleId = "truck-42";
    private String driverId = "DRIVER-1";

    @Override
    protected void setUp() throws Exception {
        super.setUp();
    }

    @LargeTest
    public void testReserveParkingSpaceAPI(){

        try {

            final CountDownLatch reservationServiceSignal = new CountDownLatch(1);

            //assert authentication
            assertNotNull(user);
            assertNotNull(user.getContextId());

            Log.d("BSTP_TEST", "user context id" + user.getContextId());

            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");

            Calendar calendar = Calendar.getInstance(); // gets a calendar using the default time zone and locale.
            calendar.add(Calendar.HOUR, 26);
            startTime = df.format(calendar.getTime());

            calendar.add(Calendar.HOUR, 2);
            endTime = df.format(calendar.getTime());


            Log.d("BSTP_TEST", "start time" + startTime);
            Log.d("BSTP_TEST","end time"+endTime);

            ReserveParkingService reserveParkingService = new ReserveParkingService();
            reserveParkingService.parkingId = parkingId;
            reserveParkingService.additionalInfo = additionalInfo;
            reserveParkingService.startTime = startTime;
            reserveParkingService.endTime = endTime;
            reserveParkingService.driverId = driverId;
            reserveParkingService.paymentMode = paymentMode;
            reserveParkingService.vehicleId = vehicleId;

            reserveParkingService.executeAsync(new ServiceCallback() {

                @Override
                public void success(IService service) {
                    final String responseString = service.getResponseString();

                    Log.d("BSTP_TEST", "Reservation service success response" + responseString);
                }

                @Override
                public void failure(IService service) {
                    Log.d("BSTP_TEST", "Reservation service failure response" + service.getResponseCode());
                }
            });

            reservationServiceSignal.await(TestUtil.WAITING_TIME, TimeUnit.SECONDS);

            Log.d("BSTP_TEST", "Reservation service response " + reserveParkingService.getResponseCode());

            assertTrue("Service is ok", reserveParkingService.isOK());
        }
        catch (Exception e) {
            Log.d("BSTP_TEST", "Reservation service exception");
        }
    }
}
