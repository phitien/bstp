package com.bosch.si.emobility.bstp.component;

import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.bosch.si.emobility.bstp.R;
import com.bosch.si.emobility.bstp.activity.Activity;
import com.bosch.si.emobility.bstp.activity.MapsActivity;
import com.bosch.si.emobility.bstp.app.Event;
import com.bosch.si.emobility.bstp.helper.Constants;
import com.bosch.si.emobility.bstp.helper.Utils;
import com.bosch.si.emobility.bstp.model.ParkingLocation;
import com.bosch.si.emobility.bstp.service.ParkingLocationInfoService;
import com.bosch.si.rest.IService;
import com.bosch.si.rest.callback.ServiceCallback;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.google.maps.android.SphericalUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Created by sgp0458 on 16/12/15.
 */
public class MapComponent extends Component {

    private LatLngBounds currentCameraBounds;
    private Marker myLocationMarker;

    private LatLng searchingLatLng;
    private Marker searchingMarker;

    private Map<String, Marker> markers = new HashMap<>();
    private Map<String, ParkingLocation> parkingLocations = new HashMap<>();

    private LatLng prevLatLng = null;
    private LatLng currLatLng = null;

    public MapComponent(Activity activity) {
        super(activity);
    }

    public LatLng getSearchingLatLng() {
        return searchingLatLng;
    }

    public void setSearchingLatLng(LatLng searchingLatLng) {
        this.searchingLatLng = searchingLatLng;
        moveCamera(this.searchingLatLng);
    }

    GoogleMap map;
    SupportMapFragment mapFragment;
    View mapView;

    @Override
    public void setActivity(Activity activity) {
        super.setActivity(activity);
        layout = (ViewGroup) this.activity.findViewById(R.id.mapLayout);
        mapFragment = (SupportMapFragment) this.activity.getSupportFragmentManager().findFragmentById(R.id.map);
        mapView = this.activity.findViewById(R.id.map);
        map = mapFragment.getMap();
    }

    private void zoomToLocation(LatLng latLng) {
        moveCamera(latLng);
    }

    private void drawMySearchingMarker(LatLng latLng) {
//        if (searchingMarker == null)
//            searchingMarker = map.addMarker(new MarkerOptions()
//                    .position(latLng)
//                    .title(activity.getString(R.string.current_search_location)));
//        else
//            searchingMarker.setPosition(latLng);
    }

