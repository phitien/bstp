package com.bosch.si.emobility.bstp;

import android.test.suitebuilder.annotation.SmallTest;
import android.util.Log;

import com.bosch.si.emobility.bstp.core.Utils;
import com.bosch.si.emobility.bstp.model.Driver;
import com.google.gson.Gson;

import junit.framework.TestCase;

import java.util.Date;

/**
 * Created by SSY1SGP on 26/1/16.
 */
public class UtilsTest extends TestCase {

    @SmallTest
    public void testDriverModel() {

        try {
            String jsonString = "{\"driverId\":\"sgp0458\",\"driverName\":\"Tien\",\"driverContact\":null,\"driverEmail\":\"ductien.phi@bosch-si.com\",\"notificationChannels\":[],\"driverPassCode\":null,\"trucks\":[],\"associatedTrucks\":[]}";
            Gson gson = new Gson();
            Driver driver = gson.fromJson(jsonString, Driver.class);
            assert (driver != null);
            Log.d("BSTP_TEST", "user context id" + driver.toString());
        } catch (Exception e) {
            Log.d("BSTP_TEST", "user context id" + e.getLocalizedMessage());
        }
        Log.d("BSTP_TEST", "user context id");

    }

    @SmallTest
    public void testDatetimeConversion() {
        String datetimeString = "Mon Feb 01 05:16:10 GMT+01:00 2016";
        Date date = new Date(datetimeString);
        assertEquals(datetimeString, date.toString());
        assertEquals("2016-02-01T05:16:10Z", Utils.getRestfulFormattedDatetime(date));
        assertEquals("2016-02-01 / 05:16", Utils.getDisplayFormattedDatetime(date));
        assertEquals("2016-02-01", Utils.getDisplayFormattedDate(date));
        assertEquals("05:16", Utils.getDisplayFormattedTime(date));
    }

}
