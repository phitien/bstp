package com.bosch.si.emobility.bstp.core;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.res.Resources;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.text.format.DateFormat;
import android.widget.Toast;

import com.bosch.si.emobility.bstp.R;
import com.bosch.si.emobility.bstp.model.ParkingLocation;
import com.google.android.gms.maps.model.LatLng;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by sgp0458 on 4/12/15.
 */
public class Utils {
    public static class Notifier {
        public static int DURATION = 1000;

        public static void notify(String title, String message) {
            Context context = Application.getInstance().getCurrentContext();
            if (context != null) {
                //noinspection ResourceType
                Toast toast = Toast.makeText(context, message, DURATION);
                toast.show();
            }
        }

        public static void notify(String message) {
            notify("", message);
        }

    }

    public static class Log {
        public static void e(String tag, String msg) {
            android.util.Log.e(tag, msg);
        }

        public static void e(String tag, String msg, Throwable tr) {
            android.util.Log.e(tag, msg, tr);
        }
    }

    public static class Indicator {
        protected static Dialog overlayDialog;
        protected static String dialogTitle;
        protected static String dialogMessage;

        public static String getDialogTitle() {
            return dialogTitle;
        }

        public static void setDialogTitle(String dialogTitle) {
            Indicator.dialogTitle = dialogTitle;
        }

        public static void show() {
            try {
                Context context = Application.getInstance().getCurrentContext();
                if (context != null) {
                    hide();
                    String title = dialogTitle;
                    String message = dialogMessage != null ? dialogMessage : Utils.getString(R.string.please_wait);
                    overlayDialog = ProgressDialog.show(context, title, message, true);
                    overlayDialog.show();
                    dialogTitle = null;
                    dialogMessage = null;
                }
            } catch (Exception e) {
                Utils.Log.e("BSTP_Utils_Indicator_show", e.getMessage());
            }
        }

        public static void hide() {
            if (overlayDialog != null) {
                overlayDialog.hide();
                overlayDialog.dismiss();
            }
        }
    }

    public static Context getContext() {
        return Application.getInstance().getApplicationContext();
    }

    public static int getImage(String name) {
        return getIdentifier(name, "drawable");
    }

    public static int getColor(int resInt) {
        return getResources().getColor(resInt);
    }

    public static int getColor(String name) {
        return getColor(getIdentifier(name, "color"));
    }

    public static String getString(int resInt) {
        return getContext().getString(resInt);
    }

    public static String getString(String name) {
        return getString(getIdentifier(name, "string"));
    }

    public static File getFile(String directoryName, String fileName) {
        File appFileDirectory = getContext().getFilesDir();
        File newDirectory = new File(appFileDirectory.getAbsolutePath() + File.separator + directoryName);
        if (newDirectory.exists() == false) {
            newDirectory.mkdirs();
        }
        return new File(newDirectory.getAbsolutePath() + File.separator + fileName);
    }