    private LatLng drawMyLocationMarker() {
        LatLng latLng = Utils.getMyLocationLatLng(this.activity);
        if (myLocationMarker == null) {
            myLocationMarker = map.addMarker(new MarkerOptions()
                    .position(latLng)
                    .title(activity.getString(R.string.your_location))
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
        } else
            myLocationMarker.setPosition(latLng);
        return latLng;
    }

    @NonNull
    private LatLngBounds getLatLngBounds(LatLng latLng, double radius) {
        LatLng southwest = SphericalUtil.computeOffset(latLng, radius * Math.sqrt(2.0), 225);
        LatLng northeast = SphericalUtil.computeOffset(latLng, radius * Math.sqrt(2.0), 45);
        return new LatLngBounds(southwest, northeast);
    }

    private CameraUpdate getZoomForDistance(LatLng latLng, double radius) {
        return CameraUpdateFactory.newLatLngBounds(getLatLngBounds(latLng, radius), 0);
    }

    public void moveCamera(LatLng latLng) {
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, Constants.DEFAULT_ZOOM_LEVEL));
//        map.moveCamera(getZoomForDistance(latLng, Constants.DEFAULT_ZOOM_RADIUS));
    }

    private void displayParkingAreas() {
        drawMyLocationMarker();
        drawMySearchingMarker(searchingLatLng);
        if (shouldRefreshMap()) {
            refresh();
        }
    }

    //refresh map if the distance between the current location and searching location is greater than 100 km
    private boolean shouldRefreshMap() {
        if (currLatLng != null && prevLatLng != null) {
            float[] results = new float[1];
            Location.distanceBetween(prevLatLng.latitude, prevLatLng.longitude, currLatLng.latitude, currLatLng.longitude, results);
            return results[0] > 100000;
        }
        return prevLatLng == null;
    }

    private void clearMarkers() {
        map.clear();
        searchingMarker = null;
        myLocationMarker = null;
        markers = new HashMap<>();
    }

    private void drawLocationMarkers() {
        for (Map.Entry<String, ParkingLocation> entry : parkingLocations.entrySet()) {
            drawParkingLocationMarker(entry.getValue());
        }
    }

    private void drawParkingLocationMarker(ParkingLocation parkingLocation) {
        String imageName = "parking_";
        try {
            float usedPercentage = 1 - (parkingLocation.getAvailabilityCount() / parkingLocation.getTotalCapacityCount());
            if (usedPercentage < 0.7)
                imageName += "green";
            else if (usedPercentage == 0)
                imageName += "red";
            else
                imageName += "orange";
        } catch (Exception e) {
            imageName += "grey";
        }
        LatLng latLng = new LatLng(parkingLocation.getLatitude(), parkingLocation.getLongitude());
        markers.put(parkingLocation.getParkingId(), map.addMarker(new MarkerOptions()
                .position(latLng)
                .title(parkingLocation.getLocationTitle())
                .icon(BitmapDescriptorFactory.fromResource(Utils.getImage(imageName)))));
    }

    public LatLngBounds getCurrentLatLngBounds() {
        if (currentCameraBounds == null) {
            LatLngBounds bounds = null;
            try {
                bounds = map.getProjection().getVisibleRegion().latLngBounds;
            } catch (Exception e) {
                Utils.Log.e("BSTP_MapComponent_getCurrentLatLngBounds: ", e.getMessage());
            }
            if (bounds == null) {
                return getLatLngBounds(Utils.getMyLocationLatLng(this.activity), Constants.DEFAULT_ZOOM_RADIUS);
            }
            currentCameraBounds = bounds;
        }
        return currentCameraBounds;
    }

    @Override
    public void setEnabled(boolean enabled, boolean noAnimation) {
        if (enabled) {
            if (mapView != null && mapView.findViewById(1) != null) {
                // Get the button view
                View locationButton = ((View) mapView.findViewById(1).getParent()).findViewById(2);
                // and next place it, on bottom right (as Google Maps app)
                RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) locationButton.getLayoutParams();
                layoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP, 0);
                layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
                layoutParams.setMargins(0, 0, 30, 310);

                map.setOnCameraChangeListener(new GoogleMap.OnCameraChangeListener() {
                    @Override
                    public void onCameraChange(CameraPosition cameraPosition) {
                        Event.broadcast("", Constants.EventType.CAMERA_CHANGED.toString());
                        currentCameraBounds = map.getProjection().getVisibleRegion().latLngBounds;
                        if (currentCameraBounds != null) {
                            LatLng center = currentCameraBounds.getCenter();
                            if (center != null) {
                                currLatLng = center;
                                displayParkingAreas();
                            }
                        }
                    }
                });
                map.setOnMyLocationButtonClickListener(new GoogleMap.OnMyLocationButtonClickListener() {
                    @Override
                    public boolean onMyLocationButtonClick() {
                        searchingLatLng = Utils.getMyLocationLatLng(activity);
                        zoomToLocation(searchingLatLng);
                        return true;
                    }
                });
                map.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                    @Override
                    public boolean onMarkerClick(Marker marker) {
                        openLocationDetail(marker);
                        return true;
                    }
                });
                searchingLatLng = Utils.isLocationServiceDisabled(this.activity) ? Constants.DEFAULT_LOCATION : drawMyLocationMarker();
                zoomToLocation(searchingLatLng);
            }
        } else {

        }
        map.setMyLocationEnabled(enabled);
        map.getUiSettings().setMyLocationButtonEnabled(enabled);
        map.getUiSettings().setZoomControlsEnabled(enabled);
        map.getUiSettings().setScrollGesturesEnabled(enabled);
        map.getUiSettings().setTiltGesturesEnabled(enabled);
        map.getUiSettings().setRotateGesturesEnabled(enabled);
    }


    private boolean openLocationDetail(Marker marker) {
        for (Map.Entry<String, Marker> entry : markers.entrySet()) {
            if (marker.equals(entry.getValue())) {
                final String parkingId = entry.getKey();
                ParkingLocationInfoService service = new ParkingLocationInfoService();
                service.parkingid = parkingId;
                service.executeAsync(new ServiceCallback() {
                    @Override
                    public void success(final IService service) {
                        final ParkingLocation parkingLocation = parkingLocations.get(parkingId);

                        AsyncTask<Void, Void, Void> task = new AsyncTask<Void, Void, Void>() {
                            @Override
                            protected Void doInBackground(Void... params) {
                                parkingLocation.merge((new GsonBuilder().create()).fromJson(service.getResponseString(), ParkingLocation.class));
                                return null;
                            }

                            @Override
                            protected void onPostExecute(Void aVoid) {
                                ((MapsActivity) activity).openLocationDetail(parkingLocation);
                            }
                        };

                        task.executeOnExecutor(AsyncTask.SERIAL_EXECUTOR);

                    }

                    @Override
                    public void failure(IService service) {
                        service.getResponseCode();
                    }
                });
                return true;
            }
        }

        if (marker.equals(myLocationMarker)) {
            //do nothing
        } else if (marker.equals(searchingMarker)) {
            //do nothing
        }
        return false;
    }

    public void refresh() {
        try {
            Geocoder geocoder;
            List<Address> addresses;
            geocoder = new Geocoder(this.activity, Locale.getDefault());
            addresses = geocoder.getFromLocation(currLatLng.latitude, currLatLng.longitude, 1);
            if (addresses.size() > 0) {
                Address address = addresses.get(0);
                ((MapsActivity) activity).getSearchCriteria()
                        .setLocationName(address.getFeatureName())
                        .setLatLng(currLatLng)
                        .createSearchService()
                        .executeAsync(new ServiceCallback() {
                            @Override
                            public void success(IService service) {
                                parkingLocations = new HashMap<>();

                                final String responseString = service.getResponseString();

                                AsyncTask<Void, Void, Void> task = new AsyncTask<Void, Void, Void>() {
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
                                        clearMarkers();
                                        drawMyLocationMarker();
                                        drawMySearchingMarker(searchingLatLng);
                                        drawLocationMarkers();
                                    }
                                };

                                task.executeOnExecutor(AsyncTask.SERIAL_EXECUTOR);
                            }

                            @Override
                            public void failure(IService service) {
                                service.getResponseCode();
                            }
                        });
            }
        } catch (Exception e) {
            Utils.Log.e("BSTP_MapComponent_displayParkingAreas: ", e.getMessage());
        }
    }
}
