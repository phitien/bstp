package com.bosch.si.emobility.bstp.core;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Handler;
import android.text.format.DateFormat;
import android.widget.Toast;

import com.bosch.si.emobility.bstp.R;
import com.bosch.si.emobility.bstp.model.ParkingLocation;
import com.google.android.gms.maps.model.LatLng;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

/**
 * Created by sgp0458 on 4/12/15.
 */
public class Utils {
    public static class Notifier {

        public static void notify(final String message) {
            notify(message, Constants.NOTIFICATION_SHOWING_DURATION);
        }

        public static void notify(final String message, final int milliseconds) {
            final Activity activity = Application.getInstance().getCurrentContext();
            if (activity != null) {
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        final Toast[] toasts = new Toast[1];
                        toasts[0] = Toast.makeText(activity, message, Toast.LENGTH_LONG);
                        toasts[0].show();
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                toasts[0].cancel();
                                toasts[0] = Toast.makeText(activity, message, Toast.LENGTH_LONG);
                                toasts[0].show();
                            }
                        }, 2500);
                    }
                });
            }
        }

        public static void alert(final String title, final String message) {
            final Activity activity = Application.getInstance().getCurrentContext();
            if (activity != null) {
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        new AlertDialog.Builder(activity)
                                .setTitle(title)
                                .setMessage(message)
                                .setCancelable(false)
                                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.cancel();
                                        Event.broadcast("", Constants.EventType.ALERT_DIALOG_HIDE.toString());
                                    }
                                }).create().show();
                    }
                });
            }
        }

        public static void alert(String message) {
            alert(getString(R.string.message), message);
        }

        public static void yesNoDialog(final String title, final String message) {
            final Activity activity = Application.getInstance().getCurrentContext();
            if (activity != null) {
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        new AlertDialog.Builder(activity)
                                .setTitle(title)
                                .setMessage(message)
                                .setCancelable(false)
                                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.cancel();
                                        Event.broadcast("", Constants.EventType.DIALOG_YES.toString());
                                    }
                                })
                                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.cancel();
                                        Event.broadcast("", Constants.EventType.DIALOG_NO.toString());
                                    }
                                })
                                .create().show();

                    }
                });
            }
        }

        public static void yesNoDialog(String message) {
            yesNoDialog(getString(R.string.message), message);
        }


    }

    public static class Log {
        public static void e(String tag, String msg) {
            if (msg != null)
                android.util.Log.e(tag, msg);
        }

        public static void e(String tag, String msg, Throwable tr) {
            if (msg != null)
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
                hide();
                Context context = Application.getInstance().getCurrentContext();
                if (context != null) {
                    hide();
                    String title = dialogTitle;
                    String message = dialogMessage != null ? dialogMessage : Utils.getString(R.string.please_wait);
                    overlayDialog = ProgressDialog.show(context, title, message, true, true);
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

    public static String getResponseDatetimeFormat() {
        return Constants.RESPONSE_DATETIME_FORMAT;
    }

    public static String getRestfulDatetimeFormat() {
        return Constants.REQUEST_DATETIME_FORMAT;
    }

    public static String getDisplayDatetimeFormat() {
        return getString(R.string.datetime_format);
    }

    public static String getDisplayDateFormat() {
        return getString(R.string.date_format);
    }

    public static String getDisplayTimeFormat() {
        return getString(R.string.time_format);
    }

    public static String getRestfulFormattedDatetime(Date datetime) {
        return (String) DateFormat.format(getRestfulDatetimeFormat(), datetime);
    }

    public static String getDisplayFormattedDatetime(Date datetime) {
        SimpleDateFormat sdf = new SimpleDateFormat(getDisplayDatetimeFormat());
        return sdf.format(datetime);
    }

    public static String getDisplayFormattedDate(Date datetime) {
        SimpleDateFormat sdf = new SimpleDateFormat(getDisplayDateFormat());
        return sdf.format(datetime);
    }

    public static String getDisplayFormattedTime(Date datetime) {
        SimpleDateFormat sdf = new SimpleDateFormat(getDisplayTimeFormat());
        return sdf.format(datetime);
    }

    public static boolean isLocationServiceDisabled(Context context) {
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
        float usedPercentage = 0;
        try {
            usedPercentage = 1 - ((float) parkingLocation.getAvailabilityCount() / parkingLocation.getTotalCapacityCount());
            if (usedPercentage == 1)
                color = "red";
            else if (usedPercentage > 0.7 && usedPercentage < 1)
                color = "orange";
            else if (usedPercentage < 0.7)
                color = "green";
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

    /**
     * Get location info for a position in any device
     *
     * @param lat
     * @param lng
     * @return
     */
    public static JSONObject getLocationInfo(double lat, double lng) {
        HttpGet httpGet = new HttpGet("http://maps.googleapis.com/maps/api/geocode/json?latlng=" + lat + "," + lng + "&sensor=true");
        HttpClient client = new DefaultHttpClient();
        HttpResponse response;
        StringBuilder stringBuilder = new StringBuilder();

        try {
            response = client.execute(httpGet);
            HttpEntity entity = response.getEntity();
            InputStream stream = entity.getContent();
            int b;
            while ((b = stream.read()) != -1) {
                stringBuilder.append((char) b);
            }
        } catch (ClientProtocolException e) {
        } catch (IOException e) {
        }

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject = new JSONObject(stringBuilder.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return jsonObject;
    }

    /**
     * Get location name for a position in any device
     *
     * @param latitude
     * @param longitude
     * @return
     */
    public static String getLocationName(double latitude, double longitude) throws Exception {
        if (latitude < -90.0 || latitude > 90.0) {
            throw new IllegalArgumentException("latitude == " + latitude);
        }
        if (longitude < -180.0 || longitude > 180.0) {
            throw new IllegalArgumentException("longitude == " + longitude);
        }
        try {
            JSONObject jsonObj = getLocationInfo(latitude, longitude);
            String currentLocation = "testing";
            String street_address = null;
            String postal_code = null;
            try {
                String status = jsonObj.getString("status").toString();
                if (status.equalsIgnoreCase("OK")) {
                    JSONArray results = jsonObj.getJSONArray("results");
                    int i = 0;
                    do {
                        JSONObject r = results.getJSONObject(i);
                        JSONArray typesArray = r.getJSONArray("types");
                        String types = typesArray.getString(0);

                        if (types.equalsIgnoreCase("street_address")) {
                            street_address = r.getString("formatted_address").split(",")[0];
                        } else if (types.equalsIgnoreCase("postal_code")) {
                            postal_code = r.getString("formatted_address");
                        }

                        if (street_address != null && postal_code != null) {
                            currentLocation = street_address + ", " + postal_code;
                            i = results.length();
                        }

                        i++;
                    } while (i < results.length());

                    return currentLocation;
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        } catch (Exception e) {
            return null;
        }
    }

}