    public static String getFileContent(String path) {
        //reading text from file
        try {
            FileInputStream fileIn = new FileInputStream(path);
            InputStreamReader streamReader = new InputStreamReader(fileIn);
            BufferedReader bufferedReader = new BufferedReader(streamReader);
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                sb.append(line).append("\n");
            }
            streamReader.close();
            return sb.toString();
        } catch (FileNotFoundException e) {
            return null;
        } catch (IOException e) {
            return null;
        }
    }

    public static String getFileContent(String directoryName, String fileName) {
        //reading text from file
        File appFileDirectory = getContext().getFilesDir();
        File newDirectory = new File(appFileDirectory.getAbsolutePath() + File.separator + directoryName);
        if (newDirectory.exists() == false) {
            newDirectory.mkdirs();
        }
        String path = newDirectory.getAbsolutePath() + File.separator + fileName;
        return getFileContent(path);
    }

    public static Resources getResources() {
        return getContext().getResources();
    }

    public static String getPackageName() {
        return getContext().getPackageName();
    }

    public static int getIdentifier(String name, String defType, String defPackage) {
        return getResources().getIdentifier(name, defType, defPackage);
    }

    public static int getIdentifier(String name, String defType) {
        return getIdentifier(name, defType, getPackageName());
    }

    public static void askPermissionsForLocation(Activity activity) {
        if (!hasAccessToLocation(activity)) {
            // No explanation needed, we can request the permission.
//            ActivityCompat.requestPermissions(activity,
//                    new String[]{
//                            Manifest.permission.ACCESS_FINE_LOCATION,
//                            Manifest.permission.ACCESS_COARSE_LOCATION
//                    },
//                    Constants.PERMISSIONS_REQUEST_RESULT);
        }
    }

    public static boolean hasAccessToLocation(Activity activity) {
//        return ContextCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
        return true;
    }

    public static Location getMyLocation(Context context) {
        Location location = null;
        try {
            LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
            // getting GPS status
            boolean isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
            // getting network status
            boolean isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
            if (!isGPSEnabled && !isNetworkEnabled) {
                // no network provider is enabled
            } else if (isNetworkEnabled) {// if GPS Enabled get lat/long using GPS Services
                locationManager.requestLocationUpdates(
                        LocationManager.NETWORK_PROVIDER,
                        Constants.MIN_TIME_BW_UPDATES,
                        Constants.MIN_DISTANCE_CHANGE_FOR_UPDATES, (LocationListener) context);
                location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            } else if (isGPSEnabled) {
                locationManager.requestLocationUpdates(
                        LocationManager.GPS_PROVIDER,
                        Constants.MIN_TIME_BW_UPDATES,
                        Constants.MIN_DISTANCE_CHANGE_FOR_UPDATES, (LocationListener) context);
                location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            }
        } catch (Exception e) {
            Log.e("BSTP_Utils_getMyLocation", e.getMessage());
        }
        return location;
    }

    public static LatLng getMyLocationLatLng(Context context) {
        Location location = Utils.getMyLocation(context);
        if (location != null)
            return new LatLng(location.getLatitude(), location.getLongitude());
        else
            return Constants.DEFAULT_LOCATION;
    }

    public static String getDbFormattedDatetime(Date datetime) {
        return (String) DateFormat.format(Constants.DB_DATETIME_FORMAT, datetime);
    }

    public static String getDbFormattedDatetime(String datetime) {
        try {
            return getDbFormattedDatetime(new Date(datetime));
        }
        catch (Exception e) {
            return null;
        }
    }

    public static String getFormattedDatetime(Date datetime) {
        return (String) DateFormat.format(Constants.DATETIME_FORMAT, datetime);
    }

    public static String getFormattedDatetime(String datetime) {
        try {
            return getFormattedDatetime(new Date(datetime));
        }
        catch (Exception e) {
            return null;
        }
    }

    public static String getFormattedDate(Date datetime) {
        return (String) DateFormat.format(Constants.DATE_FORMAT, datetime);
    }

    public static String getFormattedTime(Date datetime) {
        return (String) DateFormat.format(Constants.TIME_FORMAT, datetime);
    }

    public static boolean isLocationServiceDisabled(Context context) {
        Location location = null;
        try {
            LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
            // getting GPS status
            boolean isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
            // getting network status
            boolean isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
            if (!isGPSEnabled && !isNetworkEnabled) {
                return true;
            }
            return false;
        } catch (Exception e) {
            Log.e("BSTP_Utils_getMyLocation", e.getMessage());
        }
        return true;
    }

    public static Date getNextDateByAddingHours(int hoursInterval) {

        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.HOUR, hoursInterval);
        Date date = calendar.getTime();
        return date;
    }

    public static String getParkingIconName(ParkingLocation parkingLocation) {
        String prefix = "icon_parking";

        String bookable = "bookable1";

        String color = "blue";
        try {
            float usedPercentage = 1 - (parkingLocation.getAvailabilityCount() / parkingLocation.getTotalCapacityCount());
            if (usedPercentage < 0.7)
                color = "green";
            else if (usedPercentage == 0)
                color = "red";
            else
                color = "orange";
        } catch (Exception e) {
            bookable = "bookable0";
        }

        String type = "";
        if (parkingLocation.getParkingType().equals("PARKING_GARAGE")) {
            type = "garage";
        } else {
            if (parkingLocation.hasFacility(Constants.TOILET_FACILITY_NAME)) {
                type = "wc1";
            } else {
                type = "wc0";
            }
        }

        return prefix + "_" + bookable + "_" + color + "_" + type;
    }

}
