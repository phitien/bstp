package com.bosch.si.emobility.bstp;

import android.test.suitebuilder.annotation.SmallTest;
import android.util.Log;

import com.bosch.si.emobility.bstp.core.Constants;
import com.bosch.si.emobility.bstp.core.Utils;
import com.bosch.si.emobility.bstp.model.Driver;
import com.google.gson.Gson;

import junit.framework.TestCase;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

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
    public void testDatetimeConversion() throws ParseException {
        String datetimeString = "2016-04-19T08:05:23.000+0000";//UTC Time

        Date date = Utils.parseUTCDate(datetimeString);

        SimpleDateFormat sdf = new SimpleDateFormat(Constants.REQUEST_DATETIME_FORMAT);
        sdf.setTimeZone(TimeZone.getTimeZone("GMT+4"));//GMT+4 add 4 hours

        assertEquals(datetimeString, Utils.getUTCDatetime(date));//UTC time
        assertEquals("2016-04-19T12:05:23.000+0400", sdf.format(date));//subtract 4 hours
        assertEquals("2016-04-19 / 16:05", Utils.getLocalDatetime(date));//Singapore Time, same time
        assertEquals("2016-04-19", Utils.getLocalDate(date));//Singapore Time, same time
        assertEquals("16:05", Utils.getLocalTime(date));//Singapore Time, same time
    }

}
